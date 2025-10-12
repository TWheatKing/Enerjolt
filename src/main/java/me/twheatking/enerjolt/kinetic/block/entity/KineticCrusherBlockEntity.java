package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.kinetic.ConsumeOnlyKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Kinetic Crusher BlockEntity - Material Processing Machine
 * Crushes ores and materials using rotational energy
 *
 * Features:
 * - Resonance frequency system (works best at specific RPM)
 * - Requires minimum torque to operate
 * - Efficiency bonus when in resonance
 * - Speed affects processing time
 * - Vibration from crushing operation
 * - Heat generation from friction
 * - Can overstress if not enough torque
 * - Recipe-based processing
 */
public class KineticCrusherBlockEntity extends BaseKineticBlockEntity<ConsumeOnlyKineticStorage> {

    // Optimal operating parameters
    private static final float RESONANCE_RPM = 64.0f; // Optimal speed
    private static final float RESONANCE_TOLERANCE = 16.0f; // ±16 RPM acceptable
    private static final float MIN_OPERATING_TORQUE = 10.0f; // Minimum torque to crush

    // Processing state
    private int processingTicks = 0;
    private int requiredTicks = 100; // Base processing time (5 seconds)
    private boolean isProcessing = false;

    // Efficiency tracking
    private float currentEfficiency = 1.0f;
    private float resonanceBonus = 1.0f;

    // Crushing mechanics
    private float crushingForce = 0.0f; // Current crushing force
    private int crushedItems = 0; // Total items processed

    // Heat and vibration from operation
    private static final float CRUSHING_HEAT_GENERATION = 2.0f;
    private static final float CRUSHING_VIBRATION = 0.3f;

    // Inventory slots (simplified - actual implementation would use Container)
    private ItemStack inputStack = ItemStack.EMPTY;
    private ItemStack outputStack = ItemStack.EMPTY;

    public KineticCrusherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state,
                "Kinetic Crusher",
                256.0f,          // Max RPM
                100.0f,          // Max Torque
                2.0f,            // Inertia
                0.05f);          // Friction coefficient
    }

    @Override
    protected ConsumeOnlyKineticStorage initKineticStorage() {
        // Crushers use ConsumeOnlyKineticStorage (only receive rotation)
        return new ConsumeOnlyKineticStorage(
                0,                      // Initial RPM
                baseMaxRPM,             // Max RPM (256)
                0,                      // Initial torque
                baseMaxTorque,          // Max torque (100)
                baseInertia,            // Inertia (2.0)
                baseFriction,           // Friction (0.05)
                RESONANCE_RPM,          // Resonance frequency (64 RPM)
                RESONANCE_TOLERANCE     // Resonance tolerance (±16 RPM)
        );
    }

    /**
     * Main tick method - called every game tick
     */
    public void tick() {
        if (level == null) return;

        // Check if we have power
        if (isReceivingRotation()) {
            updateCrushingMechanics();

            // Process items if conditions are met
            if (canProcess()) {
                processItem();
            } else {
                // Can't process - reset progress
                if (isProcessing) {
                    processingTicks = 0;
                    isProcessing = false;
                }
            }

            updateEffects();
        } else {
            // No power - stop processing
            if (isProcessing) {
                processingTicks = 0;
                isProcessing = false;
            }
            coolDown();
        }

        // Sync to client periodically
        if (!level.isClientSide && level.getGameTime() % 10 == 0) {
            setChanged();
            syncKineticToPlayers(2);
        }
    }

    /**
     * Update crushing force and efficiency based on current rotation
     */
    private void updateCrushingMechanics() {
        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();

        // Calculate crushing force (torque × speed factor)
        float speedFactor = rpm / baseMaxRPM;
        crushingForce = torque * (1.0f + speedFactor);

        // Calculate resonance efficiency
        resonanceBonus = kineticStorage.getResonanceEfficiency();

        // Calculate overall efficiency
        calculateEfficiency();
    }

    /**
     * Calculate current operating efficiency
     */
    private void calculateEfficiency() {
        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();

        // Start with resonance bonus
        currentEfficiency = resonanceBonus;

        // Speed affects efficiency
        float speedEfficiency = 1.0f;
        if (rpm < RESONANCE_RPM * 0.5f) {
            // Too slow - penalty
            speedEfficiency = rpm / (RESONANCE_RPM * 0.5f);
        } else if (rpm > baseMaxRPM * 0.8f) {
            // Too fast - penalty
            speedEfficiency = 1.0f - ((rpm - baseMaxRPM * 0.8f) / (baseMaxRPM * 0.2f)) * 0.3f;
        }
        currentEfficiency *= speedEfficiency;

        // Torque sufficiency
        if (torque < MIN_OPERATING_TORQUE) {
            // Not enough torque - can't operate
            currentEfficiency = 0;
        } else if (torque < MIN_OPERATING_TORQUE * 2.0f) {
            // Low torque - reduced efficiency
            float torqueEfficiency = torque / (MIN_OPERATING_TORQUE * 2.0f);
            currentEfficiency *= torqueEfficiency;
        }

        // Temperature penalty
        float temp = kineticStorage.getTemperature();
        if (temp > 80.0f) {
            float heatPenalty = 1.0f - ((temp - 80.0f) / 120.0f) * 0.3f;
            currentEfficiency *= Math.max(0.5f, heatPenalty);
        }

        // Vibration penalty
        float vibration = kineticStorage.getVibration();
        if (vibration > 0.5f) {
            float vibrationPenalty = 1.0f - ((vibration - 0.5f) * 0.4f);
            currentEfficiency *= Math.max(0.7f, vibrationPenalty);
        }

        currentEfficiency = Math.max(0.0f, Math.min(1.5f, currentEfficiency));
    }

    /**
     * Check if crusher can process items
     */
    private boolean canProcess() {
        // Need input item
        if (inputStack.isEmpty()) {
            return false;
        }

        // Need enough torque
        if (kineticStorage.getTorque() < MIN_OPERATING_TORQUE) {
            return false;
        }

        // Need minimum RPM
        if (kineticStorage.getRPM() < 10.0f) {
            return false;
        }

        // Check if output slot can accept result
        // (Simplified - actual implementation would check recipe outputs)
        if (!outputStack.isEmpty() && outputStack.getCount() >= outputStack.getMaxStackSize()) {
            return false;
        }

        return true;
    }

    /**
     * Process the current item
     */
    private void processItem() {
        isProcessing = true;

        // Calculate processing speed based on efficiency
        float processingSpeed = currentEfficiency;

        // Speed bonus from RPM (faster = quicker processing)
        float rpm = kineticStorage.getRPM();
        float speedBonus = Math.min(2.0f, rpm / RESONANCE_RPM);
        processingSpeed *= speedBonus;

        // Increment processing ticks
        processingTicks += (int)(processingSpeed * 1.0f); // Can process faster than 1 tick

        // Check if processing is complete
        if (processingTicks >= requiredTicks) {
            completeProcessing();
        }

        // Consume rotational energy
        float rpmConsumption = 0.5f * (1.0f / currentEfficiency);
        float torqueConsumption = 0.2f * (1.0f / currentEfficiency);
        kineticStorage.consumeRotation(rpmConsumption, torqueConsumption);
    }

    /**
     * Complete the processing and produce output
     */
    private void completeProcessing() {
        // TODO: Get recipe output for input item
        // For now, simplified example

        if (!inputStack.isEmpty()) {
            // Consume input
            inputStack.shrink(1);

            // Produce output (placeholder)
            // outputStack = getRecipeOutput(inputStack);

            // Reset progress
            processingTicks = 0;
            isProcessing = false;

            // Increment counter
            crushedItems++;

            // Generate extra heat from successful crush
            float currentTemp = kineticStorage.getTemperature();
            kineticStorage.setTemperature(currentTemp + 3.0f);

            // TODO: Play crushing sound, spawn particles
        }
    }

    /**
     * Update heat and vibration effects
     */
    private void updateEffects() {
        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();

        // Heat generation from operation
        if (isProcessing) {
            float heatGeneration = CRUSHING_HEAT_GENERATION * (torque / baseMaxTorque);
            float currentTemp = kineticStorage.getTemperature();
            kineticStorage.setTemperature(currentTemp + heatGeneration);
        }

        // Vibration from crushing
        if (isProcessing) {
            float targetVibration = CRUSHING_VIBRATION * (rpm / baseMaxRPM);
            float currentVibration = kineticStorage.getVibration();

            // Add vibration
            float newVibration = Math.min(1.0f, currentVibration + targetVibration * 0.1f);
            kineticStorage.setVibration(newVibration);
        } else {
            // Vibration decays when not processing
            float currentVibration = kineticStorage.getVibration();
            if (currentVibration > 0) {
                kineticStorage.setVibration(Math.max(0, currentVibration - 0.02f));
            }
        }
    }

    /**
     * Cool down when idle
     */
    private void coolDown() {
        float currentTemp = kineticStorage.getTemperature();
        if (currentTemp > 20.0f) {
            float coolingRate = (currentTemp - 20.0f) * 0.03f;
            kineticStorage.setTemperature(Math.max(20.0f, currentTemp - coolingRate));
        }
    }

    /**
     * Get processing progress percentage
     */
    public float getProcessingProgress() {
        return (float)processingTicks / requiredTicks;
    }

    /**
     * Get processing percentage for display
     */
    public int getProcessingPercentage() {
        return (int)(getProcessingProgress() * 100.0f);
    }

    // Getters
    public boolean isProcessing() {
        return isProcessing;
    }

    public float getCurrentEfficiency() {
        return currentEfficiency;
    }

    public float getResonanceBonus() {
        return resonanceBonus;
    }

    public boolean isInResonance() {
        return kineticStorage.isInResonance();
    }

    public float getCrushingForce() {
        return crushingForce;
    }

    public int getCrushedItems() {
        return crushedItems;
    }

    public boolean hasMinimumTorque() {
        return kineticStorage.hasMinimumTorque();
    }

    public ItemStack getInputStack() {
        return inputStack;
    }

    public void setInputStack(ItemStack stack) {
        this.inputStack = stack;
        setChanged();
    }

    public ItemStack getOutputStack() {
        return outputStack;
    }

    public void setOutputStack(ItemStack stack) {
        this.outputStack = stack;
        setChanged();
    }

    // NBT Serialization
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ProcessingTicks", processingTicks);
        tag.putInt("RequiredTicks", requiredTicks);
        tag.putBoolean("IsProcessing", isProcessing);
        tag.putFloat("CurrentEfficiency", currentEfficiency);
        tag.putFloat("ResonanceBonus", resonanceBonus);
        tag.putFloat("CrushingForce", crushingForce);
        tag.putInt("CrushedItems", crushedItems);

        // Save inventory (simplified)
        if (!inputStack.isEmpty()) {
            tag.put("InputStack", inputStack.save(registries));
        }
        if (!outputStack.isEmpty()) {
            tag.put("OutputStack", outputStack.save(registries));
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        processingTicks = tag.getInt("ProcessingTicks");
        requiredTicks = tag.getInt("RequiredTicks");
        isProcessing = tag.getBoolean("IsProcessing");
        currentEfficiency = tag.getFloat("CurrentEfficiency");
        resonanceBonus = tag.getFloat("ResonanceBonus");
        crushingForce = tag.getFloat("CrushingForce");
        crushedItems = tag.getInt("CrushedItems");

        // Load inventory (simplified)
        if (tag.contains("InputStack")) {
            inputStack = ItemStack.parse(registries, tag.getCompound("InputStack")).orElse(ItemStack.EMPTY);
        }
        if (tag.contains("OutputStack")) {
            outputStack = ItemStack.parse(registries, tag.getCompound("OutputStack")).orElse(ItemStack.EMPTY);
        }
    }

    // Client-side display info
    @Override
    public String getDebugInfo() {
        return String.format(
                "%s | RPM: %.1f/%.1f | Torque: %.1f/%.1f Nm | Efficiency: %.1f%% | Resonance: %s (%.1fx) | Force: %.1f N | Progress: %d%% | Crushed: %d",
                getMachineName(),
                kineticStorage.getRPM(),
                baseMaxRPM,
                kineticStorage.getTorque(),
                baseMaxTorque,
                currentEfficiency * 100.0f,
                isInResonance() ? "§a✓" : "§c✗",
                resonanceBonus,
                crushingForce,
                getProcessingPercentage(),
                crushedItems
        );
    }

    public String getCrusherStatus() {
        StringBuilder status = new StringBuilder();

        if (isProcessing) {
            status.append(String.format("§aCrushing...§r (§e%d%%§r)\n", getProcessingPercentage()));
        } else if (canProcess()) {
            status.append("§7Ready to Crush§r\n");
        } else {
            status.append("§cIdle§r\n");
        }

        status.append(String.format("RPM: §b%.1f§r/§7%.1f§r\n", kineticStorage.getRPM(), baseMaxRPM));
        status.append(String.format("Torque: §6%.1f§r/§7%.1f§r Nm\n", kineticStorage.getTorque(), baseMaxTorque));

        if (isInResonance()) {
            status.append(String.format("§a♪ In Resonance§r (§e%.1fx§r bonus)\n", resonanceBonus));
        } else {
            status.append(String.format("§7Out of Resonance§r (%.1fx)\n", resonanceBonus));
        }

        status.append(String.format("Efficiency: §a%.1f%%§r\n", currentEfficiency * 100.0f));
        status.append(String.format("Crushing Force: §6%.1f N§r\n", crushingForce));
        status.append(String.format("Temperature: §c%.1f°C§r\n", kineticStorage.getTemperature()));
        status.append(String.format("Total Crushed: §e%d§r items", crushedItems));

        if (!hasMinimumTorque()) {
            status.append("\n§c⚠ Insufficient Torque§r");
        }

        if (kineticStorage.getTemperature() > 80.0f) {
            status.append("\n§c⚠ Overheating§r");
        }

        return status.toString();
    }

    public String getResonanceInfo() {
        return String.format(
                "Optimal RPM: §e%.1f§r ±%.1f\nCurrent RPM: %s%.1f§r\nResonance Bonus: %s%.1fx§r",
                RESONANCE_RPM,
                RESONANCE_TOLERANCE,
                isInResonance() ? "§a" : "§c",
                kineticStorage.getRPM(),
                resonanceBonus > 1.0f ? "§a" : (resonanceBonus < 1.0f ? "§c" : "§7"),
                resonanceBonus
        );
    }
}
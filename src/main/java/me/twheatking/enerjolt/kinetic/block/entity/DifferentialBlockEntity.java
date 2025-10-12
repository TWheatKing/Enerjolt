package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.kinetic.BasicKineticStorage;
import me.twheatking.enerjolt.kinetic.IKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
import me.twheatking.enerjolt.kinetic.tier.ShaftMaterialTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Differential BlockEntity - Power Distribution System
 * Splits rotational power to multiple outputs with independent control
 *
 * Features:
 * - One input, multiple outputs (typically 2-3)
 * - Independent speed/torque ratios per output
 * - Dynamic power distribution based on load
 * - Can reverse output direction per side
 * - Torque splitting based on resistance
 * - Acts like a mechanical power splitter
 * - Load balancing between outputs
 * - Material-based efficiency
 */
public class DifferentialBlockEntity extends BaseKineticBlockEntity<BasicKineticStorage> {

    /**
     * Differential modes for power distribution
     */
    public enum DifferentialMode {
        EQUAL_SPLIT(0.5f, 0.5f, "Equal Split"),          // 50/50 split
        BIASED_A(0.7f, 0.3f, "Biased Output A"),         // 70/30 split
        BIASED_B(0.3f, 0.7f, "Biased Output B"),         // 30/70 split
        PRIORITY_A(1.0f, 0.0f, "Priority Output A"),     // 100% to A first
        PRIORITY_B(0.0f, 1.0f, "Priority Output B"),     // 100% to B first
        LOAD_BALANCED(0.5f, 0.5f, "Load Balanced");      // Dynamic based on load

        public final float outputARatio;
        public final float outputBRatio;
        public final String displayName;

        DifferentialMode(float ratioA, float ratioB, String name) {
            this.outputARatio = ratioA;
            this.outputBRatio = ratioB;
            this.displayName = name;
        }
    }

    /**
     * Output configuration for each side
     */
    public static class OutputConfig {
        public float speedMultiplier = 1.0f;   // Speed adjustment
        public float torqueMultiplier = 1.0f;  // Torque adjustment
        public boolean reversed = false;        // Reverse direction
        public boolean enabled = true;          // Output enabled
        public float currentLoad = 0.0f;        // Current load on output

        public OutputConfig() {}

        public OutputConfig(float speed, float torque, boolean reversed, boolean enabled) {
            this.speedMultiplier = speed;
            this.torqueMultiplier = torque;
            this.reversed = reversed;
            this.enabled = enabled;
        }
    }

    // Differential configuration
    private DifferentialMode mode = DifferentialMode.EQUAL_SPLIT;
    private ShaftMaterialTier materialTier = ShaftMaterialTier.IRON;

    // Output configurations (A = left/right, B = up/down, input = front/back)
    private OutputConfig outputA = new OutputConfig();
    private OutputConfig outputB = new OutputConfig();

    // Input tracking
    private Direction inputDirection = Direction.NORTH;
    private float inputRPM = 0.0f;
    private float inputTorque = 0.0f;

    // Output tracking
    private float outputARPM = 0.0f;
    private float outputATorque = 0.0f;
    private float outputBRPM = 0.0f;
    private float outputBTorque = 0.0f;

    // Efficiency and losses
    private float currentEfficiency = 0.92f; // Base differential efficiency
    private static final float MIN_EFFICIENCY = 0.7f;

    // Thermal tracking
    private float gearHeat = 0.0f;
    private static final float DIFF_HEAT_GENERATION = 1.2f;

    // Load balancing (for LOAD_BALANCED mode)
    private float loadBalanceRatioA = 0.5f;
    private float loadBalanceRatioB = 0.5f;

    public DifferentialBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, ShaftMaterialTier.IRON);
    }

    public DifferentialBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ShaftMaterialTier tier) {
        super(type, pos, state,
                "Differential",
                tier.getMaxRPM(),
                tier.getMaxTorque(),
                tier.getInertia() * 1.3f,  // Higher inertia (complex gearing)
                tier.getFrictionCoefficient() * 1.5f); // More friction than shafts

        this.materialTier = tier;
    }

    @Override
    protected BasicKineticStorage initKineticStorage() {
        // Differentials use BasicKineticStorage (receive input, provide outputs)
        return new BasicKineticStorage(
                0,                      // Initial RPM
                baseMaxRPM,             // Max RPM
                0,                      // Initial torque
                baseMaxTorque,          // Max torque
                baseInertia,            // Higher inertia
                baseFriction            // Higher friction
        );
    }

    /**
     * Main tick method - called every game tick
     */
    public void tick() {
        if (level == null) return;

        // Call storage tick for basic physics
        kineticStorage.tick();

        // Process differential logic if receiving rotation
        if (isReceivingRotation()) {
            captureInput();
            calculateLoadBalancing();
            distributePower();
            updateThermalEffects();
        } else {
            // No input - reset outputs
            resetOutputs();
        }

        // Sync to client periodically
        if (!level.isClientSide && level.getGameTime() % 20 == 0) {
            setChanged();
            syncKineticToPlayers(2);
        }
    }

    /**
     * Capture input rotation values
     */
    private void captureInput() {
        inputRPM = kineticStorage.getRPM();
        inputTorque = kineticStorage.getTorque();
    }

    /**
     * Calculate load balancing ratios (for LOAD_BALANCED mode)
     */
    private void calculateLoadBalancing() {
        if (mode != DifferentialMode.LOAD_BALANCED) {
            return;
        }

        // Measure loads on each output
        float totalLoad = outputA.currentLoad + outputB.currentLoad;

        if (totalLoad > 0.01f) {
            // Distribute power inversely to load (less loaded side gets more power)
            // This simulates mechanical load balancing
            float loadRatioA = outputA.currentLoad / totalLoad;
            float loadRatioB = outputB.currentLoad / totalLoad;

            // Invert ratios (less load = more power)
            loadBalanceRatioA = 1.0f - loadRatioA;
            loadBalanceRatioB = 1.0f - loadRatioB;

            // Normalize
            float total = loadBalanceRatioA + loadBalanceRatioB;
            if (total > 0) {
                loadBalanceRatioA /= total;
                loadBalanceRatioB /= total;
            }
        } else {
            // No load - equal distribution
            loadBalanceRatioA = 0.5f;
            loadBalanceRatioB = 0.5f;
        }
    }

    /**
     * Distribute power to outputs based on mode and configuration
     */
    private void distributePower() {
        if (inputRPM < 0.1f || inputTorque < 0.1f) {
            resetOutputs();
            return;
        }

        // Calculate efficiency
        calculateEfficiency();

        // Determine power split ratios
        float ratioA, ratioB;

        if (mode == DifferentialMode.LOAD_BALANCED) {
            ratioA = loadBalanceRatioA;
            ratioB = loadBalanceRatioB;
        } else {
            ratioA = mode.outputARatio;
            ratioB = mode.outputBRatio;
        }

        // Calculate output A
        if (outputA.enabled && ratioA > 0) {
            outputARPM = inputRPM * outputA.speedMultiplier * ratioA;
            outputATorque = inputTorque * outputA.torqueMultiplier * ratioA * currentEfficiency;

            // Apply limits
            outputARPM = Math.min(outputARPM, materialTier.getMaxRPM());
            outputATorque = Math.min(outputATorque, materialTier.getMaxTorque());
        } else {
            outputARPM = 0;
            outputATorque = 0;
        }

        // Calculate output B
        if (outputB.enabled && ratioB > 0) {
            outputBRPM = inputRPM * outputB.speedMultiplier * ratioB;
            outputBTorque = inputTorque * outputB.torqueMultiplier * ratioB * currentEfficiency;

            // Apply limits
            outputBRPM = Math.min(outputBRPM, materialTier.getMaxRPM());
            outputBTorque = Math.min(outputBTorque, materialTier.getMaxTorque());
        } else {
            outputBRPM = 0;
            outputBTorque = 0;
        }

        // Update storage for propagation (average of outputs)
        float avgRPM = (outputARPM + outputBRPM) / 2.0f;
        float avgTorque = (outputATorque + outputBTorque) / 2.0f;

        kineticStorage.setRPMWithoutUpdate(avgRPM);
        kineticStorage.setTorqueWithoutUpdate(avgTorque);

        setChanged();
    }

    /**
     * Calculate current efficiency
     */
    private void calculateEfficiency() {
        // Base efficiency from material
        float materialEff = materialTier.getEfficiencyAtRPM(inputRPM);

        // Differential complexity penalty
        float complexityPenalty = 0.92f; // 8% loss from gear complexity

        // Heat penalty
        float heatPenalty = 1.0f;
        if (gearHeat > 60.0f) {
            heatPenalty = 1.0f - ((gearHeat - 60.0f) / 80.0f) * 0.2f;
            heatPenalty = Math.max(0.7f, heatPenalty);
        }

        // Mode-specific penalties
        float modePenalty = 1.0f;
        if (mode == DifferentialMode.PRIORITY_A || mode == DifferentialMode.PRIORITY_B) {
            modePenalty = 0.95f; // Priority modes are less efficient
        } else if (mode == DifferentialMode.LOAD_BALANCED) {
            modePenalty = 0.90f; // Load balancing has overhead
        }

        currentEfficiency = materialEff * complexityPenalty * heatPenalty * modePenalty;
        currentEfficiency = Math.max(MIN_EFFICIENCY, currentEfficiency);
    }

    /**
     * Update thermal effects from operation
     */
    private void updateThermalEffects() {
        // Heat generation from power throughput
        float powerThroughput = inputRPM * inputTorque;
        float heatGeneration = (powerThroughput / 10000.0f) * DIFF_HEAT_GENERATION;

        // More heat when splitting unevenly
        if (mode == DifferentialMode.BIASED_A || mode == DifferentialMode.BIASED_B) {
            heatGeneration *= 1.2f;
        }

        gearHeat = Math.min(100.0f, gearHeat + heatGeneration);

        // Natural cooling
        if (gearHeat > 20.0f) {
            float coolingRate = (gearHeat - 20.0f) * 0.03f;
            gearHeat = Math.max(20.0f, gearHeat - coolingRate);
        }

        // Update storage temperature
        kineticStorage.setTemperature(gearHeat);
    }

    /**
     * Reset all outputs to zero
     */
    private void resetOutputs() {
        outputARPM = 0;
        outputATorque = 0;
        outputBRPM = 0;
        outputBTorque = 0;

        kineticStorage.setRPMWithoutUpdate(0);
        kineticStorage.setTorqueWithoutUpdate(0);
    }

    /**
     * Get kinetic storage for specific side (output-specific)
     */
    @Override
    public @Nullable IKineticStorage getKineticStorage(@Nullable Direction side) {
        // This would be more complex in actual implementation
        // Different sides would return different output values
        // For now, simplified
        return kineticStorage;
    }

    /**
     * Cycle to next differential mode
     */
    public void cycleMode() {
        int nextOrdinal = (mode.ordinal() + 1) % DifferentialMode.values().length;
        mode = DifferentialMode.values()[nextOrdinal];
        setChanged();
    }

    /**
     * Set specific mode
     */
    public void setMode(DifferentialMode newMode) {
        this.mode = newMode;
        setChanged();
    }

    // Getters and Setters
    public DifferentialMode getMode() {
        return mode;
    }

    public ShaftMaterialTier getMaterialTier() {
        return materialTier;
    }

    public void setMaterialTier(ShaftMaterialTier tier) {
        this.materialTier = tier;

        kineticStorage.setMaxRPMWithoutUpdate(tier.getMaxRPM());
        kineticStorage.setMaxTorqueWithoutUpdate(tier.getMaxTorque());
        kineticStorage.setInertiaWithoutUpdate(tier.getInertia() * 1.3f);
        kineticStorage.setFrictionCoefficient(tier.getFrictionCoefficient() * 1.5f);

        setChanged();
    }

    public OutputConfig getOutputA() {
        return outputA;
    }

    public OutputConfig getOutputB() {
        return outputB;
    }

    public float getOutputARPM() {
        return outputARPM;
    }

    public float getOutputATorque() {
        return outputATorque;
    }

    public float getOutputBRPM() {
        return outputBRPM;
    }

    public float getOutputBTorque() {
        return outputBTorque;
    }

    public float getInputRPM() {
        return inputRPM;
    }

    public float getInputTorque() {
        return inputTorque;
    }

    public float getCurrentEfficiency() {
        return currentEfficiency;
    }

    public float getGearHeat() {
        return gearHeat;
    }

    public float getPowerSplitRatioA() {
        return mode == DifferentialMode.LOAD_BALANCED ? loadBalanceRatioA : mode.outputARatio;
    }

    public float getPowerSplitRatioB() {
        return mode == DifferentialMode.LOAD_BALANCED ? loadBalanceRatioB : mode.outputBRatio;
    }

    // NBT Serialization
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Mode", mode.name());
        tag.putString("MaterialTier", materialTier.getSerializedName());

        // Save output A config
        CompoundTag outputATag = new CompoundTag();
        outputATag.putFloat("SpeedMultiplier", outputA.speedMultiplier);
        outputATag.putFloat("TorqueMultiplier", outputA.torqueMultiplier);
        outputATag.putBoolean("Reversed", outputA.reversed);
        outputATag.putBoolean("Enabled", outputA.enabled);
        outputATag.putFloat("CurrentLoad", outputA.currentLoad);
        tag.put("OutputA", outputATag);

        // Save output B config
        CompoundTag outputBTag = new CompoundTag();
        outputBTag.putFloat("SpeedMultiplier", outputB.speedMultiplier);
        outputBTag.putFloat("TorqueMultiplier", outputB.torqueMultiplier);
        outputBTag.putBoolean("Reversed", outputB.reversed);
        outputBTag.putBoolean("Enabled", outputB.enabled);
        outputBTag.putFloat("CurrentLoad", outputB.currentLoad);
        tag.put("OutputB", outputBTag);

        tag.putFloat("InputRPM", inputRPM);
        tag.putFloat("InputTorque", inputTorque);
        tag.putFloat("OutputARPM", outputARPM);
        tag.putFloat("OutputATorque", outputATorque);
        tag.putFloat("OutputBRPM", outputBRPM);
        tag.putFloat("OutputBTorque", outputBTorque);
        tag.putFloat("CurrentEfficiency", currentEfficiency);
        tag.putFloat("GearHeat", gearHeat);
        tag.putFloat("LoadBalanceRatioA", loadBalanceRatioA);
        tag.putFloat("LoadBalanceRatioB", loadBalanceRatioB);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            mode = DifferentialMode.valueOf(tag.getString("Mode"));
        } catch (IllegalArgumentException e) {
            mode = DifferentialMode.EQUAL_SPLIT;
        }

        materialTier = ShaftMaterialTier.fromName(tag.getString("MaterialTier"));

        // Load output A config
        if (tag.contains("OutputA")) {
            CompoundTag outputATag = tag.getCompound("OutputA");
            outputA.speedMultiplier = outputATag.getFloat("SpeedMultiplier");
            outputA.torqueMultiplier = outputATag.getFloat("TorqueMultiplier");
            outputA.reversed = outputATag.getBoolean("Reversed");
            outputA.enabled = outputATag.getBoolean("Enabled");
            outputA.currentLoad = outputATag.getFloat("CurrentLoad");
        }

        // Load output B config
        if (tag.contains("OutputB")) {
            CompoundTag outputBTag = tag.getCompound("OutputB");
            outputB.speedMultiplier = outputBTag.getFloat("SpeedMultiplier");
            outputB.torqueMultiplier = outputBTag.getFloat("TorqueMultiplier");
            outputB.reversed = outputBTag.getBoolean("Reversed");
            outputB.enabled = outputBTag.getBoolean("Enabled");
            outputB.currentLoad = outputBTag.getFloat("CurrentLoad");
        }

        inputRPM = tag.getFloat("InputRPM");
        inputTorque = tag.getFloat("InputTorque");
        outputARPM = tag.getFloat("OutputARPM");
        outputATorque = tag.getFloat("OutputATorque");
        outputBRPM = tag.getFloat("OutputBRPM");
        outputBTorque = tag.getFloat("OutputBTorque");
        currentEfficiency = tag.getFloat("CurrentEfficiency");
        gearHeat = tag.getFloat("GearHeat");
        loadBalanceRatioA = tag.getFloat("LoadBalanceRatioA");
        loadBalanceRatioB = tag.getFloat("LoadBalanceRatioB");
    }

    // Client-side display info
    @Override
    public String getDebugInfo() {
        return String.format(
                "%s [%s] | Mode: %s | In: %.1f RPM, %.1f Nm | Out A: %.1f RPM, %.1f Nm (%.0f%%) | Out B: %.1f RPM, %.1f Nm (%.0f%%) | Eff: %.1f%% | Heat: %.1f°C",
                getMachineName(),
                materialTier.getSerializedName().toUpperCase(),
                mode.displayName,
                inputRPM,
                inputTorque,
                outputARPM,
                outputATorque,
                getPowerSplitRatioA() * 100.0f,
                outputBRPM,
                outputBTorque,
                getPowerSplitRatioB() * 100.0f,
                currentEfficiency * 100.0f,
                gearHeat
        );
    }

    public String getDifferentialStatus() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("§e%s§r\n", mode.displayName));

        status.append(String.format("\n§bInput:§r\n"));
        status.append(String.format("  RPM: §b%.1f§r\n", inputRPM));
        status.append(String.format("  Torque: §6%.1f§r Nm\n", inputTorque));
        status.append(String.format("  Power: §a%.1f§r W\n", inputRPM * inputTorque * 2.0f * (float)Math.PI / 60.0f));

        status.append(String.format("\n§aOutput A:§r (%.0f%%)\n", getPowerSplitRatioA() * 100.0f));
        status.append(String.format("  RPM: §b%.1f§r", outputARPM));
        if (outputA.reversed) status.append(" §c⟲§r");
        status.append(String.format("\n  Torque: §6%.1f§r Nm\n", outputATorque));
        status.append(String.format("  %s\n", outputA.enabled ? "§aEnabled§r" : "§7Disabled§r"));

        status.append(String.format("\n§aOutput B:§r (%.0f%%)\n", getPowerSplitRatioB() * 100.0f));
        status.append(String.format("  RPM: §b%.1f§r", outputBRPM));
        if (outputB.reversed) status.append(" §c⟲§r");
        status.append(String.format("\n  Torque: §6%.1f§r Nm\n", outputBTorque));
        status.append(String.format("  %s\n", outputB.enabled ? "§aEnabled§r" : "§7Disabled§r"));

        status.append(String.format("\nEfficiency: §a%.1f%%§r\n", currentEfficiency * 100.0f));
        status.append(String.format("Heat: §c%.1f°C§r", gearHeat));

        if (gearHeat > 80.0f) {
            status.append(" §c⚠§r");
        }

        return status.toString();
    }
}
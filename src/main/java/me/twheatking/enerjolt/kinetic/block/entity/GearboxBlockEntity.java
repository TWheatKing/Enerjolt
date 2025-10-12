package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.kinetic.BasicKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
import me.twheatking.enerjolt.kinetic.tier.ShaftMaterialTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Gearbox BlockEntity - Variable Transmission System
 * Allows dynamic shifting between speed and torque ratios
 *
 * Features:
 * - Multiple gear ratios (speed vs torque tradeoff)
 * - Redstone-controlled or manual ratio shifting
 * - Material-based efficiency
 * - Thermal buildup from gear friction
 * - Shift cooldown to prevent spam
 * - Heat penalties when operating at extreme ratios
 */
public class GearboxBlockEntity extends BaseKineticBlockEntity<BasicKineticStorage> {

    /**
     * Gear Ratio Modes
     * Speed multiplier affects output RPM
     * Torque multiplier affects output torque
     * Power (Watts) = Torque × Angular Velocity, so power is conserved (minus losses)
     */
    public enum GearRatio {
        OVERDRIVE(4.0f, 0.25f, "4:1 Speed", 0.85f),     // 4x speed, 1/4 torque, 85% efficiency
        HIGH(2.0f, 0.5f, "2:1 Speed", 0.90f),           // 2x speed, 1/2 torque, 90% efficiency
        NEUTRAL(1.0f, 1.0f, "1:1 Neutral", 0.95f),      // 1:1 pass-through, 95% efficiency
        LOW(0.5f, 2.0f, "1:2 Torque", 0.90f),           // 1/2 speed, 2x torque, 90% efficiency
        CRAWLER(0.25f, 4.0f, "1:4 Torque", 0.85f);      // 1/4 speed, 4x torque, 85% efficiency

        public final float speedMultiplier;
        public final float torqueMultiplier;
        public final String displayName;
        public final float baseEfficiency;

        GearRatio(float speedMult, float torqueMult, String name, float efficiency) {
            this.speedMultiplier = speedMult;
            this.torqueMultiplier = torqueMult;
            this.displayName = name;
            this.baseEfficiency = efficiency;
        }

        public GearRatio next() {
            int nextOrdinal = (this.ordinal() + 1) % values().length;
            return values()[nextOrdinal];
        }

        public GearRatio previous() {
            int prevOrdinal = (this.ordinal() - 1 + values().length) % values().length;
            return values()[prevOrdinal];
        }

        public boolean isExtreme() {
            return this == OVERDRIVE || this == CRAWLER;
        }
    }

    // Gearbox state
    private GearRatio currentRatio = GearRatio.NEUTRAL;
    private boolean isShifting = false;
    private int shiftCooldown = 0;
    private static final int SHIFT_COOLDOWN_TICKS = 20; // 1 second

    // Material tier
    private ShaftMaterialTier materialTier = ShaftMaterialTier.IRON;

    // Thermal properties
    private static final float GEARBOX_HEAT_MULTIPLIER = 1.5f; // Gearboxes generate more heat than shafts
    private static final float HEAT_DAMAGE_THRESHOLD = 0.8f; // 80% of max temp

    // Efficiency tracking
    private float currentEfficiency = 1.0f;

    public GearboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, ShaftMaterialTier.IRON);
    }

    public GearboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ShaftMaterialTier tier) {
        super(type, pos, state,
                "Gearbox",
                tier.getMaxRPM(),
                tier.getMaxTorque(),
                tier.getInertia() * 1.5f,  // Gearboxes have higher inertia
                tier.getFrictionCoefficient() * 1.2f); // More friction than shafts

        this.materialTier = tier;
    }

    @Override
    protected BasicKineticStorage initKineticStorage() {
        // Gearboxes use BasicKineticStorage (bidirectional transfer)
        return new BasicKineticStorage(
                0,                      // Initial RPM
                baseMaxRPM,             // Max RPM
                0,                      // Initial torque
                baseMaxTorque,          // Max torque
                baseInertia,            // Inertia (higher than shafts)
                baseFriction            // Friction coefficient
        );
    }

    /**
     * Main tick method - called every game tick
     */
    public void tick() {
        if (level == null) return;

        // Handle shift cooldown
        if (shiftCooldown > 0) {
            shiftCooldown--;
            if (shiftCooldown == 0) {
                isShifting = false;
            }
        }

        // Call the storage's tick method for basic friction, cooling, vibration
        kineticStorage.tick();

        // Process gearbox-specific logic if receiving rotation
        if (isReceivingRotation()) {
            applyGearRatioTransformation();
            updateThermalEffects();
            checkForHeatDamage();
        }

        // Sync to client periodically
        if (!level.isClientSide && level.getGameTime() % 20 == 0) {
            setChanged();
            syncKineticToPlayers(2); // 2 chunk range
        }
    }

    /**
     * Apply gear ratio transformation to input rotation
     */
    private void applyGearRatioTransformation() {
        float inputRPM = kineticStorage.getRPM();
        float inputTorque = kineticStorage.getTorque();

        // Apply gear ratio transformation
        float outputRPM = inputRPM * currentRatio.speedMultiplier;
        float outputTorque = inputTorque * currentRatio.torqueMultiplier;

        // Calculate total efficiency
        calculateCurrentEfficiency();

        // Apply efficiency loss to torque
        outputTorque *= currentEfficiency;

        // Check RPM limits
        if (outputRPM > materialTier.getMaxRPM()) {
            // Overspeed - scale back both RPM and torque proportionally
            float overspeedRatio = materialTier.getMaxRPM() / outputRPM;
            outputRPM = materialTier.getMaxRPM();
            outputTorque *= overspeedRatio;
        }

        // Check torque limits
        if (outputTorque > materialTier.getMaxTorque()) {
            // Overtorque - limit to max
            outputTorque = materialTier.getMaxTorque();
        }

        // Update storage with output values
        kineticStorage.setRPMWithoutUpdate(outputRPM);
        kineticStorage.setTorqueWithoutUpdate(outputTorque);

        setChanged();
    }

    /**
     * Calculate current efficiency based on multiple factors
     */
    private void calculateCurrentEfficiency() {
        // Start with gear ratio base efficiency
        float efficiency = currentRatio.baseEfficiency;

        // Material efficiency at current RPM
        float inputRPM = kineticStorage.getRPM() / currentRatio.speedMultiplier; // Calculate input RPM
        float materialEfficiency = materialTier.getEfficiencyAtRPM(inputRPM);
        efficiency *= materialEfficiency;

        // Heat penalty
        float temp = kineticStorage.getTemperature();
        float maxTemp = materialTier.getMaxTemperature();
        if (temp > maxTemp * HEAT_DAMAGE_THRESHOLD) {
            float heatPenalty = 1.0f - ((temp - maxTemp * HEAT_DAMAGE_THRESHOLD) / (maxTemp * (1.0f - HEAT_DAMAGE_THRESHOLD)) * 0.3f);
            efficiency *= Math.max(0.5f, heatPenalty);
        }

        // Vibration penalty
        float vibration = kineticStorage.getVibration();
        if (vibration > 0.5f) {
            float vibrationPenalty = 1.0f - ((vibration - 0.5f) * 0.4f);
            efficiency *= Math.max(0.7f, vibrationPenalty);
        }

        // Shifting penalty (reduced efficiency while shifting)
        if (isShifting) {
            efficiency *= 0.7f; // 30% penalty while shifting
        }

        // Extreme ratio penalty (overdrive and crawler are harder on gears)
        if (currentRatio.isExtreme()) {
            efficiency *= 0.95f; // 5% additional penalty
        }

        currentEfficiency = Math.max(0.3f, efficiency); // Minimum 30% efficiency
    }

    /**
     * Update thermal effects from gear friction
     */
    private void updateThermalEffects() {
        float inputRPM = kineticStorage.getRPM() / currentRatio.speedMultiplier;
        float inputTorque = kineticStorage.getTorque() / currentRatio.torqueMultiplier;

        // Use material tier's heat calculation with gearbox multiplier
        float baseHeat = materialTier.calculateHeatGeneration(inputRPM, inputTorque);
        float gearboxHeat = baseHeat * GEARBOX_HEAT_MULTIPLIER;

        // Additional heat from gear ratio complexity
        float ratioComplexity = (float)Math.abs(Math.log(currentRatio.speedMultiplier)) + 1.0f;
        gearboxHeat *= ratioComplexity;

        // Additional heat while shifting
        if (isShifting) {
            gearboxHeat *= 1.5f;
        }

        // Apply heat
        float currentTemp = kineticStorage.getTemperature();
        kineticStorage.setTemperature(currentTemp + gearboxHeat);
    }

    /**
     * Check for heat damage
     */
    private void checkForHeatDamage() {
        float temp = kineticStorage.getTemperature();
        float maxTemp = materialTier.getMaxTemperature();

        if (temp > maxTemp) {
            // Overheating - chance to damage gearbox
            float overheatRatio = (temp - maxTemp) / maxTemp;

            if (level.random.nextFloat() < overheatRatio * 0.01f) { // Up to 1% chance per tick
                // TODO: Damage gearbox, reduce durability, play sound, spawn particles
            }
        }
    }

    /**
     * Shift to next gear ratio (shift up)
     * @return True if shift was successful
     */
    public boolean shiftUp() {
        if (isShifting || shiftCooldown > 0) return false;

        currentRatio = currentRatio.next();
        isShifting = true;
        shiftCooldown = SHIFT_COOLDOWN_TICKS;

        // Shifting generates heat
        float currentTemp = kineticStorage.getTemperature();
        kineticStorage.setTemperature(currentTemp + 10.0f);

        // TODO: Play shift sound

        setChanged();
        return true;
    }

    /**
     * Shift to previous gear ratio (shift down)
     * @return True if shift was successful
     */
    public boolean shiftDown() {
        if (isShifting || shiftCooldown > 0) return false;

        currentRatio = currentRatio.previous();
        isShifting = true;
        shiftCooldown = SHIFT_COOLDOWN_TICKS;

        // Shifting generates heat
        float currentTemp = kineticStorage.getTemperature();
        kineticStorage.setTemperature(currentTemp + 10.0f);

        // TODO: Play shift sound

        setChanged();
        return true;
    }

    /**
     * Set specific gear ratio directly
     * @param ratio The gear ratio to set
     * @return True if set was successful
     */
    public boolean setGearRatio(GearRatio ratio) {
        if (isShifting || shiftCooldown > 0) return false;

        if (currentRatio != ratio) {
            currentRatio = ratio;
            isShifting = true;
            shiftCooldown = SHIFT_COOLDOWN_TICKS;

            float currentTemp = kineticStorage.getTemperature();
            kineticStorage.setTemperature(currentTemp + 10.0f);

            setChanged();
            return true;
        }

        return false;
    }

    /**
     * Shift based on redstone signal strength
     * @param signalStrength Redstone signal (0-15)
     */
    public void shiftByRedstone(int signalStrength) {
        if (isShifting || shiftCooldown > 0) return;

        // Map signal strength to gear ratios
        // 0-2: Crawler, 3-5: Low, 6-9: Neutral, 10-12: High, 13-15: Overdrive
        GearRatio targetRatio;
        if (signalStrength <= 2) {
            targetRatio = GearRatio.CRAWLER;
        } else if (signalStrength <= 5) {
            targetRatio = GearRatio.LOW;
        } else if (signalStrength <= 9) {
            targetRatio = GearRatio.NEUTRAL;
        } else if (signalStrength <= 12) {
            targetRatio = GearRatio.HIGH;
        } else {
            targetRatio = GearRatio.OVERDRIVE;
        }

        setGearRatio(targetRatio);
    }

    // Getters
    public GearRatio getCurrentRatio() {
        return currentRatio;
    }

    public float getCurrentEfficiency() {
        return currentEfficiency;
    }

    public boolean isShifting() {
        return isShifting;
    }

    public int getShiftCooldown() {
        return shiftCooldown;
    }

    public ShaftMaterialTier getMaterialTier() {
        return materialTier;
    }

    public void setMaterialTier(ShaftMaterialTier tier) {
        this.materialTier = tier;

        // Update base values
        kineticStorage.setMaxRPMWithoutUpdate(tier.getMaxRPM());
        kineticStorage.setMaxTorqueWithoutUpdate(tier.getMaxTorque());
        kineticStorage.setInertiaWithoutUpdate(tier.getInertia() * 1.5f);
        kineticStorage.setFrictionCoefficient(tier.getFrictionCoefficient() * 1.2f);

        setChanged();
    }

    public Component getGearRatioDisplay() {
        return Component.literal(currentRatio.displayName);
    }

    public float getInputRPM() {
        return kineticStorage.getRPM() / currentRatio.speedMultiplier;
    }

    public float getInputTorque() {
        return kineticStorage.getTorque() / currentRatio.torqueMultiplier;
    }

    public float getOutputRPM() {
        return kineticStorage.getRPM();
    }

    public float getOutputTorque() {
        return kineticStorage.getTorque();
    }

    // NBT Serialization
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("GearRatio", currentRatio.name());
        tag.putBoolean("IsShifting", isShifting);
        tag.putInt("ShiftCooldown", shiftCooldown);
        tag.putString("MaterialTier", materialTier.getSerializedName());
        tag.putFloat("CurrentEfficiency", currentEfficiency);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            currentRatio = GearRatio.valueOf(tag.getString("GearRatio"));
        } catch (IllegalArgumentException e) {
            currentRatio = GearRatio.NEUTRAL;
        }

        isShifting = tag.getBoolean("IsShifting");
        shiftCooldown = tag.getInt("ShiftCooldown");
        materialTier = ShaftMaterialTier.fromName(tag.getString("MaterialTier"));
        currentEfficiency = tag.getFloat("CurrentEfficiency");
    }

    // Client-side display info
    @Override
    public String getDebugInfo() {
        return String.format(
                "%s [%s] | Gear: %s | In: %.1f RPM, %.1f Nm | Out: %.1f RPM, %.1f Nm | Eff: %.1f%% | Temp: %.1f°C%s",
                getMachineName(),
                materialTier.getSerializedName().toUpperCase(),
                currentRatio.displayName,
                getInputRPM(),
                getInputTorque(),
                getOutputRPM(),
                getOutputTorque(),
                currentEfficiency * 100.0f,
                kineticStorage.getTemperature(),
                isShifting ? " | §eSHIFTING" : ""
        );
    }

    public String getGearboxStatus() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("§e%s§r\n", currentRatio.displayName));
        status.append(String.format("Efficiency: §a%.1f%%§r\n", currentEfficiency * 100.0f));
        status.append(String.format("Input: §b%.1f RPM§r @ §6%.1f Nm§r\n", getInputRPM(), getInputTorque()));
        status.append(String.format("Output: §b%.1f RPM§r @ §6%.1f Nm§r\n", getOutputRPM(), getOutputTorque()));
        status.append(String.format("Temperature: §c%.1f°C§r", kineticStorage.getTemperature()));

        if (isShifting) {
            status.append("\n§eShifting...§r");
        }

        if (isOverheating()) {
            status.append("\n§c⚠ OVERHEATING§r");
        }

        return status.toString();
    }
}
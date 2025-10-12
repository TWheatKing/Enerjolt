package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.kinetic.BasicKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
import me.twheatking.enerjolt.kinetic.tier.ShaftMaterialTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Flywheel BlockEntity - Kinetic Energy Storage via Momentum
 * Stores rotational energy through high inertia mass
 *
 * Features:
 * - High inertia (resists speed changes)
 * - Acts as kinetic energy buffer/capacitor
 * - Smooths power delivery to network
 * - Different sizes store different amounts of energy
 * - Gradually spins up and down
 * - Can provide backup power when input is lost
 * - Material and size affect storage capacity
 * - Bearing friction causes energy loss over time
 */
public class FlywheelBlockEntity extends BaseKineticBlockEntity<BasicKineticStorage> {

    /**
     * Flywheel size tiers - larger = more storage
     */
    public enum FlywheelSize {
        SMALL(5.0f, 32.0f, "Small", 0.85f),
        MEDIUM(15.0f, 64.0f, "Medium", 0.90f),
        LARGE(40.0f, 128.0f, "Large", 0.93f),
        MASSIVE(100.0f, 256.0f, "Massive", 0.95f);

        public final float inertiaMultiplier;
        public final float maxStoredTorque;
        public final String displayName;
        public final float efficiency; // Better efficiency = less energy loss

        FlywheelSize(float inertia, float maxTorque, String name, float efficiency) {
            this.inertiaMultiplier = inertia;
            this.maxStoredTorque = maxTorque;
            this.displayName = name;
            this.efficiency = efficiency;
        }
    }

    // Flywheel configuration
    private FlywheelSize size = FlywheelSize.MEDIUM;
    private ShaftMaterialTier materialTier = ShaftMaterialTier.IRON;

    // Energy storage tracking
    private float storedEnergy = 0.0f; // Kinetic energy in joules
    private float maxStoredEnergy = 10000.0f; // Maximum capacity

    // Momentum tracking
    private float angularMomentum = 0.0f; // L = I × ω (inertia × angular velocity)

    // Bearing properties
    private float bearingFriction = 0.001f; // Energy loss per tick
    private float bearingTemperature = 20.0f;
    private static final float BEARING_HEAT_THRESHOLD = 80.0f;

    // Spin-up/spin-down behavior
    private boolean isAccelerating = false;
    private boolean isDecelerating = false;
    private float targetRPM = 0.0f;

    // Performance tracking
    private float energyInputRate = 0.0f; // Energy added per second
    private float energyOutputRate = 0.0f; // Energy provided per second
    private int tickCounter = 0;

    public FlywheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, FlywheelSize.MEDIUM, ShaftMaterialTier.IRON);
    }

    public FlywheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                               FlywheelSize size, ShaftMaterialTier tier) {
        super(type, pos, state,
                "Flywheel",
                tier.getMaxRPM(),
                tier.getMaxTorque() + size.maxStoredTorque,
                tier.getInertia() * size.inertiaMultiplier,  // Very high inertia
                tier.getFrictionCoefficient() * 0.5f); // Lower friction due to bearings

        this.size = size;
        this.materialTier = tier;
        calculateMaxStoredEnergy();
    }

    @Override
    protected BasicKineticStorage initKineticStorage() {
        // Flywheels use BasicKineticStorage (can receive and provide)
        return new BasicKineticStorage(
                0,                      // Initial RPM
                baseMaxRPM,             // Max RPM
                0,                      // Initial torque
                baseMaxTorque,          // Max torque (high due to stored energy)
                baseInertia,            // Very high inertia
                baseFriction            // Low friction (bearings)
        );
    }

    /**
     * Calculate maximum stored energy based on size and material
     * E = 0.5 × I × ω²
     */
    private void calculateMaxStoredEnergy() {
        // Moment of inertia
        float momentOfInertia = baseInertia;

        // Max angular velocity (rad/s)
        float maxAngularVelocity = (baseMaxRPM * 2.0f * (float)Math.PI) / 60.0f;

        // Max kinetic energy (joules)
        maxStoredEnergy = 0.5f * momentOfInertia * maxAngularVelocity * maxAngularVelocity;
    }

    /**
     * Main tick method - called every game tick
     */
    public void tick() {
        if (level == null) return;

        tickCounter++;

        // Calculate current stored energy and momentum
        updateEnergyAndMomentum();

        // Call the storage's tick method for basic physics
        kineticStorage.tick();

        // Process flywheel-specific behavior
        if (isReceivingRotation()) {
            chargeFromInput();
            updateBearingHeat();
        } else {
            providePowerFromMomentum();
            coolBearings();
        }

        // Apply bearing friction losses
        applyBearingFriction();

        // Update performance metrics every second
        if (tickCounter >= 20) {
            updatePerformanceMetrics();
            tickCounter = 0;
        }

        // Sync to client periodically
        if (!level.isClientSide && level.getGameTime() % 20 == 0) {
            setChanged();
            syncKineticToPlayers(2);
        }
    }

    /**
     * Update stored energy and angular momentum
     */
    private void updateEnergyAndMomentum() {
        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();
        float inertia = kineticStorage.getInertia();

        // Calculate angular velocity (rad/s)
        float angularVelocity = (rpm * 2.0f * (float)Math.PI) / 60.0f;

        // Calculate kinetic energy: E = 0.5 × I × ω²
        storedEnergy = 0.5f * inertia * angularVelocity * angularVelocity;
        storedEnergy = Math.min(storedEnergy, maxStoredEnergy);

        // Calculate angular momentum: L = I × ω
        angularMomentum = inertia * angularVelocity;
    }

    /**
     * Charge flywheel from input rotation
     */
    private void chargeFromInput() {
        float inputRPM = kineticStorage.getRPM();
        float inputTorque = kineticStorage.getTorque();

        if (inputRPM > 0 && inputTorque > 0) {
            // Calculate input power
            float inputPower = kineticStorage.getPowerWatts();

            // Apply size efficiency
            float chargeEfficiency = size.efficiency;

            // Store energy (with losses)
            float energyToStore = inputPower * chargeEfficiency * 0.05f; // Per tick
            storedEnergy = Math.min(maxStoredEnergy, storedEnergy + energyToStore);

            energyInputRate = inputPower;
            isAccelerating = true;
            isDecelerating = false;
        }
    }

    /**
     * Provide power from stored momentum when input is lost
     */
    private void providePowerFromMomentum() {
        if (storedEnergy > 0 && kineticStorage.getRPM() > 1.0f) {
            // Flywheel continues to spin from momentum
            // Gradually reduce RPM based on bearing friction

            float currentRPM = kineticStorage.getRPM();
            float currentTorque = kineticStorage.getTorque();

            // Calculate power output
            float outputPower = kineticStorage.getPowerWatts();
            energyOutputRate = outputPower;

            // Consume stored energy
            float energyConsumed = outputPower * 0.05f; // Per tick
            storedEnergy = Math.max(0, storedEnergy - energyConsumed);

            isAccelerating = false;
            isDecelerating = true;
        } else {
            energyOutputRate = 0;
            isDecelerating = false;
        }
    }

    /**
     * Apply bearing friction losses
     */
    private void applyBearingFriction() {
        float rpm = kineticStorage.getRPM();

        if (rpm > 0) {
            // Bearing friction increases with RPM
            float frictionLoss = bearingFriction * (rpm / baseMaxRPM);

            // Material quality reduces friction
            frictionLoss *= (2.0f - materialTier.getEfficiencyAtRPM(rpm));

            // Reduce RPM from friction
            float newRPM = Math.max(0, rpm - frictionLoss);
            kineticStorage.setRPMWithoutUpdate(newRPM);

            // Bearing heat from friction
            if (rpm > baseMaxRPM * 0.5f) {
                bearingTemperature += frictionLoss * 0.1f;
            }
        }
    }

    /**
     * Update bearing heat from operation
     */
    private void updateBearingHeat() {
        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();

        // Heat generation from load
        float heatGeneration = (rpm / baseMaxRPM) * (torque / baseMaxTorque) * 0.5f;
        bearingTemperature += heatGeneration;

        // High heat increases bearing friction
        if (bearingTemperature > BEARING_HEAT_THRESHOLD) {
            bearingFriction = Math.min(0.01f, bearingFriction + 0.0001f);
        }
    }

    /**
     * Cool bearings over time
     */
    private void coolBearings() {
        if (bearingTemperature > 20.0f) {
            float coolingRate = (bearingTemperature - 20.0f) * 0.02f;
            bearingTemperature = Math.max(20.0f, bearingTemperature - coolingRate);

            // Friction reduces as bearings cool
            if (bearingTemperature < BEARING_HEAT_THRESHOLD) {
                bearingFriction = Math.max(0.001f, bearingFriction - 0.00001f);
            }
        }
    }

    /**
     * Update performance metrics for display
     */
    private void updatePerformanceMetrics() {
        // Metrics are already tracked in real-time
        setChanged();
    }

    /**
     * Get stored energy percentage
     */
    public float getStoragePercentage() {
        return (storedEnergy / maxStoredEnergy) * 100.0f;
    }

    /**
     * Check if flywheel is at full capacity
     */
    public boolean isFull() {
        return storedEnergy >= maxStoredEnergy * 0.99f;
    }

    /**
     * Check if flywheel is empty
     */
    public boolean isEmpty() {
        return storedEnergy <= maxStoredEnergy * 0.01f;
    }

    /**
     * Get spin rate relative to max
     */
    public float getSpinRatePercentage() {
        return (kineticStorage.getRPM() / baseMaxRPM) * 100.0f;
    }

    // Getters and Setters
    public FlywheelSize getSize() {
        return size;
    }

    public void setSize(FlywheelSize size) {
        this.size = size;
        calculateMaxStoredEnergy();

        // Update inertia
        kineticStorage.setInertiaWithoutUpdate(materialTier.getInertia() * size.inertiaMultiplier);

        setChanged();
    }

    public ShaftMaterialTier getMaterialTier() {
        return materialTier;
    }

    public void setMaterialTier(ShaftMaterialTier tier) {
        this.materialTier = tier;
        calculateMaxStoredEnergy();

        // Update base values
        kineticStorage.setMaxRPMWithoutUpdate(tier.getMaxRPM());
        kineticStorage.setMaxTorqueWithoutUpdate(tier.getMaxTorque() + size.maxStoredTorque);
        kineticStorage.setInertiaWithoutUpdate(tier.getInertia() * size.inertiaMultiplier);
        kineticStorage.setFrictionCoefficient(tier.getFrictionCoefficient() * 0.5f);

        setChanged();
    }

    public float getStoredEnergy() {
        return storedEnergy;
    }

    public float getMaxStoredEnergy() {
        return maxStoredEnergy;
    }

    public float getAngularMomentum() {
        return angularMomentum;
    }

    public float getBearingTemperature() {
        return bearingTemperature;
    }

    public boolean isAccelerating() {
        return isAccelerating;
    }

    public boolean isDecelerating() {
        return isDecelerating;
    }

    public float getEnergyInputRate() {
        return energyInputRate;
    }

    public float getEnergyOutputRate() {
        return energyOutputRate;
    }

    public float getNetEnergyFlow() {
        return energyInputRate - energyOutputRate;
    }

    // NBT Serialization
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Size", size.name());
        tag.putString("MaterialTier", materialTier.getSerializedName());
        tag.putFloat("StoredEnergy", storedEnergy);
        tag.putFloat("MaxStoredEnergy", maxStoredEnergy);
        tag.putFloat("AngularMomentum", angularMomentum);
        tag.putFloat("BearingFriction", bearingFriction);
        tag.putFloat("BearingTemperature", bearingTemperature);
        tag.putFloat("EnergyInputRate", energyInputRate);
        tag.putFloat("EnergyOutputRate", energyOutputRate);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            size = FlywheelSize.valueOf(tag.getString("Size"));
        } catch (IllegalArgumentException e) {
            size = FlywheelSize.MEDIUM;
        }

        materialTier = ShaftMaterialTier.fromName(tag.getString("MaterialTier"));
        storedEnergy = tag.getFloat("StoredEnergy");
        maxStoredEnergy = tag.getFloat("MaxStoredEnergy");
        angularMomentum = tag.getFloat("AngularMomentum");
        bearingFriction = tag.getFloat("BearingFriction");
        bearingTemperature = tag.getFloat("BearingTemperature");
        energyInputRate = tag.getFloat("EnergyInputRate");
        energyOutputRate = tag.getFloat("EnergyOutputRate");
    }

    // Client-side display info
    @Override
    public String getDebugInfo() {
        return String.format(
                "%s [%s %s] | RPM: %.1f/%.1f (%.1f%%) | Energy: %.1f/%.1f J (%.1f%%) | Momentum: %.1f kg·m²/s | In: %.1f W | Out: %.1f W | Bearing: %.1f°C",
                getMachineName(),
                size.displayName,
                materialTier.getSerializedName().toUpperCase(),
                kineticStorage.getRPM(),
                baseMaxRPM,
                getSpinRatePercentage(),
                storedEnergy,
                maxStoredEnergy,
                getStoragePercentage(),
                angularMomentum,
                energyInputRate,
                energyOutputRate,
                bearingTemperature
        );
    }

    public String getFlywheelStatus() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("§e%s %s Flywheel§r\n", size.displayName, materialTier.getSerializedName().toUpperCase()));
        status.append(String.format("RPM: §b%.1f§r/§7%.1f§r (§a%.1f%%§r)\n",
                kineticStorage.getRPM(), baseMaxRPM, getSpinRatePercentage()));
        status.append(String.format("Energy: §6%.1f§r/§7%.1f§r J (§a%.1f%%§r)\n",
                storedEnergy, maxStoredEnergy, getStoragePercentage()));
        status.append(String.format("Momentum: §d%.1f§r kg·m²/s\n", angularMomentum));

        if (isAccelerating) {
            status.append(String.format("§a↑ Charging§r: +%.1f W\n", energyInputRate));
        } else if (isDecelerating) {
            status.append(String.format("§c↓ Discharging§r: -%.1f W\n", energyOutputRate));
        } else {
            status.append("§7○ Idle§r\n");
        }

        status.append(String.format("Bearing: §c%.1f°C§r", bearingTemperature));

        if (bearingTemperature > BEARING_HEAT_THRESHOLD) {
            status.append(" §c⚠ HOT§r");
        }

        return status.toString();
    }
}
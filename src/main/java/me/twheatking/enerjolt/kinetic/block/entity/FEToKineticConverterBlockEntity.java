package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.energy.IEnerjoltEnergyStorage;
import me.twheatking.enerjolt.energy.ReceiveOnlyEnergyStorage;
import me.twheatking.enerjolt.kinetic.IKineticStorage;
import me.twheatking.enerjolt.kinetic.ProvideOnlyKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseConverterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * FE to Kinetic Converter BlockEntity
 * Converts electrical Forge Energy into rotational kinetic energy
 *
 * Features:
 * - Converts FE/t to kinetic power (RPM × Torque)
 * - Efficiency-based conversion (losses from heat)
 * - Multiple converter tiers (different conversion rates)
 * - Requires minimum FE/t to operate
 * - Gradual spin-up and spin-down
 * - Can drive kinetic networks
 * - Speed vs Torque modes
 */
public class FEToKineticConverterBlockEntity extends BaseConverterBlockEntity {

    /**
     * Converter tier affects efficiency and conversion rate
     */
    public enum ConverterTier {
        BASIC(0.65f, 1.0f, 64.0f, 30.0f, 100, "Basic"),        // 65% efficiency
        ADVANCED(0.80f, 1.5f, 128.0f, 60.0f, 500, "Advanced"), // 80% efficiency
        ELITE(0.90f, 2.0f, 256.0f, 120.0f, 2000, "Elite"),     // 90% efficiency
        ULTIMATE(0.96f, 3.0f, 512.0f, 240.0f, 10000, "Ultimate"); // 96% efficiency

        public final float efficiency;           // Conversion efficiency
        public final float conversionMultiplier; // Kinetic output multiplier
        public final float maxRPM;               // Max RPM output
        public final float maxTorque;            // Max torque output
        public final int maxInput;               // Max FE/t input
        public final String displayName;

        ConverterTier(float eff, float conv, float rpm, float torque, int input, String name) {
            this.efficiency = eff;
            this.conversionMultiplier = conv;
            this.maxRPM = rpm;
            this.maxTorque = torque;
            this.maxInput = input;
            this.displayName = name;
        }
    }

    /**
     * Conversion modes - trade speed for torque or vice versa
     */
    public enum ConversionMode {
        BALANCED(1.0f, 1.0f, "Balanced"),           // 1:1 ratio
        HIGH_SPEED(1.5f, 0.67f, "High Speed"),      // More speed, less torque
        HIGH_TORQUE(0.67f, 1.5f, "High Torque");    // Less speed, more torque

        public final float speedMultiplier;
        public final float torqueMultiplier;
        public final String displayName;

        ConversionMode(float speed, float torque, String name) {
            this.speedMultiplier = speed;
            this.torqueMultiplier = torque;
            this.displayName = name;
        }
    }

    // Converter configuration
    private ConverterTier tier = ConverterTier.BASIC;
    private ConversionMode mode = ConversionMode.BALANCED;

    // Conversion tracking
    private int consumedFEPerTick = 0;
    private static final int MIN_OPERATING_FE = 10; // Minimum FE/t to operate

    // Spin behavior
    private float targetRPM = 0.0f;
    private float targetTorque = 0.0f;
    private static final float SPIN_ACCELERATION = 1.0f; // RPM change per tick

    // Performance metrics
    private long totalFEConsumed = 0;
    private float averageFEPerTick = 0.0f;

    public FEToKineticConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, ConverterTier.BASIC);
    }

    public FEToKineticConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ConverterTier tier) {
        super(type, pos, state, "FE to Kinetic Converter", tier.efficiency);
        this.tier = tier;
    }

    @Override
    protected IKineticStorage initKineticStorage() {
        // Provide-only kinetic storage (generator output)
        return new ProvideOnlyKineticStorage(
                0,                  // Initial RPM
                tier.maxRPM,        // Max RPM
                0,                  // Initial torque
                tier.maxTorque,     // Max torque
                3.0f,               // Higher inertia (electric motor)
                0.02f               // Low friction (electric)
        );
    }

    @Override
    protected IEnerjoltEnergyStorage initEnergyStorage() {
        // Receive-only energy storage (no FE buffer, direct consumption)
        return new ReceiveOnlyEnergyStorage(
                10000,          // Small buffer for smoothing
                tier.maxInput,  // Max input
                tier.maxInput   // Max extract (internal use)
        );
    }

    @Override
    protected boolean canConvert() {
        // Need minimum FE to convert
        return energyStorage.getEnergyStored() >= MIN_OPERATING_FE;
    }

    @Override
    protected void performConversion() {
        // Determine how much FE to consume based on desired output
        int availableFE = energyStorage.getEnergyStored();

        // Calculate target kinetic output based on available FE
        // Conversion: 20 FE/t = 1 Watt of mechanical power
        // Then convert watts back to RPM/Torque

        // Max FE we can use this tick
        int feToConsume = Math.min(availableFE, tier.maxInput);

        // Convert FE to watts
        // 1 FE/t ≈ 20 Watts (inverse of kinetic→FE conversion)
        float mechanicalPowerWatts = feToConsume * 20.0f / tier.conversionMultiplier;

        // Apply efficiency
        mechanicalPowerWatts *= conversionEfficiency;

        // Convert watts to RPM and Torque
        // Power (W) = Torque (Nm) × Angular Velocity (rad/s)
        // We need to choose a balance between RPM and Torque

        // Use conversion mode to determine split
        calculateTargetKineticOutput(mechanicalPowerWatts);

        // Gradually spin up to target
        updateRotation();

        // Consume FE if we're generating rotation
        if (kineticStorage.getRPM() > 1.0f) {
            consumedFEPerTick = feToConsume;
            int actualConsumed = energyStorage.extractEnergy(feToConsume, false);
            totalFEConsumed += actualConsumed;
            totalConversionsPerformed++;

            // Generate rotation
            float rpmToGenerate = kineticStorage.getRPM() * 0.1f;
            float torqueToGenerate = ((ProvideOnlyKineticStorage)kineticStorage).getTorque() * 0.1f;
            ((ProvideOnlyKineticStorage)kineticStorage).generateRotation(rpmToGenerate, torqueToGenerate);
        } else {
            consumedFEPerTick = 0;
        }
    }

    /**
     * Calculate target RPM and torque from power
     */
    private void calculateTargetKineticOutput(float powerWatts) {
        // Arbitrary split: assume moderate RPM and calculate torque from power
        // Power = Torque × Angular Velocity
        // Angular Velocity = (RPM × 2π) / 60

        // Target RPM based on mode
        float baseTargetRPM = tier.maxRPM * 0.5f; // 50% of max by default
        targetRPM = baseTargetRPM * mode.speedMultiplier;
        targetRPM = Math.min(tier.maxRPM, targetRPM);

        // Calculate torque from power and RPM
        float angularVelocity = (targetRPM * 2.0f * (float)Math.PI) / 60.0f;
        if (angularVelocity > 0.01f) {
            targetTorque = powerWatts / angularVelocity;
            targetTorque *= mode.torqueMultiplier;
            targetTorque = Math.min(tier.maxTorque, targetTorque);
        } else {
            targetTorque = 0;
        }
    }

    /**
     * Gradually change RPM toward target
     */
    private void updateRotation() {
        float currentRPM = kineticStorage.getRPM();

        if (currentRPM < targetRPM) {
            // Spin up
            float spinUpAmount = Math.min(SPIN_ACCELERATION, targetRPM - currentRPM);
            kineticStorage.setRPMWithoutUpdate(currentRPM + spinUpAmount);
        } else if (currentRPM > targetRPM) {
            // Spin down
            float spinDownAmount = Math.min(SPIN_ACCELERATION * 0.5f, currentRPM - targetRPM);
            kineticStorage.setRPMWithoutUpdate(currentRPM - spinDownAmount);
        }

        // Update torque
        kineticStorage.setTorqueWithoutUpdate(targetTorque);
    }

    @Override
    protected float calculateHeatGeneration() {
        // Heat generation from conversion losses
        float efficiencyLoss = 1.0f - conversionEfficiency;
        float heatFromConversion = consumedFEPerTick * efficiencyLoss * 0.02f;
        return heatFromConversion;
    }

    /**
     * Cycle to next conversion mode
     */
    public void cycleMode() {
        int nextOrdinal = (mode.ordinal() + 1) % ConversionMode.values().length;
        mode = ConversionMode.values()[nextOrdinal];
        setChanged();
    }

    // Getters and Setters
    public ConverterTier getTier() {
        return tier;
    }

    public void setTier(ConverterTier tier) {
        this.tier = tier;
        this.conversionEfficiency = tier.efficiency;

        // Update kinetic storage limits
        kineticStorage.setMaxRPMWithoutUpdate(tier.maxRPM);
        kineticStorage.setMaxTorqueWithoutUpdate(tier.maxTorque);

        // Update energy storage
        if (energyStorage instanceof ReceiveOnlyEnergyStorage storage) {
            storage.setMaxReceive(tier.maxInput);
        }

        setChanged();
    }

    public ConversionMode getMode() {
        return mode;
    }

    public void setMode(ConversionMode mode) {
        this.mode = mode;
        setChanged();
    }

    public int getConsumedFEPerTick() {
        return consumedFEPerTick;
    }

    public long getTotalFEConsumed() {
        return totalFEConsumed;
    }

    public float getAverageFEPerTick() {
        if (activeConversionTicks > 0) {
            return (float)totalFEConsumed / activeConversionTicks;
        }
        return 0;
    }

    public float getTargetRPM() {
        return targetRPM;
    }

    public float getTargetTorque() {
        return targetTorque;
    }

    public float getSpinUpPercentage() {
        if (targetRPM > 0) {
            return (kineticStorage.getRPM() / targetRPM) * 100.0f;
        }
        return 0;
    }

    // NBT Serialization
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Tier", tier.name());
        tag.putString("Mode", mode.name());
        tag.putInt("ConsumedFEPerTick", consumedFEPerTick);
        tag.putLong("TotalFEConsumed", totalFEConsumed);
        tag.putFloat("TargetRPM", targetRPM);
        tag.putFloat("TargetTorque", targetTorque);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            tier = ConverterTier.valueOf(tag.getString("Tier"));
        } catch (IllegalArgumentException e) {
            tier = ConverterTier.BASIC;
        }

        try {
            mode = ConversionMode.valueOf(tag.getString("Mode"));
        } catch (IllegalArgumentException e) {
            mode = ConversionMode.BALANCED;
        }

        consumedFEPerTick = tag.getInt("ConsumedFEPerTick");
        totalFEConsumed = tag.getLong("TotalFEConsumed");
        targetRPM = tag.getFloat("TargetRPM");
        targetTorque = tag.getFloat("TargetTorque");
    }

    // Display info
    @Override
    public String getDebugInfo() {
        return String.format(
                "%s [%s - %s] | FE: %d/t | Kinetic: %.1f/%.1f RPM, %.1f/%.1f Nm (%.1f W) | Eff: %.1f%% | Temp: %.1f°C | %s",
                converterName,
                tier.displayName,
                mode.displayName,
                consumedFEPerTick,
                kineticStorage.getRPM(),
                tier.maxRPM,
                kineticStorage.getTorque(),
                tier.maxTorque,
                kineticStorage.getPowerWatts(),
                conversionEfficiency * 100.0f,
                converterHeat,
                isConverting ? "§aConverting§r" : "§7Idle§r"
        );
    }

    @Override
    public String getStatusDisplay() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("§e%s Converter§r\n", tier.displayName));
        status.append(String.format("Mode: §b%s§r\n", mode.displayName));

        if (isConverting) {
            status.append("§a⚡ Converting§r\n");
        } else {
            status.append("§7○ Idle§r\n");
        }

        status.append("\n§6FE Input:§r\n");
        status.append(String.format("  Consumption: §c%d§r FE/t\n", consumedFEPerTick));
        status.append(String.format("  Avg Consumption: §c%.1f§r FE/t\n", getAverageFEPerTick()));

        status.append("\n§bKinetic Output:§r\n");
        status.append(String.format("  RPM: §b%.1f§r/§7%.1f§r (Target: §e%.1f§r)\n",
                kineticStorage.getRPM(), tier.maxRPM, targetRPM));
        status.append(String.format("  Torque: §6%.1f§r/§7%.1f§r Nm (Target: §e%.1f§r)\n",
                kineticStorage.getTorque(), tier.maxTorque, targetTorque));
        status.append(String.format("  Power: §a%.1f§r W\n", kineticStorage.getPowerWatts()));

        if (targetRPM > 0) {
            status.append(String.format("  Spin-up: §a%.1f%%§r\n", getSpinUpPercentage()));
        }

        status.append(String.format("\nEfficiency: §a%.1f%%§r\n", conversionEfficiency * 100.0f));
        status.append(String.format("Temperature: §c%.1f°C§r", converterHeat));

        if (isOverheating()) {
            status.append(" §c⚠ OVERHEATING§r");
        }

        status.append(String.format("\n\n§7Total: %.2f MFE§r", totalFEConsumed / 1000000.0f));

        return status.toString();
    }
}
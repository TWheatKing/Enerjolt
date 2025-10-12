package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.energy.IEnerjoltEnergyStorage;
import me.twheatking.enerjolt.energy.ReceiveAndExtractEnergyStorage;
import me.twheatking.enerjolt.kinetic.ConsumeOnlyKineticStorage;
import me.twheatking.enerjolt.kinetic.IKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseConverterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Kinetic to FE Converter BlockEntity
 * Converts rotational kinetic energy into electrical Forge Energy
 *
 * Features:
 * - Converts kinetic power (RPM × Torque) to FE/t
 * - Efficiency-based conversion (losses from heat)
 * - Multiple converter tiers (different conversion rates)
 * - Requires minimum RPM to operate
 * - Resonance bonus at optimal speed
 * - Buffer storage for generated FE
 * - Can supply FE to adjacent machines/cables
 */
public class KineticToFEConverterBlockEntity extends BaseConverterBlockEntity {

    /**
     * Converter tier affects efficiency and conversion rate
     */
    public enum ConverterTier {
        BASIC(0.70f, 1.0f, 10000, 100, "Basic"),          // 70% efficiency, 1:1 ratio
        ADVANCED(0.85f, 1.5f, 50000, 500, "Advanced"),    // 85% efficiency, 1.5:1 ratio
        ELITE(0.92f, 2.0f, 200000, 2000, "Elite"),        // 92% efficiency, 2:1 ratio
        ULTIMATE(0.98f, 3.0f, 1000000, 10000, "Ultimate"); // 98% efficiency, 3:1 ratio

        public final float efficiency;           // Conversion efficiency
        public final float conversionMultiplier; // FE output multiplier
        public final int storageCapacity;        // FE buffer capacity
        public final int maxOutput;              // Max FE/t output
        public final String displayName;

        ConverterTier(float eff, float conv, int storage, int output, String name) {
            this.efficiency = eff;
            this.conversionMultiplier = conv;
            this.storageCapacity = storage;
            this.maxOutput = output;
            this.displayName = name;
        }
    }

    // Converter configuration
    private ConverterTier tier = ConverterTier.BASIC;

    // Conversion tracking
    private int generatedFEPerTick = 0;
    private static final float MIN_OPERATING_RPM = 16.0f;
    private static final float OPTIMAL_RPM = 128.0f; // Resonance frequency
    private static final float RESONANCE_TOLERANCE = 32.0f;

    // Performance metrics
    private long totalFEGenerated = 0;
    private float averageFEPerTick = 0.0f;

    public KineticToFEConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, ConverterTier.BASIC);
    }

    public KineticToFEConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ConverterTier tier) {
        super(type, pos, state, "Kinetic to FE Converter", tier.efficiency);
        this.tier = tier;
    }

    @Override
    protected IKineticStorage initKineticStorage() {
        // Consume-only kinetic storage (generator input)
        return new ConsumeOnlyKineticStorage(
                0,          // Initial RPM
                512.0f,     // Max RPM
                0,          // Initial torque
                200.0f,     // Max torque
                2.0f,       // Inertia
                0.04f,      // Friction
                OPTIMAL_RPM,        // Resonance RPM
                RESONANCE_TOLERANCE // Resonance tolerance
        );
    }

    @Override
    protected IEnerjoltEnergyStorage initEnergyStorage() {
        // FE buffer storage
        return new ReceiveAndExtractEnergyStorage(
                tier.storageCapacity,
                tier.maxOutput,
                tier.maxOutput
        );
    }

    @Override
    protected boolean canConvert() {
        // Need minimum RPM and torque to convert
        return kineticStorage.getRPM() >= MIN_OPERATING_RPM &&
                ((ConsumeOnlyKineticStorage)kineticStorage).hasMinimumTorque() &&
                energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored();
    }

    @Override
    protected void performConversion() {
        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();

        // Calculate mechanical power in watts
        // Power (W) = Torque (Nm) × Angular Velocity (rad/s)
        float angularVelocity = (rpm * 2.0f * (float)Math.PI) / 60.0f;
        float mechanicalPowerWatts = torque * angularVelocity;

        // Apply resonance bonus
        float resonanceBonus = ((ConsumeOnlyKineticStorage)kineticStorage).getResonanceEfficiency();

        // Calculate total efficiency with resonance
        float totalEfficiency = conversionEfficiency * resonanceBonus;

        // Convert watts to FE/tick
        // Conversion: 1 Watt ≈ 0.05 FE/tick (20 Watts = 1 FE/tick)
        // Apply tier multiplier
        float feBeforeEfficiency = mechanicalPowerWatts * 0.05f * tier.conversionMultiplier;

        // Apply efficiency losses
        float feGenerated = feBeforeEfficiency * totalEfficiency;

        // Round to integer FE
        generatedFEPerTick = Math.round(feGenerated);

        // Cap to max output
        generatedFEPerTick = Math.min(generatedFEPerTick, tier.maxOutput);

        // Add FE to buffer
        if (generatedFEPerTick > 0) {
            int actualAdded = energyStorage.receiveEnergy(generatedFEPerTick, false);
            totalFEGenerated += actualAdded;
            totalConversionsPerformed++;

            // Consume kinetic energy proportionally
            if (actualAdded > 0) {
                float consumptionRatio = (float)actualAdded / generatedFEPerTick;
                float rpmToConsume = rpm * 0.02f * consumptionRatio;
                float torqueToConsume = torque * 0.02f * consumptionRatio;
                ((ConsumeOnlyKineticStorage)kineticStorage).consumeRotation(rpmToConsume, torqueToConsume);
            }
        }
    }

    @Override
    protected float calculateHeatGeneration() {
        // Heat generation from conversion losses
        float efficiencyLoss = 1.0f - conversionEfficiency;
        float heatFromConversion = generatedFEPerTick * efficiencyLoss * 0.01f;
        return heatFromConversion;
    }

    // Getters
    public ConverterTier getTier() {
        return tier;
    }

    public void setTier(ConverterTier tier) {
        this.tier = tier;
        this.conversionEfficiency = tier.efficiency;

        // Update energy storage parameters for the new tier
        if (energyStorage instanceof ReceiveAndExtractEnergyStorage storage) {
            // Preserve current energy, capped to new capacity
            int currentEnergy = storage.getEnergyStored();
            
            // Update capacity and transfer rates
            storage.setCapacity(tier.storageCapacity);
            storage.setMaxReceive(tier.maxOutput);
            storage.setMaxExtract(tier.maxOutput);
            
            // Restore energy (will be capped to new capacity automatically)
            storage.setEnergy(currentEnergy);
        }

        setChanged();
    }

    public int getGeneratedFEPerTick() {
        return generatedFEPerTick;
    }

    public long getTotalFEGenerated() {
        return totalFEGenerated;
    }

    public float getAverageFEPerTick() {
        if (activeConversionTicks > 0) {
            return (float)totalFEGenerated / activeConversionTicks;
        }
        return 0;
    }

    public float getStoragePercentage() {
        if (energyStorage.getMaxEnergyStored() > 0) {
            return ((float)energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored()) * 100.0f;
        }
        return 0;
    }

    public boolean isInResonance() {
        return ((ConsumeOnlyKineticStorage)kineticStorage).isInResonance();
    }

    public float getResonanceBonus() {
        return ((ConsumeOnlyKineticStorage)kineticStorage).getResonanceEfficiency();
    }

    // NBT Serialization
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Tier", tier.name());
        tag.putInt("GeneratedFEPerTick", generatedFEPerTick);
        tag.putLong("TotalFEGenerated", totalFEGenerated);
        tag.putFloat("AverageFEPerTick", averageFEPerTick);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            tier = ConverterTier.valueOf(tag.getString("Tier"));
        } catch (IllegalArgumentException e) {
            tier = ConverterTier.BASIC;
        }

        generatedFEPerTick = tag.getInt("GeneratedFEPerTick");
        totalFEGenerated = tag.getLong("TotalFEGenerated");
        averageFEPerTick = tag.getFloat("AverageFEPerTick");
    }

    // Display info
    @Override
    public String getDebugInfo() {
        return String.format(
                "%s [%s] | Kinetic: %.1f RPM, %.1f Nm (%.1f W) | FE: %d/t | Buffer: %d/%d (%.1f%%) | Eff: %.1f%% | Temp: %.1f°C%s%s",
                converterName,
                tier.displayName,
                kineticStorage.getRPM(),
                kineticStorage.getTorque(),
                kineticStorage.getPowerWatts(),
                generatedFEPerTick,
                energyStorage.getEnergyStored(),
                energyStorage.getMaxEnergyStored(),
                getStoragePercentage(),
                conversionEfficiency * 100.0f,
                converterHeat,
                isInResonance() ? " | §a♪§r" : "",
                isOverheating() ? " | §c⚠§r" : ""
        );
    }

    @Override
    public String getStatusDisplay() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("§e%s Converter§r\n", tier.displayName));

        if (isConverting) {
            status.append("§a⚡ Converting§r\n");
        } else {
            status.append("§7○ Idle§r\n");
        }

        status.append("\n§bKinetic Input:§r\n");
        status.append(String.format("  RPM: §b%.1f§r/§7512§r\n", kineticStorage.getRPM()));
        status.append(String.format("  Torque: §6%.1f§r/§7200§r Nm\n", kineticStorage.getTorque()));
        status.append(String.format("  Power: §a%.1f§r W\n", kineticStorage.getPowerWatts()));

        if (isInResonance()) {
            status.append(String.format("  §a♪ Resonance§r (§e×%.2f§r)\n", getResonanceBonus()));
        }

        status.append("\n§6FE Output:§r\n");
        status.append(String.format("  Generation: §a%d§r FE/t\n", generatedFEPerTick));
        status.append(String.format("  Buffer: §e%d§r/§7%d§r FE (§a%.1f%%§r)\n",
                energyStorage.getEnergyStored(),
                energyStorage.getMaxEnergyStored(),
                getStoragePercentage()));
        status.append(String.format("  Avg Output: §a%.1f§r FE/t\n", getAverageFEPerTick()));

        status.append(String.format("\nEfficiency: §a%.1f%%§r\n", conversionEfficiency * 100.0f));
        status.append(String.format("Temperature: §c%.1f°C§r", converterHeat));

        if (isOverheating()) {
            status.append(" §c⚠ OVERHEATING§r");
        }

        status.append(String.format("\n\n§7Total: %.2f MFE§r", totalFEGenerated / 1000000.0f));

        return status.toString();
    }
}
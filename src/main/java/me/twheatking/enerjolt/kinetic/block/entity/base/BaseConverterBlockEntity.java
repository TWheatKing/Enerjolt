package me.twheatking.enerjolt.kinetic.block.entity.base;

import me.twheatking.enerjolt.energy.IEnerjoltEnergyStorage;
import me.twheatking.enerjolt.kinetic.IKineticStorage;
import me.twheatking.enerjolt.kinetic.KineticNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base BlockEntity for all energy converters (Kinetic ↔ FE)
 * Handles both kinetic storage and FE storage in a unified way
 *
 * Provides:
 * - Dual energy system support (kinetic + FE)
 * - Conversion efficiency tracking
 * - Heat management
 * - Performance metrics
 * - Network sync for both systems
 * - Common converter utilities
 */
public abstract class BaseConverterBlockEntity extends BlockEntity
        implements KineticNetworkHandler.IKineticBlockEntity {

    // Storage systems
    protected final IKineticStorage kineticStorage;
    protected final IEnerjoltEnergyStorage energyStorage;

    // Converter properties
    protected final String converterName;
    protected float conversionEfficiency;

    // Thermal management
    protected float converterHeat = 20.0f;
    protected static final float AMBIENT_TEMPERATURE = 20.0f;
    protected static final float MAX_CONVERTER_TEMP = 200.0f;

    // Performance tracking
    protected long totalConversionsPerformed = 0;
    protected int activeConversionTicks = 0;
    protected boolean isConverting = false;

    // Sync tracking
    private int syncTimer = 0;
    private static final int SYNC_INTERVAL = 20; // Sync every second

    public BaseConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                    String converterName, float baseEfficiency) {
        super(type, pos, state);

        this.converterName = converterName;
        this.conversionEfficiency = baseEfficiency;

        // Initialize storage systems (subclasses implement these)
        this.kineticStorage = initKineticStorage();
        this.energyStorage = initEnergyStorage();
    }

    /**
     * Initialize kinetic storage - implemented by subclasses
     * @return The kinetic storage instance
     */
    protected abstract IKineticStorage initKineticStorage();

    /**
     * Initialize energy storage - implemented by subclasses
     * @return The energy storage instance
     */
    protected abstract IEnerjoltEnergyStorage initEnergyStorage();

    /**
     * Perform the conversion - implemented by subclasses
     * This is where the actual kinetic ↔ FE conversion happens
     */
    protected abstract void performConversion();

    /**
     * Check if converter can operate - implemented by subclasses
     * @return True if conversion can happen
     */
    protected abstract boolean canConvert();

    /**
     * Main tick method - called every game tick
     * Handles both energy systems and conversion
     */
    public void tick() {
        if (level == null || level.isClientSide) return;

        // Check if we can convert
        if (canConvert()) {
            performConversion();
            isConverting = true;
            activeConversionTicks++;
        } else {
            isConverting = false;
        }

        // Update thermal state
        updateThermalState();

        // Distribute energy (FE to adjacent blocks)
        distributeEnergy();

        // Sync to client periodically
        syncTimer++;
        if (syncTimer >= SYNC_INTERVAL) {
            syncToClients();
            syncTimer = 0;
        }

        setChanged();
    }

    /**
     * Update thermal state (heating and cooling)
     */
    protected void updateThermalState() {
        if (isConverting) {
            // Generate heat during conversion
            float heatGeneration = calculateHeatGeneration();
            converterHeat = Math.min(MAX_CONVERTER_TEMP, converterHeat + heatGeneration);
        }

        // Natural cooling toward ambient temperature
        if (converterHeat > AMBIENT_TEMPERATURE) {
            float coolingRate = (converterHeat - AMBIENT_TEMPERATURE) * 0.02f;
            converterHeat = Math.max(AMBIENT_TEMPERATURE, converterHeat - coolingRate);
        }

        // Apply heat penalty to efficiency if overheating
        updateEfficiencyFromHeat();
    }

    /**
     * Calculate heat generation from conversion
     * Override in subclasses for specific heat models
     */
    protected float calculateHeatGeneration() {
        // Base heat generation: (1 - efficiency) produces heat
        float efficiencyLoss = 1.0f - conversionEfficiency;
        return efficiencyLoss * 0.5f; // Base heat generation
    }

    /**
     * Update efficiency based on heat
     */
    protected void updateEfficiencyFromHeat() {
        if (converterHeat > 120.0f) {
            // High heat reduces efficiency
            float heatPenalty = (converterHeat - 120.0f) / 80.0f;
            // Efficiency drops by up to 30% when overheating
            float heatMultiplier = 1.0f - (heatPenalty * 0.3f);
            conversionEfficiency *= Math.max(0.5f, heatMultiplier);
        }
    }

    /**
     * Distribute FE to adjacent blocks
     * Only for converters that generate FE
     */
    protected void distributeEnergy() {
        if (energyStorage.getEnergyStored() <= 0) return;

        // Try to push energy to all adjacent blocks
        for (Direction direction : Direction.values()) {
            if (energyStorage.getEnergyStored() <= 0) break;

            BlockPos adjacentPos = worldPosition.relative(direction);

            // TODO: Use energy capability to transfer
            // This will be implemented when we set up capabilities
            // For now, placeholder for energy distribution logic
        }
    }

    /**
     * Sync converter data to clients
     */
    protected void syncToClients() {
        if (level instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = new ChunkPos(worldPosition);

            for (ServerPlayer player : serverLevel.players()) {
                ChunkPos playerChunkPos = player.chunkPosition();

                // Sync to players within 2 chunks
                if (Math.abs(playerChunkPos.x - chunkPos.x) <= 2 &&
                        Math.abs(playerChunkPos.z - chunkPos.z) <= 2) {
                    // Send update packet
                    player.connection.send(getUpdatePacket());
                }
            }
        }
    }

    // ========== KINETIC NETWORK INTERFACE ==========

    @Override
    public @Nullable IKineticStorage getKineticStorage(@Nullable Direction side) {
        return kineticStorage;
    }

    // ========== ENERGY STORAGE ACCESS ==========

    /**
     * Get the energy storage for this converter
     * @return The FE energy storage
     */
    public IEnerjoltEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    /**
     * Get energy storage for a specific side
     * Override in subclasses for sided energy I/O
     */
    public @Nullable IEnerjoltEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage;
    }

    // ========== GETTERS ==========

    public String getConverterName() {
        return converterName;
    }

    public float getConversionEfficiency() {
        return conversionEfficiency;
    }

    public float getConverterHeat() {
        return converterHeat;
    }

    public float getHeatPercentage() {
        return (converterHeat / MAX_CONVERTER_TEMP) * 100.0f;
    }

    public boolean isOverheating() {
        return converterHeat > 120.0f;
    }

    public boolean isConverting() {
        return isConverting;
    }

    public long getTotalConversions() {
        return totalConversionsPerformed;
    }

    public int getActiveConversionTicks() {
        return activeConversionTicks;
    }

    // ========== NBT PERSISTENCE ==========

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        // Save kinetic storage
        if (kineticStorage != null) {
            tag.put("KineticStorage", kineticStorage.saveNBT());
        }

        // Save energy storage
        if (energyStorage != null) {
            CompoundTag energyTag = new CompoundTag();
            energyTag.putInt("Energy", energyStorage.getEnergyStored());
            energyTag.putInt("Capacity", energyStorage.getMaxEnergyStored());
            tag.put("EnergyStorage", energyTag);
        }

        // Save converter data
        tag.putString("ConverterName", converterName);
        tag.putFloat("ConversionEfficiency", conversionEfficiency);
        tag.putFloat("ConverterHeat", converterHeat);
        tag.putLong("TotalConversions", totalConversionsPerformed);
        tag.putInt("ActiveConversionTicks", activeConversionTicks);
        tag.putBoolean("IsConverting", isConverting);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        // Load kinetic storage
        if (kineticStorage != null && tag.contains("KineticStorage")) {
            kineticStorage.loadNBT(tag.get("KineticStorage"));
        }

        // Load energy storage
        if (energyStorage != null && tag.contains("EnergyStorage")) {
            CompoundTag energyTag = tag.getCompound("EnergyStorage");
            int storedEnergy = energyTag.getInt("Energy");
            // Energy storage will be loaded through its own methods
        }

        // Load converter data
        conversionEfficiency = tag.getFloat("ConversionEfficiency");
        converterHeat = tag.getFloat("ConverterHeat");
        totalConversionsPerformed = tag.getLong("TotalConversions");
        activeConversionTicks = tag.getInt("ActiveConversionTicks");
        isConverting = tag.getBoolean("IsConverting");
    }

    // ========== CLIENT SYNC ==========

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ========== DEBUG INFO ==========

    /**
     * Get debug information for display
     * Override in subclasses for specific info
     */
    public String getDebugInfo() {
        return String.format(
                "%s | Kinetic: %.1f RPM, %.1f Nm | FE: %d/%d | Eff: %.1f%% | Temp: %.1f°C | %s",
                converterName,
                kineticStorage.getRPM(),
                kineticStorage.getTorque(),
                energyStorage.getEnergyStored(),
                energyStorage.getMaxEnergyStored(),
                conversionEfficiency * 100.0f,
                converterHeat,
                isConverting ? "§aConverting§r" : "§7Idle§r"
        );
    }

    /**
     * Get status display for UI
     * Override in subclasses for specific status
     */
    public String getStatusDisplay() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("§e%s§r\n", converterName));
        status.append(isConverting ? "§a⚡ Converting§r\n" : "§7○ Idle§r\n");
        status.append(String.format("Efficiency: §a%.1f%%§r\n", conversionEfficiency * 100.0f));
        status.append(String.format("Temperature: §c%.1f°C§r", converterHeat));

        if (isOverheating()) {
            status.append(" §c⚠§r");
        }

        return status.toString();
    }
}
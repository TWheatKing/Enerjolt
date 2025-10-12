package me.twheatking.enerjolt.kinetic.block.entity.base;

import me.twheatking.enerjolt.kinetic.IKineticStorage;
import me.twheatking.enerjolt.kinetic.KineticNetworkHandler;
import me.twheatking.enerjolt.kinetic.KineticStoragePacketUpdate;
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
 * Base BlockEntity for all kinetic-powered machines.
 * Handles kinetic storage, syncing, and network participation.
 * Similar to ConfigurableEnergyStorageBlockEntity but for rotational energy.
 */
public abstract class BaseKineticBlockEntity<K extends IKineticStorage> extends BlockEntity
        implements KineticNetworkHandler.IKineticBlockEntity, KineticStoragePacketUpdate {

    protected final K kineticStorage;
    protected final String machineName;

    // Base kinetic properties
    protected final float baseMaxRPM;
    protected final float baseMaxTorque;
    protected final float baseInertia;
    protected final float baseFriction;

    public BaseKineticBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                  String machineName,
                                  float baseMaxRPM, float baseMaxTorque,
                                  float baseInertia, float baseFriction) {
        super(type, blockPos, blockState);

        this.machineName = machineName;
        this.baseMaxRPM = baseMaxRPM;
        this.baseMaxTorque = baseMaxTorque;
        this.baseInertia = baseInertia;
        this.baseFriction = baseFriction;

        this.kineticStorage = initKineticStorage();
    }

    /**
     * Initialize the kinetic storage for this machine.
     * Override this to create custom storage types.
     * @return The kinetic storage instance
     */
    protected abstract K initKineticStorage();

    /**
     * Gets the kinetic storage for the given side.
     * Override this to provide different storage per side (e.g., gearboxes).
     * @param side The side to check (null for internal)
     * @return The kinetic storage
     */
    @Override
    public @Nullable IKineticStorage getKineticStorage(@Nullable Direction side) {
        return kineticStorage;
    }

    /**
     * Gets the internal kinetic storage directly.
     * @return The kinetic storage
     */
    public K getKineticStorage() {
        return kineticStorage;
    }

    // ========== NBT PERSISTENCE ==========

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("kinetic_storage", kineticStorage.saveNBT());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        if (nbt.contains("kinetic_storage")) {
            kineticStorage.loadNBT(nbt.get("kinetic_storage"));
        }
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

    /**
     * Syncs kinetic data to a specific player
     * @param player The player to sync to
     */
    public void syncKineticToPlayer(ServerPlayer player) {
        if (level instanceof ServerLevel) {
            // Will be implemented with packet system later
            // ModMessages.sendToPlayer(new KineticSyncS2CPacket(...), player);
        }
    }

    /**
     * Syncs kinetic data to all players within range
     * @param range Sync range in chunks
     */
    public void syncKineticToPlayers(int range) {
        if (level instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = new ChunkPos(worldPosition);

            for (ServerPlayer player : serverLevel.players()) {
                ChunkPos playerChunkPos = player.chunkPosition();

                if (Math.abs(playerChunkPos.x - chunkPos.x) <= range &&
                        Math.abs(playerChunkPos.z - chunkPos.z) <= range) {
                    syncKineticToPlayer(player);
                }
            }
        }
    }

    // ========== KINETIC STORAGE PACKET UPDATE (for client sync) ==========

    @Override
    public void setRPM(float rpm) {
        kineticStorage.setRPMWithoutUpdate(rpm);
    }

    @Override
    public void setMaxRPM(float maxRPM) {
        kineticStorage.setMaxRPMWithoutUpdate(maxRPM);
    }

    @Override
    public void setTorque(float torque) {
        kineticStorage.setTorqueWithoutUpdate(torque);
    }

    @Override
    public void setMaxTorque(float maxTorque) {
        kineticStorage.setMaxTorqueWithoutUpdate(maxTorque);
    }

    @Override
    public void setDirection(int direction) {
        kineticStorage.setDirectionWithoutUpdate(direction);
    }

    @Override
    public void setTemperature(float temperature) {
        kineticStorage.setTemperature(temperature);
    }

    @Override
    public void setVibration(float vibration) {
        kineticStorage.setVibration(vibration);
    }

    // ========== HELPER METHODS ==========

    /**
     * Checks if this machine is currently receiving rotation
     * @return True if RPM > 0 and has minimum torque
     */
    public boolean isReceivingRotation() {
        return kineticStorage.isRotating() &&
                kineticStorage.getTorque() >= (kineticStorage.getMaxTorque() * 0.1f);
    }

    /**
     * Checks if this machine is overstressed (not enough torque)
     * @return True if torque demand exceeds capacity
     */
    public boolean isOverstressed() {
        return kineticStorage.isOverstressed();
    }

    /**
     * Checks if this machine is overheating
     * @return True if temperature is too high
     */
    public boolean isOverheating() {
        return kineticStorage.isOverheating();
    }

    /**
     * Gets the current power output in watts
     * @return Power in watts
     */
    public float getPowerWatts() {
        return kineticStorage.getPowerWatts();
    }

    /**
     * Gets the current stress units
     * @return Stress units
     */
    public float getStressUnits() {
        return kineticStorage.getStressUnits();
    }

    /**
     * Gets the machine name for display
     * @return Machine name
     */
    public String getMachineName() {
        return machineName;
    }

    // ========== DEBUG INFO ==========

    /**
     * Gets debug information for display (F3 menu, tooltips, etc.)
     * @return Debug string
     */
    public String getDebugInfo() {
        return String.format(
                "%s | RPM: %.1f/%.1f | Torque: %.1f/%.1f Nm | Power: %.1f W | Temp: %.1f°C | Vib: %.1f%%",
                machineName,
                kineticStorage.getRPM(),
                kineticStorage.getMaxRPM(),
                kineticStorage.getTorque(),
                kineticStorage.getMaxTorque(),
                getPowerWatts(),
                kineticStorage.getTemperature(),
                kineticStorage.getVibration() * 100.0f
        );
    }
}
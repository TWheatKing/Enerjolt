package me.twheatking.enerjolt.block.entity.base;

import me.twheatking.enerjolt.fluid.FluidStoragePacketUpdate;
import me.twheatking.enerjolt.energy.IEnerjoltEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public abstract class FluidEnergyStorageBlockEntity
        <E extends IEnerjoltEnergyStorage, F extends IFluidHandler>
        extends EnergyStorageBlockEntity<E>
        implements FluidStoragePacketUpdate {
    protected final FluidStorageMethods<F> fluidStorageMethods;

    protected final F fluidStorage;

    protected final int baseTankCapacity;

    public FluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                         int baseEnergyCapacity, int baseEnergyTransferRate,
                                         FluidStorageMethods<F> fluidStorageMethods, int baseTankCapacity) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate);

        this.fluidStorageMethods = fluidStorageMethods;
        this.baseTankCapacity = baseTankCapacity;

        fluidStorage = initFluidStorage();
    }

    protected abstract F initFluidStorage();

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        fluidStorageMethods.saveFluidStorage(fluidStorage, nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        fluidStorageMethods.loadFluidStorage(fluidStorage, nbt, registries);
    }

    protected final void syncFluidToPlayer(Player player) {
        fluidStorageMethods.syncFluidToPlayer(fluidStorage, player, worldPosition);
    }

    protected final void syncFluidToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            fluidStorageMethods.syncFluidToPlayers(fluidStorage, level, worldPosition, distance);
    }

    public FluidStack getFluid(int tank) {
        return fluidStorageMethods.getFluid(fluidStorage, tank);
    }

    public int getTankCapacity(int tank) {
        return fluidStorageMethods.getTankCapacity(fluidStorage, tank);
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorageMethods.setFluid(fluidStorage, tank, fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, int capacity) {
        fluidStorageMethods.setTankCapacity(fluidStorage, tank, capacity);
    }
}

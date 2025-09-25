package me.twheatking.enerjolt.block.entity.base;

import me.twheatking.enerjolt.fluid.EnerjoltFluidStorage;
import me.twheatking.enerjolt.networking.ModMessages;
import me.twheatking.enerjolt.networking.packet.FluidSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageMultiTankMethods implements FluidStorageMethods<EnerjoltFluidStorage> {
    public static final FluidStorageMultiTankMethods INSTANCE = new FluidStorageMultiTankMethods();

    private FluidStorageMultiTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull EnerjoltFluidStorage fluidStorage, @NotNull CompoundTag nbt,
                                 @NotNull HolderLookup.Provider registries) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            nbt.put("fluid." + i, fluidStorage.getFluid(i).saveOptional(registries));
    }

    @Override
    public void loadFluidStorage(@NotNull EnerjoltFluidStorage fluidStorage, @NotNull CompoundTag nbt,
                                 @NotNull HolderLookup.Provider registries) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            fluidStorage.setFluid(i, FluidStack.parseOptional(registries, nbt.getCompound("fluid." + i)));
    }

    @Override
    public void syncFluidToPlayer(EnerjoltFluidStorage fluidStorage, Player player, BlockPos pos) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            ModMessages.sendToPlayer(new FluidSyncS2CPacket(i, fluidStorage.getFluidInTank(i),
                    fluidStorage.getTankCapacity(i), pos), (ServerPlayer)player);
    }

    @Override
    public void syncFluidToPlayers(EnerjoltFluidStorage fluidStorage, Level level, BlockPos pos, int distance) {
        for(int i = 0;i < fluidStorage.getTanks();i++)
            ModMessages.sendToPlayersWithinXBlocks(
                    new FluidSyncS2CPacket(i, fluidStorage.getFluidInTank(i), fluidStorage.getTankCapacity(i), pos),
                    pos, (ServerLevel)level, distance
            );
    }

    @Override
    public FluidStack getFluid(EnerjoltFluidStorage fluidStorage, int tank) {
        return fluidStorage.getFluid(tank);
    }

    @Override
    public int getTankCapacity(EnerjoltFluidStorage fluidStorage, int tank) {
        return fluidStorage.getCapacity(tank);
    }

    @Override
    public void setFluid(EnerjoltFluidStorage fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(tank, fluidStack);
    }

    @Override
    public void setTankCapacity(EnerjoltFluidStorage fluidStorage, int tank, int capacity) {
        fluidStorage.setCapacity(tank, capacity);
    }
}

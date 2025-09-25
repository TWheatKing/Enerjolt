package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.entity.base.MenuEnergyStorageBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.energy.ReceiveOnlyEnergyStorage;
import me.twheatking.enerjolt.screen.TimeControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class TimeControllerBlockEntity extends MenuEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage> {
    public static final int CAPACITY = ModConfigs.COMMON_TIME_CONTROLLER_CAPACITY.getValue();

    public TimeControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.TIME_CONTROLLER_ENTITY.get(), blockPos, blockState,

                "time_controller",

                CAPACITY,
                ModConfigs.COMMON_TIME_CONTROLLER_TRANSFER_RATE.getValue()
        );
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new TimeControllerMenu(id, inventory, this);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    public void clearEnergy() {
        energyStorage.setEnergy(0);
    }
}
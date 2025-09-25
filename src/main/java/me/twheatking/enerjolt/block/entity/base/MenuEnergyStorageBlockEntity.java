package me.twheatking.enerjolt.block.entity.base;

import me.twheatking.enerjolt.energy.IEnerjoltEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MenuEnergyStorageBlockEntity<E extends IEnerjoltEnergyStorage>
        extends EnergyStorageBlockEntity<E>
        implements MenuProvider {
    protected final String machineName;

    protected final ContainerData data;

    public MenuEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName,
                                        int baseEnergyCapacity, int baseEnergyTransferRate) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate);

        this.machineName = machineName;

        data = initContainerData();
    }

    protected ContainerData initContainerData() {
        return new SimpleContainerData(0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower." + machineName);
    }
}
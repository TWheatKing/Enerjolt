package me.twheatking.enerjolt.block.entity.base;

import me.twheatking.enerjolt.energy.IEnerjoltEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class MenuInventoryEnergyStorageBlockEntity
        <E extends IEnerjoltEnergyStorage, I extends ItemStackHandler>
        extends InventoryEnergyStorageBlockEntity<E, I>
        implements MenuProvider {
    protected final String machineName;

    protected final ContainerData data;

    public MenuInventoryEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                 String machineName,
                                                 int baseEnergyCapacity, int baseEnergyTransferRate,
                                                 int slotCount) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate, slotCount);

        this.machineName = machineName;

        data = initContainerData();
    }

    protected ContainerData initContainerData() {
        return new SimpleContainerData(0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.enerjolt." + machineName);
    }
}
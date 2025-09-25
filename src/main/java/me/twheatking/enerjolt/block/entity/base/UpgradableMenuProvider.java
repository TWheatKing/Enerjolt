package me.twheatking.enerjolt.block.entity.base;

import me.twheatking.enerjolt.inventory.upgrade.UpgradeModuleInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface UpgradableMenuProvider {
    AbstractContainerMenu createMenu(int id, Inventory inv, BlockEntity blockEntity,
                                     UpgradeModuleInventory upgradeModuleInventory, ContainerData data);
}

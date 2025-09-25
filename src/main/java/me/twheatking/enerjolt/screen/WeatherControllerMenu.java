package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.WeatherControllerBlockEntity;
import me.twheatking.enerjolt.inventory.UpgradeModuleSlot;
import me.twheatking.enerjolt.inventory.data.SimpleBooleanValueContainerData;
import me.twheatking.enerjolt.inventory.data.SimpleEnergyValueContainerData;
import me.twheatking.enerjolt.inventory.data.SimpleShortValueContainerData;
import me.twheatking.enerjolt.inventory.upgrade.UpgradeModuleInventory;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WeatherControllerMenu extends UpgradableEnergyStorageMenu<WeatherControllerBlockEntity> {
    private final SimpleShortValueContainerData selectedWeatherTypeData = new SimpleShortValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleBooleanValueContainerData hasEnoughEnergyData = new SimpleBooleanValueContainerData();

    public WeatherControllerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.DURATION
        ), null);
    }

    public WeatherControllerMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                                 ContainerData data) {
        super(
                EnerjoltMenuTypes.WEATHER_CONTROLLER_MENU.get(), id,

                inv, blockEntity,
                EnerjoltBlocks.WEATHER_CONTROLLER.get(),

                upgradeModuleInventory, 1
        );

        addSlot(new UpgradeModuleSlot(upgradeModuleInventory, 0, 80, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addDataSlots(selectedWeatherTypeData);
            addDataSlots(energyConsumptionPerTickData);
            addDataSlots(hasEnoughEnergyData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public int getEnergyPerTickBarValue() {
        return energyConsumptionPerTickData.getValue();
    }

    public int getSelectedWeatherType() {
        return selectedWeatherTypeData.getValue();
    }

    public boolean hasEnoughEnergy() {
        return hasEnoughEnergyData.getValue();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getItem();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory
            if(!moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }
}

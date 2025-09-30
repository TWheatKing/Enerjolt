package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.IndustrialGreenhouseBlockEntity;
import me.twheatking.enerjolt.inventory.ItemCapabilityMenuHelper;
import me.twheatking.enerjolt.inventory.UpgradeModuleSlot;
import me.twheatking.enerjolt.inventory.data.*;
import me.twheatking.enerjolt.inventory.upgrade.UpgradeModuleInventory;
import me.twheatking.enerjolt.machine.configuration.ComparatorMode;
import me.twheatking.enerjolt.machine.configuration.RedstoneMode;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.screen.base.IConfigurableMenu;
import me.twheatking.enerjolt.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import me.twheatking.enerjolt.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class IndustrialGreenhouseMenu extends UpgradableEnergyStorageMenu<IndustrialGreenhouseBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {

    private final SimpleProgressValueContainerData progressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxProgressData = new SimpleProgressValueContainerData();
    private final SimpleBooleanValueContainerData isFormedData = new SimpleBooleanValueContainerData();
    private final SimpleBooleanValueContainerData isProcessingData = new SimpleBooleanValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public IndustrialGreenhouseMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), null);
    }

    public IndustrialGreenhouseMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                                    ContainerData data) {
        super(
                EnerjoltMenuTypes.INDUSTRIAL_GREENHOUSE_MENU.get(), id,
                inv, blockEntity,
                EnerjoltBlocks.INDUSTRIAL_GREENHOUSE.get(),
                upgradeModuleInventory, 3
        );

        ItemCapabilityMenuHelper.getCapabilityItemHandler(this.level, this.blockEntity).ifPresent(itemHandler -> {
            // Input slot (sapling)
            addSlot(new SlotItemHandler(itemHandler, 0, 44, 35) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });

            // Output slots
            addSlot(new SlotItemHandler(itemHandler, 1, 116, 26) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new SlotItemHandler(itemHandler, 2, 134, 26) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new SlotItemHandler(itemHandler, 3, 116, 44) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new SlotItemHandler(itemHandler, 4, 134, 44) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
        });

        // Upgrade module slots
        for(int i = 0; i < upgradeModuleInventory.getContainerSize(); i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 62 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addDataSlots(progressData);
            addDataSlots(maxProgressData);
            addDataSlots(isFormedData);
            addDataSlots(isProcessingData);
            addDataSlots(redstoneModeData);
            addDataSlots(comparatorModeData);
        } else {
            addDataSlots(data);
        }
    }

    @Override
    public int getEnergyIndicatorBarValue() {
        if (blockEntity instanceof IndustrialGreenhouseBlockEntity greenhouse) {
            int energyPerTick = greenhouse.isFormed() ?
                    (greenhouse.getPattern() != null && greenhouse.getPattern().getSize() == 7 ? 150 : 80) : 0;
            int ticksRemaining = maxProgressData.getValue() - progressData.getValue();
            return energyPerTick * ticksRemaining;
        }
        return 0;
    }

    @Override
    public int getEnergyPerTickBarValue() {
        if (blockEntity instanceof IndustrialGreenhouseBlockEntity greenhouse && greenhouse.isFormed()) {
            return greenhouse.getPattern() != null && greenhouse.getPattern().getSize() == 7 ? 150 : 80;
        }
        return 0;
    }

    public boolean isFormed() {
        return isFormedData.getValue();
    }

    public boolean isProcessing() {
        return isProcessingData.getValue();
    }

    public int getScaledProgressArrowSize() {
        int progress = progressData.getValue();
        int maxProgress = maxProgressData.getValue();
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0) ? 0 : progress * progressArrowSize / maxProgress;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneModeData.getValue();
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return comparatorModeData.getValue();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getItem();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            // Player inventory -> Merge into upgrade module inventory, then tile inventory
            if(!moveItemStackTo(sourceItem, 4 * 9 + 5, 4 * 9 + 5 + 3, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                // Only input slot, not output slots
                return ItemStack.EMPTY;
            }
        } else if(index < 4 * 9 + 5 + 3) {
            // Tile inventory and upgrade module slots -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        } else {
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
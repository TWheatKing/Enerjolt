package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.block.entity.TeleporterBlockEntity;
import me.twheatking.enerjolt.component.DimensionalPositionComponent;
import me.twheatking.enerjolt.component.EnerjoltDataComponentTypes;
import me.twheatking.enerjolt.component.InventoryComponent;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.energy.ReceiveOnlyEnergyStorage;
import me.twheatking.enerjolt.item.energy.EnerjoltEnergyItem;
import me.twheatking.enerjolt.screen.InventoryTeleporterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InventoryTeleporterItem extends EnerjoltEnergyItem implements MenuProvider {
    public static final int CAPACITY = ModConfigs.COMMON_INVENTORY_TELEPORTER_CAPACITY.getValue();
    public static final int MAX_RECEIVE = ModConfigs.COMMON_INVENTORY_TELEPORTER_TRANSFER_RATE.getValue();

    public InventoryTeleporterItem(Properties props) {
        super(props, () -> new ReceiveOnlyEnergyStorage(0, CAPACITY, MAX_RECEIVE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(interactionHand == InteractionHand.OFF_HAND)
            return InteractionResultHolder.pass(itemStack);

        if(level.isClientSide || !(player instanceof ServerPlayer serverPlayer))
            return InteractionResultHolder.success(itemStack);

        if(player.isShiftKeyDown())
            player.openMenu(this);
        else
            teleportPlayer(itemStack, serverPlayer);

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.enerjolt.inventory_teleporter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new InventoryTeleporterMenu(id, inventory, getInventory(inventory.getSelected()));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, components, tooltipFlag);

        SimpleContainer inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getItem(0);

        DimensionalPositionComponent dimPos = teleporterMatrixItemStack.get(EnerjoltDataComponentTypes.DIMENSIONAL_POSITION);
        boolean linked = TeleporterMatrixItem.isLinked(teleporterMatrixItemStack) && dimPos != null;

        components.add(Component.translatable("tooltip.enerjolt.teleporter_matrix.status").withStyle(ChatFormatting.GRAY).
                append(Component.translatable("tooltip.enerjolt.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).withStyle(linked?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(linked) {
            components.add(Component.empty());

            components.add(Component.translatable("tooltip.enerjolt.teleporter_matrix.location").
                    append(Component.literal(dimPos.x() + " " + dimPos.y() + " " + dimPos.z())));
            components.add(Component.translatable("tooltip.enerjolt.teleporter_matrix.dimension").
                    append(Component.literal(dimPos.dimensionId().toString())));
        }

        components.add(Component.empty());

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.enerjolt.inventory_teleporter.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(Component.translatable("tooltip.enerjolt.inventory_teleporter.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.add(Component.translatable("tooltip.enerjolt.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static SimpleContainer getInventory(ItemStack itemStack) {
        InventoryComponent inventory = itemStack.get(EnerjoltDataComponentTypes.INVENTORY);

        if(inventory != null) {
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            for(int i = 0;i < items.size();i++) {
                if(inventory.size() <= i)
                    break;

                items.set(i, inventory.get(i));
            }
            return new SimpleContainer(items.toArray(new ItemStack[0])) {
                @Override
                public void setChanged() {
                    super.setChanged();

                    NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
                    for(int i = 0;i < getContainerSize();i++)
                        items.set(i, getItem(i));

                    itemStack.set(EnerjoltDataComponentTypes.INVENTORY, new InventoryComponent(items));
                }

                @Override
                public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                    if(slot >= 0 && slot < getContainerSize()) {
                        return stack.is(EnerjoltItems.TELEPORTER_MATRIX.get());
                    }

                    return super.canPlaceItem(slot, stack);
                }

                @Override
                public boolean stillValid(Player player) {
                    return super.stillValid(player) && player.getInventory().getSelected() == itemStack;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            };
        }

        return new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();

                NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
                for(int i = 0;i < getContainerSize();i++)
                    items.set(i, getItem(i));

                itemStack.set(EnerjoltDataComponentTypes.INVENTORY, new InventoryComponent(items));
            }

            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < getContainerSize()) {
                    return stack.is(EnerjoltItems.TELEPORTER_MATRIX.get());
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public boolean stillValid(Player player) {
                return super.stillValid(player) && player.getInventory().getSelected() == itemStack;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    public static void teleportPlayer(ItemStack itemStack, ServerPlayer player) {
        Level level = player.level();

        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null)
            return;

        SimpleContainer inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getItem(0);

        TeleporterBlockEntity.teleportPlayer(player, energyStorage, () -> setEnergy(itemStack, 0),
                teleporterMatrixItemStack, level, null);
    }
}

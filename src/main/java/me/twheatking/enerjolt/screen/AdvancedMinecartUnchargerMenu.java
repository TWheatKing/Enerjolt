package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.AdvancedMinecartUnchargerBlockEntity;
import me.twheatking.enerjolt.screen.base.EnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdvancedMinecartUnchargerMenu extends EnergyStorageMenu<AdvancedMinecartUnchargerBlockEntity> {
    public AdvancedMinecartUnchargerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public AdvancedMinecartUnchargerMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EnerjoltMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU.get(), id,

                inv, blockEntity,
                EnerjoltBlocks.ADVANCED_MINECART_UNCHARGER.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}

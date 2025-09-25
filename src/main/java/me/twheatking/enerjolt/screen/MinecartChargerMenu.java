package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.MinecartChargerBlockEntity;
import me.twheatking.enerjolt.screen.base.EnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MinecartChargerMenu extends EnergyStorageMenu<MinecartChargerBlockEntity> {
    public MinecartChargerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public MinecartChargerMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EnerjoltMenuTypes.MINECART_CHARGER_MENU.get(), id,

                inv, blockEntity,
                EnerjoltBlocks.MINECART_CHARGER.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}

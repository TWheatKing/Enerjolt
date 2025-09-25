package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.LightningGeneratorBlockEntity;
import me.twheatking.enerjolt.screen.base.EnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LightningGeneratorMenu extends EnergyStorageMenu<LightningGeneratorBlockEntity> {
    public LightningGeneratorMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public LightningGeneratorMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EnerjoltMenuTypes.LIGHTNING_GENERATOR_MENU.get(), id,

                inv, blockEntity,
                EnerjoltBlocks.LIGHTNING_GENERATOR.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}

package me.twheatking.enerjolt.item.custom;

import me.twheatking.enerjolt.block.custom.SapTreeLog;
import me.twheatking.enerjolt.item.EnerjoltItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ManualTapItem extends Item {
    public ManualTapItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Player player = context.getPlayer();

        // Check if the block is a SapTreeLog with sap
        if (block instanceof SapTreeLog && state.getValue(SapTreeLog.HAS_SAP)) {
            if (!level.isClientSide) {
                // Remove sap from the log
                level.setBlock(pos, state.setValue(SapTreeLog.HAS_SAP, false), 3);

                // Give player raw sap
                if (player != null) {
                    ItemStack sapStack = new ItemStack(EnerjoltItems.SAP.get(), 1);
                    if (!player.addItem(sapStack)) {
                        // If inventory is full, drop at player's feet
                        player.drop(sapStack, false);
                    }
                }

                // Play sound effect
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);

                // Damage the tap tool using the new 1.21.1 method signature
                ItemStack tapStack = context.getItemInHand();
                EquipmentSlot slot = context.getHand() == InteractionHand.MAIN_HAND
                        ? EquipmentSlot.MAINHAND
                        : EquipmentSlot.OFFHAND;
                tapStack.hurtAndBreak(1, player, slot);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
package me.twheatking.enerjolt.block.behavior;

import me.twheatking.enerjolt.item.EnerjoltItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class ModBlockBehaviors {
    private ModBlockBehaviors() {}

    public static void register() {
        DispenserBlock.registerBehavior(Items.SHEARS, new ShearsDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Level level = blockSource.level();
                if(!level.isClientSide() && (level instanceof ServerLevel serverLevel)) {
                    BlockPos blockPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                    if(tryCraftCableInsulator(level, blockPos)) {
                        itemStack.hurtAndBreak(1, serverLevel, null, item -> itemStack.setCount(0));

                        setSuccess(true);

                        return itemStack;
                    }
                }

                return super.execute(blockSource, itemStack);
            }

            private static boolean tryCraftCableInsulator(Level level, BlockPos blockPos) {
                BlockState blockstate = level.getBlockState(blockPos);
                if(blockstate.is(BlockTags.WOOL)) {
                    level.destroyBlock(blockPos, false, null);

                    ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                            new ItemStack(EnerjoltItems.CABLE_INSULATOR.get(), 18), 0, 0, 0);
                    itemEntity.setPickUpDelay(20);
                    level.addFreshEntity(itemEntity);

                    level.playSound(null, blockPos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.f, 1.f);

                    return true;
                }

                return false;
            }
        });
    }
}

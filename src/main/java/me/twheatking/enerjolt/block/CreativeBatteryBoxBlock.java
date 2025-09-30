package me.twheatking.enerjolt.block;

import com.mojang.serialization.MapCodec;
import me.twheatking.enerjolt.block.entity.CreativeBatteryBoxBlockEntity;
import me.twheatking.enerjolt.block.entity.EnerjoltBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeBatteryBoxBlock extends BaseEntityBlock {
    public static final MapCodec<CreativeBatteryBoxBlock> CODEC = simpleCodec(CreativeBatteryBoxBlock::new);

    public CreativeBatteryBoxBlock(Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new CreativeBatteryBoxBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.sidedSuccess(level.isClientSide());

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof CreativeBatteryBoxBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openMenu((CreativeBatteryBoxBlockEntity)blockEntity, blockPos);

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EnerjoltBlockEntities.CREATIVE_BATTERY_BOX_ENTITY.get(), CreativeBatteryBoxBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.enerjolt.capacity.txt",
                                Component.translatable("tooltip.enerjolt.infinite.txt").
                                        withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).
                        withStyle(ChatFormatting.GRAY));
                components.add(Component.translatable("tooltip.enerjolt.transfer_rate.txt",
                                Component.translatable("tooltip.enerjolt.infinite.txt").
                                        withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).
                        withStyle(ChatFormatting.GRAY));
            }else {
                components.add(Component.translatable("tooltip.enerjolt.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}

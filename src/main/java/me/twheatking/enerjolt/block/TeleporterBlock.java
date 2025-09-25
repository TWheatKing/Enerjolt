package me.twheatking.enerjolt.block;

import com.mojang.serialization.MapCodec;
import me.twheatking.enerjolt.block.entity.TeleporterBlockEntity;
import me.twheatking.enerjolt.input.ModKeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleporterBlock extends BaseEntityBlock {
    public static final MapCodec<TeleporterBlock> CODEC = simpleCodec(TeleporterBlock::new);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public TeleporterBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(TRIGGERED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new TeleporterBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity))
            return super.getAnalogOutputSignal(state, level, blockPos);

        return teleporterBlockEntity.getRedstoneOutput();
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter getter, BlockPos blockPos, Direction direction) {
        return state.getValue(POWERED)?15:0;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TeleporterBlockEntity))
            return;

        ((TeleporterBlockEntity)blockEntity).drops(level, blockPos);

        super.onRemove(state, level, blockPos, newState, isMoving);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.sidedSuccess(level.isClientSide());

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TeleporterBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openMenu((TeleporterBlockEntity)blockEntity, blockPos);

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClientSide())
            return;

        boolean isPowered = level.hasNeighborSignal(selfPos);
        if(isPowered != selfState.getValue(TRIGGERED)) {
            if(isPowered) {
                BlockEntity blockEntity = level.getBlockEntity(selfPos);
                if(!(blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity))
                    throw new IllegalStateException("Container is invalid");

                teleporterBlockEntity.onRedstoneTriggered();
            }

            level.setBlock(selfPos, selfState.setValue(TRIGGERED, isPowered), 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED, TRIGGERED);
    }

    public static class Item extends BlockItem {

        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.energizedpower.teleporter.txt.shift.1",
                        Component.keybind(ModKeyBindings.KEY_TELEPORTER_USE)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                components.add(Component.translatable("tooltip.energizedpower.teleporter.txt.shift.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}

package me.twheatking.enerjolt.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.twheatking.enerjolt.block.entity.TransformerBlockEntity;
import me.twheatking.enerjolt.machine.tier.TransformerTier;
import me.twheatking.enerjolt.machine.tier.TransformerType;
import me.twheatking.enerjolt.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigurableTransformerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, WrenchConfigurable {
    public static final MapCodec<ConfigurableTransformerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(ExtraCodecs.NON_EMPTY_STRING.xmap(TransformerTier::valueOf, TransformerTier::toString).fieldOf("tier").
                forGetter(ConfigurableTransformerBlock::getTier),
                        Properties.CODEC.fieldOf("properties").forGetter(Block::properties)).
                apply(instance, ConfigurableTransformerBlock::new);
    });

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<EnerjoltBlockStateProperties.TransformerConnection> UP = EnerjoltBlockStateProperties.TRANSFORMER_CONNECTION_UP;
    public static final EnumProperty<EnerjoltBlockStateProperties.TransformerConnection> DOWN = EnerjoltBlockStateProperties.TRANSFORMER_CONNECTION_DOWN;
    public static final EnumProperty<EnerjoltBlockStateProperties.TransformerConnection> NORTH = EnerjoltBlockStateProperties.TRANSFORMER_CONNECTION_NORTH;
    public static final EnumProperty<EnerjoltBlockStateProperties.TransformerConnection> SOUTH = EnerjoltBlockStateProperties.TRANSFORMER_CONNECTION_SOUTH;
    public static final EnumProperty<EnerjoltBlockStateProperties.TransformerConnection> EAST = EnerjoltBlockStateProperties.TRANSFORMER_CONNECTION_EAST;
    public static final EnumProperty<EnerjoltBlockStateProperties.TransformerConnection> WEST = EnerjoltBlockStateProperties.TRANSFORMER_CONNECTION_WEST;

    @NotNull
    public static EnumProperty<EnerjoltBlockStateProperties.TransformerConnection> getTransformerConnectionPropertyFromDirection(@NotNull Direction dir) {
        return switch(dir) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    private final TransformerTier tier;

    public ConfigurableTransformerBlock(TransformerTier tier, Properties properties) {
        super(properties);

        this.tier = tier;

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).
                setValue(UP, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(DOWN, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(NORTH, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(SOUTH, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(EAST, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(WEST, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED));
    }

    public TransformerTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new TransformerBlockEntity(blockPos, state, tier, TransformerType.CONFIGURABLE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TransformerBlockEntity) || ((TransformerBlockEntity)blockEntity).getTier() != tier ||
                ((TransformerBlockEntity)blockEntity).getTransformerType() != TransformerType.CONFIGURABLE)
            throw new IllegalStateException("Container is invalid");

        player.openMenu((TransformerBlockEntity)blockEntity, blockPos);

        return InteractionResult.SUCCESS;
    }

    @Override
    @NotNull
    public InteractionResult onUseWrench(UseOnContext useOnContext, Direction selectedFace, boolean nextPreviousValue) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();

        if(level.isClientSide || !(level.getBlockEntity(blockPos) instanceof TransformerBlockEntity))
            return InteractionResult.SUCCESS;

        BlockState state = level.getBlockState(blockPos);

        Player player = useOnContext.getPlayer();

        EnumProperty<EnerjoltBlockStateProperties.TransformerConnection> transformerConnectionProperty =
                ConfigurableTransformerBlock.getTransformerConnectionPropertyFromDirection(selectedFace);

        int diff = nextPreviousValue?-1:1;

        EnerjoltBlockStateProperties.TransformerConnection transformerConnection = state.getValue(transformerConnectionProperty);
        transformerConnection = EnerjoltBlockStateProperties.TransformerConnection.values()[(transformerConnection.ordinal() + diff +
                EnerjoltBlockStateProperties.TransformerConnection.values().length) %
                EnerjoltBlockStateProperties.TransformerConnection.values().length];

        level.setBlock(blockPos, state.setValue(transformerConnectionProperty, transformerConnection), 3);

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.enerjolt.configurable_transformer.wrench_configuration.face_changed",
                            Component.translatable("tooltip.enerjolt.direction." + selectedFace.getSerializedName()).
                                    withStyle(ChatFormatting.WHITE),
                            Component.translatable(transformerConnection.getTranslationKey()).
                                    withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                    ).withStyle(ChatFormatting.GREEN)
            ));
        }

        level.invalidateCapabilities(blockPos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClientSide())
            return;

        boolean isPowered = level.hasNeighborSignal(selfPos);
        if(isPowered != selfState.getValue(POWERED))
            level.setBlock(selfPos, selfState.setValue(POWERED, isPowered), 3);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().
                setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos())).
                setValue(UP, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(DOWN, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(NORTH, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(SOUTH, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(EAST, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                setValue(WEST, EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch(rotation) {
            case CLOCKWISE_90:
                return state.
                        setValue(NORTH, state.getValue(WEST)).
                        setValue(SOUTH, state.getValue(EAST)).
                        setValue(EAST, state.getValue(NORTH)).
                        setValue(WEST, state.getValue(SOUTH));
            case CLOCKWISE_180:
                return state.
                        setValue(NORTH, state.getValue(SOUTH)).
                        setValue(SOUTH, state.getValue(NORTH)).
                        setValue(EAST, state.getValue(WEST)).
                        setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.
                        setValue(NORTH, state.getValue(EAST)).
                        setValue(SOUTH, state.getValue(WEST)).
                        setValue(EAST, state.getValue(SOUTH)).
                        setValue(WEST, state.getValue(NORTH));
            default:
                return state;
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        switch(mirror) {
            case LEFT_RIGHT:
                return state.
                        setValue(NORTH, state.getValue(SOUTH)).
                        setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.
                        setValue(EAST, state.getValue(WEST)).
                        setValue(WEST, state.getValue(EAST));
            default:
                return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED).add(UP).add(DOWN).add(NORTH).add(SOUTH).add(EAST).add(WEST);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, tier.getEntityTypeFromTierAndType(TransformerType.CONFIGURABLE), TransformerBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final TransformerTier tier;

        public Item(Block block, Properties props, TransformerTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public TransformerTier getTier() {
            return tier;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.enerjolt.wrench_configurable").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

                components.add(Component.translatable("tooltip.enerjolt.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(tier.getMaxEnergyTransferFromTier())).
                        withStyle(ChatFormatting.GRAY));
                components.add(Component.empty());
                components.add(Component.translatable("tooltip.enerjolt.transformer.txt.shift.1").withStyle(ChatFormatting.GRAY));
                components.add(Component.translatable("tooltip.enerjolt.transformer.txt.shift.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                components.add(Component.translatable("tooltip.enerjolt.transformer.txt.shift.3").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.add(Component.translatable("tooltip.enerjolt.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}

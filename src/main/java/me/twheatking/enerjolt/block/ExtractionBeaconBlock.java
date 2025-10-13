package me.twheatking.enerjolt.block;

import com.mojang.serialization.MapCodec;
import me.twheatking.enerjolt.block.entity.EnerjoltBlockEntities;
import me.twheatking.enerjolt.block.entity.ExtractionBeaconBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

/**
 * Extraction Beacon - Used to call for extraction and defend against waves.
 * Players place contaminated items inside and start extraction.
 */
public class ExtractionBeaconBlock extends BaseEntityBlock {
    public static final MapCodec<ExtractionBeaconBlock> CODEC = simpleCodec(ExtractionBeaconBlock::new);

    public ExtractionBeaconBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExtractionBeaconBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ExtractionBeaconBlockEntity beacon) {
            // Try to start extraction
            if (beacon.startExtraction(player)) {
                return InteractionResult.SUCCESS;
            }

            // If extraction already running, show status
            if (beacon.isExtracting()) {
                int remaining = beacon.getRemainingTime();
                player.displayClientMessage(
                        Component.literal("§eExtraction in progress: " + remaining + "s remaining"),
                        true
                );
                player.displayClientMessage(
                        Component.literal("§7Wave " + beacon.getCurrentWave() + " - Defend the beacon!"),
                        false
                );
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide ? null :
                createTickerHelper(type, EnerjoltBlockEntities.EXTRACTION_BEACON_ENTITY.get(),
                        ExtractionBeaconBlockEntity::tick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ExtractionBeaconBlockEntity beacon) {
                // Drop all items if extraction was in progress
                if (beacon.isExtracting()) {
                    for (int i = 0; i < beacon.getItems().size(); i++) {
                        Block.popResource(level, pos, beacon.getItem(i));
                    }
                }
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
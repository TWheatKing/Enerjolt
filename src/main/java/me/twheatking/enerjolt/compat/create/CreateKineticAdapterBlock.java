package me.twheatking.enerjolt.compat.create;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Block that acts as an adapter for Enerjolt's kinetic system.
 * Simplified version that works with Create 6.0.6 without complex dependencies.
 */
public class CreateKineticAdapterBlock extends Block implements IBE<CreateKineticAdapterBlockEntity>, EntityBlock {

    public CreateKineticAdapterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CreateKineticAdapterBlockEntity> getBlockEntityClass() {
        return CreateKineticAdapterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreateKineticAdapterBlockEntity> getBlockEntityType() {
        return CreateCompat.KINETIC_ADAPTER_BLOCK_ENTITY.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreateKineticAdapterBlockEntity(getBlockEntityType(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof CreateKineticAdapterBlockEntity adapter) {
                adapter.tick();
            }
        };
    }
}
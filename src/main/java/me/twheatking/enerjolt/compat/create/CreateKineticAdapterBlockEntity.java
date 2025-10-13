package me.twheatking.enerjolt.compat.create;

import me.twheatking.enerjolt.kinetic.BasicKineticStorage;
import me.twheatking.enerjolt.kinetic.IKineticStorage;
import me.twheatking.enerjolt.kinetic.KineticNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Simplified adapter for Enerjolt's kinetic system.
 * This is a basic block entity that stores kinetic energy.
 * For full Create integration, users can manually connect via mechanical components.
 */
public class CreateKineticAdapterBlockEntity extends BlockEntity implements KineticNetworkHandler.IKineticBlockEntity {

    // Enerjolt kinetic storage
    private final BasicKineticStorage kineticStorage = new BasicKineticStorage(0, 512, 0, 1024, 1.0f, 0.01f);

    public CreateKineticAdapterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type != null ? type : CreateCompat.KINETIC_ADAPTER_BLOCK_ENTITY.get(), pos, state);
    }

    // --- Enerjolt IKineticBlockEntity Implementation ---

    public IKineticStorage getKineticStorage(Direction side) {
        return kineticStorage;
    }

    public float getRotationSpeedModifier(Direction face) {
        return 1.0f;
    }

    // --- NBT Serialization ---

    @Override
    protected void saveAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(compound, registries);
        compound.put("EnerjoltKinetic", kineticStorage.saveNBT());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(compound, registries);
        if(compound.contains("EnerjoltKinetic")) {
            kineticStorage.loadNBT(compound.get("EnerjoltKinetic"));
        }
    }

    // --- Tick Logic ---

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Propagate Enerjolt rotation to adjacent Enerjolt blocks
        KineticNetworkHandler.transferRotationToAdjacent(level, worldPosition);

        // Apply Enerjolt physics
        kineticStorage.tick();
    }

    public static void tick(BlockEntity blockEntity) {
        if (blockEntity instanceof CreateKineticAdapterBlockEntity adapter) {
            adapter.tick();
        }
    }
}
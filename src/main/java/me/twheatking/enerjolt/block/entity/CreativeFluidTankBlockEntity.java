package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.entity.base.FluidStorageInfiniteTankMethods;
import me.twheatking.enerjolt.fluid.InfinityFluidStorage;
import me.twheatking.enerjolt.screen.CreativeFluidTankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CreativeFluidTankBlockEntity
        extends AbstractFluidTankBlockEntity<InfinityFluidStorage> {
    public CreativeFluidTankBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.CREATIVE_FLUID_TANK_ENTITY.get(), blockPos, blockState,

                "creative_fluid_tank",

                FluidStorageInfiniteTankMethods.INSTANCE,
                Integer.MAX_VALUE
        );
    }

    @Override
    protected InfinityFluidStorage initFluidStorage() {
        return new InfinityFluidStorage() {
            @Override
            protected void onChange() {
                setChanged();
                syncFluidToPlayers(64);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncFluidToPlayer(player);

        return new CreativeFluidTankMenu(id, inventory, this);
    }

    public void setFluidStack(FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }
}
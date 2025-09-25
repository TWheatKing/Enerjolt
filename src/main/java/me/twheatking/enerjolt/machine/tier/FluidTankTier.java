package me.twheatking.enerjolt.machine.tier;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.EnerjoltBlockEntities;
import me.twheatking.enerjolt.block.entity.FluidTankBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.screen.EnerjoltMenuTypes;
import me.twheatking.enerjolt.screen.FluidTankMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum FluidTankTier {
    SMALL("fluid_tank_small", 1000 * ModConfigs.COMMON_FLUID_TANK_SMALL_TANK_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    MEDIUM("fluid_tank_medium", 1000 * ModConfigs.COMMON_FLUID_TANK_MEDIUM_TANK_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    LARGE("fluid_tank_large", 1000 * ModConfigs.COMMON_FLUID_TANK_LARGE_TANK_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

    private final String resourceId;
    private final int tankCapacity;
    private final BlockBehaviour.Properties props;

    FluidTankTier(String resourceId, int tankCapacity, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.tankCapacity = tankCapacity;
        this.props = props;
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case SMALL -> EnerjoltBlocks.FLUID_TANK_SMALL.get();
            case MEDIUM -> EnerjoltBlocks.FLUID_TANK_MEDIUM.get();
            case LARGE -> EnerjoltBlocks.FLUID_TANK_LARGE.get();
        };
    }

    public BlockEntityType<FluidTankBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case SMALL -> EnerjoltBlockEntities.FLUID_TANK_SMALL_ENTITY.get();
            case MEDIUM -> EnerjoltBlockEntities.FLUID_TANK_MEDIUM_ENTITY.get();
            case LARGE -> EnerjoltBlockEntities.FLUID_TANK_LARGE_ENTITY.get();
        };
    }

    public MenuType<FluidTankMenu> getMenuTypeFromTier() {
        return switch(this) {
            case SMALL -> EnerjoltMenuTypes.FLUID_TANK_SMALL.get();
            case MEDIUM -> EnerjoltMenuTypes.FLUID_TANK_MEDIUM.get();
            case LARGE -> EnerjoltMenuTypes.FLUID_TANK_LARGE.get();
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}

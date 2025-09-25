package me.twheatking.enerjolt.machine.tier;

import me.twheatking.enerjolt.block.entity.CableBlockEntity;
import me.twheatking.enerjolt.block.entity.EnerjoltBlockEntities;
import me.twheatking.enerjolt.config.ModConfigs;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public enum CableTier {
    TIN("tin_cable", ModConfigs.COMMON_TIN_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    COPPER("copper_cable", ModConfigs.COMMON_COPPER_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    GOLD("gold_cable", ModConfigs.COMMON_GOLD_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    ENERGIZED_COPPER("energized_copper_cable", ModConfigs.COMMON_ENERGIZED_COPPER_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    ENERGIZED_GOLD("energized_gold_cable", ModConfigs.COMMON_ENERGIZED_GOLD_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    ENERGIZED_CRYSTAL_MATRIX("energized_crystal_matrix_cable", ModConfigs.COMMON_ENERGIZED_CRYSTAL_MATRIX_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL));

    private final String resourceId;
    private final int maxTransfer;
    private final BlockBehaviour.Properties props;

    CableTier(String resourceId, int maxTransfer, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.maxTransfer = maxTransfer;
        this.props = props;
    }

    public BlockEntityType<CableBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TIN -> EnerjoltBlockEntities.TIN_CABLE_ENTITY.get();
            case COPPER -> EnerjoltBlockEntities.COPPER_CABLE_ENTITY.get();
            case GOLD -> EnerjoltBlockEntities.GOLD_CABLE_ENTITY.get();
            case ENERGIZED_COPPER -> EnerjoltBlockEntities.ENERGIZED_COPPER_CABLE_ENTITY.get();
            case ENERGIZED_GOLD -> EnerjoltBlockEntities.ENERGIZED_GOLD_CABLE_ENTITY.get();
            case ENERGIZED_CRYSTAL_MATRIX -> EnerjoltBlockEntities.ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY.get();
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}

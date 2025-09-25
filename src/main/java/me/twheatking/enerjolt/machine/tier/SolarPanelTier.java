package me.twheatking.enerjolt.machine.tier;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.EnerjoltBlockEntities;
import me.twheatking.enerjolt.block.entity.SolarPanelBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.screen.EnerjoltMenuTypes;
import me.twheatking.enerjolt.screen.SolarPanelMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum SolarPanelTier {
    TIER_1("solar_panel_1", ModConfigs.COMMON_SOLAR_PANEL_1_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_1_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_1_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_2("solar_panel_2", ModConfigs.COMMON_SOLAR_PANEL_2_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_2_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_2_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_3("solar_panel_3", ModConfigs.COMMON_SOLAR_PANEL_3_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_3_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_3_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_4("solar_panel_4", ModConfigs.COMMON_SOLAR_PANEL_4_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_4_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_4_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_5("solar_panel_5", ModConfigs.COMMON_SOLAR_PANEL_5_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_5_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_5_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_6("solar_panel_6", ModConfigs.COMMON_SOLAR_PANEL_6_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_6_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_6_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

    private final String resourceId;
    private final int peakFePerTick;
    private final int maxTransfer;
    private final int capacity;
    private final BlockBehaviour.Properties props;

    SolarPanelTier(String resourceId, int peakFePerTick, int maxTransfer, int capacity, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.peakFePerTick = peakFePerTick;
        this.maxTransfer = maxTransfer;
        this.capacity = capacity;
        this.props = props;
    }

    public MenuType<SolarPanelMenu> getMenuTypeFromTier() {
        return switch(this) {
            case TIER_1 -> EnerjoltMenuTypes.SOLAR_PANEL_MENU_1.get();
            case TIER_2 -> EnerjoltMenuTypes.SOLAR_PANEL_MENU_2.get();
            case TIER_3 -> EnerjoltMenuTypes.SOLAR_PANEL_MENU_3.get();
            case TIER_4 -> EnerjoltMenuTypes.SOLAR_PANEL_MENU_4.get();
            case TIER_5 -> EnerjoltMenuTypes.SOLAR_PANEL_MENU_5.get();
            case TIER_6 -> EnerjoltMenuTypes.SOLAR_PANEL_MENU_6.get();
        };
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case TIER_1 -> EnerjoltBlocks.SOLAR_PANEL_1.get();
            case TIER_2 -> EnerjoltBlocks.SOLAR_PANEL_2.get();
            case TIER_3 -> EnerjoltBlocks.SOLAR_PANEL_3.get();
            case TIER_4 -> EnerjoltBlocks.SOLAR_PANEL_4.get();
            case TIER_5 -> EnerjoltBlocks.SOLAR_PANEL_5.get();
            case TIER_6 -> EnerjoltBlocks.SOLAR_PANEL_6.get();
        };
    }

    public BlockEntityType<SolarPanelBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TIER_1 -> EnerjoltBlockEntities.SOLAR_PANEL_ENTITY_1.get();
            case TIER_2 -> EnerjoltBlockEntities.SOLAR_PANEL_ENTITY_2.get();
            case TIER_3 -> EnerjoltBlockEntities.SOLAR_PANEL_ENTITY_3.get();
            case TIER_4 -> EnerjoltBlockEntities.SOLAR_PANEL_ENTITY_4.get();
            case TIER_5 -> EnerjoltBlockEntities.SOLAR_PANEL_ENTITY_5.get();
            case TIER_6 -> EnerjoltBlockEntities.SOLAR_PANEL_ENTITY_6.get();
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getPeakFePerTick() {
        return peakFePerTick;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public int getCapacity() {
        return capacity;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}

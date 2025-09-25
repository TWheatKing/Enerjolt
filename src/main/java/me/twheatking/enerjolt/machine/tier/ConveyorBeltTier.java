package me.twheatking.enerjolt.machine.tier;

import me.twheatking.enerjolt.block.*;
import me.twheatking.enerjolt.block.entity.*;
import me.twheatking.enerjolt.screen.EnerjoltMenuTypes;
import me.twheatking.enerjolt.screen.ItemConveyorBeltLoaderMenu;
import me.twheatking.enerjolt.screen.ItemConveyorBeltSorterMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public enum ConveyorBeltTier {
    BASIC, FAST, EXPRESS;

    public ItemConveyorBeltBlock getItemConveyorBeltBlockFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT.get();
            case FAST -> EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT.get();
            case EXPRESS -> EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltBlockEntity> getItemConveyorBeltBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY.get();
            case FAST -> EnerjoltBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY.get();
            case EXPRESS -> EnerjoltBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY.get();
        };
    }

    public ItemConveyorBeltLoaderBlock getItemConveyorBeltLoaderBlockFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER.get();
            case FAST -> EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER.get();
            case EXPRESS -> EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltLoaderBlockEntity> getItemConveyorBeltLoaderBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlockEntities.BASIC_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get();
            case FAST -> EnerjoltBlockEntities.FAST_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get();
            case EXPRESS -> EnerjoltBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get();
        };
    }

    public MenuType<ItemConveyorBeltLoaderMenu> getItemConveyorBeltLoaderMenuTypeFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU.get();
            case FAST -> EnerjoltMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU.get();
            case EXPRESS -> EnerjoltMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU.get();
        };
    }

    public ItemConveyorBeltMergerBlock getItemConveyorBeltMergerBlockFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER.get();
            case FAST -> EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER.get();
            case EXPRESS -> EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltMergerBlockEntity> getItemConveyorBeltMergerBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlockEntities.BASIC_ITEM_CONVEYOR_BELT_MERGER_ENTITY.get();
            case FAST -> EnerjoltBlockEntities.FAST_ITEM_CONVEYOR_BELT_MERGER_ENTITY.get();
            case EXPRESS -> EnerjoltBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ENTITY.get();
        };
    }

    public ItemConveyorBeltSorterBlock getItemConveyorBeltSorterBlockFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER.get();
            case FAST -> EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER.get();
            case EXPRESS -> EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltSorterBlockEntity> getItemConveyorBeltSorterBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SORTER_ENTITY.get();
            case FAST -> EnerjoltBlockEntities.FAST_ITEM_CONVEYOR_BELT_SORTER_ENTITY.get();
            case EXPRESS -> EnerjoltBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ENTITY.get();
        };
    }

    public MenuType<ItemConveyorBeltSorterMenu> getItemConveyorBeltSorterMenuTypeFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU.get();
            case FAST -> EnerjoltMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU.get();
            case EXPRESS -> EnerjoltMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU.get();
        };
    }

    public ItemConveyorBeltSplitterBlock getItemConveyorBeltSplitterBlockFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER.get();
            case FAST -> EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER.get();
            case EXPRESS -> EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltSplitterBlockEntity> getItemConveyorBeltSplitterBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY.get();
            case FAST -> EnerjoltBlockEntities.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY.get();
            case EXPRESS -> EnerjoltBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY.get();
        };
    }

    public ItemConveyorBeltSwitchBlock getItemConveyorBeltSwitchBlockFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH.get();
            case FAST -> EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH.get();
            case EXPRESS -> EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltSwitchBlockEntity> getItemConveyorBeltSwitchBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EnerjoltBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ENTITY.get();
            case FAST -> EnerjoltBlockEntities.FAST_ITEM_CONVEYOR_BELT_SWITCH_ENTITY.get();
            case EXPRESS -> EnerjoltBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ENTITY.get();
        };
    }
}

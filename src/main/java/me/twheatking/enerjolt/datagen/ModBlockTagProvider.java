package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.registry.tags.CommonBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EJOLTAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(BlockTags.MINEABLE_WITH_AXE).
                add(EnerjoltBlocks.SAWDUST_BLOCK.get());

        tag(BlockTags.PREVENT_MOB_SPAWNING_INSIDE).
                add(
                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT.get()
                );

        tag(BlockTags.MINEABLE_WITH_PICKAXE).
                add(
                        EnerjoltBlocks.SILICON_BLOCK.get(),
                        EnerjoltBlocks.TIN_BLOCK.get(),

                        EnerjoltBlocks.TIN_ORE.get(),
                        EnerjoltBlocks.DEEPSLATE_TIN_ORE.get(),

                        EnerjoltBlocks.RAW_TIN_BLOCK.get(),

                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT.get(),
                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER.get(),
                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER.get(),
                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH.get(),
                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER.get(),
                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER.get(),

                        EnerjoltBlocks.IRON_FLUID_PIPE.get(),
                        EnerjoltBlocks.GOLDEN_FLUID_PIPE.get(),

                        EnerjoltBlocks.FLUID_TANK_SMALL.get(),
                        EnerjoltBlocks.FLUID_TANK_MEDIUM.get(),
                        EnerjoltBlocks.FLUID_TANK_LARGE.get(),

                        EnerjoltBlocks.ITEM_SILO_TINY.get(),
                        EnerjoltBlocks.ITEM_SILO_SMALL.get(),
                        EnerjoltBlocks.ITEM_SILO_MEDIUM.get(),
                        EnerjoltBlocks.ITEM_SILO_LARGE.get(),
                        EnerjoltBlocks.ITEM_SILO_GIANT.get(),

                        EnerjoltBlocks.AUTO_CRAFTER.get(),
                        EnerjoltBlocks.ADVANCED_AUTO_CRAFTER.get(),

                        EnerjoltBlocks.PRESS_MOLD_MAKER.get(),

                        EnerjoltBlocks.ALLOY_FURNACE.get(),

                        EnerjoltBlocks.CHARGER.get(),
                        EnerjoltBlocks.ADVANCED_CHARGER.get(),

                        EnerjoltBlocks.UNCHARGER.get(),
                        EnerjoltBlocks.ADVANCED_UNCHARGER.get(),

                        EnerjoltBlocks.MINECART_CHARGER.get(),
                        EnerjoltBlocks.ADVANCED_MINECART_CHARGER.get(),

                        EnerjoltBlocks.MINECART_UNCHARGER.get(),
                        EnerjoltBlocks.ADVANCED_MINECART_UNCHARGER.get(),

                        EnerjoltBlocks.SOLAR_PANEL_1.get(),
                        EnerjoltBlocks.SOLAR_PANEL_2.get(),
                        EnerjoltBlocks.SOLAR_PANEL_3.get(),
                        EnerjoltBlocks.SOLAR_PANEL_4.get(),
                        EnerjoltBlocks.SOLAR_PANEL_5.get(),
                        EnerjoltBlocks.SOLAR_PANEL_6.get(),

                        EnerjoltBlocks.COAL_ENGINE.get(),

                        EnerjoltBlocks.HEAT_GENERATOR.get(),

                        EnerjoltBlocks.THERMAL_GENERATOR.get(),

                        EnerjoltBlocks.POWERED_FURNACE.get(),
                        EnerjoltBlocks.ADVANCED_POWERED_FURNACE.get(),

                        EnerjoltBlocks.LV_TRANSFORMER_1_TO_N.get(),
                        EnerjoltBlocks.LV_TRANSFORMER_3_TO_3.get(),
                        EnerjoltBlocks.LV_TRANSFORMER_N_TO_1.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_1_TO_N.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_3_TO_3.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_N_TO_1.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_1_TO_N.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_3_TO_3.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_N_TO_1.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_1_TO_N.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_3_TO_3.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_N_TO_1.get(),

                        EnerjoltBlocks.CONFIGURABLE_LV_TRANSFORMER.get(),
                        EnerjoltBlocks.CONFIGURABLE_MV_TRANSFORMER.get(),
                        EnerjoltBlocks.CONFIGURABLE_HV_TRANSFORMER.get(),
                        EnerjoltBlocks.CONFIGURABLE_EHV_TRANSFORMER.get(),

                        EnerjoltBlocks.BATTERY_BOX.get(),
                        EnerjoltBlocks.ADVANCED_BATTERY_BOX.get(),

                        EnerjoltBlocks.CRUSHER.get(),
                        EnerjoltBlocks.ADVANCED_CRUSHER.get(),

                        EnerjoltBlocks.PULVERIZER.get(),
                        EnerjoltBlocks.ADVANCED_PULVERIZER.get(),

                        EnerjoltBlocks.SAWMILL.get(),

                        EnerjoltBlocks.COMPRESSOR.get(),

                        EnerjoltBlocks.METAL_PRESS.get(),

                        EnerjoltBlocks.AUTO_PRESS_MOLD_MAKER.get(),

                        EnerjoltBlocks.AUTO_STONECUTTER.get(),

                        EnerjoltBlocks.ASSEMBLING_MACHINE.get(),

                        EnerjoltBlocks.INDUCTION_SMELTER.get(),

                        EnerjoltBlocks.PLANT_GROWTH_CHAMBER.get(),

                        EnerjoltBlocks.BLOCK_PLACER.get(),

                        EnerjoltBlocks.FLUID_FILLER.get(),

                        EnerjoltBlocks.FLUID_DRAINER.get(),

                        EnerjoltBlocks.FLUID_PUMP.get(),
                        EnerjoltBlocks.ADVANCED_FLUID_PUMP.get(),

                        EnerjoltBlocks.DRAIN.get(),

                        EnerjoltBlocks.STONE_LIQUEFIER.get(),
                        EnerjoltBlocks.STONE_SOLIDIFIER.get(),

                        EnerjoltBlocks.FILTRATION_PLANT.get(),

                        EnerjoltBlocks.FLUID_TRANSPOSER.get(),

                        EnerjoltBlocks.LIGHTNING_GENERATOR.get(),

                        EnerjoltBlocks.ENERGIZER.get(),

                        EnerjoltBlocks.CHARGING_STATION.get(),

                        EnerjoltBlocks.CRYSTAL_GROWTH_CHAMBER.get(),

                        EnerjoltBlocks.WEATHER_CONTROLLER.get(),

                        EnerjoltBlocks.TIME_CONTROLLER.get(),

                        EnerjoltBlocks.TELEPORTER.get(),

                        EnerjoltBlocks.BASIC_MACHINE_FRAME.get(),
                        EnerjoltBlocks.HARDENED_MACHINE_FRAME.get(),
                        EnerjoltBlocks.ADVANCED_MACHINE_FRAME.get(),
                        EnerjoltBlocks.REINFORCED_ADVANCED_MACHINE_FRAME.get()
                );

        tag(BlockTags.NEEDS_STONE_TOOL).
                add(
                        EnerjoltBlocks.SILICON_BLOCK.get(),
                        EnerjoltBlocks.TIN_BLOCK.get(),

                        EnerjoltBlocks.TIN_ORE.get(),
                        EnerjoltBlocks.DEEPSLATE_TIN_ORE.get(),

                        EnerjoltBlocks.RAW_TIN_BLOCK.get(),

                        //Basic Item Conveyor Belts and Belt Machines do not require stone tools
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER.get(),

                        EnerjoltBlocks.IRON_FLUID_PIPE.get(),
                        EnerjoltBlocks.GOLDEN_FLUID_PIPE.get(),

                        EnerjoltBlocks.FLUID_TANK_SMALL.get(),
                        EnerjoltBlocks.FLUID_TANK_MEDIUM.get(),
                        EnerjoltBlocks.FLUID_TANK_LARGE.get(),

                        EnerjoltBlocks.ITEM_SILO_TINY.get(),
                        EnerjoltBlocks.ITEM_SILO_SMALL.get(),
                        EnerjoltBlocks.ITEM_SILO_MEDIUM.get(),
                        EnerjoltBlocks.ITEM_SILO_LARGE.get(),
                        EnerjoltBlocks.ITEM_SILO_GIANT.get(),

                        EnerjoltBlocks.AUTO_CRAFTER.get(),
                        EnerjoltBlocks.ADVANCED_AUTO_CRAFTER.get(),

                        EnerjoltBlocks.CHARGER.get(),
                        EnerjoltBlocks.ADVANCED_CHARGER.get(),

                        EnerjoltBlocks.UNCHARGER.get(),
                        EnerjoltBlocks.ADVANCED_UNCHARGER.get(),

                        EnerjoltBlocks.MINECART_CHARGER.get(),
                        EnerjoltBlocks.ADVANCED_MINECART_CHARGER.get(),

                        EnerjoltBlocks.MINECART_UNCHARGER.get(),
                        EnerjoltBlocks.ADVANCED_MINECART_UNCHARGER.get(),

                        EnerjoltBlocks.SOLAR_PANEL_1.get(),
                        EnerjoltBlocks.SOLAR_PANEL_2.get(),
                        EnerjoltBlocks.SOLAR_PANEL_3.get(),
                        EnerjoltBlocks.SOLAR_PANEL_4.get(),
                        EnerjoltBlocks.SOLAR_PANEL_5.get(),
                        EnerjoltBlocks.SOLAR_PANEL_6.get(),

                        EnerjoltBlocks.COAL_ENGINE.get(),

                        EnerjoltBlocks.HEAT_GENERATOR.get(),

                        EnerjoltBlocks.THERMAL_GENERATOR.get(),

                        EnerjoltBlocks.POWERED_FURNACE.get(),
                        EnerjoltBlocks.ADVANCED_POWERED_FURNACE.get(),

                        EnerjoltBlocks.LV_TRANSFORMER_1_TO_N.get(),
                        EnerjoltBlocks.LV_TRANSFORMER_3_TO_3.get(),
                        EnerjoltBlocks.LV_TRANSFORMER_N_TO_1.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_1_TO_N.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_3_TO_3.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_N_TO_1.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_1_TO_N.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_3_TO_3.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_N_TO_1.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_1_TO_N.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_3_TO_3.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_N_TO_1.get(),

                        EnerjoltBlocks.CONFIGURABLE_LV_TRANSFORMER.get(),
                        EnerjoltBlocks.CONFIGURABLE_MV_TRANSFORMER.get(),
                        EnerjoltBlocks.CONFIGURABLE_HV_TRANSFORMER.get(),
                        EnerjoltBlocks.CONFIGURABLE_EHV_TRANSFORMER.get(),

                        EnerjoltBlocks.BATTERY_BOX.get(),
                        EnerjoltBlocks.ADVANCED_BATTERY_BOX.get(),

                        EnerjoltBlocks.CRUSHER.get(),
                        EnerjoltBlocks.ADVANCED_CRUSHER.get(),

                        EnerjoltBlocks.PULVERIZER.get(),
                        EnerjoltBlocks.ADVANCED_PULVERIZER.get(),

                        EnerjoltBlocks.SAWMILL.get(),

                        EnerjoltBlocks.COMPRESSOR.get(),

                        EnerjoltBlocks.METAL_PRESS.get(),

                        EnerjoltBlocks.AUTO_PRESS_MOLD_MAKER.get(),

                        EnerjoltBlocks.AUTO_STONECUTTER.get(),

                        EnerjoltBlocks.ASSEMBLING_MACHINE.get(),

                        EnerjoltBlocks.INDUCTION_SMELTER.get(),

                        EnerjoltBlocks.PLANT_GROWTH_CHAMBER.get(),

                        EnerjoltBlocks.BLOCK_PLACER.get(),

                        EnerjoltBlocks.FLUID_FILLER.get(),

                        EnerjoltBlocks.FLUID_DRAINER.get(),

                        EnerjoltBlocks.FLUID_PUMP.get(),
                        EnerjoltBlocks.ADVANCED_FLUID_PUMP.get(),

                        EnerjoltBlocks.DRAIN.get(),

                        EnerjoltBlocks.STONE_LIQUEFIER.get(),
                        EnerjoltBlocks.STONE_SOLIDIFIER.get(),

                        EnerjoltBlocks.FILTRATION_PLANT.get(),

                        EnerjoltBlocks.FLUID_TRANSPOSER.get(),

                        EnerjoltBlocks.LIGHTNING_GENERATOR.get(),

                        EnerjoltBlocks.ENERGIZER.get(),

                        EnerjoltBlocks.CHARGING_STATION.get(),

                        EnerjoltBlocks.CRYSTAL_GROWTH_CHAMBER.get(),

                        EnerjoltBlocks.WEATHER_CONTROLLER.get(),

                        EnerjoltBlocks.TIME_CONTROLLER.get(),

                        EnerjoltBlocks.TELEPORTER.get(),

                        EnerjoltBlocks.BASIC_MACHINE_FRAME.get(),
                        EnerjoltBlocks.HARDENED_MACHINE_FRAME.get(),
                        EnerjoltBlocks.ADVANCED_MACHINE_FRAME.get(),
                        EnerjoltBlocks.REINFORCED_ADVANCED_MACHINE_FRAME.get()
                );

        tag(Tags.Blocks.ORES).
                addTag(CommonBlockTags.ORES_TIN);
        tag(CommonBlockTags.ORES_TIN).
                add(EnerjoltBlocks.TIN_ORE.get(),
                        EnerjoltBlocks.DEEPSLATE_TIN_ORE.get());

        tag(Tags.Blocks.ORES_IN_GROUND_STONE).
                add(EnerjoltBlocks.TIN_ORE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).
                add(EnerjoltBlocks.DEEPSLATE_TIN_ORE.get());

        tag(Tags.Blocks.STORAGE_BLOCKS).
                addTag(CommonBlockTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonBlockTags.STORAGE_BLOCKS_TIN).
                addTag(CommonBlockTags.STORAGE_BLOCKS_RAW_TIN);
        tag(CommonBlockTags.STORAGE_BLOCKS_SILICON).
                add(EnerjoltBlocks.SILICON_BLOCK.get());
        tag(CommonBlockTags.STORAGE_BLOCKS_TIN).
                add(EnerjoltBlocks.TIN_BLOCK.get());
        tag(CommonBlockTags.STORAGE_BLOCKS_RAW_TIN).
                add(EnerjoltBlocks.RAW_TIN_BLOCK.get());
    }
}

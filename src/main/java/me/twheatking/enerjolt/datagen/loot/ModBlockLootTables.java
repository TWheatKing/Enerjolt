package me.twheatking.enerjolt.datagen.loot;

import me.twheatking.enerjolt.block.*;
import me.twheatking.enerjolt.item.EnerjoltItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.function.Function;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EnerjoltBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }

    @Override
    protected void generate() {
        // Ore Blocks - These need special loot tables to drop raw materials
        add(EnerjoltBlocks.ZINC_ORE, this::createZincOreDrops);
        add(EnerjoltBlocks.ZINC_DEEPSLATE_ORE, this::createZincOreDrops);
        add(EnerjoltBlocks.ZINC_END_ORE, this::createZincOreDrops);
        add(EnerjoltBlocks.ZINC_NETHER_ORE, this::createZincOreDrops);

        add(EnerjoltBlocks.TIN_ORE, this::createTinOreDrops);
        add(EnerjoltBlocks.DEEPSLATE_TIN_ORE, this::createTinOreDrops);

        // Mineral/Storage Blocks
        dropSelf(EnerjoltBlocks.ZINC_BLOCK);
        dropSelf(EnerjoltBlocks.WHEAT_INGOT_BLOCK);

        dropSelf(EnerjoltBlocks.SILICON_BLOCK);
        dropSelf(EnerjoltBlocks.TIN_BLOCK);
        dropSelf(EnerjoltBlocks.RAW_TIN_BLOCK);
        dropSelf(EnerjoltBlocks.SAWDUST_BLOCK);

        // Zinc Decorative Blocks
        dropSelf(EnerjoltBlocks.ZINC_STAIRS);
        dropSelf(EnerjoltBlocks.ZINC_SLAB);
        dropSelf(EnerjoltBlocks.ZINC_PRESSURE_PLATE);
        dropSelf(EnerjoltBlocks.ZINC_BUTTON);
        dropSelf(EnerjoltBlocks.ZINC_FENCE);
        dropSelf(EnerjoltBlocks.ZINC_FENCE_GATE);
        dropSelf(EnerjoltBlocks.ZINC_WALL);
        dropSelf(EnerjoltBlocks.ZINC_DOOR);
        dropSelf(EnerjoltBlocks.ZINC_TRAPDOOR);

        // Casing Blocks
        dropSelf(EnerjoltBlocks.ZINC_CASING);
        dropSelf(EnerjoltBlocks.ANDESITE_CASING);

        // Conveyor Belt System
        dropSelf(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT);
        dropSelf(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT);
        dropSelf(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT);

        dropSelf(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER);
        dropSelf(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER);
        dropSelf(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER);

        dropSelf(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER);
        dropSelf(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER);
        dropSelf(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER);

        dropSelf(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH);
        dropSelf(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH);
        dropSelf(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH);

        dropSelf(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER);
        dropSelf(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER);
        dropSelf(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER);

        dropSelf(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER);
        dropSelf(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER);
        dropSelf(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER);

        // Fluid System
        dropSelf(EnerjoltBlocks.IRON_FLUID_PIPE);
        dropSelf(EnerjoltBlocks.GOLDEN_FLUID_PIPE);

        dropSelf(EnerjoltBlocks.FLUID_TANK_SMALL);
        dropSelf(EnerjoltBlocks.FLUID_TANK_MEDIUM);
        dropSelf(EnerjoltBlocks.FLUID_TANK_LARGE);
        // Note: CREATIVE_FLUID_TANK has .noLootTable() in its properties, so no entry needed

        // Storage System
        dropSelf(EnerjoltBlocks.ITEM_SILO_TINY);
        dropSelf(EnerjoltBlocks.ITEM_SILO_SMALL);
        dropSelf(EnerjoltBlocks.ITEM_SILO_MEDIUM);
        dropSelf(EnerjoltBlocks.ITEM_SILO_LARGE);
        dropSelf(EnerjoltBlocks.ITEM_SILO_GIANT);
        // Note: CREATIVE_ITEM_SILO has .noLootTable() in its properties, so no entry needed

        // Energy System - Cables
        dropSelf(EnerjoltBlocks.TIN_CABLE);
        dropSelf(EnerjoltBlocks.COPPER_CABLE);
        dropSelf(EnerjoltBlocks.GOLD_CABLE);
        dropSelf(EnerjoltBlocks.ENERGIZED_COPPER_CABLE);
        dropSelf(EnerjoltBlocks.ENERGIZED_GOLD_CABLE);
        dropSelf(EnerjoltBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE);

        // Energy System - Transformers
        dropSelf(EnerjoltBlocks.LV_TRANSFORMER_1_TO_N);
        dropSelf(EnerjoltBlocks.LV_TRANSFORMER_3_TO_3);
        dropSelf(EnerjoltBlocks.LV_TRANSFORMER_N_TO_1);
        dropSelf(EnerjoltBlocks.MV_TRANSFORMER_1_TO_N);
        dropSelf(EnerjoltBlocks.MV_TRANSFORMER_3_TO_3);
        dropSelf(EnerjoltBlocks.MV_TRANSFORMER_N_TO_1);
        dropSelf(EnerjoltBlocks.HV_TRANSFORMER_1_TO_N);
        dropSelf(EnerjoltBlocks.HV_TRANSFORMER_3_TO_3);
        dropSelf(EnerjoltBlocks.HV_TRANSFORMER_N_TO_1);
        dropSelf(EnerjoltBlocks.EHV_TRANSFORMER_1_TO_N);
        dropSelf(EnerjoltBlocks.EHV_TRANSFORMER_3_TO_3);
        dropSelf(EnerjoltBlocks.EHV_TRANSFORMER_N_TO_1);

        dropSelf(EnerjoltBlocks.CONFIGURABLE_LV_TRANSFORMER);
        dropSelf(EnerjoltBlocks.CONFIGURABLE_MV_TRANSFORMER);
        dropSelf(EnerjoltBlocks.CONFIGURABLE_HV_TRANSFORMER);
        dropSelf(EnerjoltBlocks.CONFIGURABLE_EHV_TRANSFORMER);

        // Energy System - Storage
        dropSelf(EnerjoltBlocks.BATTERY_BOX);
        dropSelf(EnerjoltBlocks.ADVANCED_BATTERY_BOX);
        // Note: CREATIVE_BATTERY_BOX has .noLootTable() in its properties, so no entry needed

        // Processing Machines
        dropSelf(EnerjoltBlocks.PRESS_MOLD_MAKER);
        dropSelf(EnerjoltBlocks.ALLOY_FURNACE);
        dropSelf(EnerjoltBlocks.AUTO_CRAFTER);
        dropSelf(EnerjoltBlocks.ADVANCED_AUTO_CRAFTER);
        dropSelf(EnerjoltBlocks.CRUSHER);
        dropSelf(EnerjoltBlocks.ADVANCED_CRUSHER);
        dropSelf(EnerjoltBlocks.PULVERIZER);
        dropSelf(EnerjoltBlocks.ADVANCED_PULVERIZER);
        dropSelf(EnerjoltBlocks.SAWMILL);
        dropSelf(EnerjoltBlocks.COMPRESSOR);
        dropSelf(EnerjoltBlocks.METAL_PRESS);
        dropSelf(EnerjoltBlocks.AUTO_PRESS_MOLD_MAKER);
        dropSelf(EnerjoltBlocks.AUTO_STONECUTTER);
        dropSelf(EnerjoltBlocks.PLANT_GROWTH_CHAMBER);
        dropSelf(EnerjoltBlocks.BLOCK_PLACER);
        dropSelf(EnerjoltBlocks.ASSEMBLING_MACHINE);
        dropSelf(EnerjoltBlocks.INDUCTION_SMELTER);

        // Fluid Processing
        dropSelf(EnerjoltBlocks.FLUID_FILLER);
        dropSelf(EnerjoltBlocks.STONE_LIQUEFIER);
        dropSelf(EnerjoltBlocks.STONE_SOLIDIFIER);
        dropSelf(EnerjoltBlocks.FLUID_TRANSPOSER);
        dropSelf(EnerjoltBlocks.FILTRATION_PLANT);
        dropSelf(EnerjoltBlocks.FLUID_DRAINER);
        dropSelf(EnerjoltBlocks.FLUID_PUMP);
        dropSelf(EnerjoltBlocks.ADVANCED_FLUID_PUMP);
        dropSelf(EnerjoltBlocks.DRAIN);

        // Energy Processing/Charging
        dropSelf(EnerjoltBlocks.CHARGER);
        dropSelf(EnerjoltBlocks.ADVANCED_CHARGER);
        dropSelf(EnerjoltBlocks.UNCHARGER);
        dropSelf(EnerjoltBlocks.ADVANCED_UNCHARGER);
        dropSelf(EnerjoltBlocks.MINECART_CHARGER);
        dropSelf(EnerjoltBlocks.ADVANCED_MINECART_CHARGER);
        dropSelf(EnerjoltBlocks.MINECART_UNCHARGER);
        dropSelf(EnerjoltBlocks.ADVANCED_MINECART_UNCHARGER);

        // Energy Generation
        dropSelf(EnerjoltBlocks.SOLAR_PANEL_1);
        dropSelf(EnerjoltBlocks.SOLAR_PANEL_2);
        dropSelf(EnerjoltBlocks.SOLAR_PANEL_3);
        dropSelf(EnerjoltBlocks.SOLAR_PANEL_4);
        dropSelf(EnerjoltBlocks.SOLAR_PANEL_5);
        dropSelf(EnerjoltBlocks.SOLAR_PANEL_6);
        dropSelf(EnerjoltBlocks.COAL_ENGINE);
        dropSelf(EnerjoltBlocks.LIGHTNING_GENERATOR);
        dropSelf(EnerjoltBlocks.HEAT_GENERATOR);
        dropSelf(EnerjoltBlocks.THERMAL_GENERATOR);

        // Utility Blocks
        dropSelf(EnerjoltBlocks.POWERED_LAMP);
        dropSelf(EnerjoltBlocks.POWERED_FURNACE);
        dropSelf(EnerjoltBlocks.ADVANCED_POWERED_FURNACE);
        dropSelf(EnerjoltBlocks.ENERGIZER);
        dropSelf(EnerjoltBlocks.CHARGING_STATION);
        dropSelf(EnerjoltBlocks.CRYSTAL_GROWTH_CHAMBER);
        dropSelf(EnerjoltBlocks.WEATHER_CONTROLLER);
        dropSelf(EnerjoltBlocks.TIME_CONTROLLER);
        dropSelf(EnerjoltBlocks.TELEPORTER);

        // Machine Frames
        dropSelf(EnerjoltBlocks.BASIC_MACHINE_FRAME);
        dropSelf(EnerjoltBlocks.HARDENED_MACHINE_FRAME);
        dropSelf(EnerjoltBlocks.ADVANCED_MACHINE_FRAME);
        dropSelf(EnerjoltBlocks.REINFORCED_ADVANCED_MACHINE_FRAME);

        dropSelf(EnerjoltBlocks.INDUSTRIAL_GREENHOUSE.get());
        dropSelf(EnerjoltBlocks.PHOTOSYNTHETIC_CHAMBER.get());

        // Wood/Tree Blocks
        dropSelf(EnerjoltBlocks.RUBBER_OAK_LOG);
        dropSelf(EnerjoltBlocks.RUBBER_OAK_WOOD);
        dropSelf(EnerjoltBlocks.RUBBER_OAK_LEAVES);
        dropSelf(EnerjoltBlocks.RUBBER_OAK_SAPLING);
        dropSelf(EnerjoltBlocks.RUBBER_OAK_PLANKS);
        dropSelf(EnerjoltBlocks.RUBBER_DARK_OAK_LOG);
        dropSelf(EnerjoltBlocks.RUBBER_DARK_OAK_WOOD);
        dropSelf(EnerjoltBlocks.RUBBER_DARK_OAK_LEAVES);
        dropSelf(EnerjoltBlocks.RUBBER_DARK_OAK_SAPLING);
        dropSelf(EnerjoltBlocks.RUBBER_DARK_OAK_PLANKS);
        dropSelf(EnerjoltBlocks.RUBBER_BIRCH_LOG);
        dropSelf(EnerjoltBlocks.RUBBER_BIRCH_WOOD);
        dropSelf(EnerjoltBlocks.RUBBER_BIRCH_LEAVES);
        dropSelf(EnerjoltBlocks.RUBBER_BIRCH_SAPLING);
        dropSelf(EnerjoltBlocks.RUBBER_BIRCH_PLANKS);
        dropSelf(EnerjoltBlocks.RUBBER_SPRUCE_LOG);
        dropSelf(EnerjoltBlocks.RUBBER_SPRUCE_WOOD);
        dropSelf(EnerjoltBlocks.RUBBER_SPRUCE_LEAVES);
        dropSelf(EnerjoltBlocks.RUBBER_SPRUCE_SAPLING);
        dropSelf(EnerjoltBlocks.RUBBER_SPRUCE_PLANKS);

        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_OAK_LOG);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_OAK_WOOD);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_DARK_OAK_LOG);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_DARK_OAK_WOOD);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_BIRCH_LOG);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_BIRCH_WOOD);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_SPRUCE_LOG);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_SPRUCE_WOOD);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_FANCY_OAK_LOG);
        dropSelf(EnerjoltBlocks.STRIPPED_RUBBER_FANCY_OAK_WOOD);

        dropSelf(EnerjoltBlocks.RUBBER_FANCY_OAK_LOG);
        dropSelf(EnerjoltBlocks.RUBBER_FANCY_OAK_WOOD);
        dropSelf(EnerjoltBlocks.RUBBER_FANCY_OAK_LEAVES);
        dropSelf(EnerjoltBlocks.RUBBER_FANCY_OAK_SAPLING);
        dropSelf(EnerjoltBlocks.RUBBER_FANCY_OAK_PLANKS);


    }

    private void dropSelf(DeferredHolder<Block, ? extends Block> block) {
        dropSelf(block.get());
    }

    private void add(DeferredHolder<Block, ? extends Block> block, Function<Block, LootTable.Builder> builderFunction) {
        add(block.get(), builderFunction);
    }

    private LootTable.Builder createTinOreDrops(Block block) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = registries.lookupOrThrow(Registries.ENCHANTMENT);

        return createSilkTouchDispatchTable(block,
                applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(EnerjoltItems.RAW_TIN)
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    private LootTable.Builder createZincOreDrops(Block block) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = registries.lookupOrThrow(Registries.ENCHANTMENT);

        return createSilkTouchDispatchTable(block,
                applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(EnerjoltItems.RAW_ZINC)
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }
}
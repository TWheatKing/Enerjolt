package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.fluid.EnerjoltFluids;
import me.twheatking.enerjolt.item.EnerjoltItems;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EJOLTAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerBasicModels();
        registerSpecialModels();
    }

    private void registerBasicModels() {
        // Basic materials and ingots
        basicItem(EnerjoltItems.WHEAT_INGOT);
        basicItem(EnerjoltItems.ZINC_INGOT);
        basicItem(EnerjoltItems.RAW_ZINC);
        basicItem(EnerjoltItems.COBALT);
        basicItem(EnerjoltItems.RAW_COBALT);
        basicItem(EnerjoltItems.BRASS);
        basicItem(EnerjoltItems.SULFUR);
        basicItem(EnerjoltItems.GRAPHITE);
        basicItem(EnerjoltItems.BAUXITE);
        basicItem(EnerjoltItems.SALTPETER);
        basicItem(EnerjoltItems.LITHIUM);
        basicItem(EnerjoltItems.NICKEL);
        basicItem(EnerjoltItems.SILICON);
        basicItem(EnerjoltItems.COPPER_SULFIDE);
        basicItem(EnerjoltItems.MANGANESE);
        basicItem(EnerjoltItems.URANINITE);
        basicItem(EnerjoltItems.THORIUM);
        basicItem(EnerjoltItems.TUNGSTEN);
        basicItem(EnerjoltItems.PLATINUM);

        // Rare earth materials
        basicItem(EnerjoltItems.NEODYMIUM);
        basicItem(EnerjoltItems.YTTRIUM);

        // Endgame materials
        basicItem(EnerjoltItems.ENERJOLT);
        basicItem(EnerjoltItems.CRYONITE);
        basicItem(EnerjoltItems.VOIDSTONE);

        // Bits
        basicItem(EnerjoltItems.IRON_BIT);
        basicItem(EnerjoltItems.GOLD_BIT);
        basicItem(EnerjoltItems.COPPER_BIT);
        basicItem(EnerjoltItems.DIAMOND_BIT);
        basicItem(EnerjoltItems.NETHERITE_BIT);

        // Energized materials
        basicItem(EnerjoltItems.ENERGIZED_COPPER_INGOT);
        basicItem(EnerjoltItems.ENERGIZED_GOLD_INGOT);
        basicItem(EnerjoltItems.ENERGIZED_COPPER_PLATE);
        basicItem(EnerjoltItems.ENERGIZED_GOLD_PLATE);
        basicItem(EnerjoltItems.ENERGIZED_COPPER_WIRE);
        basicItem(EnerjoltItems.ENERGIZED_GOLD_WIRE);

        basicItem(EnerjoltItems.STONE_PEBBLE);
        basicItem(EnerjoltItems.RAW_TIN);

        // Dusts
        basicItem(EnerjoltItems.TIN_DUST);
        basicItem(EnerjoltItems.COPPER_DUST);
        basicItem(EnerjoltItems.IRON_DUST);
        basicItem(EnerjoltItems.GOLD_DUST);

        basicItem(EnerjoltItems.TIN_NUGGET);
        basicItem(EnerjoltItems.TIN_INGOT);

        // Plates
        basicItem(EnerjoltItems.TIN_PLATE);
        basicItem(EnerjoltItems.COPPER_PLATE);
        basicItem(EnerjoltItems.IRON_PLATE);
        basicItem(EnerjoltItems.GOLD_PLATE);
        basicItem(EnerjoltItems.BRASS_PLATE);
        basicItem(EnerjoltItems.DIAMOND_PLATE);
        basicItem(EnerjoltItems.NETHERITE_PLATE);

        basicItem(EnerjoltItems.STEEL_INGOT);

        // Alloys
        basicItem(EnerjoltItems.ZINC_ALLOY);
        basicItem(EnerjoltItems.ANDESITE_ALLOY);
        basicItem(EnerjoltItems.REDSTONE_ALLOY_INGOT);
        basicItem(EnerjoltItems.ADVANCED_ALLOY_INGOT);
        basicItem(EnerjoltItems.ADVANCED_ALLOY_PLATE);

        // Machine parts
        basicItem(EnerjoltItems.BASIC_BLADE);
        basicItem(EnerjoltItems.ADVANCED_BLADE);
        basicItem(EnerjoltItems.GRATE);
        basicItem(EnerjoltItems.WISK);
        basicItem(EnerjoltItems.RAM);
        basicItem(EnerjoltItems.EMPTY_COIL);
        basicItem(EnerjoltItems.COPPER_COIL);
        basicItem(EnerjoltItems.GOLD_COIL);
        basicItem(EnerjoltItems.DIAMOND_COIL);
        basicItem(EnerjoltItems.IRON_GEAR);
        basicItem(EnerjoltItems.IRON_ROD);

        // Wires
        basicItem(EnerjoltItems.TIN_WIRE);
        basicItem(EnerjoltItems.COPPER_WIRE);
        basicItem(EnerjoltItems.GOLD_WIRE);

        basicItem(EnerjoltItems.SAWDUST);
        basicItem(EnerjoltItems.CHARCOAL_DUST);

        // Fertilizers
        basicItem(EnerjoltItems.BASIC_FERTILIZER);
        basicItem(EnerjoltItems.GOOD_FERTILIZER);
        basicItem(EnerjoltItems.ADVANCED_FERTILIZER);

        // Press molds
        basicItem(EnerjoltItems.RAW_GEAR_PRESS_MOLD);
        basicItem(EnerjoltItems.RAW_ROD_PRESS_MOLD);
        basicItem(EnerjoltItems.RAW_WIRE_PRESS_MOLD);
        basicItem(EnerjoltItems.GEAR_PRESS_MOLD);
        basicItem(EnerjoltItems.ROD_PRESS_MOLD);
        basicItem(EnerjoltItems.WIRE_PRESS_MOLD);

        // Solar cells
        basicItem(EnerjoltItems.BASIC_SOLAR_CELL);
        basicItem(EnerjoltItems.ADVANCED_SOLAR_CELL);
        basicItem(EnerjoltItems.REINFORCED_ADVANCED_SOLAR_CELL);

        // Circuits
        basicItem(EnerjoltItems.BASIC_CIRCUIT);
        basicItem(EnerjoltItems.ADVANCED_CIRCUIT);
        basicItem(EnerjoltItems.PROCESSING_UNIT);

        // Teleporter components
        basicItem(EnerjoltItems.TELEPORTER_MATRIX);
        basicItem(EnerjoltItems.TELEPORTER_PROCESSING_UNIT);

        // Upgrade modules
        basicItem(EnerjoltItems.BASIC_UPGRADE_MODULE);
        basicItem(EnerjoltItems.ADVANCED_UPGRADE_MODULE);
        basicItem(EnerjoltItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

        // Speed upgrade modules
        basicItem(EnerjoltItems.SPEED_UPGRADE_MODULE_1);
        basicItem(EnerjoltItems.SPEED_UPGRADE_MODULE_2);
        basicItem(EnerjoltItems.SPEED_UPGRADE_MODULE_3);
        basicItem(EnerjoltItems.SPEED_UPGRADE_MODULE_4);
        basicItem(EnerjoltItems.SPEED_UPGRADE_MODULE_5);

        // Energy efficiency upgrade modules
        basicItem(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
        basicItem(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
        basicItem(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
        basicItem(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
        basicItem(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

        // Energy capacity upgrade modules
        basicItem(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
        basicItem(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
        basicItem(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
        basicItem(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
        basicItem(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

        // Duration upgrade modules
        basicItem(EnerjoltItems.DURATION_UPGRADE_MODULE_1);
        basicItem(EnerjoltItems.DURATION_UPGRADE_MODULE_2);
        basicItem(EnerjoltItems.DURATION_UPGRADE_MODULE_3);
        basicItem(EnerjoltItems.DURATION_UPGRADE_MODULE_4);
        basicItem(EnerjoltItems.DURATION_UPGRADE_MODULE_5);
        basicItem(EnerjoltItems.DURATION_UPGRADE_MODULE_6);

        // Range upgrade modules
        basicItem(EnerjoltItems.RANGE_UPGRADE_MODULE_1);
        basicItem(EnerjoltItems.RANGE_UPGRADE_MODULE_2);
        basicItem(EnerjoltItems.RANGE_UPGRADE_MODULE_3);

        // Extraction upgrade modules
        basicItem(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
        basicItem(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
        basicItem(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
        basicItem(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
        basicItem(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

        basicItem(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_1);
        basicItem(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_2);
        basicItem(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_3);
        basicItem(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_4);
        basicItem(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_5);

        // Furnace upgrade modules
        basicItem(EnerjoltItems.BLAST_FURNACE_UPGRADE_MODULE);
        basicItem(EnerjoltItems.SMOKER_UPGRADE_MODULE);

        // Moon light upgrade modules
        basicItem(EnerjoltItems.MOON_LIGHT_UPGRADE_MODULE_1);
        basicItem(EnerjoltItems.MOON_LIGHT_UPGRADE_MODULE_2);
        basicItem(EnerjoltItems.MOON_LIGHT_UPGRADE_MODULE_3);

        // Books and tools
        basicItem(EnerjoltItems.ENERJOLT_BOOK);
        basicItem(EnerjoltItems.CABLE_INSULATOR);
        basicItem(EnerjoltItems.CHARCOAL_FILTER);
        basicItem(EnerjoltItems.SAW_BLADE);
        basicItem(EnerjoltItems.CRYSTAL_MATRIX);
        basicItem(EnerjoltItems.ENERGIZED_CRYSTAL_MATRIX);

        // Inventory tools
        basicItem(EnerjoltItems.INVENTORY_CHARGER);
        basicItem(EnerjoltItems.INVENTORY_TELEPORTER);

        // Batteries
        basicItem(EnerjoltItems.BATTERY_1);
        basicItem(EnerjoltItems.BATTERY_2);
        basicItem(EnerjoltItems.BATTERY_3);
        basicItem(EnerjoltItems.BATTERY_4);
        basicItem(EnerjoltItems.BATTERY_5);
        basicItem(EnerjoltItems.BATTERY_6);
        basicItem(EnerjoltItems.BATTERY_7);
        basicItem(EnerjoltItems.BATTERY_8);
        basicItem(EnerjoltItems.CREATIVE_BATTERY);

        // Analyzers
        basicItem(EnerjoltItems.ENERGY_ANALYZER);
        basicItem(EnerjoltItems.FLUID_ANALYZER);

        // Hammers
        basicItem(EnerjoltItems.WOODEN_HAMMER);
        basicItem(EnerjoltItems.STONE_HAMMER);
        basicItem(EnerjoltItems.IRON_HAMMER);
        basicItem(EnerjoltItems.GOLDEN_HAMMER);
        basicItem(EnerjoltItems.DIAMOND_HAMMER);
        basicItem(EnerjoltItems.NETHERITE_HAMMER);

        // Other tools
        basicItem(EnerjoltItems.CUTTER);
        basicItem(EnerjoltItems.WRENCH);

        // Minecarts
        basicItem(EnerjoltItems.BATTERY_BOX_MINECART);
        basicItem(EnerjoltItems.ADVANCED_BATTERY_BOX_MINECART);

        // Armor
        basicItem(EnerjoltItems.ZINC_HELMET);
        basicItem(EnerjoltItems.ZINC_CHESTPLATE);
        basicItem(EnerjoltItems.ZINC_LEGGINGS);
        basicItem(EnerjoltItems.ZINC_BOOTS);
        basicItem(EnerjoltItems.ZINC_HORSE_ARMOR);
        basicItem(EnerjoltItems.ZINC_SMITHING_TEMPLATE);

        // Zinc tools and weapons
        basicItem(EnerjoltItems.ZINC_BOW);
        basicItem(EnerjoltItems.ZINC_SWORD);
        basicItem(EnerjoltItems.ZINC_PICKAXE);
        basicItem(EnerjoltItems.ZINC_SHOVEL);
        basicItem(EnerjoltItems.ZINC_AXE);
        basicItem(EnerjoltItems.ZINC_HOE);

        basicItem(EnerjoltItems.CHISEL);

        // Fluids
        basicItem(EnerjoltFluids.DIRTY_WATER_BUCKET_ITEM);
    }

    private void registerSpecialModels() {
        ModelFile inventoryCoalEngineActive = basicItem(EnerjoltItems.INVENTORY_COAL_ENGINE, "_active");
        ModelFile inventoryCoalEngineOn = basicItem(EnerjoltItems.INVENTORY_COAL_ENGINE, "_on");

        ResourceLocation inventoryCoalEngineItemId = Objects.requireNonNull(EnerjoltItems.INVENTORY_COAL_ENGINE.getKey()).location();

        withExistingParent(inventoryCoalEngineItemId.getPath(), "generated").
                texture("layer0", ResourceLocation.fromNamespaceAndPath(inventoryCoalEngineItemId.getNamespace(),
                        "item/" + inventoryCoalEngineItemId.getPath())).
                override().
                predicate(EJOLTAPI.id("active"), 1.f).
                model(inventoryCoalEngineActive).
                end().
                override().
                predicate(EJOLTAPI.id("active"), 1.f).
                predicate(EJOLTAPI.id("working"), 1.f).
                model(inventoryCoalEngineOn).
                end();
    }

    private ItemModelBuilder basicItem(Holder<Item> item) {
        ResourceLocation itemID = Objects.requireNonNull(item.getKey()).location();

        return withExistingParent(itemID.getPath(), "generated")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(itemID.getNamespace(), "item/" + itemID.getPath()));
    }

    private ItemModelBuilder basicItem(Holder<Item> item, String pathSuffix) {
        ResourceLocation itemID = Objects.requireNonNull(item.getKey()).location();

        return withExistingParent(itemID.getPath() + pathSuffix, "generated")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(itemID.getNamespace(), "item/" + itemID.getPath() + pathSuffix));
    }
}
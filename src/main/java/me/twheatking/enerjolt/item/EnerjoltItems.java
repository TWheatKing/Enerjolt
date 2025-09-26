package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.Enerjolt;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.item.custom.AndesiteAlloyItem;
import me.twheatking.enerjolt.item.custom.ChiselItem;
import me.twheatking.enerjolt.item.custom.ModArmorItem;
import me.twheatking.enerjolt.item.custom.ZincAlloyItem;
import me.twheatking.enerjolt.machine.tier.BatteryTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EnerjoltItems {
    private EnerjoltItems() {}
    
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EJOLTAPI.MOD_ID);

    public static final DeferredItem<Item> WHEAT_INGOT = ITEMS.register("wheat_ingot",
            () -> new Item(new Item.Properties()));
    //items from ore or from the ore output refined
    public static final DeferredItem<Item> ZINC_INGOT = ITEMS.register("zinc_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_ZINC = ITEMS.register("raw_zinc",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COBALT = ITEMS.register("cobalt",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_COBALT = ITEMS.register("raw_cobalt",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRASS = ITEMS.register("brass",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SULFUR = ITEMS.register("sulfur",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GRAPHITE = ITEMS.register("graphite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BAUXITE = ITEMS.register("bauxite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SALTPETER = ITEMS.register("saltpeter",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LITHIUM = ITEMS.register("lithium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NICKEL = ITEMS.register("nickel",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SILICON = ITEMS.register("silicon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_SULFIDE = ITEMS.register("copper_sulfide",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MANGANESE = ITEMS.register("manganese",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> URANINITE = ITEMS.register("uraninite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> THORIUM = ITEMS.register("thorium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TUNGSTEN = ITEMS.register("tungsten",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PLATINUM = ITEMS.register("platinum",
            () -> new Item(new Item.Properties()));
    //rare earth ore
    public static final DeferredItem<Item> NEODYMIUM = ITEMS.register("neodymium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> YTTRIUM = ITEMS.register("yttrium",
            () -> new Item(new Item.Properties()));
    //endgame
    public static final DeferredItem<Item> ENERJOLT = ITEMS.register("enerjolt",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRYONITE = ITEMS.register("cryonite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> VOIDSTONE = ITEMS.register("voidstone",
            () -> new Item(new Item.Properties()));
    //Bits
    public static final DeferredItem<Item> IRON_BIT = ITEMS.register("iron_bit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_BIT = ITEMS.register("gold_bit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_BIT = ITEMS.register("copper_bit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_BIT = ITEMS.register("diamond_bit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_BIT = ITEMS.register("netherite_bit",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ENERGIZED_COPPER_INGOT = ITEMS.register("energized_copper_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENERGIZED_GOLD_INGOT = ITEMS.register("energized_gold_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ENERGIZED_COPPER_PLATE = ITEMS.register("energized_copper_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENERGIZED_GOLD_PLATE = ITEMS.register("energized_gold_plate",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ENERGIZED_COPPER_WIRE = ITEMS.register("energized_copper_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENERGIZED_GOLD_WIRE = ITEMS.register("energized_gold_wire",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> STONE_PEBBLE = ITEMS.register("stone_pebble",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> RAW_TIN = ITEMS.register("raw_tin",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TIN_DUST = ITEMS.register("tin_dust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.register("copper_dust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IRON_DUST = ITEMS.register("iron_dust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_DUST = ITEMS.register("gold_dust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TIN_NUGGET = ITEMS.register("tin_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TIN_PLATE = ITEMS.register("tin_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.register("copper_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IRON_PLATE = ITEMS.register("iron_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_PLATE = ITEMS.register("gold_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRASS_PLATE = ITEMS.register("brass_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_PLATE = ITEMS.register("diamond_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_PLATE = ITEMS.register("netherite_plate",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ZINC_ALLOY = ITEMS.register("zinc_alloy",
            () -> new ZincAlloyItem(new Item.Properties()));
    public static final DeferredItem<Item> ANDESITE_ALLOY = ITEMS.register("andesite_alloy",
            () -> new AndesiteAlloyItem(new Item.Properties()));

    public static final DeferredItem<Item> REDSTONE_ALLOY_INGOT = ITEMS.register("redstone_alloy_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ADVANCED_ALLOY_INGOT = ITEMS.register("advanced_alloy_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ADVANCED_ALLOY_PLATE = ITEMS.register("advanced_alloy_plate",
            () -> new Item(new Item.Properties()));

    //fan blades
    public static final DeferredItem<Item> BASIC_BLADE = ITEMS.register("basic_blade",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ADVANCED_BLADE = ITEMS.register("advanced_blade",
            () -> new Item(new Item.Properties()));

    //machine parts
    public static final DeferredItem<Item> GRATE = ITEMS.register("grate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WISK = ITEMS.register("wisk",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAM = ITEMS.register("ram",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EMPTY_COIL = ITEMS.register("empty_coil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_COIL = ITEMS.register("copper_coil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_COIL = ITEMS.register("gold_coil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_COIL = ITEMS.register("diamond_coil",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_GEAR = ITEMS.register("iron_gear",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_ROD = ITEMS.register("iron_rod",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TIN_WIRE = ITEMS.register("tin_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_WIRE = ITEMS.register("copper_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_WIRE = ITEMS.register("gold_wire",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SAWDUST = ITEMS.register("sawdust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CHARCOAL_DUST = ITEMS.register("charcoal_dust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BASIC_FERTILIZER = ITEMS.register("basic_fertilizer",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOOD_FERTILIZER = ITEMS.register("good_fertilizer",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ADVANCED_FERTILIZER = ITEMS.register("advanced_fertilizer",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> RAW_GEAR_PRESS_MOLD = ITEMS.register("raw_gear_press_mold",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_ROD_PRESS_MOLD = ITEMS.register("raw_rod_press_mold",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_WIRE_PRESS_MOLD = ITEMS.register("raw_wire_press_mold",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GEAR_PRESS_MOLD = ITEMS.register("gear_press_mold",
            () -> new Item(new Item.Properties().durability(2000)));
    public static final DeferredItem<Item> ROD_PRESS_MOLD = ITEMS.register("rod_press_mold",
            () -> new Item(new Item.Properties().durability(2000)));
    public static final DeferredItem<Item> WIRE_PRESS_MOLD = ITEMS.register("wire_press_mold",
            () -> new Item(new Item.Properties().durability(2000)));

    public static final DeferredItem<Item> BASIC_SOLAR_CELL = ITEMS.register("basic_solar_cell",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ADVANCED_SOLAR_CELL = ITEMS.register("advanced_solar_cell",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> REINFORCED_ADVANCED_SOLAR_CELL = ITEMS.register("reinforced_advanced_solar_cell",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BASIC_CIRCUIT = ITEMS.register("basic_circuit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ADVANCED_CIRCUIT = ITEMS.register("advanced_circuit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PROCESSING_UNIT = ITEMS.register("processing_unit",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TELEPORTER_MATRIX = ITEMS.register("teleporter_matrix",
            () -> new TeleporterMatrixItem(new Item.Properties()));
    public static final DeferredItem<Item> TELEPORTER_PROCESSING_UNIT = ITEMS.register("teleporter_processing_unit",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BASIC_UPGRADE_MODULE = ITEMS.register("basic_upgrade_module",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ADVANCED_UPGRADE_MODULE = ITEMS.register("advanced_upgrade_module",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> REINFORCED_ADVANCED_UPGRADE_MODULE = ITEMS.register("reinforced_advanced_upgrade_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SPEED_UPGRADE_MODULE_1 = ITEMS.register("speed_upgrade_module_1",
            () -> new SpeedUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> SPEED_UPGRADE_MODULE_2 = ITEMS.register("speed_upgrade_module_2",
            () -> new SpeedUpgradeModuleItem(new Item.Properties(), 2));
    public static final DeferredItem<Item> SPEED_UPGRADE_MODULE_3 = ITEMS.register("speed_upgrade_module_3",
            () -> new SpeedUpgradeModuleItem(new Item.Properties(), 3));
    public static final DeferredItem<Item> SPEED_UPGRADE_MODULE_4 = ITEMS.register("speed_upgrade_module_4",
            () -> new SpeedUpgradeModuleItem(new Item.Properties(), 4));
    public static final DeferredItem<Item> SPEED_UPGRADE_MODULE_5 = ITEMS.register("speed_upgrade_module_5",
            () -> new SpeedUpgradeModuleItem(new Item.Properties(), 5));

    public static final DeferredItem<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_1 = ITEMS.register("energy_efficiency_upgrade_module_1",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_2 = ITEMS.register("energy_efficiency_upgrade_module_2",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties(), 2));
    public static final DeferredItem<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_3 = ITEMS.register("energy_efficiency_upgrade_module_3",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties(), 3));
    public static final DeferredItem<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_4 = ITEMS.register("energy_efficiency_upgrade_module_4",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties(), 4));
    public static final DeferredItem<Item> ENERGY_EFFICIENCY_UPGRADE_MODULE_5 = ITEMS.register("energy_efficiency_upgrade_module_5",
            () -> new EnergyEfficiencyUpgradeModuleItem(new Item.Properties(), 5));

    public static final DeferredItem<Item> ENERGY_CAPACITY_UPGRADE_MODULE_1 = ITEMS.register("energy_capacity_upgrade_module_1",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> ENERGY_CAPACITY_UPGRADE_MODULE_2 = ITEMS.register("energy_capacity_upgrade_module_2",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties(), 2));
    public static final DeferredItem<Item> ENERGY_CAPACITY_UPGRADE_MODULE_3 = ITEMS.register("energy_capacity_upgrade_module_3",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties(), 3));
    public static final DeferredItem<Item> ENERGY_CAPACITY_UPGRADE_MODULE_4 = ITEMS.register("energy_capacity_upgrade_module_4",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties(), 4));
    public static final DeferredItem<Item> ENERGY_CAPACITY_UPGRADE_MODULE_5 = ITEMS.register("energy_capacity_upgrade_module_5",
            () -> new EnergyCapacityUpgradeModuleItem(new Item.Properties(), 5));

    public static final DeferredItem<Item> DURATION_UPGRADE_MODULE_1 = ITEMS.register("duration_upgrade_module_1",
            () -> new DurationUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> DURATION_UPGRADE_MODULE_2 = ITEMS.register("duration_upgrade_module_2",
            () -> new DurationUpgradeModuleItem(new Item.Properties(), 2));
    public static final DeferredItem<Item> DURATION_UPGRADE_MODULE_3 = ITEMS.register("duration_upgrade_module_3",
            () -> new DurationUpgradeModuleItem(new Item.Properties(), 3));
    public static final DeferredItem<Item> DURATION_UPGRADE_MODULE_4 = ITEMS.register("duration_upgrade_module_4",
            () -> new DurationUpgradeModuleItem(new Item.Properties(), 4));
    public static final DeferredItem<Item> DURATION_UPGRADE_MODULE_5 = ITEMS.register("duration_upgrade_module_5",
            () -> new DurationUpgradeModuleItem(new Item.Properties(), 5));
    public static final DeferredItem<Item> DURATION_UPGRADE_MODULE_6 = ITEMS.register("duration_upgrade_module_6",
            () -> new DurationUpgradeModuleItem(new Item.Properties(), 6));

    public static final DeferredItem<Item> RANGE_UPGRADE_MODULE_1 = ITEMS.register("range_upgrade_module_1",
            () -> new RangeUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> RANGE_UPGRADE_MODULE_2 = ITEMS.register("range_upgrade_module_2",
            () -> new RangeUpgradeModuleItem(new Item.Properties(), 2));
    public static final DeferredItem<Item> RANGE_UPGRADE_MODULE_3 = ITEMS.register("range_upgrade_module_3",
            () -> new RangeUpgradeModuleItem(new Item.Properties(), 3));

    public static final DeferredItem<Item> EXTRACTION_DEPTH_UPGRADE_MODULE_1 = ITEMS.register("extraction_depth_upgrade_module_1",
            () -> new ExtractionDepthUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> EXTRACTION_DEPTH_UPGRADE_MODULE_2 = ITEMS.register("extraction_depth_upgrade_module_2",
            () -> new ExtractionDepthUpgradeModuleItem(new Item.Properties(), 2));
    public static final DeferredItem<Item> EXTRACTION_DEPTH_UPGRADE_MODULE_3 = ITEMS.register("extraction_depth_upgrade_module_3",
            () -> new ExtractionDepthUpgradeModuleItem(new Item.Properties(), 3));
    public static final DeferredItem<Item> EXTRACTION_DEPTH_UPGRADE_MODULE_4 = ITEMS.register("extraction_depth_upgrade_module_4",
            () -> new ExtractionDepthUpgradeModuleItem(new Item.Properties(), 4));
    public static final DeferredItem<Item> EXTRACTION_DEPTH_UPGRADE_MODULE_5 = ITEMS.register("extraction_depth_upgrade_module_5",
            () -> new ExtractionDepthUpgradeModuleItem(new Item.Properties(), 5));

    public static final DeferredItem<Item> EXTRACTION_RANGE_UPGRADE_MODULE_1 = ITEMS.register("extraction_range_upgrade_module_1",
            () -> new ExtractionRangeUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> EXTRACTION_RANGE_UPGRADE_MODULE_2 = ITEMS.register("extraction_range_upgrade_module_2",
            () -> new ExtractionRangeUpgradeModuleItem(new Item.Properties(), 2));
    public static final DeferredItem<Item> EXTRACTION_RANGE_UPGRADE_MODULE_3 = ITEMS.register("extraction_range_upgrade_module_3",
            () -> new ExtractionRangeUpgradeModuleItem(new Item.Properties(), 3));
    public static final DeferredItem<Item> EXTRACTION_RANGE_UPGRADE_MODULE_4 = ITEMS.register("extraction_range_upgrade_module_4",
            () -> new ExtractionRangeUpgradeModuleItem(new Item.Properties(), 4));
    public static final DeferredItem<Item> EXTRACTION_RANGE_UPGRADE_MODULE_5 = ITEMS.register("extraction_range_upgrade_module_5",
            () -> new ExtractionRangeUpgradeModuleItem(new Item.Properties(), 5));

    public static final DeferredItem<Item> BLAST_FURNACE_UPGRADE_MODULE = ITEMS.register("blast_furnace_upgrade_module",
            () -> new FurnaceModeUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> SMOKER_UPGRADE_MODULE = ITEMS.register("smoker_upgrade_module",
            () -> new FurnaceModeUpgradeModuleItem(new Item.Properties(), 2));

    public static final DeferredItem<Item> MOON_LIGHT_UPGRADE_MODULE_1 = ITEMS.register("moon_light_upgrade_module_1",
            () -> new MoonLightUpgradeModuleItem(new Item.Properties(), 1));
    public static final DeferredItem<Item> MOON_LIGHT_UPGRADE_MODULE_2 = ITEMS.register("moon_light_upgrade_module_2",
            () -> new MoonLightUpgradeModuleItem(new Item.Properties(), 2));
    public static final DeferredItem<Item> MOON_LIGHT_UPGRADE_MODULE_3 = ITEMS.register("moon_light_upgrade_module_3",
            () -> new MoonLightUpgradeModuleItem(new Item.Properties(), 3));

    public static final DeferredItem<Item> ENERJOLT_BOOK = ITEMS.register("enerjolt_book",
            () -> new EnerjoltBookItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> CABLE_INSULATOR = ITEMS.register("cable_insulator",
            () -> new CableInsulatorItem(new Item.Properties()));

    public static final DeferredItem<Item> CHARCOAL_FILTER = ITEMS.register("charcoal_filter",
            () -> new Item(new Item.Properties().durability(200)));

    public static final DeferredItem<Item> SAW_BLADE = ITEMS.register("saw_blade",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CRYSTAL_MATRIX = ITEMS.register("crystal_matrix",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ENERGIZED_CRYSTAL_MATRIX = ITEMS.register("energized_crystal_matrix",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> INVENTORY_COAL_ENGINE = ITEMS.register("inventory_coal_engine",
            () -> new InventoryCoalEngineItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> INVENTORY_CHARGER = ITEMS.register("inventory_charger",
            () -> new InventoryChargerItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> INVENTORY_TELEPORTER = ITEMS.register("inventory_teleporter",
            () -> new InventoryTeleporterItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BATTERY_1 = ITEMS.register("battery_1",
            () -> new BatteryItem(BatteryTier.BATTERY_1));
    public static final DeferredItem<Item> BATTERY_2 = ITEMS.register("battery_2",
            () -> new BatteryItem(BatteryTier.BATTERY_2));
    public static final DeferredItem<Item> BATTERY_3 = ITEMS.register("battery_3",
            () -> new BatteryItem(BatteryTier.BATTERY_3));
    public static final DeferredItem<Item> BATTERY_4 = ITEMS.register("battery_4",
            () -> new BatteryItem(BatteryTier.BATTERY_4));
    public static final DeferredItem<Item> BATTERY_5 = ITEMS.register("battery_5",
            () -> new BatteryItem(BatteryTier.BATTERY_5));
    public static final DeferredItem<Item> BATTERY_6 = ITEMS.register("battery_6",
            () -> new BatteryItem(BatteryTier.BATTERY_6));
    public static final DeferredItem<Item> BATTERY_7 = ITEMS.register("battery_7",
            () -> new BatteryItem(BatteryTier.BATTERY_7));
    public static final DeferredItem<Item> BATTERY_8 = ITEMS.register("battery_8",
            () -> new BatteryItem(BatteryTier.BATTERY_8));
    public static final DeferredItem<Item> CREATIVE_BATTERY = ITEMS.register("creative_battery",
            () -> new CreativeBatteryItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ENERGY_ANALYZER = ITEMS.register("energy_analyzer",
            () -> new EnergyAnalyzerItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> FLUID_ANALYZER = ITEMS.register("fluid_analyzer",
            () -> new FluidAnalyzerItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> WOODEN_HAMMER = ITEMS.register("wooden_hammer",
            () -> new HammerItem(Tiers.WOOD, new Item.Properties()));
    public static final DeferredItem<Item> STONE_HAMMER = ITEMS.register("stone_hammer",
            () -> new HammerItem(Tiers.STONE, new Item.Properties()));
    public static final DeferredItem<Item> IRON_HAMMER = ITEMS.register("iron_hammer",
            () -> new HammerItem(Tiers.IRON, new Item.Properties()));
    public static final DeferredItem<Item> GOLDEN_HAMMER = ITEMS.register("golden_hammer",
            () -> new HammerItem(Tiers.GOLD, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_HAMMER = ITEMS.register("diamond_hammer",
            () -> new HammerItem(Tiers.DIAMOND, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_HAMMER = ITEMS.register("netherite_hammer",
            () -> new HammerItem(Tiers.NETHERITE, new Item.Properties().fireResistant()));

    public static final DeferredItem<Item> CUTTER = ITEMS.register("cutter",
            () -> new CutterItem(Tiers.IRON, new Item.Properties()));

    public static final DeferredItem<Item> WRENCH = ITEMS.register("wrench",
            () -> new WrenchItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BATTERY_BOX_MINECART = ITEMS.register("battery_box_minecart",
            () -> new BatteryBoxMinecartItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> ADVANCED_BATTERY_BOX_MINECART = ITEMS.register("advanced_battery_box_minecart",
            () -> new AdvancedBatteryBoxMinecartItem(new Item.Properties().stacksTo(1)));


    //armor items
    public static final DeferredItem<ArmorItem> ZINC_HELMET = ITEMS.register("zinc_helmet",
            () -> new ModArmorItem(ModArmorMaterials.ZINC_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(19))));
    public static final DeferredItem<ArmorItem> ZINC_CHESTPLATE = ITEMS.register("zinc_chestplate",
            () -> new ArmorItem(ModArmorMaterials.ZINC_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(19))));
    public static final DeferredItem<ArmorItem> ZINC_LEGGINGS = ITEMS.register("zinc_leggings",
            () -> new ArmorItem(ModArmorMaterials.ZINC_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(19))));
    public static final DeferredItem<ArmorItem> ZINC_BOOTS = ITEMS.register("zinc_boots",
            () -> new ArmorItem(ModArmorMaterials.ZINC_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(19))));

    public static final DeferredItem<Item> ZINC_HORSE_ARMOR = ITEMS.register("zinc_horse_armor",
            () -> new AnimalArmorItem(ModArmorMaterials.ZINC_ARMOR_MATERIAL, AnimalArmorItem.BodyType.EQUESTRIAN,
                    false, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ZINC_SMITHING_TEMPLATE = ITEMS.register("zinc_armor_trim_smithing_template",
            () -> SmithingTemplateItem.createArmorTrimTemplate(ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "zinc")));

    public static final DeferredItem<Item> ZINC_BOW = ITEMS.register("zinc_bow",
            () -> new BowItem(new Item.Properties().durability(500)));
    public static final DeferredItem<SwordItem> ZINC_SWORD = ITEMS.register("zinc_sword",
            () -> new SwordItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.ZINC, 5, -2.4f))));
    public static final DeferredItem<PickaxeItem> ZINC_PICKAXE = ITEMS.register("zinc_pickaxe",
            () -> new PickaxeItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.ZINC, 1.0F, -2.8f))));
    public static final DeferredItem<ShovelItem> ZINC_SHOVEL = ITEMS.register("zinc_shovel",
            () -> new ShovelItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.ZINC, 1.5F, -3.0f))));
    public static final DeferredItem<AxeItem> ZINC_AXE = ITEMS.register("zinc_axe",
            () -> new AxeItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.ZINC, 6.0F, -3.2f))));
    public static final DeferredItem<HoeItem> ZINC_HOE = ITEMS.register("zinc_hoe",
            () -> new HoeItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.ZINC, 0F, -3.0f))));

    public static final DeferredItem<Item> CHISEL = ITEMS.register("chisel",
            () -> new ChiselItem(new Item.Properties().durability(32)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}

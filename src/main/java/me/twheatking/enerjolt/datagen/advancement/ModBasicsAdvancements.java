package me.twheatking.enerjolt.datagen.advancement;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.component.EnerjoltDataComponentTypes;
import me.twheatking.enerjolt.item.EnerjoltItems;
import me.twheatking.enerjolt.registry.tags.CommonItemTags;
import me.twheatking.enerjolt.registry.tags.EnerjoltItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ModBasicsAdvancements implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                         ExistingFileHelper existingFileHelper) {
        AdvancementHolder energizedPowerBasics = Advancement.Builder.advancement().
                display(
                        Items.COPPER_INGOT,
                        Component.translatable("advancements.enerjolt.enerjolt_basics.title"),
                        Component.translatable("advancements.enerjolt.enerjolt_basics.description"),
                        EJOLTAPI.id("textures/block/basic_machine_frame_top.png"),
                        AdvancementType.TASK,
                        true,
                        false,
                        false
                ).
                addCriterion("has_the_item",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                                Tags.Items.INGOTS_COPPER
                        ))).
                save(advancementOutput, EJOLTAPI.id("main/basics/energizedpower_basics"), existingFileHelper);

        AdvancementHolder energizedPowerBook = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EnerjoltItems.ENERJOLT_BOOK, "energized_power_book", AdvancementType.TASK
        );

        AdvancementHolder pressMoldMaker = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EnerjoltBlocks.PRESS_MOLD_MAKER_ITEM, "press_mold_maker", AdvancementType.TASK
        );

        AdvancementHolder rawPressMolds = addAdvancement(
                advancementOutput, existingFileHelper, pressMoldMaker,
                EnerjoltItems.RAW_GEAR_PRESS_MOLD, "raw_press_molds", AdvancementType.TASK,
                EnerjoltItemTags.RAW_METAL_PRESS_MOLDS
        );

        AdvancementHolder pressMolds = addAdvancement(
                advancementOutput, existingFileHelper, rawPressMolds,
                EnerjoltItems.GEAR_PRESS_MOLD, "press_molds", AdvancementType.TASK,
                EnerjoltItemTags.METAL_PRESS_MOLDS
        );

        AdvancementHolder alloyFurnace = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EnerjoltBlocks.ALLOY_FURNACE_ITEM, "alloy_furnace", AdvancementType.TASK
        );

        AdvancementHolder steelIngot = addAdvancement(
                advancementOutput, existingFileHelper, alloyFurnace,
                EnerjoltItems.STEEL_INGOT, "steel_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_STEEL
        );

        AdvancementHolder fastItemConveyorBelt = addAdvancement(
                advancementOutput, existingFileHelper, steelIngot,
                EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM, "fast_item_conveyor_belt", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltLoader = addAdvancement(
                advancementOutput, existingFileHelper, fastItemConveyorBelt,
                EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM, "fast_item_conveyor_belt_loader", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltSorter = addAdvancement(
                advancementOutput, existingFileHelper, fastItemConveyorBelt,
                EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM, "fast_item_conveyor_belt_sorter", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltSwitch = addAdvancement(
                advancementOutput, existingFileHelper, fastItemConveyorBelt,
                EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM, "fast_item_conveyor_belt_switch", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltSplitter = addAdvancement(
                advancementOutput, existingFileHelper, fastItemConveyorBelt,
                EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "fast_item_conveyor_belt_splitter", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltMerger = addAdvancement(
                advancementOutput, existingFileHelper, fastItemConveyorBelt,
                EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM, "fast_item_conveyor_belt_merger", AdvancementType.TASK
        );

        AdvancementHolder redstoneAlloyIngot = addAdvancement(
                advancementOutput, existingFileHelper, alloyFurnace,
                EnerjoltItems.REDSTONE_ALLOY_INGOT, "redstone_alloy_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_REDSTONE_ALLOY
        );

        AdvancementHolder wrench = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EnerjoltItems.WRENCH, "wrench", AdvancementType.TASK
        );

        AdvancementHolder hammer = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EnerjoltItems.IRON_HAMMER, "hammer", AdvancementType.TASK,
                CommonItemTags.TOOLS_HAMMERS
        );

        AdvancementHolder tinPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                EnerjoltItems.TIN_PLATE, "tin_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_TIN
        );

        AdvancementHolder copperPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                EnerjoltItems.COPPER_PLATE, "copper_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_COPPER
        );

        AdvancementHolder goldPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                EnerjoltItems.GOLD_PLATE, "gold_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_GOLD
        );

        AdvancementHolder ironPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                EnerjoltItems.IRON_PLATE, "iron_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_IRON
        );

        AdvancementHolder cutter = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EnerjoltItems.CUTTER, "cutter", AdvancementType.TASK,
                CommonItemTags.TOOLS_CUTTERS
        );

        AdvancementHolder tinWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                EnerjoltItems.TIN_WIRE, "tin_wire", AdvancementType.TASK,
                CommonItemTags.WIRES_TIN
        );

        AdvancementHolder goldWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                EnerjoltItems.GOLD_WIRE, "gold_wire", AdvancementType.TASK,
                CommonItemTags.WIRES_GOLD
        );

        AdvancementHolder copperWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                EnerjoltItems.COPPER_WIRE, "copper_wire", AdvancementType.TASK,
                CommonItemTags.WIRES_COPPER
        );

        AdvancementHolder basicCircuit = addAdvancement(
                advancementOutput, existingFileHelper, copperWire,
                EnerjoltItems.BASIC_CIRCUIT, "basic_circuit", AdvancementType.TASK
        );

        AdvancementHolder basicUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicCircuit,
                EnerjoltItems.BASIC_UPGRADE_MODULE, "basic_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EnerjoltItems.SPEED_UPGRADE_MODULE_1, "speed_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, speedUpgradeUpgradeModule1,
                EnerjoltItems.SPEED_UPGRADE_MODULE_2, "speed_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1, "energy_efficiency_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, energyEfficiencyUpgradeModule1,
                EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2, "energy_efficiency_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_1, "energy_capacity_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, energyCapacityUpgradeModule1,
                EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_2, "energy_capacity_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1, "extraction_depth_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, extractionDepthUpgradeModule1,
                EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2, "extraction_depth_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder extractionRangeUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_1, "extraction_range_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder extractionRangeUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, extractionRangeUpgradeModule1,
                EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_2, "extraction_range_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder blastFurnaceUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EnerjoltItems.BLAST_FURNACE_UPGRADE_MODULE, "blast_furnace_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder smokerUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EnerjoltItems.SMOKER_UPGRADE_MODULE, "smoker_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder moonLightUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EnerjoltItems.MOON_LIGHT_UPGRADE_MODULE_1, "moon_light_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBelt = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM, "item_conveyor_belt", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltLoader = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBelt,
                EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM, "item_conveyor_belt_loader", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltSorter = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM, "item_conveyor_belt_sorter", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltSwitch = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM, "item_conveyor_belt_switch", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltSplitter = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "item_conveyor_belt_splitter", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltMerger = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM, "item_conveyor_belt_merger", AdvancementType.TASK
        );

        AdvancementHolder itemSiloTiny = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EnerjoltBlocks.ITEM_SILO_TINY_ITEM, "item_silo_tiny", AdvancementType.TASK
        );

        AdvancementHolder itemSiloSmall = addAdvancement(
                advancementOutput, existingFileHelper, itemSiloTiny,
                EnerjoltBlocks.ITEM_SILO_SMALL_ITEM, "item_silo_small", AdvancementType.TASK
        );

        AdvancementHolder itemSiloMedium = addAdvancement(
                advancementOutput, existingFileHelper, itemSiloSmall,
                EnerjoltBlocks.ITEM_SILO_MEDIUM_ITEM, "item_silo_medium", AdvancementType.TASK
        );

        AdvancementHolder itemSiloLarge = addAdvancement(
                advancementOutput, existingFileHelper, itemSiloMedium,
                EnerjoltBlocks.ITEM_SILO_LARGE_ITEM, "item_silo_large", AdvancementType.TASK
        );

        AdvancementHolder itemSiloGiant = addAdvancement(
                advancementOutput, existingFileHelper, itemSiloLarge,
                EnerjoltBlocks.ITEM_SILO_GIANT_ITEM, "item_silo_giant", AdvancementType.TASK
        );

        AdvancementHolder ironFluidPipe = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EnerjoltBlocks.IRON_FLUID_PIPE_ITEM, "iron_fluid_pipe", AdvancementType.TASK
        );

        AdvancementHolder goldenFluidPipe = addAdvancement(
                advancementOutput, existingFileHelper, ironFluidPipe,
                EnerjoltBlocks.GOLDEN_FLUID_PIPE_ITEM, "golden_fluid_pipe", AdvancementType.TASK
        );

        AdvancementHolder fluidTankSmall = addAdvancement(
                advancementOutput, existingFileHelper, ironFluidPipe,
                EnerjoltBlocks.FLUID_TANK_SMALL_ITEM, "fluid_tank_small", AdvancementType.TASK
        );

        AdvancementHolder fluidTankMedium = addAdvancement(
                advancementOutput, existingFileHelper, fluidTankSmall,
                EnerjoltBlocks.FLUID_TANK_MEDIUM_ITEM, "fluid_tank_medium", AdvancementType.TASK
        );

        AdvancementHolder fluidTankLarge = addAdvancement(
                advancementOutput, existingFileHelper, fluidTankMedium,
                EnerjoltBlocks.FLUID_TANK_LARGE_ITEM, "fluid_tank_large", AdvancementType.TASK
        );

        AdvancementHolder drain = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EnerjoltBlocks.DRAIN_ITEM, "drain", AdvancementType.TASK
        );

        AdvancementHolder cableInsulator = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EnerjoltItems.CABLE_INSULATOR, "cable_insulator", AdvancementType.TASK
        );

        AdvancementHolder tinCable = addAdvancement(
                advancementOutput, existingFileHelper, cableInsulator,
                EnerjoltBlocks.TIN_CABLE_ITEM, "tin_cable", AdvancementType.TASK
        );

        AdvancementHolder copperCable = addAdvancement(
                advancementOutput, existingFileHelper, tinCable,
                EnerjoltBlocks.COPPER_CABLE_ITEM, "copper_cable", AdvancementType.TASK
        );

        AdvancementHolder goldCable = addAdvancement(
                advancementOutput, existingFileHelper, copperCable,
                EnerjoltBlocks.GOLD_CABLE_ITEM, "gold_cable", AdvancementType.TASK
        );

        AdvancementHolder lvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, tinCable,
                EnerjoltBlocks.LV_TRANSFORMER_1_TO_N_ITEM, "lv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                EnerjoltBlocks.LV_TRANSFORMER_1_TO_N_ITEM,
                                EnerjoltBlocks.LV_TRANSFORMER_3_TO_3_ITEM,
                                EnerjoltBlocks.LV_TRANSFORMER_N_TO_1_ITEM,
                                EnerjoltBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM
                        ).build()
                )
        );

        AdvancementHolder silicon = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EnerjoltItems.SILICON, "silicon", AdvancementType.TASK,
                CommonItemTags.SILICON
        );

        AdvancementHolder poweredLamp = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                EnerjoltBlocks.POWERED_LAMP_ITEM, "powered_lamp", AdvancementType.TASK
        );

        AdvancementHolder basicSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                EnerjoltItems.BASIC_SOLAR_CELL, "basic_solar_cell", AdvancementType.TASK
        );

        AdvancementHolder solarPanel1 = addAdvancement(
                advancementOutput, existingFileHelper, basicSolarCell,
                EnerjoltBlocks.SOLAR_PANEL_ITEM_1, "solar_panel_1", AdvancementType.TASK
        );

        AdvancementHolder solarPanel2 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel1,
                EnerjoltBlocks.SOLAR_PANEL_ITEM_2, "solar_panel_2", AdvancementType.TASK
        );

        AdvancementHolder solarPanel3 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel2,
                EnerjoltBlocks.SOLAR_PANEL_ITEM_3, "solar_panel_3", AdvancementType.TASK
        );

        AdvancementHolder basicMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM, "basic_machine_frame", AdvancementType.TASK
        );

        AdvancementHolder autoCrafter = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.AUTO_CRAFTER_ITEM, "auto_crafter", AdvancementType.TASK
        );

        AdvancementHolder crusher = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.CRUSHER_ITEM, "crusher", AdvancementType.TASK
        );

        AdvancementHolder sawmill = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.SAWMILL_ITEM, "sawmill", AdvancementType.TASK
        );

        AdvancementHolder compressor = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.COMPRESSOR_ITEM, "compressor", AdvancementType.TASK
        );

        AdvancementHolder autoStonecutter = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.AUTO_STONECUTTER_ITEM, "auto_stonecutter", AdvancementType.TASK
        );

        AdvancementHolder plantGrowthChamber = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.PLANT_GROWTH_CHAMBER_ITEM, "plant_growth_chamber", AdvancementType.TASK
        );

        AdvancementHolder blockPlacer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.BLOCK_PLACER_ITEM, "block_placer", AdvancementType.TASK
        );

        AdvancementHolder fluidFiller = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.FLUID_FILLER_ITEM, "fluid_filler", AdvancementType.TASK
        );

        AdvancementHolder fluidDrainer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.FLUID_DRAINER_ITEM, "fluid_drainer", AdvancementType.TASK
        );

        AdvancementHolder fluidPump = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.FLUID_PUMP_ITEM, "fluid_pump", AdvancementType.TASK
        );

        AdvancementHolder poweredFurnace = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.POWERED_FURNACE_ITEM, "powered_furnace", AdvancementType.TASK
        );

        AdvancementHolder pulverizer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.PULVERIZER_ITEM, "pulverizer", AdvancementType.TASK
        );

        AdvancementHolder charcoalFilter = addAdvancement(
                advancementOutput, existingFileHelper, pulverizer,
                EnerjoltItems.CHARCOAL_FILTER, "charcoal_filter", AdvancementType.TASK
        );

        AdvancementHolder coalEngine = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.COAL_ENGINE_ITEM, "coal_engine", AdvancementType.TASK
        );

        ItemStack inventoryCoalEngineIcon = new ItemStack(EnerjoltItems.INVENTORY_COAL_ENGINE.get());
        inventoryCoalEngineIcon.applyComponentsAndValidate(DataComponentPatch.builder().
                set(EnerjoltDataComponentTypes.ACTIVE.get(), true).
                set(EnerjoltDataComponentTypes.WORKING.get(), true).
                build());
        AdvancementHolder inventoryCoalEngine = addAdvancement(
                advancementOutput, existingFileHelper, coalEngine,
                inventoryCoalEngineIcon, "inventory_coal_engine", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(EnerjoltItems.INVENTORY_COAL_ENGINE)
        );

        AdvancementHolder heatGenerator = addAdvancement(
                advancementOutput, existingFileHelper, coalEngine,
                EnerjoltBlocks.HEAT_GENERATOR_ITEM, "heat_generator", AdvancementType.TASK
        );

        AdvancementHolder charger = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.CHARGER_ITEM, "charger", AdvancementType.TASK
        );

        AdvancementHolder minecartCharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                EnerjoltBlocks.MINECART_CHARGER_ITEM, "minecart_charger", AdvancementType.TASK
        );

        AdvancementHolder uncharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                EnerjoltBlocks.UNCHARGER_ITEM, "uncharger", AdvancementType.TASK
        );

        AdvancementHolder minecartUncharger = addAdvancement(
                advancementOutput, existingFileHelper, uncharger,
                EnerjoltBlocks.MINECART_UNCHARGER_ITEM, "minecart_uncharger", AdvancementType.TASK
        );

        AdvancementHolder inventoryCharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                EnerjoltItems.INVENTORY_CHARGER, "inventory_charger", AdvancementType.TASK
        );

        AdvancementHolder battery1 = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                EnerjoltItems.BATTERY_1, "battery_1", AdvancementType.TASK
        );

        AdvancementHolder battery2 = addAdvancement(
                advancementOutput, existingFileHelper, battery1,
                EnerjoltItems.BATTERY_2, "battery_2", AdvancementType.TASK
        );

        AdvancementHolder battery3 = addAdvancement(
                advancementOutput, existingFileHelper, battery2,
                EnerjoltItems.BATTERY_3, "battery_3", AdvancementType.TASK
        );

        AdvancementHolder energyAnalyzer = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                EnerjoltItems.ENERGY_ANALYZER, "energy_analyzer", AdvancementType.TASK
        );

        AdvancementHolder fluidAnalyzer = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                EnerjoltItems.FLUID_ANALYZER, "fluid_analyzer", AdvancementType.TASK
        );

        AdvancementHolder battery4 = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                EnerjoltItems.BATTERY_4, "battery_4", AdvancementType.TASK
        );

        AdvancementHolder battery5 = addAdvancement(
                advancementOutput, existingFileHelper, battery4,
                EnerjoltItems.BATTERY_5, "battery_5", AdvancementType.TASK
        );

        AdvancementHolder batteryBox = addAdvancement(
                advancementOutput, existingFileHelper, battery5,
                EnerjoltBlocks.BATTERY_BOX_ITEM, "battery_box", AdvancementType.TASK
        );

        AdvancementHolder batteryBoxMinecart = addAdvancement(
                advancementOutput, existingFileHelper, batteryBox,
                EnerjoltItems.BATTERY_BOX_MINECART, "battery_box_minecart", AdvancementType.TASK
        );

        AdvancementHolder metalPress = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EnerjoltBlocks.METAL_PRESS_ITEM, "metal_press", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBelt = addAdvancement(
                advancementOutput, existingFileHelper, metalPress,
                EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM, "express_item_conveyor_belt", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltLoader = addAdvancement(
                advancementOutput, existingFileHelper, expressItemConveyorBelt,
                EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM, "express_item_conveyor_belt_loader", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltSorter = addAdvancement(
                advancementOutput, existingFileHelper, expressItemConveyorBelt,
                EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM, "express_item_conveyor_belt_sorter", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltSwitch = addAdvancement(
                advancementOutput, existingFileHelper, expressItemConveyorBelt,
                EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM, "express_item_conveyor_belt_switch", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltSplitter = addAdvancement(
                advancementOutput, existingFileHelper, expressItemConveyorBelt,
                EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "express_item_conveyor_belt_splitter", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltMerger = addAdvancement(
                advancementOutput, existingFileHelper, expressItemConveyorBelt,
                EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM, "express_item_conveyor_belt_merger", AdvancementType.TASK
        );

        AdvancementHolder autoPressMoldMaker = addAdvancement(
                advancementOutput, existingFileHelper, metalPress,
                EnerjoltBlocks.AUTO_PRESS_MOLD_MAKER, "auto_press_mold_maker", AdvancementType.TASK
        );

        AdvancementHolder hardenedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, metalPress,
                EnerjoltBlocks.HARDENED_MACHINE_FRAME_ITEM, "hardened_machine_frame", AdvancementType.TASK
        );

        AdvancementHolder mvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EnerjoltBlocks.MV_TRANSFORMER_1_TO_N, "mv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                EnerjoltBlocks.MV_TRANSFORMER_1_TO_N_ITEM,
                                EnerjoltBlocks.MV_TRANSFORMER_3_TO_3_ITEM,
                                EnerjoltBlocks.MV_TRANSFORMER_N_TO_1_ITEM,
                                EnerjoltBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM
                        ).build()
                )
        );

        AdvancementHolder assemblingMachine = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EnerjoltBlocks.ASSEMBLING_MACHINE_ITEM, "assembling_machine", AdvancementType.TASK
        );

        AdvancementHolder inductionSmelter = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EnerjoltBlocks.INDUCTION_SMELTER_ITEM, "induction_smelter", AdvancementType.TASK
        );

        AdvancementHolder stoneLiquefier = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EnerjoltBlocks.STONE_LIQUEFIER_ITEM, "stone_liquefier", AdvancementType.TASK
        );

        AdvancementHolder stoneSolidifier = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EnerjoltBlocks.STONE_SOLIDIFIER_ITEM, "stone_solidifier", AdvancementType.TASK
        );

        AdvancementHolder fluidTransposer = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EnerjoltBlocks.FLUID_TRANSPOSER_ITEM, "fluid_transposer", AdvancementType.TASK
        );

        AdvancementHolder filtrationPlant = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EnerjoltBlocks.FILTRATION_PLANT_ITEM, "filtration_plant", AdvancementType.TASK
        );

        AdvancementHolder thermalGenerator = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EnerjoltBlocks.THERMAL_GENERATOR_ITEM, "thermal_generator", AdvancementType.TASK
        );
    }

    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon, advancementId, type, icon);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        trigger
                )));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type, trigger);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemStack icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return Advancement.Builder.advancement().parent(parent).
                display(
                        icon,
                        Component.translatable("advancements.enerjolt." + advancementId + ".title"),
                        Component.translatable("advancements.enerjolt." + advancementId + ".description"),
                        null,
                        type,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item", trigger).
                save(advancementOutput, EJOLTAPI.id("main/basics/" + advancementId), existingFileHelper);
    }
}

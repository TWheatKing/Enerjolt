package me.twheatking.enerjolt;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.behavior.ModBlockBehaviors;
import me.twheatking.enerjolt.block.entity.EnerjoltBlockEntities;
import me.twheatking.enerjolt.block.entity.renderer.FluidTankBlockEntityRenderer;
import me.twheatking.enerjolt.block.entity.renderer.ItemConveyorBeltBlockEntityRenderer;
import me.twheatking.enerjolt.component.EnerjoltDataComponentTypes;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.entity.EnerjoltEntityTypes;
import me.twheatking.enerjolt.fluid.EnerjoltFluidTypes;
import me.twheatking.enerjolt.fluid.EnerjoltFluids;
import me.twheatking.enerjolt.input.ModKeyBindings;
import me.twheatking.enerjolt.integration.cctweaked.EnerjoltCCTweakedIntegration;
import me.twheatking.enerjolt.integration.cctweaked.EnerjoltCCTweakedUtils;
import me.twheatking.enerjolt.item.*;
import me.twheatking.enerjolt.item.energy.EnerjoltEnergyItem;
import me.twheatking.enerjolt.item.energy.ItemCapabilityEnergy;
import me.twheatking.enerjolt.loading.EnerjoltBookReloadListener;
import me.twheatking.enerjolt.machine.tier.BatteryTier;
import me.twheatking.enerjolt.networking.ModMessages;
import me.twheatking.enerjolt.recipe.EnerjoltRecipes;
import me.twheatking.enerjolt.screen.*;
import me.twheatking.enerjolt.villager.EnerjoltVillager;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(EJOLTAPI.MOD_ID)
public class Enerjolt {
    // Add constants that other classes expect to reference
    public static final String MOD_ID = EJOLTAPI.MOD_ID;
    public static final Logger LOGGER = LogUtils.getLogger();

    public Enerjolt(IEventBus modEventBus) {
        ModConfigs.registerConfigs(true);

        EnerjoltDataComponentTypes.register(modEventBus);

        EnerjoltItems.register(modEventBus);
        EnerjoltBlocks.register(modEventBus);
        EnerjoltBlockEntities.register(modEventBus);
        EnerjoltRecipes.register(modEventBus);
        EnerjoltMenuTypes.register(modEventBus);
        EnerjoltVillager.register(modEventBus);
        EnerjoltEntityTypes.register(modEventBus);

        EnerjoltFluids.register(modEventBus);
        EnerjoltFluidTypes.register(modEventBus);

        ModBlockBehaviors.register();

        EnerjoltCreativeModeTab.register(modEventBus);

        modEventBus.addListener(this::onLoadComplete);
        modEventBus.addListener(this::addCreativeTab);
        modEventBus.addListener(this::registerCapabilities);

        modEventBus.addListener(ModMessages::register);

        LOGGER.info("Enerjolt mod initialized");
    }

    public void onLoadComplete(final FMLLoadCompleteEvent event) {
        if(EnerjoltCCTweakedUtils.isCCTweakedAvailable())
            EnerjoltCCTweakedIntegration.register();
    }

    private ItemStack getChargedItemStack(Item item, int energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(EnerjoltDataComponentTypes.ENERGY, energy);

        return itemStack;
    }

    private void addEmptyAndFullyChargedItem(BuildCreativeModeTabContentsEvent event, ItemLike item, int capacity) {
        event.accept(item);
        event.accept(getChargedItemStack(item.asItem(), capacity));
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if(event.getTab() == EnerjoltCreativeModeTab.ENERGIZED_POWER_TAB.get()) {
            event.accept(EnerjoltItems.ENERGIZED_POWER_BOOK);
            addEmptyAndFullyChargedItem(event, EnerjoltItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
            addEmptyAndFullyChargedItem(event, EnerjoltItems.FLUID_ANALYZER, FluidAnalyzerItem.ENERGY_CAPACITY);

            event.accept(EnerjoltItems.WOODEN_HAMMER);
            event.accept(EnerjoltItems.STONE_HAMMER);
            event.accept(EnerjoltItems.IRON_HAMMER);
            event.accept(EnerjoltItems.GOLDEN_HAMMER);
            event.accept(EnerjoltItems.DIAMOND_HAMMER);
            event.accept(EnerjoltItems.NETHERITE_HAMMER);

            event.accept(EnerjoltItems.CUTTER);

            event.accept(EnerjoltItems.WRENCH);

            event.accept(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM);
            event.accept(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM);
            event.accept(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM);

            event.accept(EnerjoltBlocks.ITEM_SILO_TINY_ITEM);
            event.accept(EnerjoltBlocks.ITEM_SILO_SMALL_ITEM);
            event.accept(EnerjoltBlocks.ITEM_SILO_MEDIUM_ITEM);
            event.accept(EnerjoltBlocks.ITEM_SILO_LARGE_ITEM);
            event.accept(EnerjoltBlocks.ITEM_SILO_GIANT_ITEM);
            event.accept(EnerjoltBlocks.CREATIVE_ITEM_SILO_ITEM);

            event.accept(EnerjoltBlocks.IRON_FLUID_PIPE_ITEM);
            event.accept(EnerjoltBlocks.GOLDEN_FLUID_PIPE_ITEM);

            event.accept(EnerjoltBlocks.TIN_CABLE_ITEM);
            event.accept(EnerjoltBlocks.COPPER_CABLE_ITEM);
            event.accept(EnerjoltBlocks.GOLD_CABLE_ITEM);
            event.accept(EnerjoltBlocks.ENERGIZED_COPPER_CABLE_ITEM);
            event.accept(EnerjoltBlocks.ENERGIZED_GOLD_CABLE_ITEM);
            event.accept(EnerjoltBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM);

            event.accept(EnerjoltBlocks.LV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EnerjoltBlocks.LV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EnerjoltBlocks.LV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EnerjoltBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM);
            event.accept(EnerjoltBlocks.MV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EnerjoltBlocks.MV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EnerjoltBlocks.MV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EnerjoltBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM);
            event.accept(EnerjoltBlocks.HV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EnerjoltBlocks.HV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EnerjoltBlocks.HV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EnerjoltBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM);
            event.accept(EnerjoltBlocks.EHV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EnerjoltBlocks.EHV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EnerjoltBlocks.EHV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EnerjoltBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM);

            event.accept(EnerjoltBlocks.PRESS_MOLD_MAKER_ITEM);
            event.accept(EnerjoltBlocks.ALLOY_FURNACE_ITEM);

            event.accept(EnerjoltBlocks.COAL_ENGINE_ITEM);
            event.accept(EnerjoltBlocks.HEAT_GENERATOR_ITEM);
            event.accept(EnerjoltBlocks.THERMAL_GENERATOR_ITEM);
            event.accept(EnerjoltBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(EnerjoltBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(EnerjoltBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(EnerjoltBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(EnerjoltBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(EnerjoltBlocks.SOLAR_PANEL_ITEM_5);
            event.accept(EnerjoltBlocks.SOLAR_PANEL_ITEM_6);

            event.accept(EnerjoltBlocks.BATTERY_BOX_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_BATTERY_BOX_ITEM);
            event.accept(EnerjoltBlocks.CREATIVE_BATTERY_BOX_ITEM);

            event.accept(EnerjoltBlocks.POWERED_LAMP_ITEM);
            event.accept(EnerjoltBlocks.POWERED_FURNACE_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_POWERED_FURNACE_ITEM);
            event.accept(EnerjoltBlocks.AUTO_CRAFTER_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_AUTO_CRAFTER_ITEM);
            event.accept(EnerjoltBlocks.CRUSHER_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_CRUSHER_ITEM);
            event.accept(EnerjoltBlocks.PULVERIZER_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_PULVERIZER_ITEM);
            event.accept(EnerjoltBlocks.SAWMILL_ITEM);
            event.accept(EnerjoltBlocks.COMPRESSOR_ITEM);
            event.accept(EnerjoltBlocks.METAL_PRESS_ITEM);
            event.accept(EnerjoltBlocks.AUTO_PRESS_MOLD_MAKER_ITEM);
            event.accept(EnerjoltBlocks.AUTO_STONECUTTER_ITEM);
            event.accept(EnerjoltBlocks.ASSEMBLING_MACHINE_ITEM);
            event.accept(EnerjoltBlocks.INDUCTION_SMELTER_ITEM);
            event.accept(EnerjoltBlocks.PLANT_GROWTH_CHAMBER_ITEM);
            event.accept(EnerjoltBlocks.STONE_LIQUEFIER_ITEM);
            event.accept(EnerjoltBlocks.STONE_SOLIDIFIER_ITEM);
            event.accept(EnerjoltBlocks.FILTRATION_PLANT_ITEM);
            event.accept(EnerjoltBlocks.FLUID_TRANSPOSER_ITEM);
            event.accept(EnerjoltBlocks.BLOCK_PLACER_ITEM);
            event.accept(EnerjoltBlocks.FLUID_TANK_SMALL_ITEM);
            event.accept(EnerjoltBlocks.FLUID_TANK_MEDIUM_ITEM);
            event.accept(EnerjoltBlocks.FLUID_TANK_LARGE_ITEM);
            event.accept(EnerjoltBlocks.CREATIVE_FLUID_TANK_ITEM);
            event.accept(EnerjoltBlocks.FLUID_FILLER_ITEM);
            event.accept(EnerjoltBlocks.FLUID_DRAINER_ITEM);
            event.accept(EnerjoltBlocks.FLUID_PUMP_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_FLUID_PUMP_ITEM);
            event.accept(EnerjoltBlocks.DRAIN_ITEM);
            event.accept(EnerjoltBlocks.CHARGER_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_CHARGER_ITEM);
            event.accept(EnerjoltBlocks.UNCHARGER_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_UNCHARGER_ITEM);
            event.accept(EnerjoltBlocks.MINECART_CHARGER_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_MINECART_CHARGER_ITEM);
            event.accept(EnerjoltBlocks.MINECART_UNCHARGER_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_MINECART_UNCHARGER_ITEM);

            event.accept(EnerjoltBlocks.ENERGIZER_ITEM);
            event.accept(EnerjoltBlocks.CHARGING_STATION_ITEM);
            event.accept(EnerjoltBlocks.CRYSTAL_GROWTH_CHAMBER);

            event.accept(EnerjoltBlocks.WEATHER_CONTROLLER_ITEM);
            event.accept(EnerjoltBlocks.TIME_CONTROLLER_ITEM);
            event.accept(EnerjoltBlocks.TELEPORTER_ITEM);

            addEmptyAndFullyChargedItem(event, EnerjoltItems.INVENTORY_COAL_ENGINE, InventoryCoalEngineItem.CAPACITY);
            event.accept(EnerjoltItems.INVENTORY_CHARGER);

            addEmptyAndFullyChargedItem(event, EnerjoltItems.INVENTORY_TELEPORTER, InventoryTeleporterItem.CAPACITY);

            addEmptyAndFullyChargedItem(event, EnerjoltItems.BATTERY_1, BatteryTier.BATTERY_1.getCapacity());
            addEmptyAndFullyChargedItem(event, EnerjoltItems.BATTERY_2, BatteryTier.BATTERY_2.getCapacity());
            addEmptyAndFullyChargedItem(event, EnerjoltItems.BATTERY_3, BatteryTier.BATTERY_3.getCapacity());
            addEmptyAndFullyChargedItem(event, EnerjoltItems.BATTERY_4, BatteryTier.BATTERY_4.getCapacity());
            addEmptyAndFullyChargedItem(event, EnerjoltItems.BATTERY_5, BatteryTier.BATTERY_5.getCapacity());
            addEmptyAndFullyChargedItem(event, EnerjoltItems.BATTERY_6, BatteryTier.BATTERY_6.getCapacity());
            addEmptyAndFullyChargedItem(event, EnerjoltItems.BATTERY_7, BatteryTier.BATTERY_7.getCapacity());
            addEmptyAndFullyChargedItem(event, EnerjoltItems.BATTERY_8, BatteryTier.BATTERY_8.getCapacity());
            event.accept(EnerjoltItems.CREATIVE_BATTERY);

            event.accept(EnerjoltItems.BATTERY_BOX_MINECART);
            event.accept(EnerjoltItems.ADVANCED_BATTERY_BOX_MINECART);

            event.accept(EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(EnerjoltBlocks.HARDENED_MACHINE_FRAME_ITEM);
            event.accept(EnerjoltBlocks.ADVANCED_MACHINE_FRAME_ITEM);
            event.accept(EnerjoltBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(EnerjoltItems.BASIC_SOLAR_CELL);
            event.accept(EnerjoltItems.ADVANCED_SOLAR_CELL);
            event.accept(EnerjoltItems.REINFORCED_ADVANCED_SOLAR_CELL);

            event.accept(EnerjoltItems.BASIC_CIRCUIT);
            event.accept(EnerjoltItems.ADVANCED_CIRCUIT);
            event.accept(EnerjoltItems.PROCESSING_UNIT);

            event.accept(EnerjoltItems.TELEPORTER_PROCESSING_UNIT);
            event.accept(EnerjoltItems.TELEPORTER_MATRIX);

            event.accept(EnerjoltItems.BASIC_UPGRADE_MODULE);
            event.accept(EnerjoltItems.ADVANCED_UPGRADE_MODULE);
            event.accept(EnerjoltItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

            event.accept(EnerjoltItems.SPEED_UPGRADE_MODULE_1);
            event.accept(EnerjoltItems.SPEED_UPGRADE_MODULE_2);
            event.accept(EnerjoltItems.SPEED_UPGRADE_MODULE_3);
            event.accept(EnerjoltItems.SPEED_UPGRADE_MODULE_4);
            event.accept(EnerjoltItems.SPEED_UPGRADE_MODULE_5);

            event.accept(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
            event.accept(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
            event.accept(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
            event.accept(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
            event.accept(EnerjoltItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

            event.accept(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
            event.accept(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
            event.accept(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
            event.accept(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
            event.accept(EnerjoltItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

            event.accept(EnerjoltItems.DURATION_UPGRADE_MODULE_1);
            event.accept(EnerjoltItems.DURATION_UPGRADE_MODULE_2);
            event.accept(EnerjoltItems.DURATION_UPGRADE_MODULE_3);
            event.accept(EnerjoltItems.DURATION_UPGRADE_MODULE_4);
            event.accept(EnerjoltItems.DURATION_UPGRADE_MODULE_5);
            event.accept(EnerjoltItems.DURATION_UPGRADE_MODULE_6);

            event.accept(EnerjoltItems.RANGE_UPGRADE_MODULE_1);
            event.accept(EnerjoltItems.RANGE_UPGRADE_MODULE_2);
            event.accept(EnerjoltItems.RANGE_UPGRADE_MODULE_3);

            event.accept(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
            event.accept(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
            event.accept(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
            event.accept(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
            event.accept(EnerjoltItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

            event.accept(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_1);
            event.accept(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_2);
            event.accept(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_3);
            event.accept(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_4);
            event.accept(EnerjoltItems.EXTRACTION_RANGE_UPGRADE_MODULE_5);

            event.accept(EnerjoltItems.BLAST_FURNACE_UPGRADE_MODULE);
            event.accept(EnerjoltItems.SMOKER_UPGRADE_MODULE);

            event.accept(EnerjoltItems.MOON_LIGHT_UPGRADE_MODULE_1);
            event.accept(EnerjoltItems.MOON_LIGHT_UPGRADE_MODULE_2);
            event.accept(EnerjoltItems.MOON_LIGHT_UPGRADE_MODULE_3);

            event.accept(EnerjoltBlocks.SILICON_BLOCK_ITEM);
            event.accept(EnerjoltBlocks.TIN_BLOCK_ITEM);
            event.accept(EnerjoltBlocks.SAWDUST_BLOCK_ITEM);
            event.accept(EnerjoltItems.CABLE_INSULATOR);
            event.accept(EnerjoltItems.CHARCOAL_FILTER);
            event.accept(EnerjoltItems.SAW_BLADE);
            event.accept(EnerjoltItems.CRYSTAL_MATRIX);
            event.accept(EnerjoltItems.SAWDUST);
            event.accept(EnerjoltItems.CHARCOAL_DUST);
            event.accept(EnerjoltItems.BASIC_FERTILIZER);
            event.accept(EnerjoltItems.GOOD_FERTILIZER);
            event.accept(EnerjoltItems.ADVANCED_FERTILIZER);
            event.accept(EnerjoltItems.RAW_GEAR_PRESS_MOLD);
            event.accept(EnerjoltItems.RAW_ROD_PRESS_MOLD);
            event.accept(EnerjoltItems.RAW_WIRE_PRESS_MOLD);
            event.accept(EnerjoltItems.GEAR_PRESS_MOLD);
            event.accept(EnerjoltItems.ROD_PRESS_MOLD);
            event.accept(EnerjoltItems.WIRE_PRESS_MOLD);
            event.accept(EnerjoltItems.SILICON);
            event.accept(EnerjoltItems.TIN_DUST);
            event.accept(EnerjoltItems.COPPER_DUST);
            event.accept(EnerjoltItems.IRON_DUST);
            event.accept(EnerjoltItems.GOLD_DUST);
            event.accept(EnerjoltItems.TIN_NUGGET);
            event.accept(EnerjoltItems.TIN_INGOT);
            event.accept(EnerjoltItems.TIN_PLATE);
            event.accept(EnerjoltItems.COPPER_PLATE);
            event.accept(EnerjoltItems.IRON_PLATE);
            event.accept(EnerjoltItems.GOLD_PLATE);
            event.accept(EnerjoltItems.STEEL_INGOT);
            event.accept(EnerjoltItems.REDSTONE_ALLOY_INGOT);
            event.accept(EnerjoltItems.ADVANCED_ALLOY_INGOT);
            event.accept(EnerjoltItems.ADVANCED_ALLOY_PLATE);
            event.accept(EnerjoltItems.IRON_GEAR);
            event.accept(EnerjoltItems.IRON_ROD);
            event.accept(EnerjoltItems.TIN_WIRE);
            event.accept(EnerjoltItems.COPPER_WIRE);
            event.accept(EnerjoltItems.GOLD_WIRE);
            event.accept(EnerjoltItems.ENERGIZED_COPPER_INGOT);
            event.accept(EnerjoltItems.ENERGIZED_GOLD_INGOT);
            event.accept(EnerjoltItems.ENERGIZED_COPPER_PLATE);
            event.accept(EnerjoltItems.ENERGIZED_GOLD_PLATE);
            event.accept(EnerjoltItems.ENERGIZED_COPPER_WIRE);
            event.accept(EnerjoltItems.ENERGIZED_GOLD_WIRE);
            event.accept(EnerjoltItems.ENERGIZED_CRYSTAL_MATRIX);

            event.accept(EnerjoltFluids.DIRTY_WATER_BUCKET_ITEM);

            event.accept(EnerjoltItems.STONE_PEBBLE);

            event.accept(EnerjoltItems.RAW_TIN);
            event.accept(EnerjoltBlocks.TIN_ORE_ITEM);
            event.accept(EnerjoltBlocks.DEEPSLATE_TIN_ORE_ITEM);
            event.accept(EnerjoltBlocks.RAW_TIN_BLOCK_ITEM);
        }
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        //Items
        for(Item item: BuiltInRegistries.ITEM) {
            if(item instanceof EnerjoltEnergyItem enerjoltEnergyItem) {
                event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> {
                    return new ItemCapabilityEnergy(stack, enerjoltEnergyItem.getEnergyStorageProvider().apply(stack));
                }, item);
            }
        }

        //Block Entities
        EnerjoltBlockEntities.registerCapabilities(event);
    }

    @EventBusSubscriber(modid = EJOLTAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModConfigs.registerConfigs(false);

            event.enqueueWork(() -> {
                ItemProperties.registerGeneric(EJOLTAPI.id("active"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof ActivatableItem && ((ActivatableItem)item).isActive(itemStack))?1.f:0.f;
                });
                ItemProperties.registerGeneric(EJOLTAPI.id("working"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof WorkingItem && ((WorkingItem)item).isWorking(itemStack))?1.f:0.f;
                });
            });

            EntityRenderers.register(EnerjoltEntityTypes.BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                            ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));
            EntityRenderers.register(EnerjoltEntityTypes.ADVANCED_BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer<>(entity, new ModelLayerLocation(
                            ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));

            ItemBlockRenderTypes.setRenderLayer(EnerjoltFluids.DIRTY_WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(EnerjoltFluids.FLOWING_DIRTY_WATER.get(), RenderType.translucent());

            BlockEntityRenderers.register(EnerjoltBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
            BlockEntityRenderers.register(EnerjoltBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
            BlockEntityRenderers.register(EnerjoltBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
            BlockEntityRenderers.register(EnerjoltBlockEntities.FLUID_TANK_SMALL_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EnerjoltBlockEntities.FLUID_TANK_MEDIUM_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EnerjoltBlockEntities.FLUID_TANK_LARGE_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EnerjoltBlockEntities.CREATIVE_FLUID_TANK_ENTITY.get(), FluidTankBlockEntityRenderer::new);
        }

        @SubscribeEvent
        static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public int getTintColor() {
                    return EnerjoltFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getTintColor();
                }

                @Override
                public ResourceLocation getStillTexture() {
                    return EnerjoltFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getStillTexture();
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return EnerjoltFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getFlowingTexture();
                }

                @Override
                public @Nullable ResourceLocation getOverlayTexture() {
                    return EnerjoltFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getOverlayTexture();
                }

                @Override
                public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                    return EnerjoltFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getFogColor();
                }

                @Override
                public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                    RenderSystem.setShaderFogStart(.25f);
                    RenderSystem.setShaderFogEnd(3.f);
                }
            }, EnerjoltFluidTypes.DIRTY_WATER_FLUID_TYPE.get());
        }

        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            event.register(EnerjoltMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
            event.register(EnerjoltMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
            event.register(EnerjoltMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
            event.register(EnerjoltMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
            event.register(EnerjoltMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
            event.register(EnerjoltMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
            event.register(EnerjoltMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_AUTO_CRAFTER_MENU.get(), AdvancedAutoCrafterScreen::new);
            event.register(EnerjoltMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_CRUSHER_MENU.get(), AdvancedCrusherScreen::new);
            event.register(EnerjoltMenuTypes.PULVERIZER_MENU.get(), PulverizerScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_PULVERIZER_MENU.get(), AdvancedPulverizerScreen::new);
            event.register(EnerjoltMenuTypes.SAWMILL_MENU.get(), SawmillScreen::new);
            event.register(EnerjoltMenuTypes.COMPRESSOR_MENU.get(), CompressorScreen::new);
            event.register(EnerjoltMenuTypes.PLANT_GROWTH_CHAMBER_MENU.get(), PlantGrowthChamberScreen::new);
            event.register(EnerjoltMenuTypes.STONE_LIQUEFIER_MENU.get(), StoneLiquefierScreen::new);
            event.register(EnerjoltMenuTypes.STONE_SOLIDIFIER_MENU.get(), StoneSolidifierScreen::new);
            event.register(EnerjoltMenuTypes.FILTRATION_PLANT_MENU.get(), FiltrationPlantScreen::new);
            event.register(EnerjoltMenuTypes.FLUID_TRANSPOSER_MENU.get(), FluidTransposerScreen::new);
            event.register(EnerjoltMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
            event.register(EnerjoltMenuTypes.FLUID_FILLER_MENU.get(), FluidFillerScreen::new);
            event.register(EnerjoltMenuTypes.FLUID_DRAINER_MENU.get(), FluidDrainerScreen::new);
            event.register(EnerjoltMenuTypes.FLUID_PUMP_MENU.get(), FluidPumpScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_FLUID_PUMP_MENU.get(), AdvancedFluidPumpScreen::new);
            event.register(EnerjoltMenuTypes.DRAIN_MENU.get(), DrainScreen::new);
            event.register(EnerjoltMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_CHARGER_MENU.get(), AdvancedChargerScreen::new);
            event.register(EnerjoltMenuTypes.UNCHARGER_MENU.get(), UnchargerScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_UNCHARGER_MENU.get(), AdvancedUnchargerScreen::new);
            event.register(EnerjoltMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
            event.register(EnerjoltMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);
            event.register(EnerjoltMenuTypes.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_POWERED_FURNACE_MENU.get(), AdvancedPoweredFurnaceScreen::new);
            event.register(EnerjoltMenuTypes.WEATHER_CONTROLLER_MENU.get(), WeatherControllerScreen::new);
            event.register(EnerjoltMenuTypes.TIME_CONTROLLER_MENU.get(), TimeControllerScreen::new);
            event.register(EnerjoltMenuTypes.TELEPORTER_MENU.get(), TeleporterScreen::new);
            event.register(EnerjoltMenuTypes.LIGHTNING_GENERATOR_MENU.get(), LightningGeneratorScreen::new);
            event.register(EnerjoltMenuTypes.CHARGING_STATION_MENU.get(), ChargingStationScreen::new);
            event.register(EnerjoltMenuTypes.CRYSTAL_GROWTH_CHAMBER_MENU.get(), CrystalGrowthChamberScreen::new);
            event.register(EnerjoltMenuTypes.HEAT_GENERATOR_MENU.get(), HeatGeneratorScreen::new);
            event.register(EnerjoltMenuTypes.THERMAL_GENERATOR_MENU.get(), ThermalGeneratorScreen::new);
            event.register(EnerjoltMenuTypes.BATTERY_BOX_MENU.get(), BatteryBoxScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_BATTERY_BOX_MENU.get(), AdvancedBatteryBoxScreen::new);
            event.register(EnerjoltMenuTypes.CREATIVE_BATTERY_BOX_MENU.get(), CreativeBatteryBoxScreen::new);
            event.register(EnerjoltMenuTypes.MINECART_CHARGER_MENU.get(), MinecartChargerScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_MINECART_CHARGER_MENU.get(), AdvancedMinecartChargerScreen::new);
            event.register(EnerjoltMenuTypes.MINECART_UNCHARGER_MENU.get(), MinecartUnchargerScreen::new);
            event.register(EnerjoltMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU.get(), AdvancedMinecartUnchargerScreen::new);
            event.register(EnerjoltMenuTypes.SOLAR_PANEL_MENU_1.get(), SolarPanelScreen::new);
            event.register(EnerjoltMenuTypes.SOLAR_PANEL_MENU_2.get(), SolarPanelScreen::new);
            event.register(EnerjoltMenuTypes.SOLAR_PANEL_MENU_3.get(), SolarPanelScreen::new);
            event.register(EnerjoltMenuTypes.SOLAR_PANEL_MENU_4.get(), SolarPanelScreen::new);
            event.register(EnerjoltMenuTypes.SOLAR_PANEL_MENU_5.get(), SolarPanelScreen::new);
            event.register(EnerjoltMenuTypes.SOLAR_PANEL_MENU_6.get(), SolarPanelScreen::new);
            event.register(EnerjoltMenuTypes.LV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.LV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.LV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.CONFIGURABLE_LV_TRANSFORMER_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.MV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.MV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.MV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.CONFIGURABLE_MV_TRANSFORMER_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.HV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.HV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.HV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.CONFIGURABLE_HV_TRANSFORMER_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.CONFIGURABLE_EHV_TRANSFORMER_MENU.get(), TransformerScreen::new);
            event.register(EnerjoltMenuTypes.PRESS_MOLD_MAKER_MENU.get(), PressMoldMakerScreen::new);
            event.register(EnerjoltMenuTypes.ALLOY_FURNACE_MENU.get(), AlloyFurnaceScreen::new);
            event.register(EnerjoltMenuTypes.METAL_PRESS_MENU.get(), MetalPressScreen::new);
            event.register(EnerjoltMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU.get(), AutoPressMoldMakerScreen::new);
            event.register(EnerjoltMenuTypes.AUTO_STONECUTTER_MENU.get(), AutoStonecutterScreen::new);
            event.register(EnerjoltMenuTypes.ASSEMBLING_MACHINE_MENU.get(), AssemblingMachineScreen::new);
            event.register(EnerjoltMenuTypes.INDUCTION_SMELTER_MENU.get(), InductionSmelterScreen::new);
            event.register(EnerjoltMenuTypes.FLUID_TANK_SMALL.get(), FluidTankScreen::new);
            event.register(EnerjoltMenuTypes.FLUID_TANK_MEDIUM.get(), FluidTankScreen::new);
            event.register(EnerjoltMenuTypes.FLUID_TANK_LARGE.get(), FluidTankScreen::new);
            event.register(EnerjoltMenuTypes.CREATIVE_FLUID_TANK.get(), CreativeFluidTankScreen::new);
            event.register(EnerjoltMenuTypes.ITEM_SILO_TINY.get(), ItemSiloScreen::new);
            event.register(EnerjoltMenuTypes.ITEM_SILO_SMALL.get(), ItemSiloScreen::new);
            event.register(EnerjoltMenuTypes.ITEM_SILO_MEDIUM.get(), ItemSiloScreen::new);
            event.register(EnerjoltMenuTypes.ITEM_SILO_LARGE.get(), ItemSiloScreen::new);
            event.register(EnerjoltMenuTypes.ITEM_SILO_GIANT.get(), ItemSiloScreen::new);
            event.register(EnerjoltMenuTypes.CREATIVE_ITEM_SILO_MENU.get(), CreativeItemSiloScreen::new);

            event.register(EnerjoltMenuTypes.INVENTORY_CHARGER_MENU.get(), InventoryChargerScreen::new);
            event.register(EnerjoltMenuTypes.INVENTORY_TELEPORTER_MENU.get(), InventoryTeleporterScreen::new);

            event.register(EnerjoltMenuTypes.MINECART_BATTERY_BOX_MENU.get(), MinecartBatteryBoxScreen::new);
            event.register(EnerjoltMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU.get(), MinecartAdvancedBatteryBoxScreen::new);
        }

        @SubscribeEvent
        public static void loadBookPages(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new EnerjoltBookReloadListener());
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(ModKeyBindings.TELEPORTER_USE_KEY);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.*;
import me.twheatking.enerjolt.machine.tier.TransformerType;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;

public class ModBlockStateProvider extends BlockStateProvider {
    private ModelFile itemConveyorBeltFlatTemplate;
    private ModelFile itemConveyorBeltAscendingTemplate;
    private ModelFile itemConveyorBeltDescendingTemplate;

    private ModelFile fluidPipeCoreTemplate;
    private ModelFile fluidPipeSideConnectedTemplate;
    private ModelFile fluidPipeSideExtractTemplate;

    private ModelFile fluidTankTemplate;

    private ModelFile cableCoreTemplate;
    private ModelFile cableSideTemplate;

    private ModelFile solarPanelTemplate;

    private ModelFile singleSideTemplate;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EJOLTAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerTemplates();
        registerBlocks();
    }

    private void registerTemplates() {
        itemConveyorBeltFlatTemplate = models().getBuilder("item_conveyor_belt_flat_template").
                ao(false).
                element().from(0, 1, 0).to(16, 1, 16).
                face(Direction.DOWN).uvs(16, 0, 0, 16).texture("#belt").end().
                face(Direction.UP).uvs(16, 16, 0, 0).texture("#belt").end().end();
        itemConveyorBeltAscendingTemplate = models().getBuilder("item_conveyor_belt_ascending_template").
                ao(false).
                element().from(0, 9, 0).to(16, 9, 16).
                rotation().origin(8, 9, 8).axis(Direction.Axis.X).angle(-45.f).rescale(true).end().
                face(Direction.DOWN).uvs(16, 0, 0, 16).texture("#belt").end().
                face(Direction.UP).uvs(16, 16, 0, 0).texture("#belt").end().end();
        itemConveyorBeltDescendingTemplate = models().getBuilder("item_conveyor_belt_descending_template").
                ao(false).
                element().from(0, 9, 0).to(16, 9, 16).
                rotation().origin(8, 9, 8).axis(Direction.Axis.X).angle(-45.f).rescale(true).end().
                face(Direction.DOWN).uvs(0, 16, 16, 0).texture("#belt").end().
                face(Direction.UP).uvs(0, 0, 16, 16).texture("#belt").end().end();

        fluidPipeCoreTemplate = models().getExistingFile(
                EJOLTAPI.id("fluid_pipe_core_template"));
        fluidPipeSideConnectedTemplate = models().getExistingFile(
                EJOLTAPI.id("fluid_pipe_side_connected_template"));
        fluidPipeSideExtractTemplate = models().getExistingFile(
                EJOLTAPI.id("fluid_pipe_side_extract_template"));

        fluidTankTemplate = models().getExistingFile(
                EJOLTAPI.id("fluid_tank_template"));

        cableCoreTemplate = models().
                withExistingParent("cable_core_template", ModelProvider.BLOCK_FOLDER + "/thin_block").
                element().from(6, 6, 6).to(10, 10, 10).
                face(Direction.DOWN).uvs(0, 7, 4, 11).texture("#cable").end().
                face(Direction.UP).uvs(0, 7, 4, 11).texture("#cable").end().
                face(Direction.NORTH).uvs(0, 7, 4, 11).texture("#cable").end().
                face(Direction.SOUTH).uvs(0, 7, 4, 11).texture("#cable").end().
                face(Direction.WEST).uvs(0, 7, 4, 11).texture("#cable").end().
                face(Direction.EAST).uvs(0, 7, 4, 11).texture("#cable").end().
                end();

        cableSideTemplate = models().
                withExistingParent("cable_side_template", ModelProvider.BLOCK_FOLDER + "/thin_block").
                element().from(6, 6, 0).to(10, 10, 6).
                face(Direction.DOWN).uvs(0, 0, 4, 6).texture("#cable").end().
                face(Direction.UP).uvs(0, 0, 4, 6).texture("#cable").end().
                face(Direction.NORTH).uvs(0, 12, 4, 16).texture("#cable").end().
                face(Direction.SOUTH).uvs(0, 12, 4, 16).texture("#cable").end().
                face(Direction.WEST).uvs(5, 7, 11, 11).texture("#cable").end().
                face(Direction.EAST).uvs(5, 7, 11, 11).texture("#cable").end().
                end();

        solarPanelTemplate = models().
                withExistingParent("solar_panel_template", ModelProvider.BLOCK_FOLDER + "/thin_block").
                element().from(0, 0, 0).to(16, 4, 16).
                face(Direction.DOWN).uvs(0, 0, 16, 16).cullface(Direction.DOWN).texture("#side").end().
                face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").end().
                face(Direction.NORTH).uvs(0, 12, 16, 16).cullface(Direction.NORTH).texture("#side").end().
                face(Direction.SOUTH).uvs(0, 12, 16, 16).cullface(Direction.SOUTH).texture("#side").end().
                face(Direction.WEST).uvs(0, 12, 16, 16).cullface(Direction.WEST).texture("#side").end().
                face(Direction.EAST).uvs(0, 12, 16, 16).cullface(Direction.EAST).texture("#side").end().
                end();

        singleSideTemplate = models().getExistingFile(EJOLTAPI.id("single_side"));
    }

    private void registerBlocks() {
        // Ores and storage blocks
        cubeAllBlockWithItem(EnerjoltBlocks.ZINC_ORE);
        cubeAllBlockWithItem(EnerjoltBlocks.ZINC_DEEPSLATE_ORE);
        cubeAllBlockWithItem(EnerjoltBlocks.ZINC_END_ORE);
        cubeAllBlockWithItem(EnerjoltBlocks.ZINC_NETHER_ORE);
        cubeAllBlockWithItem(EnerjoltBlocks.WHEAT_INGOT_BLOCK);
        cubeAllBlockWithItem(EnerjoltBlocks.ZINC_BLOCK);

        // Zinc building blocks
        stairsBlock(EnerjoltBlocks.ZINC_STAIRS.get(), getBlockTexture(EnerjoltBlocks.ZINC_BLOCK));

        slabBlock(EnerjoltBlocks.ZINC_SLAB.get(), getBlockTexture(EnerjoltBlocks.ZINC_BLOCK), getBlockTexture(EnerjoltBlocks.ZINC_BLOCK));

        pressurePlateBlock(EnerjoltBlocks.ZINC_PRESSURE_PLATE.get(), getBlockTexture(EnerjoltBlocks.ZINC_BLOCK));

        buttonBlock(EnerjoltBlocks.ZINC_BUTTON.get(), getBlockTexture(EnerjoltBlocks.ZINC_BLOCK));

        fenceBlock(EnerjoltBlocks.ZINC_FENCE.get(), getBlockTexture(EnerjoltBlocks.ZINC_BLOCK));

        fenceGateBlock(EnerjoltBlocks.ZINC_FENCE_GATE.get(), getBlockTexture(EnerjoltBlocks.ZINC_BLOCK));

        wallBlock(EnerjoltBlocks.ZINC_WALL.get(), getBlockTexture(EnerjoltBlocks.ZINC_BLOCK));

        doorBlockWithRenderType(EnerjoltBlocks.ZINC_DOOR.get(), createModLoc("block/zinc_door_bottom"), createModLoc("block/zinc_door_top"), "cutout");

        trapdoorBlockWithRenderType(EnerjoltBlocks.ZINC_TRAPDOOR.get(), createModLoc("block/zinc_trapdoor"), true, "cutout");

        // Casings
        cubeAllBlockWithItem(EnerjoltBlocks.ZINC_CASING);
        cubeAllBlockWithItem(EnerjoltBlocks.ANDESITE_CASING);

        // Existing blocks
        cubeAllBlockWithItem(EnerjoltBlocks.SILICON_BLOCK);
        cubeAllBlockWithItem(EnerjoltBlocks.TIN_BLOCK);
        cubeAllBlockWithItem(EnerjoltBlocks.SAWDUST_BLOCK);
        cubeAllBlockWithItem(EnerjoltBlocks.TIN_ORE);
        cubeAllBlockWithItem(EnerjoltBlocks.DEEPSLATE_TIN_ORE);
        cubeAllBlockWithItem(EnerjoltBlocks.RAW_TIN_BLOCK);

        // Conveyor belts
        itemConveyorBeltBlockWithItem(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT);
        itemConveyorBeltBlockWithItem(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT);
        itemConveyorBeltBlockWithItem(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT);

        // Conveyor belt machines
        orientableSixDirsBlockWithBackItem(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER, false);
        orientableSixDirsBlockWithBackItem(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER, false);
        orientableSixDirsBlockWithBackItem(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER, false);

        orientableBlockWithItem(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER,
                cubeBlockModel(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER, "", "_top", "_top",
                        "_input", "_output_2", "_output_3", "_output_1"));
        orientableBlockWithItem(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER,
                cubeBlockModel(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER, "", "_top", "_top",
                        "_input", "_output_2", "_output_3", "_output_1"));
        orientableBlockWithItem(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER,
                cubeBlockModel(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER, "", "_top", "_top",
                        "_input", "_output_2", "_output_3", "_output_1"));

        activatableOrientableBlockWithItem(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH,
                cubeBlockModel(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH, "", "_top", "_top",
                        "_input", "_side", "_output_disabled", "_output_enabled"),
                cubeBlockModel(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH, "_powered", "_top", "_top",
                        "_input", "_side", "_output_enabled", "_output_disabled"),
                ItemConveyorBeltSwitchBlock.POWERED);
        activatableOrientableBlockWithItem(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH,
                cubeBlockModel(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH, "", "_top", "_top",
                        "_input", "_side", "_output_disabled", "_output_enabled"),
                cubeBlockModel(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH, "_powered", "_top", "_top",
                        "_input", "_side", "_output_enabled", "_output_disabled"),
                ItemConveyorBeltSwitchBlock.POWERED);
        activatableOrientableBlockWithItem(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH,
                cubeBlockModel(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH, "", "_top", "_top",
                        "_input", "_side", "_output_disabled", "_output_enabled"),
                cubeBlockModel(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH, "_powered", "_top", "_top",
                        "_input", "_side", "_output_enabled", "_output_disabled"),
                ItemConveyorBeltSwitchBlock.POWERED);

        orientableBlockWithItem(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER,
                cubeBlockModel(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER, "", "_top", "_top",
                        "_input", "_output", "_output", "_output"));
        orientableBlockWithItem(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER,
                cubeBlockModel(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER, "", "_top", "_top",
                        "_input", "_output", "_output", "_output"));
        orientableBlockWithItem(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER,
                cubeBlockModel(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER, "", "_top", "_top",
                        "_input", "_output", "_output", "_output"));

        orientableBlockWithItem(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER,
                cubeBlockModel(EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER, "", "_top", "_top",
                        "_output", "_input", "_input", "_input"));
        orientableBlockWithItem(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER,
                cubeBlockModel(EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER, "", "_top", "_top",
                        "_output", "_input", "_input", "_input"));
        orientableBlockWithItem(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER,
                cubeBlockModel(EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER, "", "_top", "_top",
                        "_output", "_input", "_input", "_input"));

        // Fluid systems
        fluidPipeBlockWithItem(EnerjoltBlocks.IRON_FLUID_PIPE);
        fluidPipeBlockWithItem(EnerjoltBlocks.GOLDEN_FLUID_PIPE);

        fluidTankBlockWithItem(EnerjoltBlocks.FLUID_TANK_SMALL);
        fluidTankBlockWithItem(EnerjoltBlocks.FLUID_TANK_MEDIUM);
        fluidTankBlockWithItem(EnerjoltBlocks.FLUID_TANK_LARGE);
        fluidTankBlockWithItem(EnerjoltBlocks.CREATIVE_FLUID_TANK);

        // Storage
        horizontalBlockWithItem(EnerjoltBlocks.ITEM_SILO_TINY, false);
        horizontalBlockWithItem(EnerjoltBlocks.ITEM_SILO_SMALL, false);
        horizontalBlockWithItem(EnerjoltBlocks.ITEM_SILO_MEDIUM, false);
        horizontalBlockWithItem(EnerjoltBlocks.ITEM_SILO_LARGE, false);
        horizontalBlockWithItem(EnerjoltBlocks.ITEM_SILO_GIANT, false);
        horizontalBlockWithItem(EnerjoltBlocks.CREATIVE_ITEM_SILO, false);

        // Cables
        cableBlockWithItem(EnerjoltBlocks.TIN_CABLE);
        cableBlockWithItem(EnerjoltBlocks.COPPER_CABLE);
        cableBlockWithItem(EnerjoltBlocks.GOLD_CABLE);
        cableBlockWithItem(EnerjoltBlocks.ENERGIZED_COPPER_CABLE);
        cableBlockWithItem(EnerjoltBlocks.ENERGIZED_GOLD_CABLE);
        cableBlockWithItem(EnerjoltBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE);

        // Transformers
        transformerBlockWithItem(EnerjoltBlocks.LV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(EnerjoltBlocks.LV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(EnerjoltBlocks.LV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(EnerjoltBlocks.MV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(EnerjoltBlocks.MV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(EnerjoltBlocks.MV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(EnerjoltBlocks.HV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(EnerjoltBlocks.HV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(EnerjoltBlocks.HV_TRANSFORMER_N_TO_1);
        transformerBlockWithItem(EnerjoltBlocks.EHV_TRANSFORMER_1_TO_N);
        transformerBlockWithItem(EnerjoltBlocks.EHV_TRANSFORMER_3_TO_3);
        transformerBlockWithItem(EnerjoltBlocks.EHV_TRANSFORMER_N_TO_1);

        configurableTransformerBlockWithItem(EnerjoltBlocks.CONFIGURABLE_LV_TRANSFORMER);
        configurableTransformerBlockWithItem(EnerjoltBlocks.CONFIGURABLE_MV_TRANSFORMER);
        configurableTransformerBlockWithItem(EnerjoltBlocks.CONFIGURABLE_HV_TRANSFORMER);
        configurableTransformerBlockWithItem(EnerjoltBlocks.CONFIGURABLE_EHV_TRANSFORMER);

        // Energy storage
        horizontalBlockWithItem(EnerjoltBlocks.BATTERY_BOX, true);
        horizontalBlockWithItem(EnerjoltBlocks.ADVANCED_BATTERY_BOX, true);
        horizontalBlockWithItem(EnerjoltBlocks.CREATIVE_BATTERY_BOX, true);

        // Processing machines
        horizontalTwoSideBlockWithItem(EnerjoltBlocks.PRESS_MOLD_MAKER, true);

        activatableOrientableBlockWithItem(EnerjoltBlocks.ALLOY_FURNACE,
                orientableBlockModel(EnerjoltBlocks.ALLOY_FURNACE, false),
                orientableOnBlockModel(EnerjoltBlocks.ALLOY_FURNACE, false),
                AlloyFurnaceBlock.LIT);

        horizontalTwoSideBlockWithItem(EnerjoltBlocks.AUTO_CRAFTER, true);
        horizontalTwoSideBlockWithItem(EnerjoltBlocks.ADVANCED_AUTO_CRAFTER, true);

        horizontalBlockWithItem(EnerjoltBlocks.CRUSHER, true);
        horizontalBlockWithItem(EnerjoltBlocks.ADVANCED_CRUSHER, true);

        horizontalBlockWithItem(EnerjoltBlocks.PULVERIZER, true);
        horizontalBlockWithItem(EnerjoltBlocks.ADVANCED_PULVERIZER, true);

        horizontalBlockWithItem(EnerjoltBlocks.SAWMILL, true);
        horizontalBlockWithItem(EnerjoltBlocks.COMPRESSOR, true);
        horizontalBlockWithItem(EnerjoltBlocks.METAL_PRESS, false);

        horizontalTwoSideBlockWithItem(EnerjoltBlocks.AUTO_PRESS_MOLD_MAKER, true);
        horizontalBlockWithItem(EnerjoltBlocks.AUTO_STONECUTTER, false);

        orientableBlockWithItem(EnerjoltBlocks.PLANT_GROWTH_CHAMBER,
                cubeBlockModel(EnerjoltBlocks.PLANT_GROWTH_CHAMBER, "", "_top", "_top",
                        "_front", "_front", "_side", "_side"));

        orientableSixDirsBlockWithItem(EnerjoltBlocks.BLOCK_PLACER, true);

        orientableBlockWithItem(EnerjoltBlocks.ASSEMBLING_MACHINE,
                orientableBlockModel(EnerjoltBlocks.ASSEMBLING_MACHINE, false));

        activatableOrientableMachineBlockWithItem(EnerjoltBlocks.INDUCTION_SMELTER, false);

        horizontalBlockWithItem(EnerjoltBlocks.FLUID_FILLER, true);

        orientableBlockWithItem(EnerjoltBlocks.STONE_LIQUEFIER,
                orientableBlockModel(EnerjoltBlocks.STONE_LIQUEFIER, false));

        orientableBlockWithItem(EnerjoltBlocks.STONE_SOLIDIFIER,
                orientableBlockModel(EnerjoltBlocks.STONE_SOLIDIFIER, false));

        orientableBlockWithItem(EnerjoltBlocks.FLUID_TRANSPOSER,
                orientableBlockModel(EnerjoltBlocks.FLUID_TRANSPOSER, false));

        horizontalTwoSideBlockWithItem(EnerjoltBlocks.FILTRATION_PLANT, false);

        horizontalBlockWithItem(EnerjoltBlocks.FLUID_DRAINER, true);

        horizontalBlockWithItem(EnerjoltBlocks.FLUID_PUMP, false);
        horizontalBlockWithItem(EnerjoltBlocks.ADVANCED_FLUID_PUMP, false);

        horizontalBlockWithItem(EnerjoltBlocks.DRAIN, true);

        horizontalBlockWithItem(EnerjoltBlocks.CHARGER, true);
        horizontalBlockWithItem(EnerjoltBlocks.ADVANCED_CHARGER, true);

        horizontalBlockWithItem(EnerjoltBlocks.UNCHARGER, true);
        horizontalBlockWithItem(EnerjoltBlocks.ADVANCED_UNCHARGER, true);

        orientableSixDirsBlockWithItem(EnerjoltBlocks.MINECART_CHARGER, true);
        orientableSixDirsBlockWithItem(EnerjoltBlocks.ADVANCED_MINECART_CHARGER, true);

        orientableSixDirsBlockWithItem(EnerjoltBlocks.MINECART_UNCHARGER, true);
        orientableSixDirsBlockWithItem(EnerjoltBlocks.ADVANCED_MINECART_UNCHARGER, true);

        // Solar panels
        solarPanelBlockWithItem(EnerjoltBlocks.SOLAR_PANEL_1);
        solarPanelBlockWithItem(EnerjoltBlocks.SOLAR_PANEL_2);
        solarPanelBlockWithItem(EnerjoltBlocks.SOLAR_PANEL_3);
        solarPanelBlockWithItem(EnerjoltBlocks.SOLAR_PANEL_4);
        solarPanelBlockWithItem(EnerjoltBlocks.SOLAR_PANEL_5);
        solarPanelBlockWithItem(EnerjoltBlocks.SOLAR_PANEL_6);

        // Generators
        activatableOrientableMachineBlockWithItem(EnerjoltBlocks.COAL_ENGINE, false);

        poweredLampBlockWithItem(EnerjoltBlocks.POWERED_LAMP);

        activatableOrientableMachineBlockWithItem(EnerjoltBlocks.POWERED_FURNACE, false);
        activatableOrientableMachineBlockWithItem(EnerjoltBlocks.ADVANCED_POWERED_FURNACE, false);

        activatableBlockWithItem(EnerjoltBlocks.LIGHTNING_GENERATOR,
                cubeBlockModel(EnerjoltBlocks.LIGHTNING_GENERATOR, "", "_top", "_bottom",
                        "_side", "_side", "_side", "_side"),
                cubeBlockModel(EnerjoltBlocks.LIGHTNING_GENERATOR, "_on", "_top_on", "_bottom",
                        "_side_on", "_side_on", "_side_on", "_side_on"),
                LightningGeneratorBlock.HIT_BY_LIGHTNING_BOLT);

        activatableOrientableBlockWithItem(EnerjoltBlocks.ENERGIZER,
                orientableBlockModel(EnerjoltBlocks.ENERGIZER, true),
                orientableBlockModel(EnerjoltBlocks.ENERGIZER, "_on", "_top_on", "_bottom",
                        "_front_on", "_side_on"),
                EnergizerBlock.LIT);

        activatableBlockWithItem(EnerjoltBlocks.CHARGING_STATION,
                cubeBlockModel(EnerjoltBlocks.CHARGING_STATION, "", "_top", "_bottom",
                        "_side", "_side", "_side", "_side"),
                cubeBlockModel(EnerjoltBlocks.CHARGING_STATION, "_on", "_top_on", "_bottom",
                        "_side_on", "_side_on", "_side_on", "_side_on"),
                ChargingStationBlock.CHARGING);

        horizontalBlockWithItem(EnerjoltBlocks.HEAT_GENERATOR, false);

        orientableBlockWithItem(EnerjoltBlocks.THERMAL_GENERATOR,
                orientableBlockModel(EnerjoltBlocks.THERMAL_GENERATOR, false));

        horizontalBlockWithItem(EnerjoltBlocks.CRYSTAL_GROWTH_CHAMBER, false);

        horizontalTwoSideBlockWithItem(EnerjoltBlocks.WEATHER_CONTROLLER, false);

        horizontalTwoSideBlockWithItem(EnerjoltBlocks.TIME_CONTROLLER, false);

        activatableBlockWithItem(EnerjoltBlocks.TELEPORTER,
                cubeBlockModel(EnerjoltBlocks.TELEPORTER, "", "_top", "_bottom",
                        "_side", "_side", "_side", "_side"),
                cubeBlockModel(EnerjoltBlocks.TELEPORTER, "_ready", "_top_ready", "_bottom",
                        "_side", "_side", "_side", "_side"),
                TeleporterBlock.POWERED);

        // Machine frames
        horizontalBlockWithItem(EnerjoltBlocks.BASIC_MACHINE_FRAME, false);
        horizontalBlockWithItem(EnerjoltBlocks.HARDENED_MACHINE_FRAME, false);
        horizontalBlockWithItem(EnerjoltBlocks.ADVANCED_MACHINE_FRAME, false);
        horizontalBlockWithItem(EnerjoltBlocks.REINFORCED_ADVANCED_MACHINE_FRAME, false);
        activatableOrientableMachineBlockWithItem(EnerjoltBlocks.INDUSTRIAL_GREENHOUSE, false);
        activatableOrientableMachineBlockWithItem(EnerjoltBlocks.PHOTOSYNTHETIC_CHAMBER, false);
    }

    // Helper method for ResourceLocation
    private ResourceLocation createModLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, path);
    }

    private ModelFile cubeBlockModel(Holder<? extends Block> block, String fileSuffix, String upSuffix,
                                     String bottomSuffix, String northSuffix, String southSuffix,
                                     String westSuffix, String eastSuffix) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        return models().
                withExistingParent(blockId.getPath() + fileSuffix, ModelProvider.BLOCK_FOLDER + "/cube").
                texture("particle", "#up").
                texture("up", getBlockTexture(block, upSuffix)).
                texture("down", getBlockTexture(block, bottomSuffix)).
                texture("north", getBlockTexture(block, northSuffix)).
                texture("south", getBlockTexture(block, southSuffix)).
                texture("west", getBlockTexture(block, westSuffix)).
                texture("east", getBlockTexture(block, eastSuffix));
    }

    private ModelFile orientableBlockModel(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private ModelFile orientableOnBlockModel(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        return orientableBlockModel(block, "_on", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front_on", "_side");
    }

    private ModelFile orientableBlockModel(Holder<? extends Block> block, String fileSuffix, String topSuffix,
                                           String bottomSuffix, String frontSuffix, String sideSuffix) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        return models().
                withExistingParent(blockId.getPath() + fileSuffix, ModelProvider.BLOCK_FOLDER + "/orientable").
                texture("particle", "#top").
                texture("top", getBlockTexture(block, topSuffix)).
                texture("bottom", getBlockTexture(block, bottomSuffix)).
                texture("front", getBlockTexture(block, frontSuffix)).
                texture("side", getBlockTexture(block, sideSuffix));
    }

    private ModelFile orientableWithBackBlockModel(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        return orientableWithBackBlockModel(block, "", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private ModelFile orientableWithBackBlockModel(Holder<? extends Block> block, String fileSuffix, String topSuffix,
                                                   String bottomSuffix, String frontSuffix, String backSuffix, String sideSuffix) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        return models().
                withExistingParent(blockId.getPath() + fileSuffix, ModelProvider.BLOCK_FOLDER + "/cube").
                texture("particle", "#up").
                texture("up", getBlockTexture(block, topSuffix)).
                texture("down", getBlockTexture(block, bottomSuffix)).
                texture("north", getBlockTexture(block, frontSuffix)).
                texture("south", getBlockTexture(block, backSuffix)).
                texture("west", getBlockTexture(block, sideSuffix)).
                texture("east", getBlockTexture(block, sideSuffix));
    }

    private ModelFile orientableVerticalWithBackBlockModel(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        return orientableVerticalWithBackBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_back", "_side");
    }

    private ModelFile orientableVerticalWithBackBlockModel(Holder<? extends Block> block, String fileSuffix, String topSuffix,
                                                           String bottomSuffix, String frontSuffix, String backSuffix, String sideSuffix) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        return models().
                withExistingParent(blockId.getPath() + fileSuffix, ModelProvider.BLOCK_FOLDER + "/cube").
                element().
                face(Direction.UP).cullface(Direction.UP).texture("#front").end().
                face(Direction.DOWN).uvs(0, 16, 16, 0).cullface(Direction.DOWN).texture("#back").end().
                face(Direction.NORTH).cullface(Direction.NORTH).texture("#top").end().
                face(Direction.SOUTH).cullface(Direction.SOUTH).texture("#bottom").end().
                face(Direction.WEST).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).cullface(Direction.WEST).texture("#side").end().
                face(Direction.EAST).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).cullface(Direction.EAST).texture("#side").end().
                end().
                texture("particle", "#top").
                texture("top", getBlockTexture(block, topSuffix)).
                texture("bottom", getBlockTexture(block, bottomSuffix)).
                texture("front", getBlockTexture(block, frontSuffix)).
                texture("back", getBlockTexture(block, backSuffix)).
                texture("side", getBlockTexture(block, sideSuffix));
    }

    private ModelFile orientableVerticalBlockModel(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        return orientableVerticalBlockModel(block, "_vertical", "_top", uniqueBottomTexture?"_bottom":"_top",
                "_front", "_side");
    }

    private ModelFile orientableVerticalBlockModel(Holder<? extends Block> block, String fileSuffix, String topSuffix,
                                                   String bottomSuffix, String frontSuffix, String sideSuffix) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        return models().
                withExistingParent(blockId.getPath() + fileSuffix, ModelProvider.BLOCK_FOLDER + "/orientable").
                element().
                face(Direction.UP).cullface(Direction.UP).texture("#front").end().
                face(Direction.DOWN).uvs(0, 16, 16, 0).cullface(Direction.DOWN).texture("#side").end().
                face(Direction.NORTH).cullface(Direction.NORTH).texture("#top").end().
                face(Direction.SOUTH).cullface(Direction.SOUTH).texture("#bottom").end().
                face(Direction.WEST).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).cullface(Direction.WEST).texture("#side").end().
                face(Direction.EAST).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).cullface(Direction.EAST).texture("#side").end().
                end().
                texture("particle", "#top").
                texture("top", getBlockTexture(block, topSuffix)).
                texture("bottom", getBlockTexture(block, bottomSuffix)).
                texture("front", getBlockTexture(block, frontSuffix)).
                texture("side", getBlockTexture(block, sideSuffix));
    }

    private void cubeAllBlockWithItem(Holder<? extends Block> block) {
        simpleBlockWithItem(block.value(), cubeAll(block.value()));
    }

    private void horizontalBlockWithItem(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        ModelFile model = models().
                withExistingParent(blockId.getPath(), ModelProvider.BLOCK_FOLDER + "/cube").
                texture("particle", "#up").
                texture("up", getBlockTexture(block, "_top")).
                texture("down", getBlockTexture(block, uniqueBottomTexture?"_bottom":"_top")).
                texture("north", getBlockTexture(block, "_side")).
                texture("south", getBlockTexture(block, "_side")).
                texture("east", getBlockTexture(block, "_side")).
                texture("west", getBlockTexture(block, "_side"));

        simpleBlockWithItem(block.value(), model);
    }

    private void horizontalTwoSideBlockWithItem(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        ModelFile model = models().
                withExistingParent(blockId.getPath(), ModelProvider.BLOCK_FOLDER + "/cube").
                texture("particle", "#up").
                texture("up", getBlockTexture(block, "_top")).
                texture("down", getBlockTexture(block, uniqueBottomTexture?"_bottom":"_top")).
                texture("north", getBlockTexture(block, "_front")).
                texture("south", getBlockTexture(block, "_side")).
                texture("east", getBlockTexture(block, "_side")).
                texture("west", getBlockTexture(block, "_front"));

        simpleBlockWithItem(block.value(), model);
    }

    private void orientableBlockWithItem(Holder<? extends Block> block, ModelFile model) {
        getVariantBuilder(block.value()).partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).modelForState().modelFile(model).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).modelForState().rotationY(180).modelFile(model).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).modelForState().rotationY(90).modelFile(model).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).modelForState().rotationY(270).modelFile(model).addModel().partialState();

        simpleBlockItem(block.value(), model);
    }

    private void orientableSixDirsBlockWithBackItem(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        orientableSixDirsBlockWithItem(block,
                orientableWithBackBlockModel(block, uniqueBottomTexture),
                orientableVerticalWithBackBlockModel(block, uniqueBottomTexture));
    }

    private void orientableSixDirsBlockWithItem(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        orientableSixDirsBlockWithItem(block,
                orientableBlockModel(block, uniqueBottomTexture),
                orientableVerticalBlockModel(block, uniqueBottomTexture));
    }

    private void orientableSixDirsBlockWithItem(Holder<? extends Block> block, ModelFile modelNormal, ModelFile modelVertical) {
        getVariantBuilder(block.value()).partialState().
                with(BlockStateProperties.FACING, Direction.UP).modelForState().modelFile(modelVertical).addModel().partialState().
                with(BlockStateProperties.FACING, Direction.DOWN).modelForState().rotationX(180).modelFile(modelVertical).addModel().partialState().
                with(BlockStateProperties.FACING, Direction.NORTH).modelForState().modelFile(modelNormal).addModel().partialState().
                with(BlockStateProperties.FACING, Direction.SOUTH).modelForState().rotationY(180).modelFile(modelNormal).addModel().partialState().
                with(BlockStateProperties.FACING, Direction.EAST).modelForState().rotationY(90).modelFile(modelNormal).addModel().partialState().
                with(BlockStateProperties.FACING, Direction.WEST).modelForState().rotationY(270).modelFile(modelNormal).addModel().partialState();

        simpleBlockItem(block.value(), modelNormal);
    }

    private void activatableBlockWithItem(Holder<? extends Block> block, ModelFile modelNormal,
                                          ModelFile modelActive, BooleanProperty isActiveProperty) {
        getVariantBuilder(block.value()).partialState().
                with(isActiveProperty, false).modelForState().modelFile(modelNormal).addModel().partialState().
                with(isActiveProperty, true).modelForState().modelFile(modelActive).addModel().partialState();

        simpleBlockItem(block.value(), modelNormal);
    }

    private void activatableOrientableBlockWithItem(Holder<? extends Block> block, ModelFile modelNormal,
                                                    ModelFile modelActive, BooleanProperty isActiveProperty) {
        getVariantBuilder(block.value()).partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).with(isActiveProperty, false).modelForState().
                modelFile(modelNormal).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).with(isActiveProperty, true).modelForState().
                modelFile(modelActive).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).with(isActiveProperty, false).modelForState().
                rotationY(180).modelFile(modelNormal).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).with(isActiveProperty, true).modelForState().
                rotationY(180).modelFile(modelActive).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).with(isActiveProperty, false).modelForState().
                rotationY(90).modelFile(modelNormal).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).with(isActiveProperty, true).modelForState().
                rotationY(90).modelFile(modelActive).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).with(isActiveProperty, false).modelForState().
                rotationY(270).modelFile(modelNormal).addModel().partialState().
                with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).with(isActiveProperty, true).modelForState().
                rotationY(270).modelFile(modelActive).addModel().partialState();

        simpleBlockItem(block.value(), modelNormal);
    }

    private void itemConveyorBeltBlockWithItem(DeferredHolder<Block, ? extends ItemConveyorBeltBlock> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        ModelFile modelFlat = models().
                getBuilder(blockId.getPath() + "_flat").parent(itemConveyorBeltFlatTemplate).
                texture("particle", "#belt").
                texture("belt", getBlockTexture(block));

        ModelFile modelAscending = models().
                getBuilder(blockId.getPath() + "_ascending").parent(itemConveyorBeltAscendingTemplate).
                texture("particle", "#belt").
                texture("belt", getBlockTexture(block));

        ModelFile modelDescending = models().
                getBuilder(blockId.getPath() + "_descending").parent(itemConveyorBeltDescendingTemplate).
                texture("particle", "#belt").
                texture("belt", getBlockTexture(block));

        VariantBlockStateBuilder blockStateBuilder = getVariantBuilder(block.value());
        for(EnerjoltBlockStateProperties.ConveyorBeltDirection beltDir: EnerjoltBlockStateProperties.ConveyorBeltDirection.values()) {
            ConfiguredModel.Builder<VariantBlockStateBuilder> configuredModelBuilder = blockStateBuilder.partialState().
                    with(ItemConveyorBeltBlock.FACING, beltDir).modelForState();
            if(beltDir.isAscending()) {
                switch(beltDir.getDirection()) {
                    case NORTH -> configuredModelBuilder.rotationY(180);
                    case WEST -> configuredModelBuilder.rotationY(90);
                    case EAST -> configuredModelBuilder.rotationY(270);
                }

                configuredModelBuilder.modelFile(modelAscending).addModel();
            }else if(beltDir.isDescending()) {
                switch(beltDir.getDirection()) {
                    case SOUTH -> configuredModelBuilder.rotationY(180);
                    case WEST -> configuredModelBuilder.rotationY(270);
                    case EAST -> configuredModelBuilder.rotationY(90);
                }

                configuredModelBuilder.modelFile(modelDescending).addModel();
            }else {
                switch(beltDir.getDirection()) {
                    case NORTH -> configuredModelBuilder.rotationY(180);
                    case WEST -> configuredModelBuilder.rotationY(90);
                    case EAST -> configuredModelBuilder.rotationY(270);
                }

                configuredModelBuilder.modelFile(modelFlat).addModel();
            }
        }

        itemModels().getBuilder(blockId.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", getBlockTexture(block));
    }

    private void fluidPipeBlockWithItem(Holder<? extends Block> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        ModelFile fluidPipeCore = models().
                getBuilder(blockId.getPath() + "_core").parent(fluidPipeCoreTemplate).
                texture("particle", getBlockTexture(block, "_core")).
                texture("fluid_pipe_core", getBlockTexture(block, "_core"));
        ModelFile fluidPipeSideConnected = models().
                getBuilder(blockId.getPath() + "_side_connected").parent(fluidPipeSideConnectedTemplate).
                texture("particle", getBlockTexture(block, "_side_connected")).
                texture("fluid_pipe_side", getBlockTexture(block, "_side_connected"));
        ModelFile fluidPipeSideExtract = models().
                getBuilder(blockId.getPath() + "_side_extract").parent(fluidPipeSideExtractTemplate).
                texture("particle", getBlockTexture(block, "_side_outer_extract")).
                texture("fluid_pipe_side_inner", getBlockTexture(block, "_side_inner_extract")).
                texture("fluid_pipe_side_outer", getBlockTexture(block, "_side_outer_extract"));

        getMultipartBuilder(block.value()).part().
                modelFile(fluidPipeCore).addModel().end().part().
                modelFile(fluidPipeSideConnected).rotationX(270).addModel().condition(FluidPipeBlock.UP,
                        EnerjoltBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationX(270).addModel().condition(FluidPipeBlock.UP,
                        EnerjoltBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).rotationX(90).addModel().condition(FluidPipeBlock.DOWN,
                        EnerjoltBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationX(90).addModel().condition(FluidPipeBlock.DOWN,
                        EnerjoltBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).addModel().condition(FluidPipeBlock.NORTH,
                        EnerjoltBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).addModel().condition(FluidPipeBlock.NORTH,
                        EnerjoltBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).rotationX(180).addModel().condition(FluidPipeBlock.SOUTH,
                        EnerjoltBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationX(180).addModel().condition(FluidPipeBlock.SOUTH,
                        EnerjoltBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).rotationY(90).addModel().condition(FluidPipeBlock.EAST,
                        EnerjoltBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationY(90).addModel().condition(FluidPipeBlock.EAST,
                        EnerjoltBlockStateProperties.PipeConnection.EXTRACT).end().part().
                modelFile(fluidPipeSideConnected).rotationY(270).addModel().condition(FluidPipeBlock.WEST,
                        EnerjoltBlockStateProperties.PipeConnection.CONNECTED).end().part().
                modelFile(fluidPipeSideExtract).rotationY(270).addModel().condition(FluidPipeBlock.WEST,
                        EnerjoltBlockStateProperties.PipeConnection.EXTRACT).end();

        itemModels().
                getBuilder(blockId.getPath()).parent(fluidPipeCore).
                transforms().
                transform(ItemDisplayContext.GUI).rotation(30, 45, 0).end().
                transform(ItemDisplayContext.GROUND).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.FIXED).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(.65f, .65f, .65f).end().
                transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(.65f, .65f, .65f).end().end();
    }

    private void fluidTankBlockWithItem(Holder<? extends Block> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        ModelFile fluidTank = models().
                getBuilder(blockId.getPath()).parent(fluidTankTemplate).
                texture("particle", "#up").
                texture("front", getBlockTexture(block, "_front")).
                texture("side", getBlockTexture(block, "_side")).
                texture("up", getBlockTexture(block, "_top")).
                texture("interior", getBlockTexture(block, "_interior"));

        getVariantBuilder(block.value()).partialState().
                with(FluidTankBlock.FACING, Direction.NORTH).modelForState().modelFile(fluidTank).addModel().partialState().
                with(FluidTankBlock.FACING, Direction.SOUTH).modelForState().rotationY(180).modelFile(fluidTank).addModel().partialState().
                with(FluidTankBlock.FACING, Direction.EAST).modelForState().rotationY(90).modelFile(fluidTank).addModel().partialState().
                with(FluidTankBlock.FACING, Direction.WEST).modelForState().rotationY(270).modelFile(fluidTank).addModel().partialState();

        simpleBlockItem(block.value(), fluidTank);
    }

    private void cableBlockWithItem(Holder<? extends Block> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        ModelFile cableCore = models().
                getBuilder(blockId.getPath() + "_core").parent(cableCoreTemplate).
                texture("particle", getBlockTexture(block)).
                texture("cable", getBlockTexture(block));
        ModelFile cableSide = models().
                getBuilder(blockId.getPath() + "_side").parent(cableSideTemplate).
                texture("particle", getBlockTexture(block)).
                texture("cable", getBlockTexture(block));

        getMultipartBuilder(block.value()).part().
                modelFile(cableCore).addModel().end().part().
                modelFile(cableSide).rotationX(270).addModel().condition(CableBlock.UP, true).end().part().
                modelFile(cableSide).rotationX(90).addModel().condition(CableBlock.DOWN, true).end().part().
                modelFile(cableSide).addModel().condition(CableBlock.NORTH, true).end().part().
                modelFile(cableSide).rotationX(180).addModel().condition(CableBlock.SOUTH, true).end().part().
                modelFile(cableSide).rotationY(90).addModel().condition(CableBlock.EAST, true).end().part().
                modelFile(cableSide).rotationY(270).addModel().condition(CableBlock.WEST, true).end();

        itemModels().
                getBuilder(blockId.getPath()).parent(cableCore).
                transforms().
                transform(ItemDisplayContext.GUI).rotation(30, 45, 0).scale(1.5f, 1.5f, 1.5f).end().
                transform(ItemDisplayContext.GROUND).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.FIXED).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(1.01f, 1.01f, 1.01f).end().
                transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(1.01f, 1.01f, 1.01f).end().end();
    }

    private void transformerBlockWithItem(DeferredHolder<Block, ? extends TransformerBlock> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();
        String textureName = switch(block.get().getTier()) {
            case LV -> "lv_transformer";
            case MV -> "mv_transformer";
            case HV -> "hv_transformer";
            case EHV -> "ehv_transformer";
        };

        TransformerType transformerType = block.get().getTransformerType();
        switch(transformerType) {
            case TYPE_1_TO_N, TYPE_N_TO_1 -> {
                String singleSuffix = transformerType == TransformerType.TYPE_1_TO_N?"_input":"_output";
                String multipleSuffix = transformerType == TransformerType.TYPE_1_TO_N?"_output":"_input";

                ModelFile transformer = models().
                        withExistingParent(blockId.getPath(), ModelProvider.BLOCK_FOLDER + "/orientable").
                        texture("particle", "#top").
                        texture("top", ModelProvider.BLOCK_FOLDER + "/" + textureName + multipleSuffix).
                        texture("bottom", ModelProvider.BLOCK_FOLDER + "/" + textureName + multipleSuffix).
                        texture("front", ModelProvider.BLOCK_FOLDER + "/" + textureName + singleSuffix).
                        texture("side", ModelProvider.BLOCK_FOLDER + "/" + textureName + multipleSuffix);

                getVariantBuilder(block.value()).partialState().
                        with(TransformerBlock.FACING, Direction.UP).modelForState().rotationX(270).modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.DOWN).modelForState().rotationX(90).modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.NORTH).modelForState().modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.SOUTH).modelForState().rotationY(180).modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.EAST).modelForState().rotationY(90).modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.WEST).modelForState().rotationY(270).modelFile(transformer).addModel().partialState();

                simpleBlockItem(block.value(), transformer);
            }
            case TYPE_3_TO_3 -> {
                ModelFile transformer = models().
                        withExistingParent(blockId.getPath(), ModelProvider.BLOCK_FOLDER + "/cube").
                        texture("particle", "#up").
                        texture("up", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_input").
                        texture("down", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_output").
                        texture("north", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_input").
                        texture("south", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_output").
                        texture("east", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_output").
                        texture("west", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_input");

                getVariantBuilder(block.value()).partialState().
                        with(TransformerBlock.FACING, Direction.UP).modelForState().rotationX(270).modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.DOWN).modelForState().rotationX(90).rotationY(90).modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.NORTH).modelForState().modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.SOUTH).modelForState().rotationX(90).rotationY(180).modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.EAST).modelForState().rotationY(90).modelFile(transformer).addModel().partialState().
                        with(TransformerBlock.FACING, Direction.WEST).modelForState().rotationX(90).rotationY(270).modelFile(transformer).addModel().partialState();

                simpleBlockItem(block.value(), transformer);
            }
        }
    }

    private void configurableTransformerBlockWithItem(DeferredBlock<ConfigurableTransformerBlock> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();
        String textureName = switch(block.value().getTier()) {
            case LV -> "lv_transformer";
            case MV -> "mv_transformer";
            case HV -> "hv_transformer";
            case EHV -> "ehv_transformer";
        };

        ModelFile allCube = models().
                withExistingParent(blockId.getPath() + "_cube", ModelProvider.BLOCK_FOLDER + "/cube").
                texture("particle", "#up").
                texture("up", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_not_connected").
                texture("down", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_not_connected").
                texture("north", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_output").
                texture("south", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_not_connected").
                texture("east", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_input").
                texture("west", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_not_connected");;

        ModelFile notConnectedSide = models().
                getBuilder(blockId.getPath() + "_not_connected").parent(singleSideTemplate).
                texture("particle", "#side").
                texture("side", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_not_connected");
        ModelFile receiveSide = models().
                getBuilder(blockId.getPath() + "_input").parent(singleSideTemplate).
                texture("particle", "#side").
                texture("side", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_input");
        ModelFile extractSide = models().
                getBuilder(blockId.getPath() + "_output").parent(singleSideTemplate).
                texture("particle", "#side").
                texture("side", ModelProvider.BLOCK_FOLDER + "/" + textureName + "_output");

        getMultipartBuilder(block.value()).part().
                modelFile(notConnectedSide).rotationX(270).addModel().condition(ConfigurableTransformerBlock.UP,
                        EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).end().part().
                modelFile(receiveSide).rotationX(270).addModel().condition(ConfigurableTransformerBlock.UP,
                        EnerjoltBlockStateProperties.TransformerConnection.RECEIVE).end().part().
                modelFile(extractSide).rotationX(270).addModel().condition(ConfigurableTransformerBlock.UP,
                        EnerjoltBlockStateProperties.TransformerConnection.EXTRACT).end().part().
                modelFile(notConnectedSide).rotationX(90).addModel().condition(ConfigurableTransformerBlock.DOWN,
                        EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).end().part().
                modelFile(receiveSide).rotationX(90).addModel().condition(ConfigurableTransformerBlock.DOWN,
                        EnerjoltBlockStateProperties.TransformerConnection.RECEIVE).end().part().
                modelFile(extractSide).rotationX(90).addModel().condition(ConfigurableTransformerBlock.DOWN,
                        EnerjoltBlockStateProperties.TransformerConnection.EXTRACT).end().part().
                modelFile(notConnectedSide).addModel().condition(ConfigurableTransformerBlock.NORTH,
                        EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).end().part().
                modelFile(receiveSide).addModel().condition(ConfigurableTransformerBlock.NORTH,
                        EnerjoltBlockStateProperties.TransformerConnection.RECEIVE).end().part().
                modelFile(extractSide).addModel().condition(ConfigurableTransformerBlock.NORTH,
                        EnerjoltBlockStateProperties.TransformerConnection.EXTRACT).end().part().
                modelFile(notConnectedSide).rotationX(180).addModel().condition(ConfigurableTransformerBlock.SOUTH,
                        EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).end().part().
                modelFile(receiveSide).rotationX(180).addModel().condition(ConfigurableTransformerBlock.SOUTH,
                        EnerjoltBlockStateProperties.TransformerConnection.RECEIVE).end().part().
                modelFile(extractSide).rotationX(180).addModel().condition(ConfigurableTransformerBlock.SOUTH,
                        EnerjoltBlockStateProperties.TransformerConnection.EXTRACT).end().part().
                modelFile(notConnectedSide).rotationY(90).addModel().condition(ConfigurableTransformerBlock.EAST,
                        EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).end().part().
                modelFile(receiveSide).rotationY(90).addModel().condition(ConfigurableTransformerBlock.EAST,
                        EnerjoltBlockStateProperties.TransformerConnection.RECEIVE).end().part().
                modelFile(extractSide).rotationY(90).addModel().condition(ConfigurableTransformerBlock.EAST,
                        EnerjoltBlockStateProperties.TransformerConnection.EXTRACT).end().part().
                modelFile(notConnectedSide).rotationY(270).addModel().condition(ConfigurableTransformerBlock.WEST,
                        EnerjoltBlockStateProperties.TransformerConnection.NOT_CONNECTED).end().part().
                modelFile(receiveSide).rotationY(270).addModel().condition(ConfigurableTransformerBlock.WEST,
                        EnerjoltBlockStateProperties.TransformerConnection.RECEIVE).end().part().
                modelFile(extractSide).rotationY(270).addModel().condition(ConfigurableTransformerBlock.WEST,
                        EnerjoltBlockStateProperties.TransformerConnection.EXTRACT).end();

        simpleBlockItem(block.value(), allCube);
    }

    private void solarPanelBlockWithItem(Holder<? extends Block> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        ModelFile solarPanel = models().
                getBuilder(blockId.getPath()).parent(solarPanelTemplate).
                texture("particle", "#top").
                texture("top", getBlockTexture(block, "_top")).
                texture("side", getBlockTexture(block, "_side"));

        getVariantBuilder(block.value()).partialState().
                modelForState().modelFile(solarPanel).addModel();

        simpleBlockItem(block.value(), solarPanel);
    }

    private void activatableOrientableMachineBlockWithItem(Holder<? extends Block> block, boolean uniqueBottomTexture) {
        activatableOrientableBlockWithItem(block,
                orientableBlockModel(block, uniqueBottomTexture),
                orientableOnBlockModel(block, uniqueBottomTexture),
                BlockStateProperties.LIT);
    }

    private void poweredLampBlockWithItem(Holder<? extends Block> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        ModelFile modelOff = models().cubeAll(blockId.getPath(), getBlockTexture(block));
        ModelFile modelOn = models().cubeAll(blockId.getPath() + "_on", getBlockTexture(block, "_on"));

        VariantBlockStateBuilder blockStateBuilder = getVariantBuilder(block.value()).partialState().
                with(BlockStateProperties.LEVEL, 0).modelForState().modelFile(modelOff).addModel();

        for(int i = 1;i < 16;i++)
            blockStateBuilder.partialState().
                    with(BlockStateProperties.LEVEL, i).modelForState().modelFile(modelOn).addModel();

        simpleBlockItem(block.value(), modelOff);
    }

    private ResourceLocation getBlockTexture(Holder<? extends Block> block) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(),
                ModelProvider.BLOCK_FOLDER + "/" + blockId.getPath());
    }

    private ResourceLocation getBlockTexture(Holder<? extends Block> block, String pathSuffix) {
        ResourceLocation blockId = Objects.requireNonNull(block.getKey()).location();

        return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(),
                ModelProvider.BLOCK_FOLDER + "/" + blockId.getPath() + pathSuffix);
    }
}
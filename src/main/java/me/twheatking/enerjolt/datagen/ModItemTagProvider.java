package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.item.EnerjoltItems;
import me.twheatking.enerjolt.registry.tags.CommonItemTags;
import me.twheatking.enerjolt.registry.tags.CompatibilityItemTags;
import me.twheatking.enerjolt.registry.tags.EnerjoltItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTagLookup,
                              @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagLookup, EJOLTAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        // Minecraft tags
        tag(ItemTags.BOOKSHELF_BOOKS).
                add(EnerjoltItems.ENERJOLT_BOOK.get());

        tag(ItemTags.LECTERN_BOOKS).
                add(EnerjoltItems.ENERJOLT_BOOK.get());

        tag(ItemTags.PIGLIN_LOVED).
                add(EnerjoltItems.GOLD_DUST.get(),
                        EnerjoltItems.GOLD_PLATE.get(),
                        EnerjoltItems.GOLDEN_HAMMER.get());

        // Stairs
        tag(ItemTags.STAIRS).
                add(EnerjoltBlocks.ZINC_STAIRS.get().asItem());

        // Slabs
        tag(ItemTags.SLABS).
                add(EnerjoltBlocks.ZINC_SLAB.get().asItem());

        // Buttons
        tag(ItemTags.BUTTONS).
                add(EnerjoltBlocks.ZINC_BUTTON.get().asItem());

        // Fences
        tag(ItemTags.FENCES).
                add(EnerjoltBlocks.ZINC_FENCE.get().asItem());

        // Fence gates
        tag(ItemTags.FENCE_GATES).
                add(EnerjoltBlocks.ZINC_FENCE_GATE.get().asItem());

        // Walls
        tag(ItemTags.WALLS).
                add(EnerjoltBlocks.ZINC_WALL.get().asItem());

        // Doors
        tag(ItemTags.DOORS).
                add(EnerjoltBlocks.ZINC_DOOR.get().asItem());

        // Trapdoors
        tag(ItemTags.TRAPDOORS).
                add(EnerjoltBlocks.ZINC_TRAPDOOR.get().asItem());

        // Swords
        tag(ItemTags.SWORDS).
                add(EnerjoltItems.ZINC_SWORD.get());

        // Pickaxes
        tag(ItemTags.PICKAXES).
                add(EnerjoltItems.ZINC_PICKAXE.get());

        // Axes
        tag(ItemTags.AXES).
                add(EnerjoltItems.ZINC_AXE.get());

        // Shovels
        tag(ItemTags.SHOVELS).
                add(EnerjoltItems.ZINC_SHOVEL.get());

        // Hoes
        tag(ItemTags.HOES).
                add(EnerjoltItems.ZINC_HOE.get());

        // Head armor
        tag(ItemTags.HEAD_ARMOR).
                add(EnerjoltItems.ZINC_HELMET.get());

        // Chest armor
        tag(ItemTags.CHEST_ARMOR).
                add(EnerjoltItems.ZINC_CHESTPLATE.get());

        // Leg armor
        tag(ItemTags.LEG_ARMOR).
                add(EnerjoltItems.ZINC_LEGGINGS.get());

        // Foot armor
        tag(ItemTags.FOOT_ARMOR).
                add(EnerjoltItems.ZINC_BOOTS.get());

        // Trim templates
        tag(ItemTags.TRIM_TEMPLATES).
                add(EnerjoltItems.ZINC_SMITHING_TEMPLATE.get());

        // Enerjolt-specific tags
        tag(EnerjoltItemTags.RAW_METAL_PRESS_MOLDS).
                add(EnerjoltItems.RAW_GEAR_PRESS_MOLD.get(),
                        EnerjoltItems.RAW_ROD_PRESS_MOLD.get(),
                        EnerjoltItems.RAW_WIRE_PRESS_MOLD.get());

        tag(EnerjoltItemTags.METAL_PRESS_MOLDS).
                add(EnerjoltItems.GEAR_PRESS_MOLD.get(),
                        EnerjoltItems.ROD_PRESS_MOLD.get(),
                        EnerjoltItems.WIRE_PRESS_MOLD.get());

        // AE2 compatibility tags
        tag(CompatibilityItemTags.AE2_ITEM_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM.get(),
                        EnerjoltBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM.get(),
                        EnerjoltBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM.get(),
                        EnerjoltBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM.get(),

                        EnerjoltBlocks.ITEM_SILO_TINY_ITEM.get(),
                        EnerjoltBlocks.ITEM_SILO_SMALL_ITEM.get(),
                        EnerjoltBlocks.ITEM_SILO_MEDIUM_ITEM.get(),
                        EnerjoltBlocks.ITEM_SILO_LARGE_ITEM.get(),
                        EnerjoltBlocks.ITEM_SILO_GIANT_ITEM.get(),
                        EnerjoltBlocks.CREATIVE_ITEM_SILO_ITEM.get()
                );

        tag(CompatibilityItemTags.AE2_FLUID_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EnerjoltBlocks.IRON_FLUID_PIPE_ITEM.get(),
                        EnerjoltBlocks.GOLDEN_FLUID_PIPE_ITEM.get(),

                        EnerjoltBlocks.FLUID_TANK_SMALL_ITEM.get(),
                        EnerjoltBlocks.FLUID_TANK_MEDIUM_ITEM.get(),
                        EnerjoltBlocks.FLUID_TANK_LARGE_ITEM.get(),
                        EnerjoltBlocks.CREATIVE_FLUID_TANK_ITEM.get()
                );

        tag(CompatibilityItemTags.AE2_FE_P2P_TUNNEL_ATTUNEMENTS).
                add(
                        EnerjoltBlocks.TIN_CABLE_ITEM.get(),
                        EnerjoltBlocks.COPPER_CABLE_ITEM.get(),
                        EnerjoltBlocks.GOLD_CABLE_ITEM.get(),
                        EnerjoltBlocks.ENERGIZED_COPPER_CABLE_ITEM.get(),
                        EnerjoltBlocks.ENERGIZED_GOLD_CABLE_ITEM.get(),
                        EnerjoltBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM.get(),

                        EnerjoltBlocks.LV_TRANSFORMER_1_TO_N_ITEM.get(),
                        EnerjoltBlocks.LV_TRANSFORMER_3_TO_3_ITEM.get(),
                        EnerjoltBlocks.LV_TRANSFORMER_N_TO_1_ITEM.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_1_TO_N_ITEM.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_3_TO_3_ITEM.get(),
                        EnerjoltBlocks.MV_TRANSFORMER_N_TO_1_ITEM.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_1_TO_N_ITEM.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_3_TO_3_ITEM.get(),
                        EnerjoltBlocks.HV_TRANSFORMER_N_TO_1_ITEM.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_1_TO_N_ITEM.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_3_TO_3_ITEM.get(),
                        EnerjoltBlocks.EHV_TRANSFORMER_N_TO_1_ITEM.get(),

                        EnerjoltBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM.get(),
                        EnerjoltBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM.get(),
                        EnerjoltBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM.get(),
                        EnerjoltBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM.get(),

                        EnerjoltBlocks.BATTERY_BOX_ITEM.get(),
                        EnerjoltBlocks.ADVANCED_BATTERY_BOX_ITEM.get(),
                        EnerjoltBlocks.CREATIVE_BATTERY_BOX_ITEM.get()
                );

        // Common tags - Ores
        tag(Tags.Items.ORES).
                addTag(CommonItemTags.ORES_TIN);
        tag(CommonItemTags.ORES_TIN).
                add(EnerjoltBlocks.TIN_ORE_ITEM.get(),
                        EnerjoltBlocks.DEEPSLATE_TIN_ORE_ITEM.get());

        tag(Tags.Items.ORES_IN_GROUND_STONE).
                add(EnerjoltBlocks.TIN_ORE_ITEM.get(),
                        EnerjoltBlocks.ZINC_ORE.get().asItem());
        tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).
                add(EnerjoltBlocks.DEEPSLATE_TIN_ORE_ITEM.get(),
                        EnerjoltBlocks.ZINC_DEEPSLATE_ORE.get().asItem());
        tag(Tags.Items.ORES_IN_GROUND_NETHERRACK).
                add(EnerjoltBlocks.ZINC_NETHER_ORE.get().asItem());

        // Storage blocks
        tag(Tags.Items.STORAGE_BLOCKS).
                addTag(CommonItemTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                addTag(CommonItemTags.STORAGE_BLOCKS_TIN);
        tag(CommonItemTags.STORAGE_BLOCKS_SILICON).
                add(EnerjoltBlocks.SILICON_BLOCK_ITEM.get());
        tag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN).
                add(EnerjoltBlocks.RAW_TIN_BLOCK_ITEM.get());
        tag(CommonItemTags.STORAGE_BLOCKS_TIN).
                add(EnerjoltBlocks.TIN_BLOCK_ITEM.get());

        // Raw materials
        tag(Tags.Items.RAW_MATERIALS).
                addTag(CommonItemTags.RAW_MATERIALS_TIN);
        tag(CommonItemTags.RAW_MATERIALS_TIN).
                add(EnerjoltItems.RAW_TIN.get());

        // Dusts
        tag(Tags.Items.DUSTS).
                addTag(CommonItemTags.DUSTS_WOOD).
                addTag(CommonItemTags.DUSTS_CHARCOAL).
                addTag(CommonItemTags.DUSTS_TIN).
                addTag(CommonItemTags.DUSTS_COPPER).
                addTag(CommonItemTags.DUSTS_IRON).
                addTag(CommonItemTags.DUSTS_GOLD);
        tag(CommonItemTags.DUSTS_WOOD).
                add(EnerjoltItems.SAWDUST.get());
        tag(CommonItemTags.DUSTS_CHARCOAL).
                add(EnerjoltItems.CHARCOAL_DUST.get());
        tag(CommonItemTags.DUSTS_TIN).
                add(EnerjoltItems.TIN_DUST.get());
        tag(CommonItemTags.DUSTS_COPPER).
                add(EnerjoltItems.COPPER_DUST.get());
        tag(CommonItemTags.DUSTS_IRON).
                add(EnerjoltItems.IRON_DUST.get());
        tag(CommonItemTags.DUSTS_GOLD).
                add(EnerjoltItems.GOLD_DUST.get());

        // Nuggets
        tag(Tags.Items.NUGGETS).
                addTag(CommonItemTags.NUGGETS_TIN);
        tag(CommonItemTags.NUGGETS_TIN).
                add(EnerjoltItems.TIN_NUGGET.get());

        // Silicon
        tag(CommonItemTags.SILICON).
                add(EnerjoltItems.SILICON.get());

        // Ingots
        tag(Tags.Items.INGOTS).
                addTag(CommonItemTags.INGOTS_TIN).
                addTag(CommonItemTags.INGOTS_STEEL).
                addTag(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                addTag(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                addTag(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                addTag(CommonItemTags.INGOTS_ENERGIZED_GOLD);
        tag(CommonItemTags.INGOTS_TIN).
                add(EnerjoltItems.TIN_INGOT.get());
        tag(CommonItemTags.INGOTS_STEEL).
                add(EnerjoltItems.STEEL_INGOT.get());
        tag(CommonItemTags.INGOTS_REDSTONE_ALLOY).
                add(EnerjoltItems.REDSTONE_ALLOY_INGOT.get());
        tag(CommonItemTags.INGOTS_ADVANCED_ALLOY).
                add(EnerjoltItems.ADVANCED_ALLOY_INGOT.get());
        tag(CommonItemTags.INGOTS_ENERGIZED_COPPER).
                add(EnerjoltItems.ENERGIZED_COPPER_INGOT.get());
        tag(CommonItemTags.INGOTS_ENERGIZED_GOLD).
                add(EnerjoltItems.ENERGIZED_GOLD_INGOT.get());

        // Plates
        tag(CommonItemTags.PLATES).
                addTag(CommonItemTags.PLATES_TIN).
                addTag(CommonItemTags.PLATES_COPPER).
                addTag(CommonItemTags.PLATES_IRON).
                addTag(CommonItemTags.PLATES_GOLD).
                addTag(CommonItemTags.PLATES_ADVANCED_ALLOY).
                addTag(CommonItemTags.PLATES_ENERGIZED_COPPER).
                addTag(CommonItemTags.PLATES_ENERGIZED_GOLD);
        tag(CommonItemTags.PLATES_TIN).
                add(EnerjoltItems.TIN_PLATE.get());
        tag(CommonItemTags.PLATES_COPPER).
                add(EnerjoltItems.COPPER_PLATE.get());
        tag(CommonItemTags.PLATES_IRON).
                add(EnerjoltItems.IRON_PLATE.get());
        tag(CommonItemTags.PLATES_GOLD).
                add(EnerjoltItems.GOLD_PLATE.get());
        tag(CommonItemTags.PLATES_ADVANCED_ALLOY).
                add(EnerjoltItems.ADVANCED_ALLOY_PLATE.get());
        tag(CommonItemTags.PLATES_ENERGIZED_COPPER).
                add(EnerjoltItems.ENERGIZED_COPPER_PLATE.get());
        tag(CommonItemTags.PLATES_ENERGIZED_GOLD).
                add(EnerjoltItems.ENERGIZED_GOLD_PLATE.get());

        // Gears
        tag(CommonItemTags.GEARS).
                addTag(CommonItemTags.GEARS_IRON);
        tag(CommonItemTags.GEARS_IRON).
                add(EnerjoltItems.IRON_GEAR.get());

        // Rods
        tag(Tags.Items.RODS).
                addTag(CommonItemTags.RODS_IRON);
        tag(CommonItemTags.RODS_IRON).
                add(EnerjoltItems.IRON_ROD.get());

        // Wires
        tag(CommonItemTags.WIRES).
                addTag(CommonItemTags.WIRES_TIN).
                addTag(CommonItemTags.WIRES_COPPER).
                addTag(CommonItemTags.WIRES_GOLD).
                addTag(CommonItemTags.WIRES_ENERGIZED_COPPER).
                addTag(CommonItemTags.WIRES_ENERGIZED_GOLD);
        tag(CommonItemTags.WIRES_TIN).
                add(EnerjoltItems.TIN_WIRE.get());
        tag(CommonItemTags.WIRES_COPPER).
                add(EnerjoltItems.COPPER_WIRE.get());
        tag(CommonItemTags.WIRES_GOLD).
                add(EnerjoltItems.GOLD_WIRE.get());
        tag(CommonItemTags.WIRES_ENERGIZED_COPPER).
                add(EnerjoltItems.ENERGIZED_COPPER_WIRE.get());
        tag(CommonItemTags.WIRES_ENERGIZED_GOLD).
                add(EnerjoltItems.ENERGIZED_GOLD_WIRE.get());

        // Tools
        tag(Tags.Items.TOOLS).
                addTag(CommonItemTags.TOOLS_HAMMERS).
                addTag(CommonItemTags.TOOLS_CUTTERS);

        tag(CommonItemTags.TOOLS_HAMMERS).
                add(EnerjoltItems.WOODEN_HAMMER.get()).
                add(EnerjoltItems.STONE_HAMMER.get()).
                add(EnerjoltItems.IRON_HAMMER.get()).
                add(EnerjoltItems.GOLDEN_HAMMER.get()).
                add(EnerjoltItems.DIAMOND_HAMMER.get()).
                add(EnerjoltItems.NETHERITE_HAMMER.get());

        tag(CommonItemTags.TOOLS_CUTTERS).
                add(EnerjoltItems.CUTTER.get());
    }
}
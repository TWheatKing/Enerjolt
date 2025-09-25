package me.twheatking.enerjolt.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.item.EnerjoltItems;
import me.twheatking.enerjolt.item.EnerjoltBookItem;
import me.twheatking.enerjolt.networking.ModMessages;
import me.twheatking.enerjolt.networking.packet.OpenEnergizedPowerBookS2CPacket;
import me.twheatking.enerjolt.villager.EnerjoltVillager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = EJOLTAPI.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        if(event.getType() == EnerjoltVillager.ELECTRICIAN_PROFESSION.get()) {
            //Level 1
            addOffer(trades, 1,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemCost(Items.BOOK),
                    new ItemStack(EnerjoltItems.ENERGIZED_POWER_BOOK.get()),
                    3, 3, .02f);
            addOffer(trades, 1,
                    new ItemCost(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);
            addOffer(trades, 1,
                    new ItemCost(EnerjoltItems.SILICON.get(), 3),
                    new ItemStack(Items.EMERALD, 2),
                    15, 2, .02f);
            addOffer(trades, 1,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemStack(EnerjoltItems.CABLE_INSULATOR.get(), 16),
                    5, 3, .02f);
            addOffer(trades, 1,
                    new ItemCost(Items.EMERALD, 9),
                    new ItemStack(EnerjoltItems.IRON_HAMMER.get()),
                    2, 3, .02f);

            //Level 2
            addOffer(trades, 2,
                    new ItemCost(Items.EMERALD, 35),
                    new ItemStack(EnerjoltBlocks.COPPER_CABLE_ITEM.get(), 6),
                    3, 5, .02f);
            addOffer(trades, 2,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemCost(Items.COPPER_INGOT, 4),
                    new ItemStack(EnerjoltItems.BATTERY_2.get()),
                    3, 7, .02f);
            addOffer(trades, 2,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemCost(Items.COPPER_INGOT, 12),
                    new ItemStack(EnerjoltItems.ENERGY_ANALYZER.get()),
                    2, 8, .02f);
            addOffer(trades, 2,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemCost(Items.COPPER_INGOT, 12),
                    new ItemStack(EnerjoltItems.FLUID_ANALYZER.get()),
                    2, 8, .02f);
            addOffer(trades, 2,
                    new ItemCost(EnerjoltItems.COPPER_PLATE.get(), 3),
                    new ItemStack(Items.EMERALD, 8),
                    15, 6, .02f);
            addOffer(trades, 2,
                    new ItemCost(Items.EMERALD, 12),
                    new ItemStack(EnerjoltItems.CUTTER.get()),
                    2, 8, .02f);

            //Level 3
            addOffer(trades, 3,
                    new ItemCost(Items.EMERALD, 21),
                    new ItemCost(EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.COAL_ENGINE_ITEM.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemCost(Items.EMERALD, 31),
                    new ItemCost(EnerjoltItems.BASIC_SOLAR_CELL.get(), 2),
                    new ItemStack(EnerjoltBlocks.SOLAR_PANEL_ITEM_1.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemCost(Items.EMERALD, 33),
                    new ItemCost(EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.FLUID_FILLER_ITEM.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemCost(Items.EMERALD, 38),
                    new ItemCost(EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.AUTO_CRAFTER_ITEM.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemCost(Items.EMERALD, 46),
                    new ItemCost(EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.CHARGER_ITEM.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemCost(EnerjoltItems.BASIC_SOLAR_CELL.get(), 3),
                    new ItemStack(Items.EMERALD, 9),
                    15, 9, .02f);

            //Level 4
            addOffer(trades, 4,
                    new ItemCost(Items.EMERALD, 34),
                    new ItemCost(EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.SAWMILL_ITEM.get()),
                    3, 20, .02f);
            addOffer(trades, 4,
                    new ItemCost(Items.EMERALD, 39),
                    new ItemCost(EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.CRUSHER_ITEM.get()),
                    3, 20, .02f);
            addOffer(trades, 4,
                    new ItemCost(Items.EMERALD, 52),
                    new ItemCost(EnerjoltBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.COMPRESSOR_ITEM.get()),
                    3, 20, .02f);
            addOffer(trades, 4,
                    new ItemCost(Items.EMERALD, 29),
                    new ItemCost(Items.COPPER_INGOT, 9),
                    new ItemStack(EnerjoltItems.BATTERY_4.get()),
                    2, 19, .02f);
            addOffer(trades, 4,
                    new ItemCost(EnerjoltItems.SAWDUST.get(), 17),
                    new ItemStack(Items.EMERALD, 4),
                    20, 18, .02f);

            //Level 5
            addOffer(trades, 5,
                    new ItemCost(Items.EMERALD, 32),
                    new ItemCost(EnerjoltBlocks.HARDENED_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.THERMAL_GENERATOR_ITEM.get()),
                    1, 30, .02f);
            addOffer(trades, 5,
                    new ItemCost(Items.EMERALD_BLOCK, 9),
                    new ItemCost(EnerjoltBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.ENERGIZER_ITEM.get()),
                    1, 30, .02f);
            addOffer(trades, 5,
                    new ItemCost(Items.EMERALD_BLOCK, 12),
                    new ItemCost(EnerjoltBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(EnerjoltBlocks.LIGHTNING_GENERATOR_ITEM.get()),
                    1, 30, .02f);
            addOffer(trades, 5,
                    new ItemCost(EnerjoltItems.ENERGIZED_COPPER_INGOT.get()),
                    new ItemStack(Items.EMERALD, 23),
                    15, 30, .02f);
        }
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int level,
                                 ItemCost cost, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        addOffer(trades, level, cost, null, result, maxUses, xp, priceMultiplier);
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int level,
                                 ItemCost costA, ItemCost costB, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        trades.get(level).add((trader, rand) -> new MerchantOffer(costA, Optional.ofNullable(costB), result, maxUses, xp, priceMultiplier));
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        handlePlayerLecternInteraction(event);
        if(event.isCanceled())
            return;

        handlePlayerInWorldCraftingInteract(event);
    }

    private static void handlePlayerLecternInteraction(PlayerInteractEvent.RightClickBlock event) {
        BlockPos blockPos = event.getPos();
        BlockEntity blockEntity = event.getLevel().getBlockEntity(blockPos);

        if(!(blockEntity instanceof LecternBlockEntity))
            return;

        LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;

        BlockState blockState = event.getLevel().getBlockState(blockPos);
        if(!blockState.getValue(LecternBlock.HAS_BOOK))
            return;

        ItemStack bookItemStack = lecternBlockEntity.getBook();
        if(!bookItemStack.is(EnerjoltItems.ENERGIZED_POWER_BOOK.get()))
            return;

        Item bookItem = bookItemStack.getItem();
        if(!(bookItem instanceof EnerjoltBookItem))
            return;

        Player player = event.getEntity();

        if(!event.getLevel().isClientSide)
            ModMessages.sendToPlayer(new OpenEnergizedPowerBookS2CPacket(blockPos), (ServerPlayer)player);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static void handlePlayerInWorldCraftingInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        if(!itemStack.is(Tags.Items.TOOLS_SHEAR))
            return;

        Level level = event.getLevel();

        BlockPos blockPos = event.getPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(BlockTags.WOOL))
            return;

        Player player = event.getEntity();

        if(!event.getLevel().isClientSide) {
            if(!player.isCreative())
                itemStack.hurtAndBreak(1, player,
                        event.getHand() == InteractionHand.MAIN_HAND?EquipmentSlot.MAINHAND:EquipmentSlot.OFFHAND);

            level.destroyBlock(blockPos, false, player);

            ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                    new ItemStack(EnerjoltItems.CABLE_INSULATOR.get(), 18), 0, 0, 0);
            itemEntity.setPickUpDelay(20);
            level.addFreshEntity(itemEntity);

            level.playSound(null, blockPos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.f, 1.f);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}

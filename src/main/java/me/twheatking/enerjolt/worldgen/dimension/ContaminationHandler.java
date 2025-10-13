package me.twheatking.enerjolt.worldgen.dimension;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.component.ContaminatedComponent;
import me.twheatking.enerjolt.component.EnerjoltDataComponentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Handles item contamination mechanics:
 * - Items picked up in Contamination Zone become contaminated
 * - Contaminated items can't be used outside the zone
 * - Items dropped by death in the zone become lootable by others
 */
@EventBusSubscriber(modid = EJOLTAPI.MOD_ID)
public class ContaminationHandler {

    /**
     * When player picks up an item in the Contamination Zone, mark it as contaminated
     */
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        // Check if player is in Contamination Zone
        if (player.level().dimension() != ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            return;
        }

        ItemEntity itemEntity = event.getItemEntity();
        ItemStack stack = itemEntity.getItem();

        // Don't contaminate if already contaminated or extracted
        ContaminatedComponent existing = stack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());
        if (existing != null && existing.contaminated()) {
            return; // Already contaminated
        }

        // Mark as contaminated
        stack.set(EnerjoltDataComponentTypes.CONTAMINATED.get(), ContaminatedComponent.CONTAMINATED);
        itemEntity.setItem(stack);

        // Visual feedback
        player.displayClientMessage(
                Component.literal("§6⚠ Item contaminated - requires extraction!"),
                true
        );
    }

    /**
     * Prevent using contaminated items outside the Contamination Zone
     */
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        // Allow usage in Contamination Zone
        if (player.level().dimension() == ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            return;
        }

        // Check if contaminated and not extracted
        ContaminatedComponent component = stack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());
        if (component != null && component.needsExtraction()) {
            player.displayClientMessage(
                    Component.literal("§c⚠ This item is contaminated! Extract it first!"),
                    true
            );
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    /**
     * Prevent using contaminated blocks outside the Contamination Zone
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        // Allow usage in Contamination Zone
        if (player.level().dimension() == ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            return;
        }

        // Check if contaminated and not extracted
        ContaminatedComponent component = stack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());
        if (component != null && component.needsExtraction()) {
            player.displayClientMessage(
                    Component.literal("§c⚠ This item is contaminated! Extract it first!"),
                    true
            );
            event.setCanceled(true);
        }
    }

    /**
     * Prevent placing contaminated blocks outside the Contamination Zone
     */
    @SubscribeEvent
    public static void onBlockPlace(net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Allow placement in Contamination Zone
        if (player.level().dimension() == ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            return;
        }

        // Get the item used to place the block
        ItemStack stack = ItemStack.EMPTY;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = player.getItemInHand(hand);
            if (!held.isEmpty()) {
                stack = held;
                break;
            }
        }

        if (stack.isEmpty()) {
            return;
        }

        // Check if contaminated and not extracted
        ContaminatedComponent component = stack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());
        if (component != null && component.needsExtraction()) {
            player.displayClientMessage(
                    Component.literal("§c⚠ This item is contaminated! Extract it first!"),
                    true
            );
            event.setCanceled(true);
        }
    }

    /**
     * When items are dropped (thrown) in the Contamination Zone, mark them as contaminated
     */
    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        Player player = event.getPlayer();

        // Only process in Contamination Zone
        if (player.level().dimension() != ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            return;
        }

        ItemEntity itemEntity = event.getEntity();
        ItemStack stack = itemEntity.getItem();

        // Don't re-contaminate already contaminated items
        ContaminatedComponent existing = stack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());
        if (existing != null && existing.contaminated()) {
            return;
        }

        // Mark as contaminated
        stack.set(EnerjoltDataComponentTypes.CONTAMINATED.get(), ContaminatedComponent.CONTAMINATED);
        itemEntity.setItem(stack);
    }
}
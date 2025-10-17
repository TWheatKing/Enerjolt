package me.twheatking.enerjolt.event;

import me.twheatking.enerjolt.contamination.EBCContaminationManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static me.twheatking.enerjolt.api.EJOLTAPI.MOD_ID;

/**
 * Event handler for the E.B.C (Enerjolt Bio Contamination) system.
 * Handles player ticks, death, and respawn events.
 */
@EventBusSubscriber(modid = MOD_ID)
public class EBCEventHandler {

    /**
     * Tick the E.B.C system for all players every tick
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // Only run on server side
        if (player.level().isClientSide()) {
            return;
        }

        // Tick the E.B.C contamination system
        EBCContaminationManager.tick(player);
    }

    /**
     * Handle player death - set E.B.C to 49 (hidden contamination)
     */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Only run on server side
        if (player.level().isClientSide()) {
            return;
        }

        // Set E.B.C to 49 (just under detection threshold)
        // Player appears "clean" but is actually contaminated
        EBCContaminationManager.onPlayerDeath(player);
    }
}
package me.twheatking.enerjolt.contamination;

import me.twheatking.enerjolt.effect.EnerjoltMobEffects;
import me.twheatking.enerjolt.item.armor.ArmorSetBonus;
import me.twheatking.enerjolt.item.armor.EnerjoltArmorMaterials;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

/**
 * Main manager for the E.B.C (Enerjolt Bio Contamination) system.
 * Handles biome detection, timer updates, and effect application.
 */
public class EBCContaminationManager {

    /**
     * Tick the E.B.C system for a player
     * Called every tick from the event handler
     */
    public static void tick(Player player) {
        if (player.level().isClientSide()) {
            return; // Only run on server
        }

        // Get or create E.B.C data for this player
        EBCData data = player.getData(EBCDataAttachment.EBC_DATA);

        // Check if player is in Plagueland biome
        boolean inPlagueland = isInPlagueland(player);

        // Check if player is wearing full Enerjolt Hazmat armor (4 pieces with energy)
        boolean hasHazmatProtection = ArmorSetBonus.hasFullSet(player, EnerjoltArmorMaterials.ENERJOLT);

        // Update E.B.C accumulation
        if (inPlagueland && !hasHazmatProtection) {
            // Player is exposed - accumulate E.B.C
            // 1 tick = 0.05 seconds (20 ticks per second)
            // 5 seconds = 1 E.B.C count
            data.addTime(0.05f);
        }
        // If not in Plagueland or wearing hazmat armor, timer pauses (does nothing)

        // Update biome state for next tick
        data.setInPlagueland(inPlagueland);

        // Apply effects based on E.B.C count
        applyEffects(player, data);
    }

    /**
     * Check if player is in the Plagueland biome
     */
    private static boolean isInPlagueland(Player player) {
        ResourceKey<Biome> biomeKey = player.level()
                .getBiome(player.blockPosition())
                .unwrapKey()
                .orElse(null);

        if (biomeKey == null) {
            return false;
        }

        // Check if this is the Plagueland biome
        return biomeKey.location().toString().equals("enerjolt:plagueland");
    }

    /**
     * Apply effects based on current E.B.C count
     */
    private static void applyEffects(Player player, EBCData data) {
        int count = data.getEBCCount();

        // Stage 3: Plagued by E.B.C (100+ counts)
        if (count >= 100) {
            // Apply both Plagued and E.B.C Contamination effects
            // Both have infinite duration and are reapplied every tick
            applyInfiniteEffect(player, EnerjoltMobEffects.PLAGUED_BY_EBC);
            applyInfiniteEffect(player, EnerjoltMobEffects.EBC_CONTAMINATION);
        }
        // Stage 2: E.B.C Contamination (50-99 counts)
        else if (count >= 50) {
            // Apply only E.B.C Contamination effect
            applyInfiniteEffect(player, EnerjoltMobEffects.EBC_CONTAMINATION);
            // Remove Plagued effect if present
            player.removeEffect(EnerjoltMobEffects.PLAGUED_BY_EBC);
        }
        // Stage 1: Undetected (0-49 counts)
        else {
            // Remove all effects
            player.removeEffect(EnerjoltMobEffects.EBC_CONTAMINATION);
            player.removeEffect(EnerjoltMobEffects.PLAGUED_BY_EBC);
        }
    }

    /**
     * Apply an effect with "infinite" duration (actually 100 ticks, reapplied constantly)
     * This ensures the effect never expires while conditions are met
     */
    private static void applyInfiniteEffect(Player player, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect) {
        MobEffectInstance currentEffect = player.getEffect(effect);

        // If player doesn't have the effect or it's about to expire, reapply it
        if (currentEffect == null || currentEffect.getDuration() < 20) {
            player.addEffect(new MobEffectInstance(
                    effect,
                    100,  // 5 seconds duration, but reapplied every tick
                    0,    // Amplifier 0
                    false, // Not ambient
                    true,  // Show particles (for Plagued effect)
                    true   // Show icon
            ));
        }
    }

    /**
     * Handle player death - set E.B.C to 49 (hidden contamination)
     */
    public static void onPlayerDeath(Player player) {
        EBCData data = player.getData(EBCDataAttachment.EBC_DATA);
        data.setEBCCount(49); // Just under detection threshold
        data.resetAccumulatedTime();

        // Effects are removed naturally on death
    }

    /**
     * Apply B.C.R potion effect - remove E.B.C counts
     */
    public static void applyBCRPotion(Player player, int countsToRemove) {
        EBCData data = player.getData(EBCDataAttachment.EBC_DATA);

        int oldCount = data.getEBCCount();
        data.removeEBCCount(countsToRemove);
        int newCount = data.getEBCCount();

        // Log for debugging
        if (player.level().isClientSide()) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "E.B.C reduced: " + oldCount + " → " + newCount + " (-" + countsToRemove + ")"
                    ),
                    true // Action bar message
            );
        }

        // Effects will be updated on next tick
    }

    /**
     * Get E.B.C count for a player (for debugging/display)
     */
    public static int getEBCCount(Player player) {
        EBCData data = player.getData(EBCDataAttachment.EBC_DATA);
        return data.getEBCCount();
    }

    /**
     * Get E.B.C stage for a player
     */
    public static int getEBCStage(Player player) {
        EBCData data = player.getData(EBCDataAttachment.EBC_DATA);
        return data.getStage();
    }
}
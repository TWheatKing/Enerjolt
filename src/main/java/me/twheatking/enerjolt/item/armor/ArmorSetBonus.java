package me.twheatking.enerjolt.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/**
 * Utility class for checking and applying armor set bonuses.
 * Centralizes the logic for detecting how many pieces of a set are worn.
 */
public class ArmorSetBonus {

    /**
     * Count how many pieces of a specific armor set the player is wearing
     * Only counts pieces that have energy (not depleted)
     */
    public static int getArmorSetPieceCount(Player player, Holder<ArmorMaterial> material) {
        int count = 0;

        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) {
                continue;
            }

            if (!(stack.getItem() instanceof ArmorItem armorItem)) {
                continue;
            }

            // Check if it's the right material
            if (armorItem.getMaterial() != material) {
                continue;
            }

            // Check if it's an energy armor piece
            if (!(armorItem instanceof EnergyArmorItem)) {
                continue;
            }

            // Only count pieces with energy
            if (EnergyArmorItem.hasEnergy(stack)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Check if player has at least X pieces of a set with energy
     */
    public static boolean hasSetPieces(Player player, Holder<ArmorMaterial> material, int requiredPieces) {
        return getArmorSetPieceCount(player, material) >= requiredPieces;
    }

    /**
     * Check if player has the full set (4 pieces) with energy
     */
    public static boolean hasFullSet(Player player, Holder<ArmorMaterial> material) {
        return hasSetPieces(player, material, 4);
    }

    /**
     * Apply all set bonuses for the player based on equipped armor
     * Should be called every tick from a player tick event
     */
    public static void tickSetBonuses(Player player) {
        // Apply Enerjolt set bonuses
        EnerjoltArmorItem.applySetBonuses(player);

        // Apply Cryonite set bonuses
        CryoniteArmorItem.applySetBonuses(player);

        // Apply Voidstone set bonuses
        VoidstoneArmorItem.applySetBonuses(player);
    }

    /**
     * Check if player is wearing any endgame armor
     */
    public static boolean isWearingEndgameArmor(Player player) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof EnergyArmorItem) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the total energy stored across all equipped endgame armor
     */
    public static int getTotalArmorEnergy(Player player) {
        int totalEnergy = 0;

        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof EnergyArmorItem) {
                totalEnergy += EnergyArmorItem.getEnergy(stack);
            }
        }

        return totalEnergy;
    }

    /**
     * Get the total max energy capacity across all equipped endgame armor
     */
    public static int getTotalMaxArmorEnergy(Player player) {
        int totalMaxEnergy = 0;

        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof EnergyArmorItem) {
                totalMaxEnergy += EnergyArmorItem.getMaxEnergy(stack);
            }
        }

        return totalMaxEnergy;
    }
}
package me.twheatking.enerjolt.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Enerjolt Armor Set - Hazmat Protection Theme
 *
 * Set Bonuses:
 * - 2 Pieces: Poison Resistance
 * - 4 Pieces: Complete hazmat protection (immunity to poison, wither, and harmful potions)
 */
public class EnerjoltArmorItem extends EnergyArmorItem {

    public EnerjoltArmorItem(Type type, Properties properties) {
        super(EnerjoltArmorMaterials.ENERJOLT, type, properties);
    }

    @Override
    protected Component getSetNameComponent() {
        return Component.translatable("armor.enerjolt.set.enerjolt");
    }

    @Override
    protected Component get2PieceBonusComponent() {
        return Component.translatable("armor.enerjolt.enerjolt.2piece")
                .withStyle(ChatFormatting.YELLOW);
    }

    @Override
    protected Component get4PieceBonusComponent() {
        return Component.translatable("armor.enerjolt.enerjolt.4piece")
                .withStyle(ChatFormatting.YELLOW);
    }

    /**
     * Apply set bonuses when armor is ticked on player
     * Called from LivingEntity armor tick events
     */
    public static void applySetBonuses(Player player) {
        int pieceCount = ArmorSetBonus.getArmorSetPieceCount(player, EnerjoltArmorMaterials.ENERJOLT);

        if (pieceCount < 2) {
            return; // No bonuses without at least 2 pieces
        }

        // 2-Piece Bonus: Poison Resistance
        if (pieceCount >= 2) {
            if (!player.hasEffect(MobEffects.POISON)) {
                // Preemptively give poison resistance
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 2, 0, false, false, false));
            }
        }

        // 4-Piece Bonus: Complete Hazmat Protection
        if (pieceCount >= 4) {
            // Remove harmful effects
            if (player.hasEffect(MobEffects.POISON)) {
                player.removeEffect(MobEffects.POISON);
            }
            if (player.hasEffect(MobEffects.WITHER)) {
                player.removeEffect(MobEffects.WITHER);
            }
            if (player.hasEffect(MobEffects.HUNGER)) {
                player.removeEffect(MobEffects.HUNGER);
            }
            if (player.hasEffect(MobEffects.WEAKNESS)) {
                player.removeEffect(MobEffects.WEAKNESS);
            }
            if (player.hasEffect(MobEffects.CONFUSION)) {
                player.removeEffect(MobEffects.CONFUSION);
            }
        }
    }

    /**
     * Check if player should be immune to a specific effect
     * Called from potion application events
     */
    public static boolean isImmuneToEffect(Player player, MobEffectInstance effect) {
        int pieceCount = ArmorSetBonus.getArmorSetPieceCount(player, EnerjoltArmorMaterials.ENERJOLT);

        // Full set provides complete hazmat immunity
        if (pieceCount >= 4) {
            return effect.getEffect() == MobEffects.POISON ||
                    effect.getEffect() == MobEffects.WITHER ||
                    effect.getEffect() == MobEffects.HUNGER ||
                    effect.getEffect() == MobEffects.WEAKNESS ||
                    effect.getEffect() == MobEffects.CONFUSION;
        }

        return false;
    }
}
package me.twheatking.enerjolt.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import static me.twheatking.enerjolt.api.EJOLTAPI.MOD_ID;

/**
 * Voidstone Armor Set - Tank/Endurance Theme
 *
 * Set Bonuses:
 * - 2 Pieces: No damage from Ender Pearl teleportation
 * - 4 Pieces: Massive stat boost (+4 armor, +2 toughness, +4 hearts)
 */
public class VoidstoneArmorItem extends EnergyArmorItem {
    // ResourceLocations for attribute modifiers (1.21.1+ uses ResourceLocation instead of UUID)
    private static final ResourceLocation ARMOR_BONUS_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "voidstone_armor_bonus");
    private static final ResourceLocation TOUGHNESS_BONUS_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "voidstone_toughness_bonus");
    private static final ResourceLocation HEALTH_BONUS_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "voidstone_health_bonus");

    public VoidstoneArmorItem(Type type, Properties properties) {
        super(EnerjoltArmorMaterials.VOIDSTONE, type, properties);
    }

    @Override
    protected Component getSetNameComponent() {
        return Component.translatable("armor.enerjolt.set.voidstone");
    }

    @Override
    protected Component get2PieceBonusComponent() {
        return Component.translatable("armor.enerjolt.voidstone.2piece")
                .withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    protected Component get4PieceBonusComponent() {
        return Component.translatable("armor.enerjolt.voidstone.4piece")
                .withStyle(ChatFormatting.DARK_PURPLE);
    }

    /**
     * Apply set bonuses when armor is ticked on player
     */
    public static void applySetBonuses(Player player) {
        int pieceCount = ArmorSetBonus.getArmorSetPieceCount(player, EnerjoltArmorMaterials.VOIDSTONE);

        // 4-Piece Bonus: Massive stat boost
        if (pieceCount >= 4) {
            applyStatBoosts(player);
        } else {
            removeStatBoosts(player);
        }
    }

    /**
     * Apply the 4-piece stat boosts
     */
    private static void applyStatBoosts(Player player) {
        // Check if modifiers are already applied
        if (!player.getAttribute(Attributes.ARMOR).hasModifier(ARMOR_BONUS_ID)) {
            // +4 Armor
            player.getAttribute(Attributes.ARMOR).addTransientModifier(
                    new AttributeModifier(ARMOR_BONUS_ID, 4.0, AttributeModifier.Operation.ADD_VALUE)
            );
        }

        if (!player.getAttribute(Attributes.ARMOR_TOUGHNESS).hasModifier(TOUGHNESS_BONUS_ID)) {
            // +2 Armor Toughness
            player.getAttribute(Attributes.ARMOR_TOUGHNESS).addTransientModifier(
                    new AttributeModifier(TOUGHNESS_BONUS_ID, 2.0, AttributeModifier.Operation.ADD_VALUE)
            );
        }

        if (!player.getAttribute(Attributes.MAX_HEALTH).hasModifier(HEALTH_BONUS_ID)) {
            // +4 Hearts (8 health points)
            player.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(
                    new AttributeModifier(HEALTH_BONUS_ID, 8.0, AttributeModifier.Operation.ADD_VALUE)
            );
        }
    }

    /**
     * Remove the 4-piece stat boosts when set is broken
     */
    private static void removeStatBoosts(Player player) {
        if (player.getAttribute(Attributes.ARMOR).hasModifier(ARMOR_BONUS_ID)) {
            player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_BONUS_ID);
        }

        if (player.getAttribute(Attributes.ARMOR_TOUGHNESS).hasModifier(TOUGHNESS_BONUS_ID)) {
            player.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(TOUGHNESS_BONUS_ID);
        }

        if (player.getAttribute(Attributes.MAX_HEALTH).hasModifier(HEALTH_BONUS_ID)) {
            player.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_BONUS_ID);
        }
    }

    /**
     * Check if ender pearl damage should be cancelled
     */
    public static boolean shouldCancelEnderPearlDamage(Player player) {
        int pieceCount = ArmorSetBonus.getArmorSetPieceCount(player, EnerjoltArmorMaterials.VOIDSTONE);
        return pieceCount >= 2;
    }
}
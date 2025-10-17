package me.twheatking.enerjolt.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cryonite Armor Set - Coolant/Ice Protection Theme
 *
 * Set Bonuses:
 * - 2 Pieces: Fire Resistance + Lava Protection
 * - 4 Pieces: Converts nearby lava source blocks to basalt (Nether) or obsidian (Overworld)
 */
public class CryoniteArmorItem extends EnergyArmorItem {

    public CryoniteArmorItem(Type type, Properties properties) {
        super(EnerjoltArmorMaterials.CRYONITE, type, properties);
    }

    @Override
    protected Component getSetNameComponent() {
        return Component.translatable("armor.enerjolt.set.cryonite");
    }

    @Override
    protected Component get2PieceBonusComponent() {
        return Component.translatable("armor.enerjolt.cryonite.2piece")
                .withStyle(ChatFormatting.AQUA);
    }

    @Override
    protected Component get4PieceBonusComponent() {
        return Component.translatable("armor.enerjolt.cryonite.4piece")
                .withStyle(ChatFormatting.AQUA);
    }

    /**
     * Apply set bonuses when armor is ticked on player
     */
    public static void applySetBonuses(Player player) {
        int pieceCount = ArmorSetBonus.getArmorSetPieceCount(player, EnerjoltArmorMaterials.CRYONITE);

        if (pieceCount < 2) {
            return; // No bonuses without at least 2 pieces
        }

        // 2-Piece Bonus: Fire Resistance
        if (pieceCount >= 2) {
            if (!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false, true));
            }
        }

        // 4-Piece Bonus: Lava Solidification
        if (pieceCount >= 4 && player.tickCount % 20 == 0) { // Check every second
            solidifyNearbyLava(player);
        }
    }

    /**
     * Convert lava beneath player's feet to basalt or obsidian
     * Only freezes lava the player is about to step on
     */
    private static void solidifyNearbyLava(Player player) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Check positions below and around the player's feet
        // This creates a small platform under them as they walk
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // Check 1 block below player's feet
                BlockPos checkPos = playerPos.offset(x, -1, z);
                BlockState state = level.getBlockState(checkPos);

                // Only convert lava source blocks directly under feet
                if (state.is(Blocks.LAVA) && state.getFluidState().isSource()) {
                    BlockState newBlock;

                    // Nether: Convert to basalt
                    // Overworld/End: Convert to obsidian
                    if (level.dimension() == Level.NETHER) {
                        newBlock = Blocks.BASALT.defaultBlockState();
                    } else {
                        newBlock = Blocks.OBSIDIAN.defaultBlockState();
                    }

                    level.setBlockAndUpdate(checkPos, newBlock);

                    // Play sound effect
                    level.levelEvent(1501, checkPos, 0); // Extinguish sound
                }
            }
        }
    }

    /**
     * Prevent fire damage when wearing 2+ pieces
     */
    public static boolean shouldCancelFireDamage(Player player) {
        int pieceCount = ArmorSetBonus.getArmorSetPieceCount(player, EnerjoltArmorMaterials.CRYONITE);
        return pieceCount >= 2;
    }
}
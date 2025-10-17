package me.twheatking.enerjolt.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

/**
 * Plagued by E.B.C Effect - Stage 3 (100+ counts)
 *
 * Causes:
 * - Damage over time: 1 heart (2 HP) every 2 seconds
 * - All Stage 2 saturation depletion effects
 * - Green wither-like particles
 *
 * Cannot be cured by milk.
 */
public class PlaguedByEBCEffect extends MobEffect {

    public PlaguedByEBCEffect() {
        super(
                MobEffectCategory.HARMFUL,
                0x32CD32  // Green color for icon (lime green)
        );
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Apply every tick for saturation and damage timing
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Apply damage every 40 ticks (2 seconds) = 1 heart
        if (entity.tickCount % 40 == 0) {
            // Use magic damage source (bypasses armor like wither)
            entity.hurt(entity.damageSources().magic(), 2.0F);
        }

        // Also apply saturation depletion (Stage 2 effects continue)
        if (entity instanceof Player player) {
            FoodData foodData = player.getFoodData();

            // Detect player movement state
            double movementSpeed = player.getDeltaMovement().horizontalDistanceSqr();
            boolean isMoving = movementSpeed > 0.001;
            boolean isSprinting = player.isSprinting();

            if (isSprinting) {
                // Sprinting → 2x sprint rate
                foodData.addExhaustion(0.1f);
            } else if (isMoving) {
                // Walking → sprint rate
                foodData.addExhaustion(0.09f);
            } else {
                // Standing → walking rate
                foodData.addExhaustion(0.01f);
            }
        }

        return true;
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }
}
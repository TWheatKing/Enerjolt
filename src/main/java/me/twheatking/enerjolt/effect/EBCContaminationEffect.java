package me.twheatking.enerjolt.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

/**
 * E.B.C Contamination Effect - Stage 2 (50-100 counts)
 *
 * Causes accelerated saturation/food depletion:
 * - Standing still → depletes as if walking
 * - Walking → depletes as if sprinting
 * - Sprinting → depletes at 2x sprint rate
 *
 * No particles, just icon.
 * Cannot be cured by milk.
 */
public class EBCContaminationEffect extends MobEffect {

    public EBCContaminationEffect() {
        super(
                MobEffectCategory.HARMFUL,
                0xFFA500  // Orange color for icon
        );
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Apply every tick for smooth saturation depletion
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        FoodData foodData = player.getFoodData();

        // Detect player movement state
        double movementSpeed = player.getDeltaMovement().horizontalDistanceSqr();
        boolean isMoving = movementSpeed > 0.001;
        boolean isSprinting = player.isSprinting();

        // Apply additional exhaustion based on movement state
        // Vanilla rates:
        // - Standing: 0 per tick
        // - Walking: 0.01 per tick
        // - Sprinting: 0.1 per tick
        // - Jump-sprinting: 0.2 per tick

        if (isSprinting) {
            // Sprinting → 2x sprint rate (0.2 total)
            // Natural sprint is 0.1, so add 0.1 more
            foodData.addExhaustion(0.1f);
        } else if (isMoving) {
            // Walking → sprint rate (0.1 total)
            // Natural walk is 0.01, so add 0.09 more
            foodData.addExhaustion(0.09f);
        } else {
            // Standing → walking rate (0.01 total)
            // Natural standing is 0, so add 0.01
            foodData.addExhaustion(0.01f);
        }

        return true;
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }
}
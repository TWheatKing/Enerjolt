package me.twheatking.enerjolt.effect;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers custom mob effects for Enerjolt mod.
 * Includes the E.B.C (Enerjolt Bio Contamination) system effects.
 */
public class EnerjoltMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, EJOLTAPI.MOD_ID);

    /**
     * E.B.C Effect - Stage 2 (50-100 counts)
     * Causes accelerated saturation depletion
     * No particles, just icon
     */
    public static final Holder<MobEffect> EBC_CONTAMINATION = MOB_EFFECTS.register("ebc_contamination",
            () -> new EBCContaminationEffect());

    /**
     * Plagued by E.B.C - Stage 3 (100+ counts)
     * Causes damage over time with green wither-like effect
     * Shows particles
     */
    public static final Holder<MobEffect> PLAGUED_BY_EBC = MOB_EFFECTS.register("plagued_by_ebc",
            () -> new PlaguedByEBCEffect());

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}
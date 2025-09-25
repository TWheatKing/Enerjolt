package me.twheatking.enerjolt.paintings;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public final class EnerjoltPaintingVariants {
    private EnerjoltPaintingVariants() {}

    public static final ResourceKey<PaintingVariant> GEAR = registerKey("gear");
    public static final ResourceKey<PaintingVariant> FACTORY = registerKey("factory");


    public static void bootstrap(BootstrapContext<PaintingVariant> context) {
        register(context, GEAR, 2, 2);
        register(context, FACTORY, 2, 2);
    }

    public static ResourceKey<PaintingVariant> registerKey(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT,
                EJOLTAPI.id(name));
    }

    private static void register(BootstrapContext<PaintingVariant> context, ResourceKey<PaintingVariant> key,
                                 int width, int height) {
        context.register(key, new PaintingVariant(width, height, key.location()));
    }
}
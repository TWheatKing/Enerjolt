package me.twheatking.enerjolt.api;

import net.minecraft.resources.ResourceLocation;

public final class EJOLTAPI {
    private EJOLTAPI() {}

    public static final String MOD_ID = "enerjolt";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}

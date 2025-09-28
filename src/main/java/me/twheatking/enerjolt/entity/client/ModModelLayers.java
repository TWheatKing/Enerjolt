package me.twheatking.enerjolt.entity.client;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation EVIL_IRON_ZOMBIE_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "evil_iron_zombie"), "main");
}
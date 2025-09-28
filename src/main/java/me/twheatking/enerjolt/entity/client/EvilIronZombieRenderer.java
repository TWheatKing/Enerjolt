package me.twheatking.enerjolt.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.entity.custom.EvilIronZombieEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EvilIronZombieRenderer extends MobRenderer<EvilIronZombieEntity, EvilIronZombieModel<EvilIronZombieEntity>> {

    public EvilIronZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new EvilIronZombieModel<>(context.bakeLayer(ModModelLayers.EVIL_IRON_ZOMBIE_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EvilIronZombieEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "textures/entity/evil_iron_zombie/evil_iron_zombie.png");
    }

    @Override
    public void render(EvilIronZombieEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {

        // Scale the entity if needed
        //if (entity.isBaby()) {
        //    poseStack.scale(0.5f, 0.5f, 0.5f);
        //}

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
package me.twheatking.enerjolt.event;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.entity.EnerjoltEntityTypes;
import me.twheatking.enerjolt.entity.client.EvilIronZombieModel;
import me.twheatking.enerjolt.entity.custom.EvilIronZombieEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = EJOLTAPI.MOD_ID)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EvilIronZombieModel.LAYER_LOCATION, EvilIronZombieModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EnerjoltEntityTypes.EVILIRONZOMBIE.get(), EvilIronZombieEntity.createAttributes().build());
    }
}
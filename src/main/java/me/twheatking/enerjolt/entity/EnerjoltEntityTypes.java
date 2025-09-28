package me.twheatking.enerjolt.entity;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.entity.custom.EvilIronZombieEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EnerjoltEntityTypes {
    private EnerjoltEntityTypes() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, EJOLTAPI.MOD_ID);

    public static final Supplier<EntityType<MinecartBatteryBox>> BATTERY_BOX_MINECART =
            ENTITY_TYPES.register("battery_box_minecart",
                    () -> EntityType.Builder.<MinecartBatteryBox>of(MinecartBatteryBox::new, MobCategory.MISC).
                            sized(.98f, .7f).
                            clientTrackingRange(8).
                            build(EJOLTAPI.id("battery_box_minecart").
                                    toString()));
    public static final Supplier<EntityType<MinecartAdvancedBatteryBox>> ADVANCED_BATTERY_BOX_MINECART =
            ENTITY_TYPES.register("advanced_battery_box_minecart",
                    () -> EntityType.Builder.<MinecartAdvancedBatteryBox>of(MinecartAdvancedBatteryBox::new, MobCategory.MISC).
                            sized(.98f, .7f).
                            clientTrackingRange(8).
                            build(EJOLTAPI.id("advanced_battery_box_minecart").
                                    toString()));

    public static final Supplier<EntityType<EvilIronZombieEntity>> EVILIRONZOMBIE =
            ENTITY_TYPES.register("evil_iron_zombie",
                    () -> EntityType.Builder.of(EvilIronZombieEntity::new, MobCategory.MONSTER).
                            sized(1.4F, 2.7F).
                            clientTrackingRange(10).
                            build(EJOLTAPI.id("evil_iron_zombie").
                                    toString()));

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}

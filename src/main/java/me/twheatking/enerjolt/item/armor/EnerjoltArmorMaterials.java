package me.twheatking.enerjolt.item.armor;

import me.twheatking.enerjolt.item.EnerjoltItems;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

import static me.twheatking.enerjolt.api.EJOLTAPI.MOD_ID;

/**
 * Defines the three endgame armor materials: Enerjolt, Cryonite, and Voidstone.
 * Each material has unique defense values and properties.
 */
public class EnerjoltArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, MOD_ID);

    /**
     * ENERJOLT ARMOR - Hazmat suit theme
     * Balanced defense with focus on protection from environmental hazards
     */
    public static final Holder<ArmorMaterial> ENERJOLT = ARMOR_MATERIALS.register("enerjolt",
            () -> new ArmorMaterial(
                    // Defense values per armor type
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.BODY, 11); // For entities
                    }),
                    // Enchantability
                    15,
                    // Equip sound
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    // Repair ingredient
                    () -> Ingredient.of(EnerjoltItems.ENERJOLT.get()),
                    // Equipment models (layer textures)
                    List.of(
                            new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "enerjolt")
                            )
                    ),
                    // Toughness
                    2.0F,
                    // Knockback resistance
                    0.0F
            )
    );

    /**
     * CRYONITE ARMOR - Coolant/Ice theme
     * Balanced defense with focus on fire and lava protection
     */
    public static final Holder<ArmorMaterial> CRYONITE = ARMOR_MATERIALS.register("cryonite",
            () -> new ArmorMaterial(
                    // Defense values per armor type
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.BODY, 11);
                    }),
                    // Enchantability
                    15,
                    // Equip sound
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    // Repair ingredient
                    () -> Ingredient.of(EnerjoltItems.CRYONITE.get()),
                    // Equipment models
                    List.of(
                            new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "cryonite")
                            )
                    ),
                    // Toughness
                    2.0F,
                    // Knockback resistance
                    0.0F
            )
    );

    /**
     * VOIDSTONE ARMOR - Tank/Endurance theme
     * Higher defense values for maximum protection
     */
    public static final Holder<ArmorMaterial> VOIDSTONE = ARMOR_MATERIALS.register("voidstone",
            () -> new ArmorMaterial(
                    // Defense values per armor type (naturally higher)
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.HELMET, 4);
                        map.put(ArmorItem.Type.CHESTPLATE, 9);
                        map.put(ArmorItem.Type.LEGGINGS, 7);
                        map.put(ArmorItem.Type.BOOTS, 4);
                        map.put(ArmorItem.Type.BODY, 12);
                    }),
                    // Enchantability
                    15,
                    // Equip sound
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    // Repair ingredient
                    () -> Ingredient.of(EnerjoltItems.VOIDSTONE.get()),
                    // Equipment models
                    List.of(
                            new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "voidstone")
                            )
                    ),
                    // Toughness (higher than others)
                    3.0F,
                    // Knockback resistance
                    0.0F
            )
    );
}
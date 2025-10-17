package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.loot.EnerjoltArmorLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

import static me.twheatking.enerjolt.api.EJOLTAPI.MOD_ID;

/**
 * Datagen for global loot modifiers that inject endgame armor into chest loot.
 * This generates the JSON files automatically.
 */
public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MOD_ID);
    }

    @Override
    protected void start() {
        // End Cities
        add("armor_in_end_cities", new EnerjoltArmorLootModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(
                                ResourceLocation.withDefaultNamespace("chests/end_city_treasure")
                        ).build()
                }
        ));

        // Nether Bastions (multiple chest types)
        add("armor_in_bastions", new EnerjoltArmorLootModifier(
                new LootItemCondition[]{
                        AnyOfCondition.anyOf(
                                LootTableIdCondition.builder(
                                        ResourceLocation.withDefaultNamespace("chests/bastion_treasure")
                                ),
                                LootTableIdCondition.builder(
                                        ResourceLocation.withDefaultNamespace("chests/bastion_bridge")
                                ),
                                LootTableIdCondition.builder(
                                        ResourceLocation.withDefaultNamespace("chests/bastion_hoglin_stable")
                                ),
                                LootTableIdCondition.builder(
                                        ResourceLocation.withDefaultNamespace("chests/bastion_other")
                                )
                        ).build()
                }
        ));

        // Nether Fortresses
        add("armor_in_fortresses", new EnerjoltArmorLootModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(
                                ResourceLocation.withDefaultNamespace("chests/nether_bridge")
                        ).build()
                }
        ));

        // Strongholds (multiple chest types)
        add("armor_in_strongholds", new EnerjoltArmorLootModifier(
                new LootItemCondition[]{
                        AnyOfCondition.anyOf(
                                LootTableIdCondition.builder(
                                        ResourceLocation.withDefaultNamespace("chests/stronghold_corridor")
                                ),
                                LootTableIdCondition.builder(
                                        ResourceLocation.withDefaultNamespace("chests/stronghold_crossing")
                                ),
                                LootTableIdCondition.builder(
                                        ResourceLocation.withDefaultNamespace("chests/stronghold_library")
                                )
                        ).build()
                }
        ));
    }
}
package me.twheatking.enerjolt.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.twheatking.enerjolt.item.EnerjoltItems;
import me.twheatking.enerjolt.item.armor.ArmorAttributeGenerator;
import me.twheatking.enerjolt.item.armor.ArmorRarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;

/**
 * Global Loot Modifier that adds random endgame armor to chest loot.
 * Uses NeoForge's loot modifier system to inject armor without replacing tables.
 */
public class EnerjoltArmorLootModifier extends LootModifier {
    public static final MapCodec<EnerjoltArmorLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).apply(inst, EnerjoltArmorLootModifier::new));

    // All armor items that can spawn
    private static final List<Item> ARMOR_ITEMS = List.of(
            // Enerjolt set
            EnerjoltItems.ENERJOLT_HELMET.get(),
            EnerjoltItems.ENERJOLT_CHESTPLATE.get(),
            EnerjoltItems.ENERJOLT_LEGGINGS.get(),
            EnerjoltItems.ENERJOLT_BOOTS.get(),

            // Cryonite set
            EnerjoltItems.CRYONITE_HELMET.get(),
            EnerjoltItems.CRYONITE_CHESTPLATE.get(),
            EnerjoltItems.CRYONITE_LEGGINGS.get(),
            EnerjoltItems.CRYONITE_BOOTS.get(),

            // Voidstone set
            EnerjoltItems.VOIDSTONE_HELMET.get(),
            EnerjoltItems.VOIDSTONE_CHESTPLATE.get(),
            EnerjoltItems.VOIDSTONE_LEGGINGS.get(),
            EnerjoltItems.VOIDSTONE_BOOTS.get()
    );

    public EnerjoltArmorLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Calculate total spawn chance (15% common + 5% rare + 1% legendary = 21%)
        double totalChance = ArmorRarity.getTotalSpawnChance();

        // Roll to see if armor should spawn
        if (context.getRandom().nextDouble() < totalChance) {
            // Select random armor piece
            Item armorItem = ARMOR_ITEMS.get(context.getRandom().nextInt(ARMOR_ITEMS.size()));
            ItemStack armorStack = new ItemStack(armorItem);

            // Generate random rarity and attributes
            ArmorAttributeGenerator.generateRandomArmor(armorStack, context.getRandom());

            // Add to loot
            generatedLoot.add(armorStack);
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
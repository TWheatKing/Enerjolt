package me.twheatking.enerjolt.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

/**
 * Defines the three rarity tiers for endgame armor pieces.
 * Each rarity determines the number of attributes and spawn chance.
 */
public enum ArmorRarity {
    COMMON(1, ChatFormatting.GREEN, 0.15, "common"),
    RARE(2, ChatFormatting.BLUE, 0.05, "rare"),
    LEGENDARY(3, ChatFormatting.LIGHT_PURPLE, 0.01, "legendary");

    private final int attributeCount;
    private final ChatFormatting color;
    private final double spawnChance;
    private final String name;

    ArmorRarity(int attributeCount, ChatFormatting color, double spawnChance, String name) {
        this.attributeCount = attributeCount;
        this.color = color;
        this.spawnChance = spawnChance;
        this.name = name;
    }

    /**
     * How many random attributes this rarity gets
     */
    public int getAttributeCount() {
        return attributeCount;
    }

    /**
     * The color used for displaying this rarity
     */
    public ChatFormatting getColor() {
        return color;
    }

    /**
     * The spawn chance in chest loot (0.0 to 1.0)
     */
    public double getSpawnChance() {
        return spawnChance;
    }

    /**
     * The translation key for this rarity
     */
    public String getTranslationKey() {
        return "armor.enerjolt.rarity." + name;
    }

    /**
     * Get display component with proper color
     */
    public Component getDisplayName() {
        return Component.translatable(getTranslationKey()).withStyle(color);
    }

    /**
     * Get rarity by ordinal (for deserialization)
     */
    public static ArmorRarity fromOrdinal(int ordinal) {
        ArmorRarity[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return COMMON;
        }
        return values[ordinal];
    }

    /**
     * Roll a random rarity based on spawn chances
     * Uses cumulative probability for fair distribution
     */
    public static ArmorRarity rollRandom(RandomSource random) {
        double roll = random.nextDouble();

        // Check legendary first (1%)
        if (roll < LEGENDARY.spawnChance) {
            return LEGENDARY;
        }

        // Check rare (5%)
        if (roll < LEGENDARY.spawnChance + RARE.spawnChance) {
            return RARE;
        }

        // Otherwise common (15%)
        // Note: Total chance is 21%, remaining 79% means no armor drops
        if (roll < LEGENDARY.spawnChance + RARE.spawnChance + COMMON.spawnChance) {
            return COMMON;
        }

        // No armor drops (fallback, shouldn't normally reach here in loot context)
        return COMMON;
    }

    /**
     * Get the total combined spawn chance for any armor
     */
    public static double getTotalSpawnChance() {
        return COMMON.spawnChance + RARE.spawnChance + LEGENDARY.spawnChance;
    }
}
package me.twheatking.enerjolt.item.armor;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates random attribute rolls for endgame armor pieces.
 * Handles rarity-based attribute counts and value ranges.
 */
public class ArmorAttributeGenerator {

    /**
     * Generate a complete armor piece with random rarity and attributes
     */
    public static ItemStack generateRandomArmor(ItemStack baseStack, RandomSource random) {
        // Roll rarity
        ArmorRarity rarity = ArmorRarity.rollRandom(random);

        // Generate attributes for this rarity
        return generateArmorWithRarity(baseStack, rarity, random);
    }

    /**
     * Generate an armor piece with a specific rarity
     */
    public static ItemStack generateArmorWithRarity(ItemStack stack, ArmorRarity rarity, RandomSource random) {
        // Set rarity
        EnergyArmorItem.setRarity(stack, rarity);

        // Generate random attributes
        List<ArmorAttributeRoll> attributes = generateRandomAttributes(rarity, random);
        EnergyArmorItem.setAttributes(stack, attributes);

        // Set initial energy to max
        int maxEnergy = EnergyArmorItem.getMaxEnergy(stack);
        EnergyArmorItem.setEnergy(stack, maxEnergy);

        return stack;
    }

    /**
     * Generate random attributes based on rarity
     */
    private static List<ArmorAttributeRoll> generateRandomAttributes(ArmorRarity rarity, RandomSource random) {
        List<ArmorAttributeRoll> rolls = new ArrayList<>();
        int attributeCount = rarity.getAttributeCount();

        // Get all available attributes for this rarity
        List<ArmorAttribute> availableAttributes = getAvailableAttributes(rarity);

        // Shuffle to randomize selection (manual Fisher-Yates shuffle)
        List<ArmorAttribute> shuffled = new ArrayList<>(availableAttributes);
        for (int i = shuffled.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            ArmorAttribute temp = shuffled.get(i);
            shuffled.set(i, shuffled.get(j));
            shuffled.set(j, temp);
        }

        // Pick the required number of attributes
        for (int i = 0; i < Math.min(attributeCount, shuffled.size()); i++) {
            ArmorAttribute attribute = shuffled.get(i);
            double value = rollAttributeValue(attribute, rarity, random);
            rolls.add(new ArmorAttributeRoll(attribute, value));
        }

        return rolls;
    }

    /**
     * Get list of attributes that can roll for this rarity
     */
    private static List<ArmorAttribute> getAvailableAttributes(ArmorRarity rarity) {
        List<ArmorAttribute> available = new ArrayList<>();

        for (ArmorAttribute attribute : ArmorAttribute.values()) {
            if (attribute.canRollForRarity(rarity)) {
                available.add(attribute);
            }
        }

        return available;
    }

    /**
     * Roll a random value for an attribute within its rarity range
     */
    private static double rollAttributeValue(ArmorAttribute attribute, ArmorRarity rarity, RandomSource random) {
        double minValue = attribute.getMinValue(rarity);
        double maxValue = attribute.getMaxValue(rarity);

        // Special case for step height (always 1.0)
        if (attribute == ArmorAttribute.STEP_HEIGHT) {
            return 1.0;
        }

        // Roll random value between min and max
        double range = maxValue - minValue;
        double roll = minValue + (random.nextDouble() * range);

        // Round to 1 decimal place for cleaner numbers
        return Math.round(roll * 10.0) / 10.0;
    }

    /**
     * Generate a specific rarity armor for creative/testing
     */
    public static ItemStack generateCommonArmor(ItemStack stack, RandomSource random) {
        return generateArmorWithRarity(stack, ArmorRarity.COMMON, random);
    }

    public static ItemStack generateRareArmor(ItemStack stack, RandomSource random) {
        return generateArmorWithRarity(stack, ArmorRarity.RARE, random);
    }

    public static ItemStack generateLegendaryArmor(ItemStack stack, RandomSource random) {
        return generateArmorWithRarity(stack, ArmorRarity.LEGENDARY, random);
    }

    /**
     * Re-roll attributes on an existing armor piece (for testing/creative)
     * Maintains current rarity
     */
    public static ItemStack rerollAttributes(ItemStack stack, RandomSource random) {
        ArmorRarity rarity = EnergyArmorItem.getRarity(stack);
        List<ArmorAttributeRoll> newAttributes = generateRandomAttributes(rarity, random);
        EnergyArmorItem.setAttributes(stack, newAttributes);

        // Update max energy and refill
        int maxEnergy = EnergyArmorItem.getMaxEnergy(stack);
        EnergyArmorItem.setEnergy(stack, maxEnergy);

        return stack;
    }

    /**
     * Create a "perfect roll" armor piece with max values on all attributes
     * For testing/creative purposes
     */
    public static ItemStack generatePerfectArmor(ItemStack stack, ArmorRarity rarity) {
        EnergyArmorItem.setRarity(stack, rarity);

        List<ArmorAttributeRoll> attributes = new ArrayList<>();
        List<ArmorAttribute> availableAttributes = getAvailableAttributes(rarity);

        // Take first N attributes with max values
        int count = Math.min(rarity.getAttributeCount(), availableAttributes.size());
        for (int i = 0; i < count; i++) {
            ArmorAttribute attribute = availableAttributes.get(i);
            double maxValue = attribute.getMaxValue(rarity);
            attributes.add(new ArmorAttributeRoll(attribute, maxValue));
        }

        EnergyArmorItem.setAttributes(stack, attributes);

        int maxEnergy = EnergyArmorItem.getMaxEnergy(stack);
        EnergyArmorItem.setEnergy(stack, maxEnergy);

        return stack;
    }
}
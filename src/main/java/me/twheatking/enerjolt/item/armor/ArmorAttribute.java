package me.twheatking.enerjolt.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Defines all possible attributes that can roll on endgame armor pieces.
 * Each attribute has different value ranges based on rarity tier.
 */
public enum ArmorAttribute {
    ENERGY_CAPACITY("energy_capacity", "%", ChatFormatting.AQUA),
    ENERGY_EFFICIENCY("energy_efficiency", "%", ChatFormatting.GOLD),
    MOVEMENT_SPEED("movement_speed", "%", ChatFormatting.WHITE),
    SWIM_SPEED("swim_speed", "%", ChatFormatting.BLUE),
    MINING_SPEED("mining_speed", "%", ChatFormatting.GRAY),
    ATTACK_DAMAGE("attack_damage", "", ChatFormatting.RED),
    MAX_HEALTH("max_health", " ❤", ChatFormatting.DARK_RED),
    ARMOR("armor", "", ChatFormatting.GRAY),
    ARMOR_TOUGHNESS("armor_toughness", "", ChatFormatting.DARK_GRAY),
    JUMP_BOOST("jump_boost", "%", ChatFormatting.GREEN),
    STEP_HEIGHT("step_height", "", ChatFormatting.YELLOW),
    KNOCKBACK_RESISTANCE("knockback_resistance", "%", ChatFormatting.DARK_PURPLE);

    private final String translationKey;
    private final String suffix;
    private final ChatFormatting color;

    ArmorAttribute(String translationKey, String suffix, ChatFormatting color) {
        this.translationKey = translationKey;
        this.suffix = suffix;
        this.color = color;
    }

    public String getTranslationKey() {
        return "attribute.enerjolt.armor." + translationKey;
    }

    public String getSuffix() {
        return suffix;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public Component getDisplayName() {
        return Component.translatable(getTranslationKey()).withStyle(color);
    }

    /**
     * Returns the min value for this attribute at the given rarity
     */
    public double getMinValue(ArmorRarity rarity) {
        return switch (this) {
            case ENERGY_CAPACITY -> switch (rarity) {
                case COMMON -> 5.0;
                case RARE -> 10.0;
                case LEGENDARY -> 20.0;
            };
            case ENERGY_EFFICIENCY -> switch (rarity) {
                case COMMON -> 5.0;
                case RARE -> 10.0;
                case LEGENDARY -> 20.0;
            };
            case MOVEMENT_SPEED -> switch (rarity) {
                case COMMON -> 2.0;
                case RARE -> 5.0;
                case LEGENDARY -> 10.0;
            };
            case SWIM_SPEED -> switch (rarity) {
                case COMMON -> 5.0;
                case RARE -> 10.0;
                case LEGENDARY -> 20.0;
            };
            case MINING_SPEED -> switch (rarity) {
                case COMMON -> 5.0;
                case RARE -> 10.0;
                case LEGENDARY -> 20.0;
            };
            case ATTACK_DAMAGE -> switch (rarity) {
                case COMMON -> 0.5;
                case RARE -> 1.5;
                case LEGENDARY -> 3.0;
            };
            case MAX_HEALTH -> switch (rarity) {
                case COMMON -> 1.0;
                case RARE -> 2.0;
                case LEGENDARY -> 4.0;
            };
            case ARMOR -> switch (rarity) {
                case COMMON -> 1.0;
                case RARE -> 2.0;
                case LEGENDARY -> 3.0;
            };
            case ARMOR_TOUGHNESS -> switch (rarity) {
                case COMMON -> 0.5;
                case RARE -> 1.0;
                case LEGENDARY -> 2.0;
            };
            case JUMP_BOOST -> switch (rarity) {
                case COMMON -> 5.0;
                case RARE -> 10.0;
                case LEGENDARY -> 20.0;
            };
            case STEP_HEIGHT -> 1.0; // Only legendary
            case KNOCKBACK_RESISTANCE -> switch (rarity) {
                case COMMON -> 5.0;
                case RARE -> 10.0;
                case LEGENDARY -> 20.0;
            };
        };
    }

    /**
     * Returns the max value for this attribute at the given rarity
     */
    public double getMaxValue(ArmorRarity rarity) {
        return switch (this) {
            case ENERGY_CAPACITY -> switch (rarity) {
                case COMMON -> 10.0;
                case RARE -> 20.0;
                case LEGENDARY -> 35.0;
            };
            case ENERGY_EFFICIENCY -> switch (rarity) {
                case COMMON -> 10.0;
                case RARE -> 20.0;
                case LEGENDARY -> 30.0;
            };
            case MOVEMENT_SPEED -> switch (rarity) {
                case COMMON -> 5.0;
                case RARE -> 10.0;
                case LEGENDARY -> 15.0;
            };
            case SWIM_SPEED -> switch (rarity) {
                case COMMON -> 10.0;
                case RARE -> 20.0;
                case LEGENDARY -> 35.0;
            };
            case MINING_SPEED -> switch (rarity) {
                case COMMON -> 10.0;
                case RARE -> 20.0;
                case LEGENDARY -> 35.0;
            };
            case ATTACK_DAMAGE -> switch (rarity) {
                case COMMON -> 1.5;
                case RARE -> 3.0;
                case LEGENDARY -> 5.0;
            };
            case MAX_HEALTH -> switch (rarity) {
                case COMMON -> 2.0;
                case RARE -> 4.0;
                case LEGENDARY -> 6.0;
            };
            case ARMOR -> switch (rarity) {
                case COMMON -> 2.0;
                case RARE -> 3.0;
                case LEGENDARY -> 5.0;
            };
            case ARMOR_TOUGHNESS -> switch (rarity) {
                case COMMON -> 1.0;
                case RARE -> 2.0;
                case LEGENDARY -> 3.0;
            };
            case JUMP_BOOST -> switch (rarity) {
                case COMMON -> 10.0;
                case RARE -> 20.0;
                case LEGENDARY -> 30.0;
            };
            case STEP_HEIGHT -> 1.0; // Only legendary
            case KNOCKBACK_RESISTANCE -> switch (rarity) {
                case COMMON -> 10.0;
                case RARE -> 20.0;
                case LEGENDARY -> 30.0;
            };
        };
    }

    /**
     * Check if this attribute can roll for the given rarity
     */
    public boolean canRollForRarity(ArmorRarity rarity) {
        // Step height is legendary-only
        if (this == STEP_HEIGHT) {
            return rarity == ArmorRarity.LEGENDARY;
        }
        return true;
    }

    /**
     * Format the value for display
     */
    public String formatValue(double value) {
        if (this == STEP_HEIGHT) {
            return "Full Block Climb";
        }

        // Format as integer for whole numbers, otherwise 1 decimal place
        if (value == Math.floor(value)) {
            return String.format("+%d%s", (int)value, suffix);
        } else {
            return String.format("+%.1f%s", value, suffix);
        }
    }
}
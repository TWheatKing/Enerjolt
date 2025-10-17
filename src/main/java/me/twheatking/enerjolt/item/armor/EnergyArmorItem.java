package me.twheatking.enerjolt.item.armor;

import me.twheatking.enerjolt.component.EnerjoltDataComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all endgame energy-powered armor.
 * Handles energy storage, damage system, random attributes, and tooltips.
 * Uses DataComponents (1.21.1+) instead of NBT.
 */
public abstract class EnergyArmorItem extends ArmorItem {
    // Base energy capacity for all endgame armor
    public static final int BASE_ENERGY_CAPACITY = 3_000_000;

    // Energy cost per damage point taken
    public static final int ENERGY_PER_DAMAGE = 1_500;

    protected EnergyArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    /**
     * Get the current energy stored in the armor piece
     */
    public static int getEnergy(ItemStack stack) {
        return stack.getOrDefault(EnerjoltDataComponentTypes.ARMOR_ENERGY.get(), 0);
    }

    /**
     * Set the energy stored in the armor piece
     */
    public static void setEnergy(ItemStack stack, int energy) {
        int maxEnergy = getMaxEnergy(stack);
        stack.set(EnerjoltDataComponentTypes.ARMOR_ENERGY.get(), Math.max(0, Math.min(energy, maxEnergy)));
    }

    /**
     * Get the maximum energy capacity (base + attribute bonuses)
     */
    public static int getMaxEnergy(ItemStack stack) {
        int baseCapacity = BASE_ENERGY_CAPACITY;

        // Apply energy capacity attribute bonus
        List<ArmorAttributeRoll> attributes = getAttributes(stack);
        for (ArmorAttributeRoll roll : attributes) {
            if (roll.getAttribute() == ArmorAttribute.ENERGY_CAPACITY) {
                double bonus = roll.getValue() / 100.0; // Convert percentage to multiplier
                baseCapacity += (int)(BASE_ENERGY_CAPACITY * bonus);
            }
        }

        return baseCapacity;
    }

    /**
     * Check if the armor piece has energy
     */
    public static boolean hasEnergy(ItemStack stack) {
        return getEnergy(stack) > 0;
    }

    /**
     * Check if the armor is depleted (no energy)
     */
    public static boolean isDepleted(ItemStack stack) {
        return getEnergy(stack) <= 0;
    }

    /**
     * Get the rarity of this armor piece
     */
    public static ArmorRarity getRarity(ItemStack stack) {
        int rarityOrdinal = stack.getOrDefault(EnerjoltDataComponentTypes.ARMOR_RARITY.get(), 0);
        return ArmorRarity.fromOrdinal(rarityOrdinal);
    }

    /**
     * Set the rarity of this armor piece
     */
    public static void setRarity(ItemStack stack, ArmorRarity rarity) {
        stack.set(EnerjoltDataComponentTypes.ARMOR_RARITY.get(), rarity.ordinal());
    }

    /**
     * Get all attribute rolls on this armor piece
     */
    public static List<ArmorAttributeRoll> getAttributes(ItemStack stack) {
        return stack.getOrDefault(EnerjoltDataComponentTypes.ARMOR_ATTRIBUTES.get(), new ArrayList<>());
    }

    /**
     * Set all attribute rolls on this armor piece
     */
    public static void setAttributes(ItemStack stack, List<ArmorAttributeRoll> attributes) {
        stack.set(EnerjoltDataComponentTypes.ARMOR_ATTRIBUTES.get(), attributes);
    }

    /**
     * Get the energy efficiency modifier (0.0 to 1.0)
     */
    private static double getEnergyEfficiency(ItemStack stack) {
        List<ArmorAttributeRoll> attributes = getAttributes(stack);
        double efficiency = 0.0;

        for (ArmorAttributeRoll roll : attributes) {
            if (roll.getAttribute() == ArmorAttribute.ENERGY_EFFICIENCY) {
                efficiency += roll.getValue() / 100.0;
            }
        }

        return Math.min(efficiency, 0.5); // Cap at 50% efficiency
    }

    /**
     * Display durability bar as energy bar
     */
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    /**
     * Show energy level as durability bar
     */
    @Override
    public int getBarWidth(ItemStack stack) {
        int energy = getEnergy(stack);
        int maxEnergy = getMaxEnergy(stack);
        if (maxEnergy == 0) return 0;
        return Math.round(13.0F * energy / maxEnergy);
    }

    /**
     * Color the bar based on energy level
     */
    @Override
    public int getBarColor(ItemStack stack) {
        int energy = getEnergy(stack);
        int maxEnergy = getMaxEnergy(stack);
        if (maxEnergy == 0) return 0xFF0000;

        float ratio = (float)energy / maxEnergy;

        if (ratio > 0.66f) {
            return 0x00FF00; // Green
        } else if (ratio > 0.33f) {
            return 0xFFFF00; // Yellow
        } else if (ratio > 0) {
            return 0xFF6600; // Orange
        } else {
            return 0xFF0000; // Red
        }
    }

    /**
     * Add tooltip information
     */
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ArmorRarity rarity = getRarity(stack);

        // Show rarity
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(rarity.getDisplayName());

        // Show energy status (matching your existing energy item style)
        int energy = getEnergy(stack);
        int maxEnergy = getMaxEnergy(stack);
        tooltipComponents.add(Component.translatable("tooltip.enerjolt.energy_meter.content.txt",
                        me.twheatking.enerjolt.util.EnergyUtils.getEnergyWithPrefix(energy),
                        me.twheatking.enerjolt.util.EnergyUtils.getEnergyWithPrefix(maxEnergy))
                .withStyle(energy > 0 ? ChatFormatting.AQUA : ChatFormatting.RED));

        // Show depleted warning
        if (isDepleted(stack)) {
            tooltipComponents.add(Component.literal("")
                    .append(Component.translatable("armor.enerjolt.depleted"))
                    .withStyle(ChatFormatting.RED));
        }

        // Show set name and bonuses
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("").append(getSetNameComponent()).withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("armor.enerjolt.bonus.2piece")
                .withStyle(ChatFormatting.GRAY)
                .append(" ")
                .append(get2PieceBonusComponent()));
        tooltipComponents.add(Component.translatable("armor.enerjolt.bonus.4piece")
                .withStyle(ChatFormatting.GRAY)
                .append(" ")
                .append(get4PieceBonusComponent()));

        // Show attributes
        List<ArmorAttributeRoll> attributes = getAttributes(stack);
        if (!attributes.isEmpty()) {
            tooltipComponents.add(Component.empty());
            for (ArmorAttributeRoll roll : attributes) {
                tooltipComponents.add(Component.literal("  ").append(roll.getDisplayComponent()));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    /**
     * Override in subclasses to provide set-specific name
     */
    protected abstract Component getSetNameComponent();

    /**
     * Override in subclasses to provide 2-piece bonus description
     */
    protected abstract Component get2PieceBonusComponent();

    /**
     * Override in subclasses to provide 4-piece bonus description
     */
    protected abstract Component get4PieceBonusComponent();

    /**
     * Make the armor chargeable in machines
     */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
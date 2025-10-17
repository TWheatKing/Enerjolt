package me.twheatking.enerjolt.item.armor;

import net.minecraft.network.chat.Component;

/**
 * Represents a single attribute roll on an armor piece.
 * Contains the attribute type and its rolled value.
 */
public class ArmorAttributeRoll {
    private final ArmorAttribute attribute;
    private final double value;

    public ArmorAttributeRoll(ArmorAttribute attribute, double value) {
        this.attribute = attribute;
        this.value = value;
    }

    public ArmorAttribute getAttribute() {
        return attribute;
    }

    public double getValue() {
        return value;
    }

    /**
     * Get formatted display text for tooltip
     */
    public Component getDisplayComponent() {
        String formattedValue = attribute.formatValue(value);
        return Component.literal(formattedValue + " ")
                .withStyle(attribute.getColor())
                .append(attribute.getDisplayName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ArmorAttributeRoll other)) return false;
        return attribute == other.attribute && Double.compare(value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        int result = attribute.hashCode();
        long temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ArmorAttributeRoll{" +
                "attribute=" + attribute +
                ", value=" + value +
                '}';
    }
}
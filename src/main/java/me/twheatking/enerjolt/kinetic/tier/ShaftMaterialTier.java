package me.twheatking.enerjolt.kinetic.tier;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Defines material tiers for kinetic transmission components (shafts, gearboxes, etc.)
 * Different materials have different friction, durability, and performance characteristics.
 */
public enum ShaftMaterialTier implements StringRepresentable {

    // Basic tier - cheap but inefficient
    WOOD(
            "wood",
            64.0f,          // Max RPM
            10.0f,          // Max Torque (Nm)
            0.15f,          // Friction coefficient (15% loss)
            50.0f,          // Max temperature (°C) before damage
            2.0f,           // Inertia
            0.5f            // Durability multiplier
    ),

    // Early tier - decent performance
    IRON(
            "iron",
            128.0f,         // Max RPM
            50.0f,          // Max Torque
            0.08f,          // Friction coefficient (8% loss)
            150.0f,         // Max temperature
            4.0f,           // Inertia
            1.0f            // Durability multiplier
    ),

    // Mid tier - good performance
    STEEL(
            "steel",
            256.0f,         // Max RPM
            150.0f,         // Max Torque
            0.04f,          // Friction coefficient (4% loss)
            300.0f,         // Max temperature
            6.0f,           // Inertia
            2.0f            // Durability multiplier
    ),

    // Advanced tier - excellent performance
    HARDENED_STEEL(
            "hardened_steel",
            384.0f,         // Max RPM
            300.0f,         // Max Torque
            0.02f,          // Friction coefficient (2% loss)
            500.0f,         // Max temperature
            8.0f,           // Inertia
            3.0f            // Durability multiplier
    ),

    // Late tier - near-perfect performance
    DIAMOND(
            "diamond",
            512.0f,         // Max RPM
            500.0f,         // Max Torque
            0.01f,          // Friction coefficient (1% loss)
            800.0f,         // Max temperature
            10.0f,          // Inertia
            5.0f            // Durability multiplier
    ),

    // Creative/endgame tier - unlimited
    CREATIVE(
            "creative",
            Float.MAX_VALUE, // Max RPM
            Float.MAX_VALUE, // Max Torque
            0.0f,           // No friction
            Float.MAX_VALUE, // No heat limit
            1.0f,           // Low inertia (instant response)
            Float.MAX_VALUE  // Indestructible
    );

    private final String name;
    private final float maxRPM;
    private final float maxTorque;
    private final float frictionCoefficient;
    private final float maxTemperature;
    private final float inertia;
    private final float durabilityMultiplier;

    ShaftMaterialTier(String name, float maxRPM, float maxTorque, float frictionCoefficient,
                      float maxTemperature, float inertia, float durabilityMultiplier) {
        this.name = name;
        this.maxRPM = maxRPM;
        this.maxTorque = maxTorque;
        this.frictionCoefficient = frictionCoefficient;
        this.maxTemperature = maxTemperature;
        this.inertia = inertia;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    /**
     * @return Maximum RPM this material can handle before breaking
     */
    public float getMaxRPM() {
        return maxRPM;
    }

    /**
     * @return Maximum torque this material can handle
     */
    public float getMaxTorque() {
        return maxTorque;
    }

    /**
     * @return Friction coefficient (0.0 = no loss, 1.0 = complete loss)
     * Lower is better - less energy lost to friction
     */
    public float getFrictionCoefficient() {
        return frictionCoefficient;
    }

    /**
     * @return Maximum safe operating temperature in Celsius
     * Exceeding this causes damage over time
     */
    public float getMaxTemperature() {
        return maxTemperature;
    }

    /**
     * @return Inertia value (resistance to speed changes)
     * Higher inertia = slower acceleration/deceleration but maintains momentum
     */
    public float getInertia() {
        return inertia;
    }

    /**
     * @return Durability multiplier for wear and tear
     * Higher values = lasts longer under stress
     */
    public float getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    /**
     * Calculates the efficiency of this material at a given RPM
     * @param currentRPM Current operating RPM
     * @return Efficiency multiplier (0.0 to 1.0)
     */
    public float getEfficiencyAtRPM(float currentRPM) {
        if (this == CREATIVE)
            return 1.0f;

        // Efficiency drops as we approach max RPM
        float rpmRatio = currentRPM / maxRPM;

        if (rpmRatio <= 0.5f) {
            // Under 50% max RPM - full efficiency
            return 1.0f;
        } else if (rpmRatio <= 0.8f) {
            // 50-80% max RPM - slight efficiency loss
            return 1.0f - (rpmRatio - 0.5f) * 0.2f; // 100% to 94%
        } else if (rpmRatio <= 1.0f) {
            // 80-100% max RPM - significant efficiency loss
            return 0.94f - (rpmRatio - 0.8f) * 1.5f; // 94% to 64%
        } else {
            // Over max RPM - severe efficiency loss and damage
            return Math.max(0.0f, 0.64f - (rpmRatio - 1.0f) * 2.0f);
        }
    }

    /**
     * Calculates heat generation per tick based on friction
     * @param rpm Current RPM
     * @param torque Current torque
     * @return Heat generated in degrees Celsius per tick
     */
    public float calculateHeatGeneration(float rpm, float torque) {
        if (this == CREATIVE)
            return 0.0f;

        // Heat = friction × power × time
        // Power = torque × angular velocity
        float angularVelocity = (rpm * 2.0f * (float)Math.PI) / 60.0f;
        float power = torque * angularVelocity;

        // Convert to heat (arbitrary scaling for gameplay)
        return frictionCoefficient * power * 0.001f;
    }

    /**
     * Checks if this material can handle the given conditions
     * @param rpm Current RPM
     * @param torque Current torque
     * @param temperature Current temperature
     * @return True if within safe operating limits
     */
    public boolean canHandleConditions(float rpm, float torque, float temperature) {
        if (this == CREATIVE)
            return true;

        return rpm <= maxRPM &&
                torque <= maxTorque &&
                temperature <= maxTemperature;
    }

    /**
     * Calculates damage rate when operating outside safe limits
     * @param rpm Current RPM
     * @param torque Current torque
     * @param temperature Current temperature
     * @return Damage per tick (0.0 = no damage, 1.0 = instant break)
     */
    public float calculateDamageRate(float rpm, float torque, float temperature) {
        if (this == CREATIVE)
            return 0.0f;

        float damageRate = 0.0f;

        // RPM overstress damage
        if (rpm > maxRPM) {
            float rpmOverstress = (rpm - maxRPM) / maxRPM;
            damageRate += rpmOverstress * 0.01f; // 1% per 100% overspeed
        }

        // Torque overstress damage
        if (torque > maxTorque) {
            float torqueOverstress = (torque - maxTorque) / maxTorque;
            damageRate += torqueOverstress * 0.02f; // 2% per 100% overtorque
        }

        // Heat damage
        if (temperature > maxTemperature) {
            float heatOverload = (temperature - maxTemperature) / maxTemperature;
            damageRate += heatOverload * 0.015f; // 1.5% per 100% overtemp
        }

        // Apply durability multiplier (better materials last longer)
        return damageRate / durabilityMultiplier;
    }

    /**
     * Gets the tier level (0 = wood, 5 = creative)
     * @return Tier index
     */
    public int getTierLevel() {
        return ordinal();
    }

    /**
     * Gets the next tier up (or current if already max)
     * @return Next material tier
     */
    public ShaftMaterialTier getNextTier() {
        int nextOrdinal = Math.min(ordinal() + 1, values().length - 1);
        return values()[nextOrdinal];
    }

    /**
     * Gets the previous tier down (or current if already min)
     * @return Previous material tier
     */
    public ShaftMaterialTier getPreviousTier() {
        int prevOrdinal = Math.max(ordinal() - 1, 0);
        return values()[prevOrdinal];
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

    /**
     * Gets a tier by name
     * @param name The tier name
     * @return The matching tier, or WOOD if not found
     */
    public static ShaftMaterialTier fromName(String name) {
        for (ShaftMaterialTier tier : values()) {
            if (tier.name.equals(name))
                return tier;
        }
        return WOOD;
    }

    /**
     * Gets a tier by ordinal (with bounds checking)
     * @param ordinal The tier index
     * @return The matching tier, or WOOD if out of bounds
     */
    public static ShaftMaterialTier fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length)
            return WOOD;
        return values()[ordinal];
    }
}
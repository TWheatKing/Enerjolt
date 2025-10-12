package me.twheatking.enerjolt.weather.wind;


/**
 * Defines the different levels of wind strength.
 * Each level has a power multiplier and a user-friendly display name.
 */
public enum WindStrength {
    // Defines the different strengths of wind, from calm to a severe gale.
    // multiplier: How much this wind level affects windmill power generation.
    // displayName: The name shown in the UI.
    CALM(1.0f, "Calm"), // Calm is the baseline, no bonus or penalty.
    GENTLE_BREEZE(1.3f, "Gentle Breeze"),
    MODERATE_WIND(1.6f, "Moderate Wind"),
    STRONG_WIND(2.0f, "Strong Wind"),
    GALE(2.5f, "Gale"),
    SEVERE_GALE(3.0f, "Severe Gale");

    public final float multiplier;
    public final String displayName;

    /**
     * Constructor for a WindStrength enum constant.
     * @param multiplier The power generation multiplier for this wind level.
     * @param displayName The name to be displayed in-game.
     */
    WindStrength(float multiplier, String displayName) {
        this.multiplier = multiplier;
        this.displayName = displayName;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if a wind event is currently active (i.e., not CALM).
     * @return true if the wind strength is anything other than CALM.
     */
    public boolean isActive() {
        return this != CALM;
    }
}

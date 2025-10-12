package me.twheatking.enerjolt.weather.wind;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * A data object that holds the state of a single wind event for a specific dimension.
 * This includes its strength, direction, and remaining duration.
 */
public class WindData {
    private static final String NBT_STRENGTH_KEY = "WindStrength";
    private static final String NBT_DIRECTION_KEY = "WindDirection";
    private static final String NBT_TICKS_REMAINING_KEY = "TicksRemaining";

    private WindStrength strength = WindStrength.CALM;
    private Direction direction = Direction.NORTH;
    private int ticksRemaining = 0;

    /**
     * Ticks down the duration of the current wind event. If it ends, resets to CALM.
     */
    public void tick() {
        if (this.ticksRemaining > 0) {
            this.ticksRemaining--;
            if (this.ticksRemaining == 0) {
                endEvent();
            }
        }
    }

    /**
     * Starts a new wind event with the given parameters.
     * @param strength The strength of the new event.
     * @param direction The direction of the new event.
     * @param durationTicks The duration in ticks for the new event.
     */
    public void startEvent(WindStrength strength, Direction direction, int durationTicks) {
        this.strength = strength;
        this.direction = direction;
        this.ticksRemaining = durationTicks;
    }

    /**
     * Resets the wind state to calm, effectively ending any active event.
     */
    public void endEvent() {
        this.strength = WindStrength.CALM;
        this.direction = Direction.NORTH; // Reset to a default direction
        this.ticksRemaining = 0;
    }

    // --- Getters ---
    public WindStrength getStrength() {
        return strength;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isActive() {
        return this.strength.isActive();
    }


    // --- Serialization for Saving/Loading ---

    /**
     * Saves the current wind data to an NBT compound tag.
     * @param tag The CompoundTag to write data to.
     * @return The CompoundTag with wind data.
     */
    public CompoundTag save(CompoundTag tag) {
        tag.putString(NBT_STRENGTH_KEY, this.strength.name());
        tag.putInt(NBT_DIRECTION_KEY, this.direction.get3DDataValue());
        tag.putInt(NBT_TICKS_REMAINING_KEY, this.ticksRemaining);
        return tag;
    }

    /**
     * Loads wind data from an NBT compound tag.
     * @param tag The CompoundTag to read data from.
     */
    public void load(CompoundTag tag) {
        try {
            this.strength = WindStrength.valueOf(tag.getString(NBT_STRENGTH_KEY));
        } catch (IllegalArgumentException e) {
            this.strength = WindStrength.CALM; // Default to calm if saved data is invalid
        }
        this.direction = Direction.from3DDataValue(tag.getInt(NBT_DIRECTION_KEY));
        this.ticksRemaining = tag.getInt(NBT_TICKS_REMAINING_KEY);
    }
}
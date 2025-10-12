package me.twheatking.enerjolt.weather.wind;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * The main logic handler for the wind weather system.
 * This manager is ticked on the server side to control wind events.
 */
public class WindWeatherManager {
    // Cooldown between wind events, in ticks. (20 ticks = 1 second)
    // 5 to 15 minutes cooldown.
    private static final int MIN_COOLDOWN = 20 * 60 * 5;
    private static final int MAX_COOLDOWN = 20 * 60 * 15;

    // Duration of wind events, in ticks.
    // 10 to 30 minutes duration.
    private static final int MIN_DURATION = 20 * 60 * 10;
    private static final int MAX_DURATION = 20 * 60 * 30;

    private static int cooldownTicks = 0;

    /**
     * The main tick method, called for each server level every tick.
     * @param level The ServerLevel to tick wind for.
     */
    public static void tick(ServerLevel level) {
        // We only want wind in the Overworld for now.
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        // This logic runs once per tick for the entire Overworld, not per level instance.
        if(cooldownTicks > 0) {
            cooldownTicks--;
        }

        WindSavedData savedData = WindSavedData.get(level);
        WindData windData = savedData.getWindData();

        if (windData.isActive()) {
            windData.tick();
            // If the event just ended, mark this saved data as dirty to ensure it saves.
            if(!windData.isActive()){
                savedData.setDirty();
            }
        } else {
            // If not active, check if we should start a new event.
            if (cooldownTicks <= 0) {
                // Use a small chance to start an event each tick once cooldown is over.
                if (level.random.nextInt(100) == 0) {
                    startRandomWindEvent(level, windData);
                    savedData.setDirty(); // Mark for saving
                    // Reset cooldown after an event starts
                    cooldownTicks = level.random.nextInt(MIN_COOLDOWN, MAX_COOLDOWN);
                }
            }
        }
    }

    /**
     * Starts a new wind event with random properties.
     * @param level The level to start the event in.
     * @param windData The WindData object to modify.
     */
    private static void startRandomWindEvent(ServerLevel level, WindData windData) {
        RandomSource random = level.random;

        // Pick a random wind strength, excluding CALM.
        WindStrength[] strengths = WindStrength.values();
        WindStrength strength = strengths[random.nextInt(1, strengths.length)]; // Skips CALM at index 0

        // Pick a random horizontal direction.
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        // Pick a random duration.
        int duration = random.nextInt(MIN_DURATION, MAX_DURATION);

        windData.startEvent(strength, direction, duration);
    }

    /**
     * Public accessor to get the current WindData for any level (client or server).
     * On the client, this will be empty/calm unless synced from the server.
     * For now, this is primarily for server-side access (like from a BlockEntity).
     * @param level The level to get wind data for.
     * @return The current WindData for that level.
     */
    public static WindData getWindData(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return WindSavedData.get(serverLevel).getWindData();
        }
        // Client-side syncing would be needed here for this to work on the client.
        // For now, it will just return a default (calm) state on the client.
        return new WindData();
    }
}

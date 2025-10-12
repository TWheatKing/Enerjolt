package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.kinetic.ProvideOnlyKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hand Crank - manual kinetic generator operated by player interaction.
 * Early-game power source requiring no resources, just player effort.
 *
 * Features:
 * - Right-click to crank and generate rotation
 * - Fast response (low inertia) for immediate feedback
 * - Moderate RPM, low torque (arm-powered)
 * - Gradually slows down when not cranked
 * - Can exhaust player if cranked too much
 */
public class HandCrankBlockEntity extends BaseKineticBlockEntity<ProvideOnlyKineticStorage> {

    // Hand crank specifications
    private static final float MAX_RPM = 128.0f;          // Moderate speed
    private static final float MAX_TORQUE = 20.0f;        // Low torque (human power)
    private static final float INERTIA = 0.5f;            // Very responsive
    private static final float FRICTION = 0.08f;          // Moderate friction

    // Cranking mechanics
    private static final float RPM_PER_CRANK = 32.0f;     // RPM added per crank
    private static final float TORQUE_PER_CRANK = 5.0f;   // Torque added per crank
    private static final int CRANK_COOLDOWN = 10;         // Ticks between cranks (0.5s)
    private static final float DECAY_RATE = 0.95f;        // Speed decay multiplier per tick

    // State tracking
    private int crankCooldown = 0;
    private int totalCranks = 0;                          // Total cranks (for stats/achievements)
    private int recentCranks = 0;                         // Cranks in last 100 ticks (for exhaustion)
    private int crankTimer = 0;                           // Timer for recent cranks window

    public HandCrankBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                null, // BlockEntityType - will be registered later
                blockPos,
                blockState,
                "hand_crank",
                MAX_RPM,
                MAX_TORQUE,
                INERTIA,
                FRICTION
        );
    }

    @Override
    protected ProvideOnlyKineticStorage initKineticStorage() {
        return new ProvideOnlyKineticStorage(0, baseMaxRPM, 0, baseMaxTorque, baseInertia, baseFriction) {
            @Override
            protected void onChange() {
                setChanged();
                syncKineticToPlayers(32);
            }
        };
    }

    /**
     * Hand cranks output rotation on all horizontal sides
     */
    @Override
    public @Nullable ProvideOnlyKineticStorage getKineticStorage(@Nullable Direction side) {
        if (side == null || side.getAxis().isHorizontal()) {
            return kineticStorage;
        }
        return null;
    }

    /**
     * Main tick method
     */
    public static void tick(Level level, BlockPos blockPos, BlockState state, HandCrankBlockEntity blockEntity) {
        if (level.isClientSide)
            return;

        // Cooldown management
        if (blockEntity.crankCooldown > 0) {
            blockEntity.crankCooldown--;
        }

        // Recent cranks timer
        blockEntity.crankTimer++;
        if (blockEntity.crankTimer >= 100) { // 5 second window
            blockEntity.crankTimer = 0;
            blockEntity.recentCranks = 0;
        }

        // Apply physics (decay, friction, cooling)
        blockEntity.applyPhysics();

        // Transfer rotation to connected blocks
        blockEntity.transferRotation(level, blockPos);

        setChanged(level, blockPos, state);
    }

    /**
     * Called when player right-clicks the hand crank
     * @param player The player cranking
     * @return True if crank was successful
     */
    public boolean onCrank(Player player) {
        if (crankCooldown > 0) {
            // Still on cooldown
            return false;
        }

        // Check if player is exhausted (too many recent cranks)
        if (recentCranks >= 20) {
            // Player is exhausted - add exhaustion and reduce efficiency
            player.causeFoodExhaustion(0.5f); // More exhaustion

            // Reduced power when exhausted
            float exhaustionMultiplier = 0.3f;
            kineticStorage.generateRotation(
                    RPM_PER_CRANK * exhaustionMultiplier,
                    TORQUE_PER_CRANK * exhaustionMultiplier
            );
        } else {
            // Normal cranking
            player.causeFoodExhaustion(0.1f); // Small food exhaustion

            kineticStorage.generateRotation(RPM_PER_CRANK, TORQUE_PER_CRANK);
        }

        // Update counters
        crankCooldown = CRANK_COOLDOWN;
        totalCranks++;
        recentCranks++;

        setChanged();
        syncKineticToPlayers(32);

        return true;
    }

    /**
     * Applies physics - decay, friction, cooling
     */
    private void applyPhysics() {
        float currentRPM = kineticStorage.getRPM();

        if (currentRPM > 0) {
            // Rapid decay when not being cranked (simulates stopping hand motion)
            currentRPM *= DECAY_RATE;

            // Additional friction loss
            float frictionLoss = currentRPM * baseFriction * 0.01f;
            currentRPM = Math.max(0, currentRPM - frictionLoss);

            kineticStorage.setRPM(currentRPM);

            // Torque also decays
            float currentTorque = kineticStorage.getTorque();
            currentTorque *= DECAY_RATE;
            kineticStorage.setTorque(currentTorque);
        }

        // Generate minimal heat (hand-powered doesn't generate much heat)
        if (currentRPM > 0) {
            float heatGeneration = kineticStorage.getTorque() * baseFriction * 0.01f;
            float currentTemp = kineticStorage.getTemperature();
            kineticStorage.setTemperature(currentTemp + heatGeneration);
        }

        // Cool down quickly (exposed to air)
        float currentTemp = kineticStorage.getTemperature();
        if (currentTemp > 20.0f) {
            float cooling = (currentTemp - 20.0f) * 0.05f; // 5% cooling per tick
            kineticStorage.setTemperature(Math.max(20.0f, currentTemp - cooling));
        }

        // Vibration from cranking motion
        float vibration = kineticStorage.getVibration();
        if (crankCooldown > 0) {
            // Just cranked - add vibration
            kineticStorage.setVibration(Math.min(1.0f, vibration + 0.05f));
        } else {
            // Vibration decay
            kineticStorage.setVibration(Math.max(0, vibration - 0.03f));
        }
    }

    /**
     * Transfers rotation to adjacent kinetic blocks
     */
    private void transferRotation(Level level, BlockPos pos) {
        if (!kineticStorage.isRotating())
            return;

        // Transfer to all horizontal neighbors
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos targetPos = pos.relative(direction);

            if (level.getBlockEntity(targetPos) instanceof BaseKineticBlockEntity<?> targetEntity) {
                var targetStorage = targetEntity.getKineticStorage(direction.getOpposite());

                if (targetStorage != null && targetStorage.canReceiveRotation()) {
                    // Transfer up to 40% of current rotation per tick (fast transfer for manual crank)
                    float transferRPM = kineticStorage.getRPM() * 0.4f;
                    float transferTorque = kineticStorage.getTorque() * 0.4f;

                    float actualTransfer = targetStorage.addRotation(transferRPM, transferTorque, false);

                    if (actualTransfer > 0) {
                        float torqueExtracted = transferTorque * (actualTransfer / transferRPM);
                        kineticStorage.extractRotation(actualTransfer, torqueExtracted, false);
                    }
                }
            }
        }
    }

    // ========== NBT PERSISTENCE ==========

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putInt("crank_cooldown", crankCooldown);
        nbt.putInt("total_cranks", totalCranks);
        nbt.putInt("recent_cranks", recentCranks);
        nbt.putInt("crank_timer", crankTimer);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        crankCooldown = nbt.getInt("crank_cooldown");
        totalCranks = nbt.getInt("total_cranks");
        recentCranks = nbt.getInt("recent_cranks");
        crankTimer = nbt.getInt("crank_timer");
    }

    // ========== GETTERS ==========

    /**
     * Gets the current crank cooldown
     * @return Ticks until next crank
     */
    public int getCrankCooldown() {
        return crankCooldown;
    }

    /**
     * Checks if the crank is ready to use
     * @return True if cooldown is 0
     */
    public boolean canCrank() {
        return crankCooldown == 0;
    }

    /**
     * Gets the total number of cranks performed
     * @return Total crank count
     */
    public int getTotalCranks() {
        return totalCranks;
    }

    /**
     * Gets the number of recent cranks (for exhaustion)
     * @return Recent crank count
     */
    public int getRecentCranks() {
        return recentCranks;
    }

    /**
     * Checks if player would be exhausted by cranking
     * @return True if too many recent cranks
     */
    public boolean isExhausted() {
        return recentCranks >= 20;
    }

    /**
     * Gets the exhaustion level as a percentage
     * @return Exhaustion (0.0 to 1.0)
     */
    public float getExhaustionLevel() {
        return Math.min(1.0f, recentCranks / 20.0f);
    }

    @Override
    public String getDebugInfo() {
        return String.format(
                "%s | RPM: %.1f | Torque: %.1f Nm | Cooldown: %d | Total: %d | Exhaustion: %.0f%%",
                machineName,
                kineticStorage.getRPM(),
                kineticStorage.getTorque(),
                crankCooldown,
                totalCranks,
                getExhaustionLevel() * 100.0f
        );
    }
}
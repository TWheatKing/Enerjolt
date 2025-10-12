package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.kinetic.ProvideOnlyKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * Water Wheel - generates rotation from flowing water.
 * Basic kinetic generator, early-game power source.
 *
 * Features:
 * - Generates rotation when water flows nearby
 * - RPM depends on water flow speed and direction
 * - Gradual spinup/spindown based on inertia
 * - More efficient when water flows in optimal direction
 */
public class WaterWheelBlockEntity extends BaseKineticBlockEntity<ProvideOnlyKineticStorage> {

    // Water wheel specifications
    private static final float MAX_RPM = 64.0f;           // Slow but steady
    private static final float MAX_TORQUE = 100.0f;       // High torque for low speed
    private static final float INERTIA = 8.0f;            // Heavy wheel = slow acceleration
    private static final float FRICTION = 0.03f;          // Low friction (water lubricated)

    // Generation rates
    private static final float RPM_PER_WATER_BLOCK = 8.0f;    // RPM generated per flowing water
    private static final float TORQUE_PER_WATER_BLOCK = 12.5f; // Torque per flowing water

    // Water detection
    private int waterBlocksDetected = 0;
    private float optimalFlowMultiplier = 1.0f;

    public WaterWheelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                null, // BlockEntityType - will be registered later
                blockPos,
                blockState,
                "water_wheel",
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
     * Water wheels only output rotation (can't receive input)
     */
    @Override
    public @Nullable ProvideOnlyKineticStorage getKineticStorage(@Nullable Direction side) {
        // Output on horizontal sides only (wheel axis)
        if (side == null || side.getAxis().isHorizontal()) {
            return kineticStorage;
        }
        return null;
    }

    /**
     * Main tick method - called every tick
     */
    public static void tick(Level level, BlockPos blockPos, BlockState state, WaterWheelBlockEntity blockEntity) {
        if (level.isClientSide)
            return;

        // Detect water flow around the wheel
        blockEntity.detectWaterFlow(level, blockPos);

        // Generate rotation based on water
        blockEntity.generateRotation();

        // Apply physics (friction, cooling)
        blockEntity.applyPhysics();

        // Transfer rotation to connected blocks
        blockEntity.transferRotation(level, blockPos);

        setChanged(level, blockPos, state);
    }

    /**
     * Detects water flow around the water wheel
     */
    private void detectWaterFlow(Level level, BlockPos pos) {
        waterBlocksDetected = 0;
        float totalFlowX = 0;
        float totalFlowZ = 0;

        // Check 3x3x3 area around the wheel
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    FluidState fluidState = level.getFluidState(checkPos);

                    if (fluidState.getType() == Fluids.FLOWING_WATER || fluidState.getType() == Fluids.WATER) {
                        waterBlocksDetected++;

                        // Detect flow direction (simplified)
                        if (fluidState.getType() == Fluids.FLOWING_WATER) {
                            totalFlowX += dx;
                            totalFlowZ += dz;
                        }
                    }
                }
            }
        }

        // Calculate optimal flow multiplier
        // Water flowing perpendicular to wheel axis is most efficient
        if (waterBlocksDetected > 0) {
            float flowStrength = (float) Math.sqrt(totalFlowX * totalFlowX + totalFlowZ * totalFlowZ);
            optimalFlowMultiplier = 0.5f + Math.min(1.0f, flowStrength / waterBlocksDetected);
        } else {
            optimalFlowMultiplier = 0.0f;
        }
    }

    /**
     * Generates rotation based on detected water
     */
    private void generateRotation() {
        if (waterBlocksDetected == 0) {
            // No water - wheel gradually stops due to friction
            return;
        }

        // Calculate target RPM and torque
        float targetRPM = Math.min(
                waterBlocksDetected * RPM_PER_WATER_BLOCK * optimalFlowMultiplier,
                baseMaxRPM
        );

        float targetTorque = Math.min(
                waterBlocksDetected * TORQUE_PER_WATER_BLOCK * optimalFlowMultiplier,
                baseMaxTorque
        );

        // Generate rotation (ProvideOnlyKineticStorage handles inertia-based spinup)
        kineticStorage.generateRotation(
                targetRPM * 0.1f,  // Gradual increase (10% per tick)
                targetTorque * 0.1f
        );
    }

    /**
     * Applies friction loss and cooling
     */
    private void applyPhysics() {
        // Apply friction loss
        float currentRPM = kineticStorage.getRPM();
        if (currentRPM > 0) {
            float frictionLoss = currentRPM * baseFriction * 0.01f;
            kineticStorage.setRPM(Math.max(0, currentRPM - frictionLoss));
        }

        // Generate heat from friction
        if (currentRPM > 0) {
            float heatGeneration = kineticStorage.getTorque() * baseFriction * 0.05f;
            float currentTemp = kineticStorage.getTemperature();
            kineticStorage.setTemperature(currentTemp + heatGeneration);
        }

        // Cool down (water cooling is very efficient)
        float currentTemp = kineticStorage.getTemperature();
        if (currentTemp > 20.0f) {
            float cooling = (currentTemp - 20.0f) * 0.1f; // 10% cooling per tick (water-cooled)
            kineticStorage.setTemperature(Math.max(20.0f, currentTemp - cooling));
        }

        // Calculate vibration from water turbulence
        float vibration = kineticStorage.getVibration();
        if (waterBlocksDetected > 0) {
            // Slight vibration from water flow
            float waterVibration = (waterBlocksDetected / 27.0f) * 0.1f; // Max 10% from full area
            kineticStorage.setVibration(Math.min(1.0f, vibration + waterVibration * 0.01f));
        } else {
            // Vibration decay when no water
            kineticStorage.setVibration(Math.max(0, vibration - 0.02f));
        }
    }

    /**
     * Transfers rotation to adjacent kinetic blocks
     */
    private void transferRotation(Level level, BlockPos pos) {
        if (!kineticStorage.isRotating())
            return;

        // Transfer to horizontal neighbors (wheel axis)
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos targetPos = pos.relative(direction);

            if (level.getBlockEntity(targetPos) instanceof BaseKineticBlockEntity<?> targetEntity) {
                var targetStorage = targetEntity.getKineticStorage(direction.getOpposite());

                if (targetStorage != null && targetStorage.canReceiveRotation()) {
                    // Transfer up to 25% of current rotation per tick
                    float transferRPM = kineticStorage.getRPM() * 0.25f;
                    float transferTorque = kineticStorage.getTorque() * 0.25f;

                    float actualTransfer = targetStorage.addRotation(transferRPM, transferTorque, false);

                    if (actualTransfer > 0) {
                        // Extract from source (with proportional torque)
                        float torqueExtracted = transferTorque * (actualTransfer / transferRPM);
                        kineticStorage.extractRotation(actualTransfer, torqueExtracted, false);
                    }
                }
            }
        }
    }

    /**
     * Gets the number of water blocks detected
     * @return Water block count
     */
    public int getWaterBlocksDetected() {
        return waterBlocksDetected;
    }

    /**
     * Gets the flow efficiency multiplier
     * @return Efficiency multiplier (0.0 to 1.5)
     */
    public float getFlowEfficiency() {
        return optimalFlowMultiplier;
    }

    /**
     * Checks if the water wheel is currently generating
     * @return True if water is present and wheel is spinning
     */
    public boolean isGenerating() {
        return waterBlocksDetected > 0 && kineticStorage.isRotating();
    }

    @Override
    public String getDebugInfo() {
        return String.format(
                "%s | Water: %d blocks | Flow: %.1f%% | RPM: %.1f/%.1f | Torque: %.1f Nm | Power: %.1f W",
                machineName,
                waterBlocksDetected,
                optimalFlowMultiplier * 100.0f,
                kineticStorage.getRPM(),
                kineticStorage.getMaxRPM(),
                kineticStorage.getTorque(),
                getPowerWatts()
        );
    }
}
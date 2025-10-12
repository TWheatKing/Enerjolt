package me.twheatking.enerjolt.kinetic.block.entity;
import me.twheatking.enerjolt.kinetic.BasicKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
import me.twheatking.enerjolt.kinetic.tier.ShaftMaterialTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

/**
 * Shaft BlockEntity - Basic Rotational Energy Transmission
 * Transfers kinetic energy between blocks with material-based efficiency
 *
 * Features:
 * - Material tier-based friction and efficiency
 * - Thermal buildup from friction
 * - Vibration generation at high speeds
 * - Directional energy transfer along axis
 * - Network connectivity
 * - Durability and damage system
 */
public class ShaftBlockEntity extends BaseKineticBlockEntity<BasicKineticStorage> {

    // Shaft orientation (horizontal or vertical)
    private Direction.Axis shaftAxis = Direction.Axis.Y;

    // Material tier determines all performance characteristics
    private ShaftMaterialTier materialTier = ShaftMaterialTier.IRON;

    // Vibration tracking
    private static final float VIBRATION_DAMAGE_THRESHOLD = 0.75f; // 75%

    // Durability system
    private float durability = 1.0f; // 1.0 = full health, 0.0 = broken
    private static final float MIN_DURABILITY = 0.0f;
    private int damageCooldown = 0;

    public ShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, ShaftMaterialTier.IRON);
    }

    public ShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ShaftMaterialTier tier) {
        super(type, pos, state,
                "Shaft",
                tier.getMaxRPM(),
                tier.getMaxTorque(),
                tier.getInertia(),
                tier.getFrictionCoefficient());

        this.materialTier = tier;
        updateShaftAxis(state);
    }

    @Override
    protected BasicKineticStorage initKineticStorage() {
        // Shafts use BasicKineticStorage because they both RECEIVE and PROVIDE rotation
        return new BasicKineticStorage(
                0,                      // Initial RPM
                baseMaxRPM,             // Max RPM
                0,                      // Initial torque
                baseMaxTorque,          // Max torque
                baseInertia,            // Inertia
                baseFriction            // Friction coefficient
        );
    }

    /**
     * Update shaft axis from block state
     */
    private void updateShaftAxis(BlockState state) {
        if (state.hasProperty(BlockStateProperties.AXIS)) {
            shaftAxis = state.getValue(BlockStateProperties.AXIS);
        }
    }

    /**
     * Main tick method - called every game tick
     */
    public void tick() {
        if (level == null) return;

        // Update shaft axis if changed
        updateShaftAxis(getBlockState());

        // Handle damage cooldown
        if (damageCooldown > 0) {
            damageCooldown--;
        }

        // Check if shaft is broken
        if (durability <= MIN_DURABILITY) {
            // Shaft is broken - no energy transfer
            kineticStorage.setRPMWithoutUpdate(0);
            kineticStorage.setTorqueWithoutUpdate(0);
            setChanged();
            return;
        }

        // Call the storage's tick method for friction, cooling, vibration
        kineticStorage.tick();

        // Transfer kinetic energy if receiving rotation
        if (isReceivingRotation()) {
            applyMaterialEfficiency();
            updateThermalEffects();
            updateVibrationEffects();
            checkForDamage();
        }

        // Sync to client periodically
        if (!level.isClientSide && level.getGameTime() % 20 == 0) {
            setChanged();
            syncKineticToPlayers(2); // 2 chunk range
        }
    }

    /**
     * Apply material-based efficiency modifiers to the rotation
     */
    private void applyMaterialEfficiency() {
        float currentRPM = kineticStorage.getRPM();
        float currentTorque = kineticStorage.getTorque();

        // Calculate efficiency at current RPM
        float efficiencyAtRPM = materialTier.getEfficiencyAtRPM(currentRPM);

        // Apply efficiency to torque (RPM stays same in basic shafts)
        float adjustedTorque = currentTorque * efficiencyAtRPM;

        // Apply vibration penalty if excessive
        float vibration = kineticStorage.getVibration();
        if (vibration > VIBRATION_DAMAGE_THRESHOLD) {
            float vibrationPenalty = 1.0f - ((vibration - VIBRATION_DAMAGE_THRESHOLD) / (1.0f - VIBRATION_DAMAGE_THRESHOLD) * 0.2f);
            adjustedTorque *= Math.max(0.7f, vibrationPenalty);
        }

        // Apply durability penalty (damaged shafts are less efficient)
        adjustedTorque *= (0.5f + (durability * 0.5f)); // 50% to 100% efficiency based on durability

        // Update storage with adjusted values
        if (adjustedTorque != currentTorque) {
            kineticStorage.setTorqueWithoutUpdate(adjustedTorque);
            setChanged();
        }
    }

    /**
     * Update thermal effects from friction
     */
    private void updateThermalEffects() {
        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();
        float currentTemp = kineticStorage.getTemperature();

        // Use material tier's heat calculation
        float heatGeneration = materialTier.calculateHeatGeneration(rpm, torque);

        // Add heat beyond what BasicKineticStorage already generates
        // (BasicKineticStorage already applies some heat in its tick method)
        float additionalHeat = heatGeneration * 0.5f; // 50% additional heat for shaft-specific calculations
        float newTemp = currentTemp + additionalHeat;
        kineticStorage.setTemperature(newTemp);
    }

    /**
     * Update vibration effects from high-speed rotation
     */
    private void updateVibrationEffects() {
        float rpm = kineticStorage.getRPM();
        float currentVibration = kineticStorage.getVibration();
        float maxRPM = materialTier.getMaxRPM();

        // Vibration increases with speed, especially near max RPM
        float targetVibration = 0;

        if (rpm > maxRPM * 0.5f) { // Above 50% max RPM
            float rpmRatio = rpm / maxRPM;
            // Vibration increases exponentially
            targetVibration = (float)Math.pow((rpmRatio - 0.5f) * 2.0f, 2.0f);
            targetVibration = Math.min(1.0f, targetVibration);
        }

        // Adjust vibration toward target (beyond BasicKineticStorage's basic vibration)
        if (targetVibration > currentVibration) {
            float vibrationIncrease = (targetVibration - currentVibration) * 0.1f;
            kineticStorage.setVibration(Math.min(1.0f, currentVibration + vibrationIncrease));
        }
    }

    /**
     * Check for damage conditions and apply damage
     */
    private void checkForDamage() {
        if (damageCooldown > 0) return;

        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();
        float temp = kineticStorage.getTemperature();

        // Use material tier's damage calculation
        float damageRate = materialTier.calculateDamageRate(rpm, torque, temp);

        if (damageRate > 0) {
            // Apply damage
            durability = Math.max(MIN_DURABILITY, durability - damageRate);
            damageCooldown = 20; // 1 second between damage ticks

            // Visual/audio feedback when damaged
            if (durability <= 0.25f && level.random.nextFloat() < 0.1f) {
                // TODO: Play damage sound, spawn particles
            }

            setChanged();
        }
    }

    /**
     * Repair the shaft (for maintenance mechanics)
     */
    public void repairShaft(float repairAmount) {
        durability = Math.min(1.0f, durability + repairAmount);
        setChanged();
    }

    /**
     * Fully repair the shaft
     */
    public void fullyRepair() {
        durability = 1.0f;
        damageCooldown = 0;
        setChanged();
    }

    /**
     * Get connected shaft positions along the axis
     */
    public BlockPos[] getConnectedShaftPositions() {
        Direction[] directions = getAxisDirections(shaftAxis);
        BlockPos[] positions = new BlockPos[directions.length];

        for (int i = 0; i < directions.length; i++) {
            positions[i] = worldPosition.relative(directions[i]);
        }

        return positions;
    }

    /**
     * Get the two directions for a given axis
     */
    private Direction[] getAxisDirections(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Direction[]{Direction.EAST, Direction.WEST};
            case Y -> new Direction[]{Direction.UP, Direction.DOWN};
            case Z -> new Direction[]{Direction.NORTH, Direction.SOUTH};
        };
    }

    /**
     * Check if shaft can connect to a direction
     */
    public boolean canConnectTo(Direction direction) {
        return direction.getAxis() == shaftAxis;
    }

    /**
     * Check if shaft can handle current conditions safely
     */
    public boolean isWithinSafeLimits() {
        return materialTier.canHandleConditions(
                kineticStorage.getRPM(),
                kineticStorage.getTorque(),
                kineticStorage.getTemperature()
        );
    }

    // Getters
    public Direction.Axis getShaftAxis() {
        return shaftAxis;
    }

    public ShaftMaterialTier getMaterialTier() {
        return materialTier;
    }

    public void setMaterialTier(ShaftMaterialTier tier) {
        this.materialTier = tier;

        // Update base values
        kineticStorage.setMaxRPMWithoutUpdate(tier.getMaxRPM());
        kineticStorage.setMaxTorqueWithoutUpdate(tier.getMaxTorque());
        kineticStorage.setInertiaWithoutUpdate(tier.getInertia());
        kineticStorage.setFrictionCoefficient(tier.getFrictionCoefficient());

        setChanged();
    }

    public float getDurability() {
        return durability;
    }

    public float getDurabilityPercentage() {
        return durability * 100.0f;
    }

    public boolean isBroken() {
        return durability <= MIN_DURABILITY;
    }

    public boolean isDamaged() {
        return durability < 1.0f;
    }

    public boolean isCriticalCondition() {
        return !isWithinSafeLimits() || kineticStorage.getVibration() > VIBRATION_DAMAGE_THRESHOLD;
    }

    public String getConditionStatus() {
        if (isBroken()) return "§cBROKEN";
        if (durability < 0.25f) return "§cCRITICAL";
        if (durability < 0.5f) return "§6DAMAGED";
        if (durability < 0.75f) return "§eWORN";
        return "§aGOOD";
    }

    // NBT Serialization
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("ShaftAxis", shaftAxis.getName());
        tag.putString("MaterialTier", materialTier.getSerializedName());
        tag.putFloat("Durability", durability);
        tag.putInt("DamageCooldown", damageCooldown);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            shaftAxis = Direction.Axis.byName(tag.getString("ShaftAxis"));
        } catch (Exception e) {
            shaftAxis = Direction.Axis.Y;
        }

        materialTier = ShaftMaterialTier.fromName(tag.getString("MaterialTier"));
        durability = tag.getFloat("Durability");
        damageCooldown = tag.getInt("DamageCooldown");
    }

    // Client-side display info
    @Override
    public String getDebugInfo() {
        return String.format(
                "%s [%s] | Axis: %s | RPM: %.1f/%.1f | Torque: %.1f/%.1f Nm | Power: %.1f W | Temp: %.1f°C | Vib: %.1f%% | Durability: %s (%.1f%%)",
                getMachineName(),
                materialTier.getSerializedName().toUpperCase(),
                shaftAxis.getName().toUpperCase(),
                kineticStorage.getRPM(),
                kineticStorage.getMaxRPM(),
                kineticStorage.getTorque(),
                kineticStorage.getMaxTorque(),
                getPowerWatts(),
                kineticStorage.getTemperature(),
                kineticStorage.getVibration() * 100.0f,
                getConditionStatus(),
                getDurabilityPercentage()
        );
    }
}
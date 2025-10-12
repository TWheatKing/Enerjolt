package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.kinetic.BasicKineticStorage;
import me.twheatking.enerjolt.kinetic.IKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
import me.twheatking.enerjolt.kinetic.tier.ShaftMaterialTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Clutch BlockEntity - Engagement/Disengagement System
 * Allows dynamic connection and disconnection of kinetic networks
 *
 * Features:
 * - Engage/disengage rotation transfer
 * - Redstone control
 * - Manual control via interaction
 * - Smooth engagement (prevents sudden stress)
 * - Friction heating during slip
 * - Wear and tear from engagement cycles
 * - Material-based durability
 */
public class ClutchBlockEntity extends BaseKineticBlockEntity<BasicKineticStorage> {

    /**
     * Clutch engagement states
     */
    public enum ClutchState {
        DISENGAGED(0.0f, "Disengaged"),
        ENGAGING(0.5f, "Engaging"),
        ENGAGED(1.0f, "Engaged"),
        DISENGAGING(0.5f, "Disengaging"),
        SLIPPING(0.3f, "Slipping");

        public final float transferEfficiency;
        public final String displayName;

        ClutchState(float efficiency, String name) {
            this.transferEfficiency = efficiency;
            this.displayName = name;
        }
    }

    // Clutch state
    private ClutchState clutchState = ClutchState.DISENGAGED;
    private float engagementLevel = 0.0f; // 0.0 = fully disengaged, 1.0 = fully engaged
    private static final float ENGAGEMENT_RATE = 0.05f; // How fast clutch engages/disengages per tick

    // Material tier
    private ShaftMaterialTier materialTier = ShaftMaterialTier.IRON;

    // Durability tracking
    private float durability = 1.0f;
    private int engagementCycles = 0;
    private static final int MAX_CYCLES_PER_DURABILITY = 1000; // Cycles before wear

    // Thermal properties from friction
    private static final float SLIP_HEAT_MULTIPLIER = 3.0f; // Slipping generates lots of heat
    private static final float ENGAGEMENT_HEAT = 5.0f; // Heat per engagement

    // Control
    private boolean wantsEngaged = false; // True = player/redstone wants engaged
    private boolean isPowered = false; // Redstone power state

    // Slipping detection
    private float lastInputRPM = 0.0f;
    private float lastOutputRPM = 0.0f;

    public ClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, ShaftMaterialTier.IRON);
    }

    public ClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ShaftMaterialTier tier) {
        super(type, pos, state,
                "Clutch",
                tier.getMaxRPM(),
                tier.getMaxTorque(),
                tier.getInertia() * 0.5f,  // Lower inertia for quick response
                tier.getFrictionCoefficient() * 2.0f); // Higher friction when slipping

        this.materialTier = tier;
    }

    @Override
    protected BasicKineticStorage initKineticStorage() {
        return new BasicKineticStorage(
                0,                      // Initial RPM
                baseMaxRPM,             // Max RPM
                0,                      // Initial torque
                baseMaxTorque,          // Max torque
                baseInertia,            // Lower inertia
                baseFriction            // Higher friction
        );
    }

    /**
     * Main tick method - called every game tick
     */
    public void tick() {
        if (level == null) return;

        // Update engagement level based on desired state
        updateEngagement();

        // Call the storage's tick method for basic friction, cooling, vibration
        kineticStorage.tick();

        // Process clutch logic if receiving rotation
        if (isReceivingRotation()) {
            processClutchTransfer();
            detectSlipping();
            updateThermalEffects();
            checkForWear();
        } else {
            // No input - disengage
            if (clutchState == ClutchState.ENGAGED || clutchState == ClutchState.ENGAGING) {
                clutchState = ClutchState.DISENGAGING;
            }
        }

        // Sync to client periodically
        if (!level.isClientSide && level.getGameTime() % 20 == 0) {
            setChanged();
            syncKineticToPlayers(2);
        }
    }

    /**
     * Update engagement level based on desired state
     */
    private void updateEngagement() {
        boolean shouldBeEngaged = wantsEngaged || isPowered;

        if (shouldBeEngaged && engagementLevel < 1.0f) {
            // Engaging
            engagementLevel = Math.min(1.0f, engagementLevel + ENGAGEMENT_RATE);
            clutchState = ClutchState.ENGAGING;

            if (engagementLevel >= 1.0f) {
                clutchState = ClutchState.ENGAGED;
                engagementCycles++;

                // Generate heat from engagement
                float currentTemp = kineticStorage.getTemperature();
                kineticStorage.setTemperature(currentTemp + ENGAGEMENT_HEAT);
            }
        } else if (!shouldBeEngaged && engagementLevel > 0.0f) {
            // Disengaging
            engagementLevel = Math.max(0.0f, engagementLevel - ENGAGEMENT_RATE);
            clutchState = ClutchState.DISENGAGING;

            if (engagementLevel <= 0.0f) {
                clutchState = ClutchState.DISENGAGED;
            }
        }
    }

    /**
     * Process clutch power transfer based on engagement level
     */
    private void processClutchTransfer() {
        float inputRPM = kineticStorage.getRPM();
        float inputTorque = kineticStorage.getTorque();

        // Store for slip detection
        lastInputRPM = inputRPM;

        // Calculate output based on engagement level
        float transferMultiplier = engagementLevel * clutchState.transferEfficiency;

        // Apply material efficiency
        float materialEfficiency = materialTier.getEfficiencyAtRPM(inputRPM);
        transferMultiplier *= materialEfficiency;

        // Apply durability penalty
        transferMultiplier *= (0.7f + (durability * 0.3f)); // 70% to 100% based on durability

        // Calculate output
        float outputRPM = inputRPM * transferMultiplier;
        float outputTorque = inputTorque * transferMultiplier;

        lastOutputRPM = outputRPM;

        // Update storage
        kineticStorage.setRPMWithoutUpdate(outputRPM);
        kineticStorage.setTorqueWithoutUpdate(outputTorque);

        setChanged();
    }

    /**
     * Detect if clutch is slipping (RPM mismatch during engagement)
     */
    private void detectSlipping() {
        if (clutchState == ClutchState.ENGAGING || clutchState == ClutchState.ENGAGED) {
            float rpmDifference = Math.abs(lastInputRPM - lastOutputRPM);
            float slipThreshold = lastInputRPM * 0.1f; // 10% difference = slipping

            if (rpmDifference > slipThreshold && engagementLevel > 0.5f) {
                clutchState = ClutchState.SLIPPING;
            }
        }
    }

    /**
     * Update thermal effects from friction (especially during slipping)
     */
    private void updateThermalEffects() {
        float rpm = kineticStorage.getRPM();
        float torque = kineticStorage.getTorque();

        // Base heat from material
        float baseHeat = materialTier.calculateHeatGeneration(rpm, torque);

        // Additional heat based on clutch state
        float clutchHeat = baseHeat;

        switch (clutchState) {
            case ENGAGING:
                // Moderate heat during engagement
                clutchHeat *= 1.5f;
                break;
            case SLIPPING:
                // Massive heat during slipping
                clutchHeat *= SLIP_HEAT_MULTIPLIER;
                break;
            case DISENGAGING:
                // Slight heat during disengagement
                clutchHeat *= 1.2f;
                break;
            case ENGAGED:
                // Normal heat when fully engaged
                break;
            case DISENGAGED:
                // No additional heat when disengaged
                clutchHeat = 0;
                break;
        }

        // Partial engagement generates extra heat
        if (engagementLevel > 0 && engagementLevel < 1.0f) {
            float partialEngagementHeat = (1.0f - engagementLevel) * baseHeat * 2.0f;
            clutchHeat += partialEngagementHeat;
        }

        // Apply heat
        float currentTemp = kineticStorage.getTemperature();
        kineticStorage.setTemperature(currentTemp + clutchHeat);
    }

    /**
     * Check for wear and tear on clutch
     */
    private void checkForWear() {
        // Wear based on engagement cycles
        if (engagementCycles >= MAX_CYCLES_PER_DURABILITY) {
            float wearAmount = 0.001f * materialTier.getDurabilityMultiplier();
            durability = Math.max(0.0f, durability - wearAmount);
            engagementCycles = 0;
        }

        // Additional wear from slipping
        if (clutchState == ClutchState.SLIPPING) {
            float slipWear = 0.0001f / materialTier.getDurabilityMultiplier();
            durability = Math.max(0.0f, durability - slipWear);
        }

        // Heat damage
        float temp = kineticStorage.getTemperature();
        float maxTemp = materialTier.getMaxTemperature();
        if (temp > maxTemp) {
            float heatDamageRate = materialTier.calculateDamageRate(kineticStorage.getRPM(), kineticStorage.getTorque(), temp);
            durability = Math.max(0.0f, durability - heatDamageRate);
        }

        // Broken clutch can't engage
        if (durability <= 0.1f) {
            wantsEngaged = false;
            clutchState = ClutchState.DISENGAGED;
            engagementLevel = 0.0f;
        }
    }

    /**
     * Toggle clutch engagement (manual control)
     */
    public void toggleEngagement() {
        wantsEngaged = !wantsEngaged;
        setChanged();
    }

    /**
     * Set clutch engagement directly
     */
    public void setEngaged(boolean engaged) {
        wantsEngaged = engaged;
        setChanged();
    }

    /**
     * Set redstone power state
     */
    public void setPowered(boolean powered) {
        this.isPowered = powered;
        setChanged();
    }

    /**
     * Repair the clutch
     */
    public void repairClutch(float repairAmount) {
        durability = Math.min(1.0f, durability + repairAmount);
        engagementCycles = 0;
        setChanged();
    }

    /**
     * Override to provide different storage for input vs output sides
     */
    @Override
    public @Nullable IKineticStorage getKineticStorage(@Nullable Direction side) {
        // Input side gets full rotation, output side gets engaged rotation
        // This is simplified - actual implementation would depend on block facing
        return kineticStorage;
    }

    // Getters
    public ClutchState getClutchState() {
        return clutchState;
    }

    public float getEngagementLevel() {
        return engagementLevel;
    }

    public float getEngagementPercentage() {
        return engagementLevel * 100.0f;
    }

    public boolean isEngaged() {
        return clutchState == ClutchState.ENGAGED;
    }

    public boolean isSlipping() {
        return clutchState == ClutchState.SLIPPING;
    }

    public boolean wantsEngaged() {
        return wantsEngaged;
    }

    public boolean isPowered() {
        return isPowered;
    }

    public float getDurability() {
        return durability;
    }

    public float getDurabilityPercentage() {
        return durability * 100.0f;
    }

    public int getEngagementCycles() {
        return engagementCycles;
    }

    public ShaftMaterialTier getMaterialTier() {
        return materialTier;
    }

    public void setMaterialTier(ShaftMaterialTier tier) {
        this.materialTier = tier;

        // Update base values
        kineticStorage.setMaxRPMWithoutUpdate(tier.getMaxRPM());
        kineticStorage.setMaxTorqueWithoutUpdate(tier.getMaxTorque());
        kineticStorage.setInertiaWithoutUpdate(tier.getInertia() * 0.5f);
        kineticStorage.setFrictionCoefficient(tier.getFrictionCoefficient() * 2.0f);

        setChanged();
    }

    public String getClutchCondition() {
        if (durability >= 0.9f) return "§aExcellent";
        if (durability >= 0.7f) return "§aGood";
        if (durability >= 0.5f) return "§eWorn";
        if (durability >= 0.3f) return "§6Damaged";
        if (durability >= 0.1f) return "§cCritical";
        return "§cBroken";
    }

    // NBT Serialization
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("ClutchState", clutchState.name());
        tag.putFloat("EngagementLevel", engagementLevel);
        tag.putString("MaterialTier", materialTier.getSerializedName());
        tag.putFloat("Durability", durability);
        tag.putInt("EngagementCycles", engagementCycles);
        tag.putBoolean("WantsEngaged", wantsEngaged);
        tag.putBoolean("IsPowered", isPowered);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            clutchState = ClutchState.valueOf(tag.getString("ClutchState"));
        } catch (IllegalArgumentException e) {
            clutchState = ClutchState.DISENGAGED;
        }

        engagementLevel = tag.getFloat("EngagementLevel");
        materialTier = ShaftMaterialTier.fromName(tag.getString("MaterialTier"));
        durability = tag.getFloat("Durability");
        engagementCycles = tag.getInt("EngagementCycles");
        wantsEngaged = tag.getBoolean("WantsEngaged");
        isPowered = tag.getBoolean("IsPowered");
    }

    // Client-side display info
    @Override
    public String getDebugInfo() {
        return String.format(
                "%s [%s] | State: %s (%.1f%%) | RPM: %.1f → %.1f | Torque: %.1f Nm | Temp: %.1f°C | Condition: %s (%.1f%%) | Cycles: %d",
                getMachineName(),
                materialTier.getSerializedName().toUpperCase(),
                clutchState.displayName,
                getEngagementPercentage(),
                lastInputRPM,
                lastOutputRPM,
                kineticStorage.getTorque(),
                kineticStorage.getTemperature(),
                getClutchCondition(),
                getDurabilityPercentage(),
                engagementCycles
        );
    }

    public String getClutchStatus() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("§e%s§r (%.1f%%)\n", clutchState.displayName, getEngagementPercentage()));
        status.append(String.format("Input: §b%.1f RPM§r\n", lastInputRPM));
        status.append(String.format("Output: §b%.1f RPM§r\n", lastOutputRPM));
        status.append(String.format("Temperature: §c%.1f°C§r\n", kineticStorage.getTemperature()));
        status.append(String.format("Condition: %s§r (%.1f%%)\n", getClutchCondition(), getDurabilityPercentage()));
        status.append(String.format("Cycles: %d", engagementCycles));

        if (isSlipping()) {
            status.append("\n§c⚠ SLIPPING!§r");
        }

        if (isOverheating()) {
            status.append("\n§c⚠ OVERHEATING!§r");
        }

        return status.toString();
    }
}

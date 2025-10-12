package me.twheatking.enerjolt.kinetic.block.entity;

import me.twheatking.enerjolt.kinetic.ProvideOnlyKineticStorage;
import me.twheatking.enerjolt.kinetic.block.entity.base.BaseKineticBlockEntity;
// NEW: Import the new wind system classes
import me.twheatking.enerjolt.weather.wind.WindData;
import me.twheatking.enerjolt.weather.wind.WindStrength;
import me.twheatking.enerjolt.weather.wind.WindWeatherManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WindmillBlockEntity extends BaseKineticBlockEntity<ProvideOnlyKineticStorage> {

    // ... existing enum RotorSize ...
    public enum RotorSize {
        SMALL(1.0f, 48.0f, 15.0f, 3, "Small"),
        MEDIUM(1.8f, 80.0f, 30.0f, 5, "Medium"),
        LARGE(3.0f, 128.0f, 50.0f, 7, "Large"),
        MASSIVE(5.0f, 192.0f, 80.0f, 9, "Massive");

        public final float powerMultiplier;
        public final float maxRPM;
        public final float maxTorque;
        public final int rotorRadius; // Blocks from center
        public final String displayName;

        RotorSize(float power, float rpm, float torque, int radius, String name) {
            this.powerMultiplier = power;
            this.maxRPM = rpm;
            this.maxTorque = torque;
            this.rotorRadius = radius;
            this.displayName = name;
        }
    }


    private RotorSize rotorSize = RotorSize.MEDIUM;
    // MODIFIED: This is now set in your WindmillBlock, but we still need it here.
    private Direction facing = Direction.NORTH;

    // ... existing fields for wind conditions, obstruction, etc. ...
    private float windStrength = 0.0f;
    private float heightBonus = 1.0f;
    private float biomeWindFactor = 1.0f;
    private float timeOfDayFactor = 1.0f;

    private int obstructionCount = 0;
    private float obstructionPenalty = 1.0f;
    private boolean hasClearSky = true;

    private float targetRPM = 0.0f;
    private float currentGeneratedTorque = 0.0f;
    private boolean isGenerating = false;

    private static final float SPIN_ACCELERATION = 0.3f;
    private int windCheckTimer = 0;
    private static final int WIND_CHECK_INTERVAL = 40;

    private boolean isStormy = false;
    private int stormDamageTimer = 0;
    private float structuralIntegrity = 1.0f;

    private float averagePowerOutput = 0.0f;
    private long totalEnergyGenerated = 0;
    private int generationTicks = 0;

    // NEW: Fields for tracking custom wind events
    private WindStrength currentWindEvent = WindStrength.CALM;
    private Direction windDirection = Direction.NORTH;
    private float windEventMultiplier = 1.0f;
    private float facingBonus = 1.0f;
    private static final float OPTIMAL_FACING_BONUS = 1.5f; // 50% bonus
    private static final float WRONG_FACING_PENALTY = 0.6f; // 40% penalty

    // NEW: NBT Keys for client syncing
    private static final String NBT_WIND_EVENT_STRENGTH_KEY = "WindEventStrength";
    private static final String NBT_WIND_EVENT_DIRECTION_KEY = "WindEventDirection";


    public WindmillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, RotorSize.MEDIUM);
    }

    public WindmillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RotorSize size) {
        super(type, pos, state, "Windmill", size.maxRPM, size.maxTorque, 4.0f, 0.02f);
        this.rotorSize = size;
    }

    @Override
    protected ProvideOnlyKineticStorage initKineticStorage() {
        return new ProvideOnlyKineticStorage(0, baseMaxRPM, 0, baseMaxTorque, baseInertia, baseFriction);
    }

    public void tick() {
        if (level == null) return;

        // NEW: On the server, update from the global wind manager each tick.
        if (!level.isClientSide) {
            updateFromWindEvent();
        }

        windCheckTimer++;

        if (windCheckTimer >= WIND_CHECK_INTERVAL) {
            updateWindConditions();
            windCheckTimer = 0;
        }

        calculateWindPower();
        updateRotation();

        if (kineticStorage.getRPM() > 1.0f) {
            generatePower();
            isGenerating = true;
            generationTicks++;
        } else {
            isGenerating = false;
        }

        if (isStormy) {
            checkStormDamage();
        }

        if (generationTicks % 20 == 0) {
            updatePerformanceMetrics();
        }

        // MODIFIED: This existing call will now also sync our new wind data to clients.
        if (!level.isClientSide && level.getGameTime() % 20 == 0) {
            setChanged(); // This marks the block entity for saving and client updates.
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            syncKineticToPlayers(2);
        }
    }

    // NEW: Method to fetch global wind data from the manager.
    private void updateFromWindEvent() {
        if(level == null || level.isClientSide()){
            return;
        }
        WindData windData = WindWeatherManager.getWindData(level);
        this.currentWindEvent = windData.getStrength();
        this.windDirection = windData.getDirection();
        this.windEventMultiplier = this.currentWindEvent.getMultiplier();

        calculateFacingBonus();
    }

    // NEW: Method to calculate the bonus/penalty for facing into the wind.
    private void calculateFacingBonus() {
        if (!currentWindEvent.isActive()) {
            this.facingBonus = 1.0f; // No bonus if no event is active
            return;
        }

        // The 'facing' field is already in the class from your block state
        if (facing == windDirection) {
            this.facingBonus = OPTIMAL_FACING_BONUS;
        } else if (facing == windDirection.getOpposite()) {
            this.facingBonus = WRONG_FACING_PENALTY;
        } else {
            this.facingBonus = 1.0f; // Perpendicular to the wind
        }
    }


    private void updateWindConditions() {
        // ... (this method remains mostly the same) ...
        if (level == null) return;

        hasClearSky = level.canSeeSky(worldPosition.above());

        if (!hasClearSky) {
            windStrength = 0.0f;
            return;
        }

        calculateHeightBonus();
        calculateBiomeWindFactor();
        calculateTimeOfDayFactor();
        updateWeatherConditions(); // This method is now less important
        checkObstructions();
        calculateFinalWindStrength();
    }

    // ... calculateHeightBonus, calculateBiomeWindFactor, calculateTimeOfDayFactor are unchanged ...
    private void calculateHeightBonus() {
        int height = worldPosition.getY();
        int seaLevel = level.getSeaLevel();

        if (height <= seaLevel) {
            heightBonus = 0.3f;
        } else if (height <= seaLevel + 32) {
            heightBonus = 0.5f + ((height - seaLevel) / 32.0f) * 0.3f;
        } else if (height <= seaLevel + 96) {
            heightBonus = 0.8f + ((height - seaLevel - 32) / 64.0f) * 0.5f;
        } else {
            heightBonus = 1.3f + ((height - seaLevel - 96) / 128.0f) * 0.7f;
            heightBonus = Math.min(2.0f, heightBonus);
        }
    }

    private void calculateBiomeWindFactor() {
        Biome biome = level.getBiome(worldPosition).value();
        Biome.ClimateSettings climate = biome.getModifiedClimateSettings();
        float temperature = climate.temperature();

        if (temperature > 1.5f) {
            biomeWindFactor = 1.4f;
        } else if (temperature < 0.0f) {
            biomeWindFactor = 1.3f;
        } else if (temperature < 0.3f) {
            biomeWindFactor = 1.1f;
        } else {
            biomeWindFactor = 1.0f;
        }

        if (climate.downfall() > 0.5f) {
            biomeWindFactor *= 0.9f;
        }
    }

    private void calculateTimeOfDayFactor() {
        long timeOfDay = level.getDayTime() % 24000;

        if (timeOfDay >= 0 && timeOfDay < 6000) {
            timeOfDayFactor = 0.6f + (timeOfDay / 6000.0f) * 0.3f;
        } else if (timeOfDay >= 6000 && timeOfDay < 12000) {
            timeOfDayFactor = 0.9f + ((timeOfDay - 6000) / 6000.0f) * 0.5f;
        } else if (timeOfDay >= 12000 && timeOfDay < 18000) {
            timeOfDayFactor = 1.4f - ((timeOfDay - 12000) / 6000.0f) * 0.6f;
        } else {
            timeOfDayFactor = 0.8f + ((timeOfDay - 18000) / 6000.0f) * 0.3f;
        }
    }

    /**
     * MODIFIED: This method now only provides a small base multiplier for vanilla weather.
     * The main storm effect comes from the custom Wind Event.
     */
    private void updateWeatherConditions() {
        if (level.isThundering()) {
            isStormy = true;
            windStrength = 1.2f; // Small base boost for storms
        } else if (level.isRaining()) {
            isStormy = false;
            windStrength = 1.1f; // Tiny base boost for rain
        } else {
            isStormy = false;
            windStrength = 1.0f; // Base for clear weather
        }
    }

    // ... checkObstructions is unchanged ...
    private void checkObstructions() {
        obstructionCount = 0;
        int radius = rotorSize.rotorRadius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    BlockPos checkPos = worldPosition.relative(facing, 1).offset(x, y, 0);
                    if (!level.getBlockState(checkPos).isAir()) {
                        obstructionCount++;
                    }
                }
            }
        }
        int totalBlocks = (int)(Math.PI * radius * radius);
        float obstructionRatio = (float)obstructionCount / totalBlocks;
        obstructionPenalty = 1.0f - (obstructionRatio * 0.8f);
        obstructionPenalty = Math.max(0.1f, obstructionPenalty);
    }


    /**
     * MODIFIED: The final wind strength calculation now includes the event multiplier and facing bonus.
     */
    private void calculateFinalWindStrength() {
        // Combine all factors, including the new event data
        windStrength *= heightBonus * biomeWindFactor * timeOfDayFactor
                * obstructionPenalty * windEventMultiplier * facingBonus;

        windStrength *= structuralIntegrity;

        // Increased the cap to allow for powerful gales
        windStrength = Math.max(0.0f, Math.min(3.5f, windStrength));
    }

    // ... other methods like calculateWindPower, updateRotation, generatePower etc. are unchanged ...
    private void calculateWindPower() {
        if (!hasClearSky || windStrength < 0.1f) {
            targetRPM = 0.0f;
            currentGeneratedTorque = 0.0f;
            return;
        }
        float basePower = windStrength * rotorSize.powerMultiplier;
        targetRPM = Math.min(baseMaxRPM, basePower * 12.0f);
        currentGeneratedTorque = Math.min(baseMaxTorque, basePower * 2.0f);
        targetRPM = Math.min(baseMaxRPM, targetRPM);
        currentGeneratedTorque = Math.min(baseMaxTorque, currentGeneratedTorque);
    }

    private void updateRotation() {
        float currentRPM = kineticStorage.getRPM();
        if (currentRPM < targetRPM) {
            float spinUpAmount = Math.min(SPIN_ACCELERATION, targetRPM - currentRPM);
            kineticStorage.setRPMWithoutUpdate(currentRPM + spinUpAmount);
        } else if (currentRPM > targetRPM) {
            float spinDownAmount = Math.min(SPIN_ACCELERATION * 0.5f, currentRPM - targetRPM);
            kineticStorage.setRPMWithoutUpdate(currentRPM - spinDownAmount);
        }
    }

    private void generatePower() {
        float rpmToGenerate = targetRPM * 0.08f;
        float torqueToGenerate = currentGeneratedTorque * 0.08f;
        kineticStorage.generateRotation(rpmToGenerate, torqueToGenerate);
        totalEnergyGenerated += (long)kineticStorage.getPowerWatts();
    }

    private void checkStormDamage() {
        stormDamageTimer++;
        if (stormDamageTimer >= 100 && level.random.nextFloat() < 0.1f) {
            float damage = 0.01f;
            structuralIntegrity = Math.max(0.2f, structuralIntegrity - damage);
            stormDamageTimer = 0;
        }
    }

    public void repairStructure(float repairAmount) {
        structuralIntegrity = Math.min(1.0f, structuralIntegrity + repairAmount);
        setChanged();
    }

    private void updatePerformanceMetrics() {
        averagePowerOutput = kineticStorage.getPowerWatts();
    }

    public float getWindStrengthPercentage() {
        return (windStrength / 3.5f) * 100.0f; // MODIFIED: Use new max strength for percentage
    }

    // ... other getters are unchanged ...
    public float getGenerationEfficiency() {
        if (targetRPM == 0) return 0.0f;
        return (kineticStorage.getRPM() / targetRPM) * 100.0f;
    }
    public RotorSize getRotorSize() { return rotorSize; }
    public void setRotorSize(RotorSize size) { this.rotorSize = size; kineticStorage.setMaxRPMWithoutUpdate(size.maxRPM); kineticStorage.setMaxTorqueWithoutUpdate(size.maxTorque); setChanged(); }
    public Direction getFacing() { return facing; }
    public void setFacing(Direction facing) { this.facing = facing; setChanged(); }
    public float getWindStrength() { return windStrength; }
    public float getHeightBonus() { return heightBonus; }
    public float getBiomeWindFactor() { return biomeWindFactor; }
    public float getTimeOfDayFactor() { return timeOfDayFactor; }
    public int getObstructionCount() { return obstructionCount; }
    public float getObstructionPenalty() { return obstructionPenalty; }
    public boolean hasClearSky() { return hasClearSky; }
    public float getTargetRPM() { return targetRPM; }
    public boolean isGenerating() { return isGenerating; }
    public boolean isStormy() { return isStormy; }
    public float getStructuralIntegrity() { return structuralIntegrity; }
    public boolean needsRepair() { return structuralIntegrity < 0.9f; }
    public long getTotalEnergyGenerated() { return totalEnergyGenerated; }


    // --- NBT and Client Syncing ---

    // MODIFIED: We only save base data to disk. Event data is transient.
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("RotorSize", rotorSize.name());
        tag.putString("Facing", facing.getName());
        // We no longer save windStrength and other derived values, they are recalculated.
        tag.putFloat("StructuralIntegrity", structuralIntegrity);
        tag.putLong("TotalEnergyGenerated", totalEnergyGenerated);
    }

    // MODIFIED: We only load base data from disk.
    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        try {
            rotorSize = RotorSize.valueOf(tag.getString("RotorSize"));
        } catch (IllegalArgumentException e) {
            rotorSize = RotorSize.MEDIUM;
        }
        facing = Direction.byName(tag.getString("Facing"));
        if (tag.contains("StructuralIntegrity")) {
            structuralIntegrity = tag.getFloat("StructuralIntegrity");
        }
        if (tag.contains("TotalEnergyGenerated")) {
            totalEnergyGenerated = tag.getLong("TotalEnergyGenerated");
        }
    }

    // NEW: Overriding methods to sync custom data to the client for UI rendering.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries); // Re-use save logic for syncing
        // Add our custom wind data to the sync packet
        tag.putString(NBT_WIND_EVENT_STRENGTH_KEY, this.currentWindEvent.name());
        tag.putInt(NBT_WIND_EVENT_DIRECTION_KEY, this.windDirection.get3DDataValue());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries); // Re-use load logic for syncing
        // Receive the data on the client and update fields
        if (tag.contains(NBT_WIND_EVENT_STRENGTH_KEY)) {
            try {
                this.currentWindEvent = WindStrength.valueOf(tag.getString(NBT_WIND_EVENT_STRENGTH_KEY));
            } catch (IllegalArgumentException e) {
                this.currentWindEvent = WindStrength.CALM;
            }
        }
        if (tag.contains(NBT_WIND_EVENT_DIRECTION_KEY)) {
            this.windDirection = Direction.from3DDataValue(tag.getInt(NBT_WIND_EVENT_DIRECTION_KEY));
        }

        // Recalculate derived values on client for UI
        this.windEventMultiplier = this.currentWindEvent.getMultiplier();
        calculateFacingBonus();
    }


    @Override
    public String getDebugInfo() {
        // ... This can be updated later if desired ...
        return super.getDebugInfo();
    }

    /**
     * MODIFIED: The status display now includes the dynamic wind event information.
     */
    public String getWindmillStatus() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("§e%s Windmill§r\n", rotorSize.displayName));

        if (isGenerating) {
            status.append(ChatFormatting.GREEN + "⚡ Generating" + ChatFormatting.RESET + "\n");
        } else {
            status.append(ChatFormatting.GRAY + "○ Idle" + ChatFormatting.RESET + "\n");
        }

        status.append(String.format("RPM: §b%.1f§r/§7%.1f§r (§e%.1f%%§r)\n",
                kineticStorage.getRPM(), baseMaxRPM, getGenerationEfficiency()));
        status.append(String.format("Power: §a%.1f W§r\n", kineticStorage.getPowerWatts()));

        status.append("\n§bWind Conditions:§r\n");
        status.append(String.format("  Strength: §b%.2f§r (§e%.1f%%§r)\n", windStrength, getWindStrengthPercentage()));

        // NEW: Add Wind Event section
        if (currentWindEvent.isActive()) {
            status.append("\n§b" + ChatFormatting.AQUA + "⚡ Wind Event:" + ChatFormatting.RESET + "§r\n");
            ChatFormatting eventColor = currentWindEvent.getMultiplier() > 2.0f ? ChatFormatting.RED :
                    currentWindEvent.getMultiplier() > 1.5f ? ChatFormatting.YELLOW : ChatFormatting.GREEN;

            status.append(String.format("  %s%s§r from %s\n",
                    eventColor, currentWindEvent.getDisplayName(), this.windDirection.getName().toUpperCase()));

            if (facingBonus > 1.2f) {
                status.append(String.format("  §a✓ Optimal Facing! (+%.0f%%)§r\n", (facingBonus - 1.0f) * 100.0f));
            } else if (facingBonus < 0.8f) {
                status.append(String.format("  §c✗ Poor Facing! (-%.0f%%)§r\n", (1.0f - facingBonus) * 100.0f));
            }
        }

        if (!hasClearSky) {
            status.append("\n" + ChatFormatting.RED + "✗ No Sky Access" + ChatFormatting.RESET);
        } else if (obstructionCount > 0) {
            status.append(String.format("  §e⚠ Obstructions: %d (-%.0f%%)§r\n",
                    obstructionCount, (1.0f - obstructionPenalty) * 100.0f));
        }

        status.append(String.format("\n\nIntegrity: §%s%.1f%%§r",
                structuralIntegrity > 0.7f ? "a" : (structuralIntegrity > 0.4f ? "e" : "c"),
                structuralIntegrity * 100.0f));

        return status.toString();
    }
}
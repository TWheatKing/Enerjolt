package me.twheatking.enerjolt.kinetic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * Kinetic storage that can ONLY consume rotation (machines).
 * Used for: Crushers, Mills, Pumps, Mechanical Crafters, etc.
 * Similar to ReceiveOnlyEnergyStorage for FE system.
 */
public class ConsumeOnlyKineticStorage implements IKineticStorage {

    protected float rpm;
    protected float maxRPM;
    protected float torque;
    protected float maxTorque;
    protected float inertia;
    protected int direction;
    protected float frictionCoefficient;
    protected float temperature;
    protected float vibration;

    // Resonance frequency - machine works best at this RPM
    protected float resonanceRPM;
    protected float resonanceTolerance; // Acceptable range around resonance

    public ConsumeOnlyKineticStorage() {
        this(0, 256, 0, 100, 1.0f, 0.02f, 128.0f, 32.0f);
    }

    /**
     * Creates a consume-only kinetic storage (machine)
     * @param rpm Initial RPM
     * @param maxRPM Maximum RPM
     * @param torque Initial torque
     * @param maxTorque Maximum torque needed to operate
     * @param inertia Inertia value
     * @param frictionCoefficient Friction loss
     * @param resonanceRPM Optimal operating RPM
     * @param resonanceTolerance Acceptable RPM range (±)
     */
    public ConsumeOnlyKineticStorage(float rpm, float maxRPM, float torque, float maxTorque,
                                     float inertia, float frictionCoefficient,
                                     float resonanceRPM, float resonanceTolerance) {
        this.rpm = rpm;
        this.maxRPM = maxRPM;
        this.torque = torque;
        this.maxTorque = maxTorque;
        this.inertia = inertia;
        this.frictionCoefficient = frictionCoefficient;
        this.direction = 0;
        this.temperature = 20.0f;
        this.vibration = 0.0f;
        this.resonanceRPM = resonanceRPM;
        this.resonanceTolerance = resonanceTolerance;
    }

    // ========== RPM ==========

    @Override
    public float getRPM() {
        return rpm;
    }

    @Override
    public void setRPM(float rpm) {
        this.rpm = Math.max(0, Math.min(rpm, maxRPM));
        updateDirection();
        onChange();
    }

    @Override
    public void setRPMWithoutUpdate(float rpm) {
        this.rpm = Math.max(0, Math.min(rpm, maxRPM));
        updateDirection();
    }

    @Override
    public float getMaxRPM() {
        return maxRPM;
    }

    @Override
    public void setMaxRPM(float maxRPM) {
        this.maxRPM = maxRPM;
        onChange();
    }

    @Override
    public void setMaxRPMWithoutUpdate(float maxRPM) {
        this.maxRPM = maxRPM;
    }

    // ========== TORQUE ==========

    @Override
    public float getTorque() {
        return torque;
    }

    @Override
    public void setTorque(float torque) {
        this.torque = Math.max(0, Math.min(torque, maxTorque));
        onChange();
    }

    @Override
    public void setTorqueWithoutUpdate(float torque) {
        this.torque = Math.max(0, Math.min(torque, maxTorque));
    }

    @Override
    public float getMaxTorque() {
        return maxTorque;
    }

    @Override
    public void setMaxTorque(float maxTorque) {
        this.maxTorque = maxTorque;
        onChange();
    }

    @Override
    public void setMaxTorqueWithoutUpdate(float maxTorque) {
        this.maxTorque = maxTorque;
    }

    // ========== INERTIA ==========

    @Override
    public float getInertia() {
        return inertia;
    }

    @Override
    public void setInertia(float inertia) {
        this.inertia = Math.max(0.1f, inertia);
        onChange();
    }

    @Override
    public void setInertiaWithoutUpdate(float inertia) {
        this.inertia = Math.max(0.1f, inertia);
    }

    // ========== DIRECTION ==========

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public void setDirection(int direction) {
        this.direction = Math.max(-1, Math.min(1, direction));
        onChange();
    }

    @Override
    public void setDirectionWithoutUpdate(int direction) {
        this.direction = Math.max(-1, Math.min(1, direction));
    }

    private void updateDirection() {
        if (rpm > 0.01f) {
            if (direction == 0) direction = 1;
        } else {
            direction = 0;
        }
    }

    // ========== EFFICIENCY & FRICTION ==========

    @Override
    public float getFrictionCoefficient() {
        return frictionCoefficient;
    }

    @Override
    public void setFrictionCoefficient(float friction) {
        this.frictionCoefficient = Math.max(0.0f, Math.min(1.0f, friction));
        onChange();
    }

    @Override
    public float getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(float temperature) {
        this.temperature = Math.max(0.0f, temperature);
        onChange();
    }

    @Override
    public float getVibration() {
        return vibration;
    }

    @Override
    public void setVibration(float vibration) {
        this.vibration = Math.max(0.0f, Math.min(1.0f, vibration));
        onChange();
    }

    // ========== TRANSFER (Consume Only!) ==========

    @Override
    public float addRotation(float rpmToAdd, float torqueToAdd, boolean simulate) {
        if (!canReceiveRotation())
            return 0;

        float actualRPMAdded = Math.min(rpmToAdd, maxRPM - rpm);
        float actualTorqueAdded = Math.min(torqueToAdd, maxTorque - torque);

        if (actualRPMAdded <= 0 && actualTorqueAdded <= 0)
            return 0;

        // Apply inertia
        float inertiaFactor = 1.0f / inertia;
        actualRPMAdded *= inertiaFactor;

        if (!simulate) {
            rpm += actualRPMAdded;
            torque += actualTorqueAdded;
            updateDirection();
            onChange();
        }

        return actualRPMAdded;
    }

    @Override
    public float extractRotation(float rpmToExtract, float torqueToExtract, boolean simulate) {
        // Machines don't provide rotation to others
        return 0;
    }

    @Override
    public boolean canProvideRotation() {
        return false; // Machines don't output rotation
    }

    @Override
    public boolean canReceiveRotation() {
        return true;
    }

    // ========== RESONANCE SYSTEM ==========

    /**
     * Gets the resonance frequency (optimal operating RPM)
     * @return The RPM at which this machine works best
     */
    public float getResonanceRPM() {
        return resonanceRPM;
    }

    /**
     * Sets the resonance frequency
     * @param resonanceRPM Optimal operating RPM
     */
    public void setResonanceRPM(float resonanceRPM) {
        this.resonanceRPM = resonanceRPM;
        onChange();
    }

    /**
     * Gets the resonance tolerance (acceptable RPM range)
     * @return Tolerance range (±)
     */
    public float getResonanceTolerance() {
        return resonanceTolerance;
    }

    /**
     * Sets the resonance tolerance
     * @param resonanceTolerance Acceptable RPM range
     */
    public void setResonanceTolerance(float resonanceTolerance) {
        this.resonanceTolerance = resonanceTolerance;
        onChange();
    }

    /**
     * Checks if the machine is operating within resonance range
     * @return True if RPM is within resonance tolerance
     */
    public boolean isInResonance() {
        return Math.abs(rpm - resonanceRPM) <= resonanceTolerance;
    }

    /**
     * Calculates efficiency multiplier based on resonance
     * @return Efficiency multiplier (0.5 to 1.5)
     * 1.0 = normal, 1.5 = perfect resonance, 0.5 = far from resonance
     */
    public float getResonanceEfficiency() {
        float deviation = Math.abs(rpm - resonanceRPM);

        if (deviation <= resonanceTolerance) {
            // Within tolerance - bonus efficiency
            float bonus = 1.0f - (deviation / resonanceTolerance) * 0.5f;
            return 1.0f + bonus * 0.5f; // Range: 1.0 to 1.5
        } else {
            // Outside tolerance - penalty
            float penalty = Math.min(1.0f, (deviation - resonanceTolerance) / resonanceRPM);
            return 1.0f - penalty * 0.5f; // Range: 0.5 to 1.0
        }
    }

    /**
     * Checks if the machine has enough torque to operate
     * @return True if current torque meets minimum requirement
     */
    public boolean hasMinimumTorque() {
        return torque >= (maxTorque * 0.1f); // Need at least 10% of max torque
    }

    /**
     * Consumes rotation for machine operation
     * @param rpmConsumed RPM to consume this tick
     * @param torqueConsumed Torque to consume
     * @return True if consumption was successful
     */
    public boolean consumeRotation(float rpmConsumed, float torqueConsumed) {
        if (rpm < rpmConsumed || torque < torqueConsumed)
            return false;

        rpm -= rpmConsumed;
        torque -= torqueConsumed;
        updateDirection();
        onChange();
        return true;
    }

    protected void onChange() {
        // Override in subclasses
    }

    // ========== NBT ==========

    @Override
    public Tag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("rpm", rpm);
        tag.putFloat("maxRPM", maxRPM);
        tag.putFloat("torque", torque);
        tag.putFloat("maxTorque", maxTorque);
        tag.putFloat("inertia", inertia);
        tag.putInt("direction", direction);
        tag.putFloat("friction", frictionCoefficient);
        tag.putFloat("temperature", temperature);
        tag.putFloat("vibration", vibration);
        tag.putFloat("resonanceRPM", resonanceRPM);
        tag.putFloat("resonanceTolerance", resonanceTolerance);
        return tag;
    }

    @Override
    public void loadNBT(Tag tag) {
        if (!(tag instanceof CompoundTag compoundTag)) {
            rpm = 0;
            torque = 0;
            direction = 0;
            temperature = 20.0f;
            vibration = 0.0f;
            return;
        }

        rpm = compoundTag.getFloat("rpm");
        maxRPM = compoundTag.getFloat("maxRPM");
        torque = compoundTag.getFloat("torque");
        maxTorque = compoundTag.getFloat("maxTorque");
        inertia = compoundTag.getFloat("inertia");
        direction = compoundTag.getInt("direction");
        frictionCoefficient = compoundTag.getFloat("friction");
        temperature = compoundTag.getFloat("temperature");
        vibration = compoundTag.getFloat("vibration");
        resonanceRPM = compoundTag.getFloat("resonanceRPM");
        resonanceTolerance = compoundTag.getFloat("resonanceTolerance");
    }
}
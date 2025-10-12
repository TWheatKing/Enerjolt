package me.twheatking.enerjolt.kinetic;

import net.minecraft.nbt.Tag;

/**
 * Core interface for kinetic rotational energy storage.
 * Handles RPM (speed), Torque (power), Inertia (momentum), and Direction.
 */
public interface IKineticStorage {

    // ========== RPM (Speed) ==========

    /**
     * @return Current rotations per minute (RPM)
     */
    float getRPM();

    /**
     * Sets the current RPM
     * @param rpm The new RPM value
     */
    void setRPM(float rpm);

    /**
     * Sets RPM without triggering update/sync
     * @param rpm The new RPM value
     */
    void setRPMWithoutUpdate(float rpm);

    /**
     * @return Maximum RPM this component can handle before breaking
     */
    float getMaxRPM();

    /**
     * Sets the maximum RPM
     * @param maxRPM The new maximum RPM
     */
    void setMaxRPM(float maxRPM);

    /**
     * Sets max RPM without triggering update/sync
     * @param maxRPM The new maximum RPM
     */
    void setMaxRPMWithoutUpdate(float maxRPM);

    // ========== TORQUE (Power) ==========

    /**
     * @return Current torque in Newton-meters (Nm)
     */
    float getTorque();

    /**
     * Sets the current torque
     * @param torque The new torque value in Nm
     */
    void setTorque(float torque);

    /**
     * Sets torque without triggering update/sync
     * @param torque The new torque value in Nm
     */
    void setTorqueWithoutUpdate(float torque);

    /**
     * @return Maximum torque this component can handle
     */
    float getMaxTorque();

    /**
     * Sets the maximum torque
     * @param maxTorque The new maximum torque in Nm
     */
    void setMaxTorque(float maxTorque);

    /**
     * Sets max torque without triggering update/sync
     * @param maxTorque The new maximum torque in Nm
     */
    void setMaxTorqueWithoutUpdate(float maxTorque);

    // ========== POWER CALCULATION ==========

    /**
     * Calculates power output in watts
     * Power (W) = Torque (Nm) × Angular Velocity (rad/s)
     * Angular Velocity = (RPM × 2π) / 60
     *
     * @return Power in watts
     */
    default float getPowerWatts() {
        float angularVelocity = (getRPM() * 2.0f * (float)Math.PI) / 60.0f;
        return getTorque() * angularVelocity;
    }

    /**
     * Calculates stress units (similar to Create mod)
     * @return Stress capacity/usage
     */
    default float getStressUnits() {
        return getTorque() * (getRPM() / 256.0f);
    }

    // ========== INERTIA (Momentum) ==========

    /**
     * @return Inertia value (resistance to change in rotation)
     * Higher inertia = harder to start/stop, maintains momentum better
     */
    float getInertia();

    /**
     * Sets the inertia
     * @param inertia The new inertia value
     */
    void setInertia(float inertia);

    /**
     * Sets inertia without triggering update/sync
     * @param inertia The new inertia value
     */
    void setInertiaWithoutUpdate(float inertia);

    // ========== DIRECTION ==========

    /**
     * @return Rotation direction: 1 = clockwise, -1 = counterclockwise, 0 = stopped
     */
    int getDirection();

    /**
     * Sets the rotation direction
     * @param direction 1 = clockwise, -1 = counterclockwise, 0 = stopped
     */
    void setDirection(int direction);

    /**
     * Sets direction without triggering update/sync
     * @param direction The new direction
     */
    void setDirectionWithoutUpdate(int direction);

    /**
     * Reverses the current rotation direction
     */
    default void reverseDirection() {
        setDirection(-getDirection());
    }

    // ========== EFFICIENCY & FRICTION ==========

    /**
     * @return Friction coefficient (0.0 = no loss, 1.0 = complete loss)
     * Material-dependent: wood > iron > steel > diamond
     */
    float getFrictionCoefficient();

    /**
     * Sets the friction coefficient
     * @param friction Friction value between 0.0 and 1.0
     */
    void setFrictionCoefficient(float friction);

    /**
     * @return Current temperature in degrees Celsius
     * High-speed rotation generates heat
     */
    float getTemperature();

    /**
     * Sets the temperature
     * @param temperature Temperature in Celsius
     */
    void setTemperature(float temperature);

    /**
     * @return Vibration level (0.0 = smooth, 1.0 = breaking apart)
     */
    float getVibration();

    /**
     * Sets the vibration level
     * @param vibration Vibration value between 0.0 and 1.0
     */
    void setVibration(float vibration);

    // ========== TRANSFER & LIMITS ==========

    /**
     * Attempts to add rotational force
     * @param rpm RPM to add
     * @param torque Torque to add
     * @param simulate If true, don't actually apply changes
     * @return Amount of RPM actually added
     */
    float addRotation(float rpm, float torque, boolean simulate);

    /**
     * Attempts to extract rotational force
     * @param rpm RPM to extract
     * @param torque Torque to extract
     * @param simulate If true, don't actually apply changes
     * @return Amount of RPM actually extracted
     */
    float extractRotation(float rpm, float torque, boolean simulate);

    /**
     * @return True if this component can provide rotation to others
     */
    boolean canProvideRotation();

    /**
     * @return True if this component can receive rotation from others
     */
    boolean canReceiveRotation();

    /**
     * @return True if this component is currently rotating
     */
    default boolean isRotating() {
        return Math.abs(getRPM()) > 0.01f;
    }

    /**
     * @return True if this component is overstressed (torque demand exceeds capacity)
     */
    default boolean isOverstressed() {
        return getTorque() > getMaxTorque();
    }

    /**
     * @return True if this component is overheating
     */
    default boolean isOverheating() {
        return getTemperature() > 100.0f; // Default threshold
    }

    // ========== NBT PERSISTENCE ==========

    /**
     * Saves kinetic data to NBT
     * @return NBT tag containing all kinetic data
     */
    Tag saveNBT();

    /**
     * Loads kinetic data from NBT
     * @param tag NBT tag containing kinetic data
     */
    void loadNBT(Tag tag);
}
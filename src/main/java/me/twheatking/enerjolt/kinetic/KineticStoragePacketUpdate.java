package me.twheatking.enerjolt.kinetic;

/**
 * Interface for BlockEntities that need to sync kinetic data to clients.
 * Similar to EnergyStoragePacketUpdate for FE system.
 * Used by KineticSyncS2CPacket.
 */
public interface KineticStoragePacketUpdate {

    /**
     * Sets the RPM value (for client sync)
     * @param rpm The new RPM value
     */
    void setRPM(float rpm);

    /**
     * Sets the max RPM value (for client sync)
     * @param maxRPM The new max RPM value
     */
    void setMaxRPM(float maxRPM);

    /**
     * Sets the torque value (for client sync)
     * @param torque The new torque value
     */
    void setTorque(float torque);

    /**
     * Sets the max torque value (for client sync)
     * @param maxTorque The new max torque value
     */
    void setMaxTorque(float maxTorque);

    /**
     * Sets the direction (for client sync)
     * @param direction 1 = clockwise, -1 = counterclockwise, 0 = stopped
     */
    void setDirection(int direction);

    /**
     * Sets the temperature (for client sync)
     * @param temperature Temperature in Celsius
     */
    void setTemperature(float temperature);

    /**
     * Sets the vibration level (for client sync)
     * @param vibration Vibration value between 0.0 and 1.0
     */
    void setVibration(float vibration);
}
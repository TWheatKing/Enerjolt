package me.twheatking.enerjolt.kinetic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * Kinetic storage that can ONLY provide rotation (generators).
 * Used for: Water Wheels, Windmills, Hand Cranks, Steam Engines, etc.
 * Similar to ExtractOnlyEnergyStorage for FE system.
 */
public class ProvideOnlyKineticStorage implements IKineticStorage {

    protected float rpm;
    protected float maxRPM;
    protected float torque;
    protected float maxTorque;
    protected float inertia;
    protected int direction;
    protected float frictionCoefficient;
    protected float temperature;
    protected float vibration;

    public ProvideOnlyKineticStorage() {
        this(0, 256, 0, 100, 1.0f, 0.02f);
    }

    /**
     * Creates a provide-only kinetic storage (generator)
     * @param rpm Initial RPM
     * @param maxRPM Maximum RPM
     * @param torque Initial torque
     * @param maxTorque Maximum torque
     * @param inertia Inertia value
     * @param frictionCoefficient Friction loss
     */
    public ProvideOnlyKineticStorage(float rpm, float maxRPM, float torque, float maxTorque,
                                     float inertia, float frictionCoefficient) {
        this.rpm = rpm;
        this.maxRPM = maxRPM;
        this.torque = torque;
        this.maxTorque = maxTorque;
        this.inertia = inertia;
        this.frictionCoefficient = frictionCoefficient;
        this.direction = rpm > 0 ? 1 : 0;
        this.temperature = 20.0f;
        this.vibration = 0.0f;
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

    // ========== TRANSFER (Provide Only!) ==========

    @Override
    public float addRotation(float rpmToAdd, float torqueToAdd, boolean simulate) {
        // Generators cannot receive rotation from outside
        return 0;
    }

    @Override
    public float extractRotation(float rpmToExtract, float torqueToExtract, boolean simulate) {
        if (!canProvideRotation())
            return 0;

        float actualRPMExtracted = Math.min(rpmToExtract, rpm);
        float actualTorqueExtracted = Math.min(torqueToExtract, torque);

        if (actualRPMExtracted <= 0)
            return 0;

        if (!simulate) {
            rpm -= actualRPMExtracted;
            torque -= actualTorqueExtracted;
            updateDirection();
            onChange();
        }

        return actualRPMExtracted;
    }

    @Override
    public boolean canProvideRotation() {
        return true;
    }

    @Override
    public boolean canReceiveRotation() {
        return false; // Generators don't accept input
    }

    /**
     * Generators can directly generate rotation (called by generator logic)
     * @param rpmToGenerate RPM to generate this tick
     * @param torqueToGenerate Torque to generate
     */
    public void generateRotation(float rpmToGenerate, float torqueToGenerate) {
        // Apply inertia - generators build up speed gradually
        float inertiaFactor = 1.0f / inertia;
        float actualRPMAdded = rpmToGenerate * inertiaFactor;

        rpm = Math.min(maxRPM, rpm + actualRPMAdded);
        torque = Math.min(maxTorque, torque + torqueToGenerate);
        updateDirection();
        onChange();
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
    }
}
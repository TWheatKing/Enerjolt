package me.twheatking.enerjolt.energy;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

public class ReceiveAndExtractEnergyStorage implements IEnerjoltEnergyStorage {
    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public ReceiveAndExtractEnergyStorage() {
        this(0, 0, 0);
    }

    /**
     * Constructor with capacity and unified max transfer rate
     * @param capacity Maximum energy capacity
     * @param maxTransfer Maximum energy transfer rate (both receive and extract)
     */
    public ReceiveAndExtractEnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    /**
     * Constructor with separate receive and extract rates
     * @param capacity Maximum energy capacity
     * @param maxReceive Maximum energy receive rate
     * @param maxExtract Maximum energy extract rate
     */
    public ReceiveAndExtractEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    /**
     * Full constructor with initial energy
     * @param capacity Maximum energy capacity
     * @param maxReceive Maximum energy receive rate
     * @param maxExtract Maximum energy extract rate
     * @param energy Initial energy stored
     */
    public ReceiveAndExtractEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
        onChange();
    }

    @Override
    public void setEnergyWithoutUpdate(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        // Clamp current energy to new capacity
        if (this.energy > capacity) {
            this.energy = capacity;
        }
        onChange();
    }

    @Override
    public void setCapacityWithoutUpdate(int capacity) {
        this.capacity = capacity;
        // Clamp current energy to new capacity
        if (this.energy > capacity) {
            this.energy = capacity;
        }
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public void setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
        onChange();
    }

    public void setMaxReceiveWithoutUpdate(int maxReceive) {
        this.maxReceive = maxReceive;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public void setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
        onChange();
    }

    public void setMaxExtractWithoutUpdate(int maxExtract) {
        this.maxExtract = maxExtract;
    }

    /**
     * Legacy method for backward compatibility
     * Sets both maxReceive and maxExtract to the same value
     */
    public int getMaxTransfer() {
        return Math.min(maxReceive, maxExtract);
    }

    /**
     * Legacy method for backward compatibility
     * Sets both maxReceive and maxExtract to the same value
     */
    public void setMaxTransfer(int maxTransfer) {
        this.maxReceive = maxTransfer;
        this.maxExtract = maxTransfer;
        onChange();
    }

    /**
     * Legacy method for backward compatibility
     * Sets both maxReceive and maxExtract to the same value
     */
    public void setMaxTransferWithoutUpdate(int maxTransfer) {
        this.maxReceive = maxTransfer;
        this.maxExtract = maxTransfer;
    }

    protected void onChange() {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int received = Math.max(0, Math.min(getMaxEnergyStored() - energy, Math.min(this.maxReceive, maxReceive)));

        if (!simulate) {
            energy += received;
            onChange();
        }

        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int extracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

        if (!simulate) {
            energy -= extracted;
            onChange();
        }

        return extracted;
    }

    @Override
    public final int getEnergyStored() {
        return getEnergy();
    }

    @Override
    public final int getMaxEnergyStored() {
        return getCapacity();
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    @Override
    public Tag saveNBT() {
        return IntTag.valueOf(energy);
    }

    @Override
    public void loadNBT(Tag tag) {
        if (!(tag instanceof IntTag)) {
            energy = 0;
            return;
        }

        energy = Math.max(0, Math.min(capacity, ((IntTag)tag).getAsInt()));
    }
}
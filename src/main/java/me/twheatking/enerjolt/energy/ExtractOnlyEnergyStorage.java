package me.twheatking.enerjolt.energy;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

public class ExtractOnlyEnergyStorage implements IEnerjoltEnergyStorage {
    protected int energy;
    protected int capacity;
    protected int maxExtract;

    public ExtractOnlyEnergyStorage() {}

    public ExtractOnlyEnergyStorage(int energy, int capacity, int maxExtract) {
        this.energy = energy;
        this.capacity = capacity;
        this.maxExtract = maxExtract;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
        onChange();
    }

    @Override
    public void setEnergyWithoutUpdate(int energy) {
        this.energy = energy;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        onChange();
    }

    @Override
    public void setCapacityWithoutUpdate(int capacity) {
        this.capacity = capacity;
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

    protected void onChange() {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if(!canExtract())
            return 0;

        int extracted = Math.min(energy, Math.min(getMaxExtract(), maxExtract));

        if(!simulate) {
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
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public Tag saveNBT() {
        return IntTag.valueOf(energy);
    }

    @Override
    public void loadNBT(Tag tag) {
        if(!(tag instanceof IntTag)) {
            energy = 0;

            return;
        }

        energy = ((IntTag)tag).getAsInt();
    }
}

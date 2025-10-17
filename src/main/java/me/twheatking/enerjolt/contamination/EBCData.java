package me.twheatking.enerjolt.contamination;

import net.minecraft.nbt.CompoundTag;

/**
 * Stores E.B.C (Enerjolt Bio Contamination) data for a player.
 * Tracks contamination count, time accumulation, and biome state.
 */
public class EBCData {
    private int ebcCount;              // Total E.B.C counts (0-infinity)
    private float accumulatedTime;     // Time in biome (0.0-5.0 seconds)
    private boolean wasInPlagueland;   // Was in Plagueland last tick?

    public EBCData() {
        this.ebcCount = 0;
        this.accumulatedTime = 0.0f;
        this.wasInPlagueland = false;
    }

    /**
     * Get the current E.B.C count
     */
    public int getEBCCount() {
        return ebcCount;
    }

    /**
     * Set the E.B.C count
     */
    public void setEBCCount(int count) {
        this.ebcCount = Math.max(0, count); // Never go below 0
    }

    /**
     * Add to E.B.C count
     */
    public void addEBCCount(int amount) {
        this.ebcCount += amount;
        if (this.ebcCount < 0) {
            this.ebcCount = 0;
        }
    }

    /**
     * Remove from E.B.C count (used by B.C.R potion)
     */
    public void removeEBCCount(int amount) {
        this.ebcCount -= amount;
        if (this.ebcCount < 0) {
            this.ebcCount = 0;
            this.accumulatedTime = 0.0f; // Reset timer when fully cured
        }
    }

    /**
     * Get accumulated time in biome
     */
    public float getAccumulatedTime() {
        return accumulatedTime;
    }

    /**
     * Add time spent in biome
     * Automatically converts to E.B.C counts when reaching 5 seconds
     */
    public void addTime(float seconds) {
        this.accumulatedTime += seconds;

        // Every 5 seconds = 1 E.B.C count
        while (this.accumulatedTime >= 5.0f) {
            this.ebcCount++;
            this.accumulatedTime -= 5.0f;
        }
    }

    /**
     * Reset accumulated time (used when timer should reset)
     */
    public void resetAccumulatedTime() {
        this.accumulatedTime = 0.0f;
    }

    /**
     * Check if player was in Plagueland last tick
     */
    public boolean wasInPlagueland() {
        return wasInPlagueland;
    }

    /**
     * Set whether player is currently in Plagueland
     */
    public void setInPlagueland(boolean inPlagueland) {
        this.wasInPlagueland = inPlagueland;
    }

    /**
     * Get the current stage based on E.B.C count
     * 0 = Undetected (0-50)
     * 1 = E.B.C Contamination (50-100)
     * 2 = Plagued (100+)
     */
    public int getStage() {
        if (ebcCount >= 100) {
            return 2;
        } else if (ebcCount >= 50) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Serialize to NBT
     */
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("EBCCount", ebcCount);
        tag.putFloat("AccumulatedTime", accumulatedTime);
        tag.putBoolean("WasInPlagueland", wasInPlagueland);
        return tag;
    }

    /**
     * Deserialize from NBT
     */
    public void fromNBT(CompoundTag tag) {
        this.ebcCount = tag.getInt("EBCCount");
        this.accumulatedTime = tag.getFloat("AccumulatedTime");
        this.wasInPlagueland = tag.getBoolean("WasInPlagueland");
    }

    /**
     * Copy data from another EBCData
     */
    public void copyFrom(EBCData other) {
        this.ebcCount = other.ebcCount;
        this.accumulatedTime = other.accumulatedTime;
        this.wasInPlagueland = other.wasInPlagueland;
    }

    @Override
    public String toString() {
        return "EBCData{count=" + ebcCount + ", time=" + accumulatedTime + ", stage=" + getStage() + "}";
    }
}
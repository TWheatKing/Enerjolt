package me.twheatking.enerjolt.weather.wind;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * Manages the persistence of wind data for a single dimension.
 * This class is responsible for saving the wind state when the world is saved
 * and loading it back when the world is loaded.
 */
public class WindSavedData extends SavedData {
    private static final String DATA_NAME = "enerjolt_wind_data";
    private final WindData windData = new WindData();

    /**
     * Loads the wind data from an NBT tag. This is the 'deserializer'.
     * In MC 1.21.1, this constructor now requires a HolderLookup.Provider argument.
     * @param pCompoundTag The NBT data from the world save.
     * @param pRegistries The registry provider (unused by us, but required by the parent class).
     */
    public WindSavedData(CompoundTag pCompoundTag, HolderLookup.Provider pRegistries) {
        this.windData.load(pCompoundTag);
    }

    /**
     * Default constructor for when no save data exists yet. This is the 'factory'.
     */
    public WindSavedData() {}

    /**
     * Gets the WindData instance for this dimension.
     * @return The WindData object.
     */
    public WindData getWindData() {
        return this.windData;
    }

    /**
     * Saves the current wind state to an NBT tag.
     * In MC 1.21.1, this method's signature has changed to include the HolderLookup.Provider.
     * @param pCompoundTag The NBT tag to write to.
     * @param pRegistries The registry provider (unused by us, but required by the parent class).
     * @return The populated NBT tag.
     */
    @Override
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider pRegistries) {
        return windData.save(pCompoundTag);
    }

    /**
     * A static helper method to easily retrieve the WindSavedData for a given server level.
     * It will create a new instance if one doesn't already exist for that dimension.
     * @param level The server level (dimension) to get the wind data for.
     * @return The WindSavedData instance for that level.
     */
    public static WindSavedData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();

        // The factory provides methods to create a new SavedData instance or load one from NBT.
        // The deserializer function now takes two arguments: the tag and the provider.
        SavedData.Factory<WindSavedData> factory = new SavedData.Factory<>(
                WindSavedData::new, // Supplier for a new, empty instance: () -> new WindSavedData()
                WindSavedData::new  // Deserializer for loading from NBT: (tag, provider) -> new WindSavedData(tag, provider)
        );

        return storage.computeIfAbsent(factory, DATA_NAME);
    }
}


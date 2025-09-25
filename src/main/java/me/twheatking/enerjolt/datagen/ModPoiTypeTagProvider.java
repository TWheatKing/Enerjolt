package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.villager.EnerjoltVillager;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModPoiTypeTagProvider extends PoiTypeTagsProvider {
    public ModPoiTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                 @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EJOLTAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).
                add(Objects.requireNonNull(EnerjoltVillager.BASIC_MACHINE_FRAME_POI.getKey()));
    }
}

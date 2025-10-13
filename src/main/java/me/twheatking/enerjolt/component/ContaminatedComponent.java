package me.twheatking.enerjolt.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component that marks items as contaminated.
 * Contaminated items cannot be used outside the Contamination Zone
 * and must be extracted to become usable.
 */
public record ContaminatedComponent(boolean contaminated, boolean extracted) {

    // Default values
    public static final ContaminatedComponent CONTAMINATED = new ContaminatedComponent(true, false);
    public static final ContaminatedComponent EXTRACTED = new ContaminatedComponent(true, true);
    public static final ContaminatedComponent CLEAN = new ContaminatedComponent(false, false);

    // Codec for serialization (save to disk)
    public static final Codec<ContaminatedComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("contaminated").forGetter(ContaminatedComponent::contaminated),
                    Codec.BOOL.fieldOf("extracted").forGetter(ContaminatedComponent::extracted)
            ).apply(instance, ContaminatedComponent::new)
    );

    // Stream codec for network sync (client-server communication)
    public static final StreamCodec<RegistryFriendlyByteBuf, ContaminatedComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ContaminatedComponent::contaminated,
            ByteBufCodecs.BOOL,
            ContaminatedComponent::extracted,
            ContaminatedComponent::new
    );

    /**
     * Checks if this item is contaminated and not yet extracted
     */
    public boolean needsExtraction() {
        return contaminated && !extracted;
    }

    /**
     * Checks if this item is contaminated but has been extracted (safe to use)
     */
    public boolean isExtracted() {
        return contaminated && extracted;
    }

    /**
     * Checks if this item is clean (never contaminated)
     */
    public boolean isClean() {
        return !contaminated;
    }
}
package me.twheatking.enerjolt.contamination;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Registers the E.B.C Data attachment for players.
 * Uses NeoForge's modern attachment system to store persistent data.
 */
public class EBCDataAttachment {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EJOLTAPI.MOD_ID);

    /**
     * Codec for serializing EBCData
     */
    private static final Codec<EBCData> EBC_DATA_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("ebc_count").forGetter(EBCData::getEBCCount),
                    Codec.FLOAT.fieldOf("accumulated_time").forGetter(EBCData::getAccumulatedTime),
                    Codec.BOOL.fieldOf("was_in_plagueland").forGetter(EBCData::wasInPlagueland)
            ).apply(instance, (count, time, inBiome) -> {
                EBCData data = new EBCData();
                data.setEBCCount(count);
                data.addTime(time);
                data.setInPlagueland(inBiome);
                return data;
            })
    );

    /**
     * The E.B.C Data attachment type
     * Attached to players to store contamination information
     */
    public static final Supplier<AttachmentType<EBCData>> EBC_DATA = ATTACHMENT_TYPES.register(
            "ebc_data",
            () -> AttachmentType.builder(() -> new EBCData())
                    .serialize(EBC_DATA_CODEC)
                    .copyOnDeath() // Data persists through death
                    .build()
    );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
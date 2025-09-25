package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EnerjoltCreativeModeTab {
    private EnerjoltCreativeModeTab() {}

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EJOLTAPI.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENERJOLT_TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder().
                    title(Component.translatable("itemGroup.enerjolt.tab")).
                    icon(() -> new ItemStack(EnerjoltItems.ADVANCED_CIRCUIT.get())).
                    build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}

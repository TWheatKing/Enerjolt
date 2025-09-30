package me.twheatking.enerjolt.item.energy;

import me.twheatking.enerjolt.energy.IEnerjoltEnergyStorage;
import me.twheatking.enerjolt.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnerjoltEnergyItem extends Item {
    private final Function<ItemStack, IEnerjoltEnergyStorage> energyStorageProvider;

    protected static int getEnergy(ItemStack itemStack) {
        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage instanceof ItemCapabilityEnergy?energyStorage.getEnergyStored():0;
    }
    protected static void setEnergy(ItemStack itemStack, int energy) {
        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage instanceof ItemCapabilityEnergy energizedPowerEnergyStorage)
            energizedPowerEnergyStorage.setEnergy(energy);
    }
    protected static int getCapacity(ItemStack itemStack) {
        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage instanceof ItemCapabilityEnergy?energyStorage.getMaxEnergyStored():0;
    }

    public EnerjoltEnergyItem(Properties props, Supplier<IEnerjoltEnergyStorage> energyStorageProvider) {
        this(props, stack -> energyStorageProvider.get());
    }

    public EnerjoltEnergyItem(Properties props, Function<ItemStack, IEnerjoltEnergyStorage> energyStorageProvider) {
        super(props);

        this.energyStorageProvider = energyStorageProvider;
    }

    public Function<ItemStack, IEnerjoltEnergyStorage> getEnergyStorageProvider() {
        return energyStorageProvider;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(getEnergy(stack) * 13.f / getCapacity(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.f, getEnergy(stack) / (float)getCapacity(stack));
        return Mth.hsvToRgb(f * .33f, 1.f, 1.f);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(Component.translatable("tooltip.enerjolt.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(getEnergy(itemStack)), EnergyUtils.getEnergyWithPrefix(getCapacity(itemStack))).
                withStyle(ChatFormatting.GRAY));
    }
}

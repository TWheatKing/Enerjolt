package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.energy.ReceiveAndExtractEnergyStorage;
import me.twheatking.enerjolt.item.energy.EnerjoltEnergyItem;
import me.twheatking.enerjolt.machine.tier.BatteryTier;
import me.twheatking.enerjolt.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class BatteryItem extends EnerjoltEnergyItem {
    private final BatteryTier tier;

    public BatteryItem(BatteryTier tier) {
        super(new Item.Properties().stacksTo(1), () -> new ReceiveAndExtractEnergyStorage(0, tier.getCapacity(), tier.getMaxTransfer()));

        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, components, tooltipFlag);

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.enerjolt.battery.txt.shift.1",
                            EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).withStyle(ChatFormatting.GRAY));
        }else {
            components.add(Component.translatable("tooltip.enerjolt.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

}

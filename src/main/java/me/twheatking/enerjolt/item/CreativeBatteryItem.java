package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.energy.InfinityEnergyStorage;
import me.twheatking.enerjolt.item.energy.EnerjoltEnergyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CreativeBatteryItem extends EnerjoltEnergyItem {
    public CreativeBatteryItem(Properties props) {
        super(props, InfinityEnergyStorage::new);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return 13;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 16733695; //ChatFormatting.LIGHT_PURPLE
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, components, tooltipFlag);

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.enerjolt.capacity.txt",
                            Component.translatable("tooltip.enerjolt.infinite.txt").
                                    withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).
                    withStyle(ChatFormatting.GRAY));
            components.add(Component.translatable("tooltip.enerjolt.transfer_rate.txt",
                            Component.translatable("tooltip.enerjolt.infinite.txt").
                                    withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).
                    withStyle(ChatFormatting.GRAY));
        }else {
            components.add(Component.translatable("tooltip.enerjolt.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }
}

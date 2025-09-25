package me.twheatking.enerjolt.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CableInsulatorItem extends Item {
    public CableInsulatorItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.cable_insulator.txt.shift.1").withStyle(ChatFormatting.GRAY));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }
}

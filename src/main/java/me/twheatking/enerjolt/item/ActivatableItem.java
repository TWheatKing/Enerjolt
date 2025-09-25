package me.twheatking.enerjolt.item;

import net.minecraft.world.item.ItemStack;

public interface ActivatableItem {
    boolean isActive(ItemStack itemStack);
}

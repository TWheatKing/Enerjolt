package me.twheatking.enerjolt.screen.base;

import me.twheatking.enerjolt.machine.configuration.ComparatorMode;
import me.twheatking.enerjolt.machine.configuration.RedstoneMode;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IConfigurableMenu {
    BlockEntity getBlockEntity();

    RedstoneMode getRedstoneMode();
    ComparatorMode getComparatorMode();
}

package me.twheatking.enerjolt.machine.configuration;

import org.jetbrains.annotations.NotNull;

public interface IRedstoneModeHandler {
    @NotNull
    RedstoneMode @NotNull [] getAvailableRedstoneModes();

    @NotNull
    RedstoneMode getRedstoneMode();

    boolean setRedstoneMode(@NotNull RedstoneMode redstoneMode);
}

package me.twheatking.enerjolt.integration.cctweaked;

import dan200.computercraft.api.ComputerCraftAPI;

public final class EnerjoltCCTweakedIntegration {
    private EnerjoltCCTweakedIntegration() {}

    public static void register() {
        ComputerCraftAPI.registerGenericSource(new RedstoneModeGenericPeripheral());
        ComputerCraftAPI.registerGenericSource(new ComparatorModeGenericPeripheral());
        ComputerCraftAPI.registerGenericSource(new WeatherControllerGenericPeripheral());
        ComputerCraftAPI.registerGenericSource(new TimeControllerGenericPeripheral());
    }
}

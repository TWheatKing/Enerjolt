package me.twheatking.enerjolt.integration.cctweaked;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.entity.TimeControllerBlockEntity;
import net.minecraft.server.level.ServerLevel;

public class TimeControllerGenericPeripheral implements GenericPeripheral {
    @Override
    public String id() {
        return EJOLTAPI.MOD_ID + ":time_controller_peripheral";
    }

    @LuaFunction(mainThread = true)
    public final boolean setTime(TimeControllerBlockEntity timeController, int time) throws LuaException {
        if(time < 0 || time >= 24000)
            throw new LuaException("Time must be >= 0 and < 24000");

        if(!(timeController.getLevel() instanceof ServerLevel level) || timeController.getEnergy() < TimeControllerBlockEntity.CAPACITY)
            return false;

        timeController.clearEnergy();

        long currentTime = level.getDayTime();

        int currentDayTime = (int)(currentTime % 24000);

        if(currentDayTime <= time)
            level.setDayTime(currentTime - currentDayTime + time);
        else
            level.setDayTime(currentTime + 24000 - currentDayTime + time);

        return true;
    }
}

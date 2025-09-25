package me.twheatking.enerjolt.integration.cctweaked;

public final class EnerjoltCCTweakedUtils {
    private EnerjoltCCTweakedUtils() {}

    public static boolean isCCTweakedAvailable() {
        try {
            Class.forName("dan200.computercraft.api.ComputerCraftAPI");

            return true;
        }catch(ClassNotFoundException e) {
            return false;
        }
    }
}

package me.twheatking.enerjolt.inventory;

import net.minecraft.world.inventory.ContainerData;

public class UpgradeModuleViewContainerData implements ContainerData {
    private boolean inUpgradeModuleView = false;

    @Override
    public int get(int index) {
        if(index == 0)
            return inUpgradeModuleView?1:0;

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            inUpgradeModuleView = value != 0;
    }

    public boolean isInUpgradeModuleView() {
        return inUpgradeModuleView;
    }

    public void toggleInUpgradeModuleView() {
        inUpgradeModuleView = !inUpgradeModuleView;
    }

    @Override
    public int getCount() {
        return 1;
    }
}

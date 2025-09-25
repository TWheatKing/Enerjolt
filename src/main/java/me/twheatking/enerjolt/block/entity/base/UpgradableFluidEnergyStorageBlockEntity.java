package me.twheatking.enerjolt.block.entity.base;

import me.twheatking.enerjolt.energy.IEnerjoltEnergyStorage;
import me.twheatking.enerjolt.inventory.upgrade.UpgradeModuleInventory;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public abstract class UpgradableFluidEnergyStorageBlockEntity
        <E extends IEnerjoltEnergyStorage, F extends IFluidHandler>
        extends MenuFluidEnergyStorageBlockEntity<E, F> {
    protected final UpgradeModuleInventory upgradeModuleInventory;
    protected final ContainerListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    public UpgradableFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                   String machineName,
                                                   int baseEnergyCapacity, int baseEnergyTransferRate,
                                                   FluidStorageMethods<F> fluidStorageMethods, int baseTankCapacity,
                                                   UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, fluidStorageMethods,
                baseTankCapacity);

        this.upgradeModuleInventory = new UpgradeModuleInventory(upgradeModifierSlots);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT(registries));

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"), registries);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        super.loadAdditional(nbt, registries);
    }

    public void drops(Level level, BlockPos worldPosition) {
        Containers.dropContents(level, worldPosition, upgradeModuleInventory);
    }

    protected void updateUpgradeModules() {
        setChanged();
        syncEnergyToPlayers(32);
    }
}
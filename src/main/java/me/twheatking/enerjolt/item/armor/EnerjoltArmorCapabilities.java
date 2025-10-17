package me.twheatking.enerjolt.item.armor;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Registers energy storage capabilities for endgame armor items.
 * This allows them to be charged in the Advanced Charger and other energy machines.
 */
public class EnerjoltArmorCapabilities {

    /**
     * Register energy capabilities for armor items
     * Call this from your capability registration event
     */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Register for all armor items that extend EnergyArmorItem
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, context) -> new ArmorEnergyStorage(stack),
                // List all your armor items here
                me.twheatking.enerjolt.item.EnerjoltItems.ENERJOLT_HELMET.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.ENERJOLT_CHESTPLATE.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.ENERJOLT_LEGGINGS.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.ENERJOLT_BOOTS.get(),

                me.twheatking.enerjolt.item.EnerjoltItems.CRYONITE_HELMET.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.CRYONITE_CHESTPLATE.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.CRYONITE_LEGGINGS.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.CRYONITE_BOOTS.get(),

                me.twheatking.enerjolt.item.EnerjoltItems.VOIDSTONE_HELMET.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.VOIDSTONE_CHESTPLATE.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.VOIDSTONE_LEGGINGS.get(),
                me.twheatking.enerjolt.item.EnerjoltItems.VOIDSTONE_BOOTS.get()
        );
    }

    /**
     * IEnergyStorage implementation for armor items
     */
    private static class ArmorEnergyStorage implements IEnergyStorage {
        private final ItemStack stack;

        public ArmorEnergyStorage(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceive()) {
                return 0;
            }

            int currentEnergy = EnergyArmorItem.getEnergy(stack);
            int maxEnergy = EnergyArmorItem.getMaxEnergy(stack);
            int energyReceived = Math.min(maxEnergy - currentEnergy, maxReceive);

            if (!simulate && energyReceived > 0) {
                EnergyArmorItem.setEnergy(stack, currentEnergy + energyReceived);
            }

            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            // Armor cannot provide energy
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return EnergyArmorItem.getEnergy(stack);
        }

        @Override
        public int getMaxEnergyStored() {
            return EnergyArmorItem.getMaxEnergy(stack);
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
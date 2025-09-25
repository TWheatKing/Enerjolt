package me.twheatking.enerjolt.entity;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.inventory.CombinedContainerData;
import me.twheatking.enerjolt.inventory.data.EnergyValueContainerData;
import me.twheatking.enerjolt.item.EnerjoltItems;
import me.twheatking.enerjolt.screen.MinecartAdvancedBatteryBoxMenu;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MinecartAdvancedBatteryBox extends AbstractMinecartBatteryBox {
    public static final int CAPACITY = ModConfigs.COMMON_ADVANCED_BATTERY_BOX_MINECART_CAPACITY.getValue();
    public static final int MAX_TRANSFER = ModConfigs.COMMON_ADVANCED_BATTERY_BOX_MINECART_TRANSFER_RATE.getValue();

    private static final EntityDataAccessor<Integer> DATA_ID_ENERGY =
            SynchedEntityData.defineId(MinecartAdvancedBatteryBox.class, EntityDataSerializers.INT);

    protected final ContainerData data = new CombinedContainerData(
            new EnergyValueContainerData(MinecartAdvancedBatteryBox.this::getEnergy, MinecartAdvancedBatteryBox.this::setEnergy),
            new EnergyValueContainerData(MinecartAdvancedBatteryBox.this::getCapacity, value -> {})
    );

    public MinecartAdvancedBatteryBox(EntityType<? extends MinecartAdvancedBatteryBox> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartAdvancedBatteryBox(Level level, double x, double y, double z) {
        super(EnerjoltEntityTypes.ADVANCED_BATTERY_BOX_MINECART.get(), level, x, y, z);
    }

    @Override
    protected Item getDropItem() {
        return EnerjoltItems.ADVANCED_BATTERY_BOX_MINECART.get();
    }

    public BlockState getDefaultDisplayBlockState() {
        return EnerjoltBlocks.ADVANCED_BATTERY_BOX.get().defaultBlockState();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MinecartAdvancedBatteryBoxMenu(id, inventory, this, data);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builer) {
        super.defineSynchedData(builer);

        builer.define(DATA_ID_ENERGY, 0);
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int getTransferRate() {
        return MAX_TRANSFER;
    }

    @Override
    public int getEnergy() {
        return entityData.get(DATA_ID_ENERGY);
    }

    @Override
    public void setEnergy(int energy) {
        entityData.set(DATA_ID_ENERGY, energy);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(EnerjoltItems.ADVANCED_BATTERY_BOX_MINECART.get());
    }
}

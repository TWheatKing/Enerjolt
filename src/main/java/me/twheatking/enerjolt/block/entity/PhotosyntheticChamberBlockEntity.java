package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.PhotosyntheticChamberBlock;
import me.twheatking.enerjolt.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.twheatking.enerjolt.block.multiblock.MultiblockPattern;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.energy.ReceiveOnlyEnergyStorage;
import me.twheatking.enerjolt.inventory.CombinedContainerData;
import me.twheatking.enerjolt.inventory.InputOutputItemHandler;
import me.twheatking.enerjolt.inventory.data.*;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.screen.PhotosyntheticChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.twheatking.enerjolt.networking.ModMessages;
import me.twheatking.enerjolt.networking.packet.HologramSyncS2CPacket;

import java.util.*;

public class PhotosyntheticChamberBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler> {

    // Config values
    private static final int ENERGY_CONSUMPTION_5X5 = 60;
    private static final int ENERGY_CONSUMPTION_7X7 = 120;
    private static final int BASE_PROCESS_TIME = 300; // 15 seconds
    private static final int WATER_PER_OPERATION = 1000; // mB

    // Slots: 0 = seed input (optional), 1 = fertilizer input (optional), 2-5 = output slots
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler,
            (i, stack) -> i >= 0 && i <= 1, i -> i >= 2 && i <= 5);

    // Fluid tank for water
    private final FluidTank fluidTank = new FluidTank(10000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }
    };

    // Multiblock
    private MultiblockPattern pattern;
    private boolean isFormed = false;
    private int multiblockSize = 5; // 5 or 7
    private boolean showHologram = false;
    private Set<UUID> playersViewingHologram = new HashSet<>();

    // Processing
    private int progress = 0;
    private int maxProgress = BASE_PROCESS_TIME;
    private boolean isProcessing = false;

    public PhotosyntheticChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.PHOTOSYNTHETIC_CHAMBER_ENTITY.get(),
                blockPos, blockState,
                "photosynthetic_chamber",
                80000,  // Base capacity
                800,    // Base transfer rate
                6,      // 6 slots: 2 input (seed + fertilizer) + 4 output
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.pattern = new MultiblockPattern(null, blockPos, facing, 5);
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxReceive() {
                return Math.max(1, (int)Math.ceil(maxReceive * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (slot == 0) {
                    // Seed input slot - accept seeds
                    return isSeed(stack);
                } else if (slot == 1) {
                    // Fertilizer slot - accept bone meal
                    return stack.getItem() == Items.BONE_MEAL;
                }
                // Output slots - no direct insertion
                return false;
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new BooleanValueContainerData(() -> isFormed, value -> {}),
                new BooleanValueContainerData(() -> isProcessing, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        return new PhotosyntheticChamberMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;
        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidTank;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt("Progress", progress);
        nbt.putInt("MaxProgress", maxProgress);
        nbt.putBoolean("IsFormed", isFormed);
        nbt.putBoolean("ShowHologram", showHologram);
        nbt.putInt("MultiblockSize", multiblockSize);
        nbt.put("FluidTank", fluidTank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        progress = nbt.getInt("Progress");
        maxProgress = nbt.getInt("MaxProgress");
        isFormed = nbt.getBoolean("IsFormed");
        showHologram = nbt.getBoolean("ShowHologram");
        multiblockSize = nbt.getInt("MultiblockSize");
        if (nbt.contains("FluidTank")) {
            fluidTank.readFromNBT(registries, nbt.getCompound("FluidTank"));
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, PhotosyntheticChamberBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        // Validate multiblock every 20 ticks
        if (level.getGameTime() % 20 == 0) {
            blockEntity.validateMultiblock();
        }

        // Only process if formed and redstone allows
        if (!blockEntity.isFormed || !blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED))) {
            blockEntity.resetProgress();
            return;
        }

        // Check if we can process
        if (blockEntity.canProcess()) {
            blockEntity.isProcessing = true;

            // Calculate energy consumption
            int energyPerTick = blockEntity.getEnergyConsumptionPerTick();

            if (blockEntity.energyStorage.getEnergy() >= energyPerTick) {
                // Consume energy
                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyPerTick);

                // Increment progress
                blockEntity.progress++;

                // Update LIT state
                if (!state.getValue(PhotosyntheticChamberBlock.LIT)) {
                    level.setBlock(blockPos, state.setValue(PhotosyntheticChamberBlock.LIT, true), 3);
                }

                // Check if processing complete
                if (blockEntity.progress >= blockEntity.maxProgress) {
                    blockEntity.processCrop();
                    blockEntity.resetProgress();
                }
            } else {
                blockEntity.isProcessing = false;
            }
        } else {
            blockEntity.resetProgress();
        }

        // Update LIT state if not processing
        if (!blockEntity.isProcessing && state.getValue(PhotosyntheticChamberBlock.LIT)) {
            level.setBlock(blockPos, state.setValue(PhotosyntheticChamberBlock.LIT, false), 3);
        }
    }

    private void validateMultiblock() {
        Direction facing = getBlockState().getValue(PhotosyntheticChamberBlock.FACING);

        // Try 5x5 first
        pattern = new MultiblockPattern(level, worldPosition, facing, 5);
        if (pattern.validate(true)) {
            multiblockSize = 5;
            isFormed = true;
            updateFormedState(true);
            return;
        }

        // Try 7x7
        pattern = new MultiblockPattern(level, worldPosition, facing, 7);
        if (pattern.validate(true)) {
            multiblockSize = 7;
            isFormed = true;
            updateFormedState(true);
            return;
        }

        // Not formed
        if (isFormed) {
            isFormed = false;
            updateFormedState(false);
        }
    }

    private void updateFormedState(boolean formed) {
        BlockState state = getBlockState();
        if (state.getValue(PhotosyntheticChamberBlock.FORMED) != formed) {
            level.setBlock(worldPosition, state.setValue(PhotosyntheticChamberBlock.FORMED, formed), 3);
        }
    }

    private boolean canProcess() {
        if (!isFormed) return false;

        // Check for crop in center
        if (pattern == null) return false;
        BlockPos plantPos = pattern.getCenterPos().above();
        BlockState plantState = level.getBlockState(plantPos);
        if (!isCrop(plantState)) {
            return false;
        }

        // Check water
        if (fluidTank.getFluidAmount() < WATER_PER_OPERATION) {
            return false;
        }

        // Check output space
        return hasOutputSpace();
    }

    private boolean hasOutputSpace() {
        for (int i = 2; i <= 5; i++) {
            if (itemHandler.getStackInSlot(i).getCount() < itemHandler.getSlotLimit(i)) {
                return true;
            }
        }
        return false;
    }

    private void processCrop() {
        if (pattern == null) return;

        // Get crop type from center
        BlockPos plantPos = pattern.getCenterPos().above();
        BlockState cropState = level.getBlockState(plantPos);

        // Consume water
        fluidTank.drain(WATER_PER_OPERATION, IFluidHandler.FluidAction.EXECUTE);

        // Calculate output multiplier based on size
        int multiplier = multiblockSize == 7 ? 2 : 1;

        // Check if fertilizer is used (doubles output)
        boolean useFertilizer = !itemHandler.getStackInSlot(1).isEmpty();
        if (useFertilizer) {
            itemHandler.extractItem(1, 1, false);
            multiplier *= 2;
        }

        // Output crops
        ItemStack crop = getCropOutput(cropState);
        if (!crop.isEmpty()) {
            crop.setCount((2 + level.random.nextInt(3)) * multiplier); // 2-4 crops
            insertOutput(crop);
        }

        // Output seeds (30% chance)
        if (level.random.nextFloat() < 0.3f) {
            ItemStack seed = getSeedForCrop(cropState);
            if (!seed.isEmpty()) {
                seed.setCount(1 * multiplier);
                insertOutput(seed);
            }
        }

        // Output stone pebble (5% chance) - using gravel as pebble
        if (level.random.nextFloat() < 0.05f) {
            insertOutput(new ItemStack(Items.GRAVEL, 1));
        }
    }

    private boolean isCrop(BlockState state) {
        Block block = state.getBlock();
        return block instanceof CropBlock ||
                block == Blocks.WHEAT ||
                block == Blocks.CARROTS ||
                block == Blocks.POTATOES ||
                block == Blocks.BEETROOTS;
    }

    private boolean isSeed(ItemStack stack) {
        return stack.getItem() == Items.WHEAT_SEEDS ||
                stack.getItem() == Items.CARROT ||
                stack.getItem() == Items.POTATO ||
                stack.getItem() == Items.BEETROOT_SEEDS;
    }

    private ItemStack getCropOutput(BlockState cropState) {
        Block block = cropState.getBlock();
        if (block == Blocks.WHEAT) return new ItemStack(Items.WHEAT);
        if (block == Blocks.CARROTS) return new ItemStack(Items.CARROT);
        if (block == Blocks.POTATOES) return new ItemStack(Items.POTATO);
        if (block == Blocks.BEETROOTS) return new ItemStack(Items.BEETROOT);
        return ItemStack.EMPTY;
    }

    private ItemStack getSeedForCrop(BlockState cropState) {
        Block block = cropState.getBlock();
        if (block == Blocks.WHEAT) return new ItemStack(Items.WHEAT_SEEDS);
        if (block == Blocks.CARROTS) return new ItemStack(Items.CARROT);
        if (block == Blocks.POTATOES) return new ItemStack(Items.POTATO);
        if (block == Blocks.BEETROOTS) return new ItemStack(Items.BEETROOT_SEEDS);
        return ItemStack.EMPTY;
    }

    private void insertOutput(ItemStack stack) {
        for (int i = 2; i <= 5; i++) {
            ItemStack slotStack = itemHandler.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                itemHandler.setStackInSlot(i, stack.copy());
                return;
            } else if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int space = itemHandler.getSlotLimit(i) - slotStack.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, stack.getCount());
                    slotStack.grow(toAdd);
                    if (stack.getCount() == toAdd) return;
                    stack.shrink(toAdd);
                }
            }
        }
    }

    private int getEnergyConsumptionPerTick() {
        int baseConsumption = multiblockSize == 5 ? ENERGY_CONSUMPTION_5X5 : ENERGY_CONSUMPTION_7X7;
        return Math.max(1, (int)Math.ceil(baseConsumption *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));
    }

    private void resetProgress() {
        progress = 0;
        isProcessing = false;
    }

    public void toggleHologram(ServerPlayer player) {
        UUID playerId = player.getUUID();
        boolean newState;

        if (playersViewingHologram.contains(playerId)) {
            playersViewingHologram.remove(playerId);
            newState = false;
        } else {
            playersViewingHologram.add(playerId);
            newState = true;
        }

        // Send packet to client
        ModMessages.sendToPlayer(new HologramSyncS2CPacket(worldPosition, playerId, newState), player);

        setChanged();
    }

    public void setHologramStateClient(UUID playerId, boolean show) {
        if (show) {
            playersViewingHologram.add(playerId);
        } else {
            playersViewingHologram.remove(playerId);
        }
    }

    public MultiblockPattern getPattern() {
        return pattern;
    }

    public boolean isShowingHologramFor(UUID playerId) {
        return playersViewingHologram.contains(playerId);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public boolean isFormed() {
        return isFormed;
    }
}
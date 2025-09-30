package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.IndustrialGreenhouseBlock;
import me.twheatking.enerjolt.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.twheatking.enerjolt.block.multiblock.MultiblockPattern;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.energy.ReceiveOnlyEnergyStorage;
import me.twheatking.enerjolt.inventory.CombinedContainerData;
import me.twheatking.enerjolt.inventory.InputOutputItemHandler;
import me.twheatking.enerjolt.inventory.data.*;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.screen.IndustrialGreenhouseMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
import me.twheatking.enerjolt.networking.packet.MultiblockPatternSyncS2CPacket;

import java.util.*;

public class IndustrialGreenhouseBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler> {

    // Config values
    private static final int ENERGY_CONSUMPTION_5X5 = 80;
    private static final int ENERGY_CONSUMPTION_7X7 = 150;
    private static final int BASE_PROCESS_TIME = 300; // 15 seconds
    private static final int WATER_PER_OPERATION = 1000; // mB

    // Slots: 0 = sapling input, 1-4 = output slots
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler,
            (i, stack) -> i == 0, i -> i >= 1 && i <= 4);

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

    private MultiblockPattern.Shape greenhouseShape = MultiblockPattern.Shape.CUBE; // Default to dome for chambers

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

    public IndustrialGreenhouseBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.INDUSTRIAL_GREENHOUSE_ENTITY.get(),
                blockPos, blockState,
                "industrial_greenhouse",
                100000, // Base capacity
                1000,   // Base transfer rate
                5,      // 5 slots: 1 input + 4 output
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
                    // Input slot - only saplings
                    return stack.getItem().toString().toLowerCase().contains("sapling");
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
        return new IndustrialGreenhouseMenu(id, inventory, this, upgradeModuleInventory, this.data);
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
        nbt.putString("GreenhouseShape", greenhouseShape.name());
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
        try {
            greenhouseShape = MultiblockPattern.Shape.valueOf(nbt.getString("GreenhouseShape"));
        } catch (Exception e) {
            greenhouseShape = MultiblockPattern.Shape.CUBE;
        }
        if (nbt.contains("FluidTank")) {
            fluidTank.readFromNBT(registries, nbt.getCompound("FluidTank"));
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, IndustrialGreenhouseBlockEntity blockEntity) {
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
                if (!state.getValue(IndustrialGreenhouseBlock.LIT)) {
                    level.setBlock(blockPos, state.setValue(IndustrialGreenhouseBlock.LIT, true), 3);
                }

                // Check if processing complete
                if (blockEntity.progress >= blockEntity.maxProgress) {
                    blockEntity.processTree();
                    blockEntity.resetProgress();
                }
            } else {
                blockEntity.isProcessing = false;
            }
        } else {
            blockEntity.resetProgress();
        }

        // Update LIT state if not processing
        if (!blockEntity.isProcessing && state.getValue(IndustrialGreenhouseBlock.LIT)) {
            level.setBlock(blockPos, state.setValue(IndustrialGreenhouseBlock.LIT, false), 3);
        }
    }

    private void validateMultiblock() {
        Direction facing = getBlockState().getValue(IndustrialGreenhouseBlock.FACING);
        boolean wasFormed = isFormed;


        // Try 5x5 first
        pattern = new MultiblockPattern(level, worldPosition, facing, 5);
        pattern.setShape(greenhouseShape);
        if (pattern.validate(true)) {
            multiblockSize = 5;
            isFormed = true;
            updateFormedState(true);
            if (!wasFormed) {
                syncPatternToClients();
            }
            return;
        }

        // Try 7x7
        pattern = new MultiblockPattern(level, worldPosition, facing, 7);
        pattern.setShape(greenhouseShape);
        if (pattern.validate(true)) {
            multiblockSize = 7;
            isFormed = true;
            updateFormedState(true);
            if (!wasFormed) {
                syncPatternToClients();
            }
            return;
        }

        // Not formed
        if (isFormed) {
            isFormed = false;
            updateFormedState(false);
            syncPatternToClients();
        }
    }

    private void updateFormedState(boolean formed) {
        BlockState state = getBlockState();
        if (state.getValue(IndustrialGreenhouseBlock.FORMED) != formed) {
            level.setBlock(worldPosition, state.setValue(IndustrialGreenhouseBlock.FORMED, formed), 3);
        }
    }

    private void syncPatternToClients() {
        if (level == null || level.isClientSide) return;

        MultiblockPatternSyncS2CPacket packet = new MultiblockPatternSyncS2CPacket(
                worldPosition,
                getBlockState().getValue(IndustrialGreenhouseBlock.FACING),
                pattern.getSize(),
                pattern.isValid(),
                new ArrayList<>(pattern.getMissingBlocks()),
                new ArrayList<>(pattern.getGlassPositions()),
                pattern.getCenterPos()
        );

        ModMessages.sendToPlayersWithinXBlocks(packet, worldPosition, (ServerLevel) level, 64);
    }

    private boolean canProcess() {
        if (!isFormed) return false;

        // Check for sapling in center
        if (pattern == null) return false;
        BlockPos plantPos = pattern.getCenterPos().above();
        BlockState plantState = level.getBlockState(plantPos);
        if (!plantState.getBlock().toString().toLowerCase().contains("sapling")) {
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
        for (int i = 1; i <= 4; i++) {
            if (itemHandler.getStackInSlot(i).getCount() < itemHandler.getSlotLimit(i)) {
                return true;
            }
        }
        return false;
    }

    private void processTree() {
        if (pattern == null) return;

        // Get tree type from sapling
        BlockPos plantPos = pattern.getCenterPos().above();
        BlockState saplingState = level.getBlockState(plantPos);

        // Consume water
        fluidTank.drain(WATER_PER_OPERATION, IFluidHandler.FluidAction.EXECUTE);

        // Calculate output multiplier based on size
        int multiplier = multiblockSize == 7 ? 2 : 1;

        // Output logs
        ItemStack logs = getLogsForSapling(saplingState);
        if (!logs.isEmpty()) {
            logs.setCount((4 + level.random.nextInt(3)) * multiplier); // 4-6 logs (8-12 for 7x7)
            insertOutput(logs);
        }

        // Output saplings (20% chance)
        if (level.random.nextFloat() < 0.2f) {
            ItemStack sapling = new ItemStack(saplingState.getBlock().asItem(), 1 * multiplier);
            insertOutput(sapling);
        }

        // Output apples for oak (5% chance)
        if (saplingState.getBlock() == Blocks.OAK_SAPLING && level.random.nextFloat() < 0.05f) {
            insertOutput(new ItemStack(Items.APPLE, 1 * multiplier));
        }
    }

    private ItemStack getLogsForSapling(BlockState sapling) {
        return switch (sapling.getBlock().toString()) {
            case "Block{minecraft:oak_sapling}" -> new ItemStack(Items.OAK_LOG);
            case "Block{minecraft:birch_sapling}" -> new ItemStack(Items.BIRCH_LOG);
            case "Block{minecraft:spruce_sapling}" -> new ItemStack(Items.SPRUCE_LOG);
            case "Block{minecraft:jungle_sapling}" -> new ItemStack(Items.JUNGLE_LOG);
            case "Block{minecraft:acacia_sapling}" -> new ItemStack(Items.ACACIA_LOG);
            case "Block{minecraft:dark_oak_sapling}" -> new ItemStack(Items.DARK_OAK_LOG);
            case "Block{minecraft:cherry_sapling}" -> new ItemStack(Items.CHERRY_LOG);
            case "Block{minecraft:mangrove_propagule}" -> new ItemStack(Items.MANGROVE_LOG);
            default -> new ItemStack(Items.OAK_LOG); // Default fallback
        };
    }

    private void insertOutput(ItemStack stack) {
        for (int i = 1; i <= 4; i++) {
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
            // Also sync pattern when enabling hologram
            syncPatternToClients();
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

    public void setPatternClient(MultiblockPatternSyncS2CPacket packet) {
        // Reconstruct pattern on client side
        this.pattern = new MultiblockPattern(level, packet.controllerPos(), packet.facing(), packet.size());
        // Manually set the validation results
        this.pattern.setClientData(packet.isValid(), packet.missingBlocks(), packet.glassPositions(), packet.centerPos());
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
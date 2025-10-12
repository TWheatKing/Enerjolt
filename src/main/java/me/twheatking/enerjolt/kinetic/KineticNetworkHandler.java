package me.twheatking.enerjolt.kinetic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

/**
 * Handles kinetic network propagation and management.
 * Connects kinetic blocks, propagates rotation, and manages power distribution.
 * Similar to cable networks but for rotational energy.
 */
public class KineticNetworkHandler {

    /**
     * Represents a connected kinetic network
     */
    public static class KineticNetwork {
        private final Set<BlockPos> connectedBlocks = new HashSet<>();
        private float totalRPM;
        private float totalTorque;
        private int direction; // 1 = clockwise, -1 = counterclockwise
        private float averageTemperature;
        private float totalVibration;

        public KineticNetwork() {
            this.direction = 0;
        }

        public void addBlock(BlockPos pos) {
            connectedBlocks.add(pos);
        }

        public void removeBlock(BlockPos pos) {
            connectedBlocks.remove(pos);
        }

        public Set<BlockPos> getConnectedBlocks() {
            return Collections.unmodifiableSet(connectedBlocks);
        }

        public int getBlockCount() {
            return connectedBlocks.size();
        }

        public float getTotalRPM() {
            return totalRPM;
        }

        public void setTotalRPM(float rpm) {
            this.totalRPM = rpm;
        }

        public float getTotalTorque() {
            return totalTorque;
        }

        public void setTotalTorque(float torque) {
            this.totalTorque = torque;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public float getAverageTemperature() {
            return averageTemperature;
        }

        public void setAverageTemperature(float temp) {
            this.averageTemperature = temp;
        }

        public float getTotalVibration() {
            return totalVibration;
        }

        public void setTotalVibration(float vibration) {
            this.totalVibration = vibration;
        }

        public boolean isEmpty() {
            return connectedBlocks.isEmpty();
        }

        public void clear() {
            connectedBlocks.clear();
        }
    }

    /**
     * Finds all kinetic blocks connected to the given position
     * @param level The world
     * @param startPos Starting position
     * @param maxDistance Maximum search distance (prevents infinite loops)
     * @return A network of connected blocks
     */
    public static KineticNetwork buildNetwork(Level level, BlockPos startPos, int maxDistance) {
        KineticNetwork network = new KineticNetwork();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();

        toVisit.add(startPos);
        visited.add(startPos);

        int distance = 0;

        while (!toVisit.isEmpty() && distance < maxDistance) {
            int levelSize = toVisit.size();

            for (int i = 0; i < levelSize; i++) {
                BlockPos currentPos = toVisit.poll();
                if (currentPos == null)
                    continue;

                BlockEntity blockEntity = level.getBlockEntity(currentPos);
                if (blockEntity instanceof IKineticBlockEntity) {
                    network.addBlock(currentPos);

                    // Check all adjacent blocks
                    for (Direction direction : Direction.values()) {
                        BlockPos adjacentPos = currentPos.relative(direction);

                        if (visited.contains(adjacentPos))
                            continue;

                        BlockEntity adjacentEntity = level.getBlockEntity(adjacentPos);
                        if (adjacentEntity instanceof IKineticBlockEntity kineticEntity) {
                            // Check if they can connect
                            if (canConnect((IKineticBlockEntity) blockEntity, kineticEntity, direction)) {
                                toVisit.add(adjacentPos);
                                visited.add(adjacentPos);
                            }
                        }
                    }
                }
            }

            distance++;
        }

        return network;
    }

    /**
     * Checks if two kinetic blocks can connect in the given direction
     * @param from Source block
     * @param to Destination block
     * @param direction Direction from source to destination
     * @return True if they can connect
     */
    private static boolean canConnect(IKineticBlockEntity from, IKineticBlockEntity to, Direction direction) {
        // Check if both blocks have kinetic capability on the connecting faces
        IKineticStorage fromStorage = from.getKineticStorage(direction);
        IKineticStorage toStorage = to.getKineticStorage(direction.getOpposite());

        if (fromStorage == null || toStorage == null)
            return false;

        // At least one must be able to provide and one must be able to receive
        boolean canTransfer = (fromStorage.canProvideRotation() && toStorage.canReceiveRotation()) ||
                (toStorage.canProvideRotation() && fromStorage.canReceiveRotation());

        return canTransfer;
    }

    /**
     * Propagates rotation through a network
     * @param level The world
     * @param network The kinetic network
     */
    public static void propagateRotation(Level level, KineticNetwork network) {
        if (network.isEmpty())
            return;

        // Step 1: Calculate total generation and consumption
        float totalGeneration = 0;
        float totalConsumption = 0;
        float totalTorqueGeneration = 0;
        float totalTorqueConsumption = 0;

        List<IKineticBlockEntity> generators = new ArrayList<>();
        List<IKineticBlockEntity> consumers = new ArrayList<>();

        for (BlockPos pos : network.getConnectedBlocks()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof IKineticBlockEntity kineticEntity))
                continue;

            IKineticStorage storage = kineticEntity.getKineticStorage(null);
            if (storage == null)
                continue;

            if (storage.canProvideRotation() && storage.isRotating()) {
                totalGeneration += storage.getRPM();
                totalTorqueGeneration += storage.getTorque();
                generators.add(kineticEntity);
            }

            if (storage.canReceiveRotation()) {
                // Calculate consumption based on machine type
                float consumption = calculateConsumption(kineticEntity);
                totalConsumption += consumption;
                totalTorqueConsumption += storage.getMaxTorque() * 0.1f; // Request 10% of max
                consumers.add(kineticEntity);
            }
        }

        // Step 2: Calculate network RPM and torque
        float networkRPM = 0;
        float networkTorque = 0;

        if (!generators.isEmpty()) {
            // Average RPM from all generators
            networkRPM = totalGeneration / generators.size();
            networkTorque = totalTorqueGeneration;

            // Apply friction loss over distance
            float frictionLoss = calculateNetworkFrictionLoss(network);
            networkRPM *= (1.0f - frictionLoss);

            // Check if network is overstressed
            if (totalTorqueConsumption > totalTorqueGeneration) {
                // Overstressed - reduce RPM proportionally
                float stressRatio = totalTorqueGeneration / totalTorqueConsumption;
                networkRPM *= stressRatio;
                networkTorque = totalTorqueGeneration;
            }
        }

        // Step 3: Update network stats
        network.setTotalRPM(networkRPM);
        network.setTotalTorque(networkTorque);

        // Step 4: Distribute rotation to consumers
        if (!consumers.isEmpty() && networkRPM > 0) {
            float rpmPerConsumer = networkRPM / consumers.size();
            float torquePerConsumer = networkTorque / consumers.size();

            for (IKineticBlockEntity consumer : consumers) {
                IKineticStorage storage = consumer.getKineticStorage(null);
                if (storage != null) {
                    storage.addRotation(rpmPerConsumer, torquePerConsumer, false);
                }
            }
        }

        // Step 5: Calculate average temperature and vibration
        calculateNetworkAverages(level, network);
    }

    /**
     * Calculates the consumption rate of a kinetic consumer
     * @param entity The kinetic block entity
     * @return Consumption rate (RPM per tick)
     */
    private static float calculateConsumption(IKineticBlockEntity entity) {
        IKineticStorage storage = entity.getKineticStorage(null);
        if (storage == null)
            return 0;

        // Base consumption depends on machine type
        // This is a simplified calculation - actual machines will override this
        float baseConsumption = storage.getMaxTorque() * 0.1f;

        // Consumption increases with current RPM (higher speed = more friction)
        float speedMultiplier = 1.0f + (storage.getRPM() / storage.getMaxRPM()) * 0.5f;

        return baseConsumption * speedMultiplier;
    }

    /**
     * Calculates friction loss across the entire network
     * @param network The kinetic network
     * @return Friction loss multiplier (0.0 = no loss, 1.0 = complete loss)
     */
    private static float calculateNetworkFrictionLoss(KineticNetwork network) {
        // Base friction loss per block in network
        float baseLossPerBlock = 0.001f; // 0.1% per block

        // Larger networks have more friction
        int blockCount = network.getBlockCount();
        float loss = blockCount * baseLossPerBlock;

        // Cap friction loss at 50%
        return Math.min(0.5f, loss);
    }

    /**
     * Calculates and updates average network temperature and vibration
     * @param level The world
     * @param network The kinetic network
     */
    private static void calculateNetworkAverages(Level level, KineticNetwork network) {
        float totalTemp = 0;
        float totalVibration = 0;
        int count = 0;

        for (BlockPos pos : network.getConnectedBlocks()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof IKineticBlockEntity kineticEntity))
                continue;

            IKineticStorage storage = kineticEntity.getKineticStorage(null);
            if (storage != null) {
                totalTemp += storage.getTemperature();
                totalVibration += storage.getVibration();
                count++;
            }
        }

        if (count > 0) {
            network.setAverageTemperature(totalTemp / count);
            network.setTotalVibration(totalVibration);
        }
    }

    /**
     * Transfers rotation from one block to adjacent blocks
     * @param level The world
     * @param pos The source position
     */
    public static void transferRotationToAdjacent(Level level, BlockPos pos) {
        BlockEntity sourceEntity = level.getBlockEntity(pos);
        if (!(sourceEntity instanceof IKineticBlockEntity sourceKinetic))
            return;

        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.relative(direction);
            BlockEntity targetEntity = level.getBlockEntity(targetPos);

            if (!(targetEntity instanceof IKineticBlockEntity targetKinetic))
                continue;

            IKineticStorage sourceStorage = sourceKinetic.getKineticStorage(direction);
            IKineticStorage targetStorage = targetKinetic.getKineticStorage(direction.getOpposite());

            if (sourceStorage == null || targetStorage == null)
                continue;

            if (!sourceStorage.canProvideRotation() || !targetStorage.canReceiveRotation())
                continue;

            // Calculate how much rotation to transfer
            float availableRPM = sourceStorage.getRPM();
            float availableTorque = sourceStorage.getTorque();

            if (availableRPM <= 0)
                continue;

            // Transfer up to 50% of available rotation per tick
            float transferRPM = availableRPM * 0.5f;
            float transferTorque = availableTorque * 0.5f;

            // Check direction compatibility
            if (shouldReverseDirection(sourceKinetic, targetKinetic, direction)) {
                // Reverse direction for gears
                targetStorage.setDirection(-sourceStorage.getDirection());
            }

            // Attempt transfer
            float actualTransfer = targetStorage.addRotation(transferRPM, transferTorque, false);
            if (actualTransfer > 0) {
                sourceStorage.extractRotation(actualTransfer, transferTorque * (actualTransfer / transferRPM), false);
            }
        }
    }

    /**
     * Checks if rotation direction should be reversed between blocks
     * @param source Source block
     * @param target Target block
     * @param direction Direction of connection
     * @return True if direction should be reversed (like gears)
     */
    private static boolean shouldReverseDirection(IKineticBlockEntity source, IKineticBlockEntity target, Direction direction) {
        // This would be expanded based on block types
        // For now, assume direct connections don't reverse
        // Cogwheels/gears would override this
        return false;
    }

    /**
     * Marker interface for blocks that participate in kinetic networks
     */
    public interface IKineticBlockEntity {
        /**
         * Gets the kinetic storage for the given side
         * @param side The side to check (null for internal storage)
         * @return The kinetic storage, or null if not available
         */
        IKineticStorage getKineticStorage(Direction side);
    }
}
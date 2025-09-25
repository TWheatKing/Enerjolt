package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.entity.MinecartBatteryBox;
import me.twheatking.enerjolt.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class BatteryBoxMinecartItem extends Item {
    private static final DefaultDispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        private static final DefaultDispenseItemBehavior DEFAULT_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
            Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
            Level level = blockSource.level();
            double xOffset = blockSource.center().x() + direction.getStepX() * 1.125;
            double yOffset = Math.floor(blockSource.center().y()) + direction.getStepY();
            double zOffset = blockSource.center().z() + direction.getStepZ() * 1.125;
            BlockPos blockPos = blockSource.pos().relative(direction);
            BlockState blockState = level.getBlockState(blockPos);
            RailShape railShape = blockState.getBlock() instanceof BaseRailBlock?
                    ((BaseRailBlock)blockState.getBlock()).getRailDirection(blockState, level, blockPos, null):
                    RailShape.NORTH_SOUTH;

            double additionalYOffset;
            if(blockState.is(BlockTags.RAILS)) {
                additionalYOffset = railShape.isAscending()?.6:.1;
            }else {
                if(!blockState.isAir() || !level.getBlockState(blockPos.below()).is(BlockTags.RAILS))
                    return DEFAULT_ITEM_BEHAVIOR.dispense(blockSource, itemStack);

                BlockState blockStateBelow = level.getBlockState(blockPos.below());
                RailShape railShapeBelow = blockStateBelow.getBlock() instanceof BaseRailBlock?
                        blockStateBelow.getValue(((BaseRailBlock)blockStateBelow.getBlock()).getShapeProperty()):
                        RailShape.NORTH_SOUTH;

                additionalYOffset = direction != Direction.DOWN && railShapeBelow.isAscending()?-.4:-.9;
            }

            MinecartBatteryBox minecartBatteryBox = new MinecartBatteryBox(level, xOffset,
                    yOffset + additionalYOffset, zOffset);
            if(itemStack.has(DataComponents.CUSTOM_NAME))
                minecartBatteryBox.setCustomName(itemStack.getHoverName());

            level.addFreshEntity(minecartBatteryBox);
            itemStack.shrink(1);
            return itemStack;
        }
    };

    public BatteryBoxMinecartItem(Properties props) {
        super(props);

        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.capacity.txt",
                            EnergyUtils.getEnergyWithPrefix(MinecartBatteryBox.CAPACITY)).
                    withStyle(ChatFormatting.GRAY));
            components.add(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                            EnergyUtils.getEnergyWithPrefix(MinecartBatteryBox.MAX_TRANSFER)).
                    withStyle(ChatFormatting.GRAY));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(BlockTags.RAILS))
            return InteractionResult.FAIL;

        ItemStack itemStack = useOnContext.getItemInHand();
        if(!level.isClientSide) {
            RailShape railShape = blockState.getBlock() instanceof BaseRailBlock?
                    ((BaseRailBlock)blockState.getBlock()).getRailDirection(blockState, level, blockPos, null):
                    RailShape.NORTH_SOUTH;
            double yOffset = railShape.isAscending()?.5:0.;

            MinecartBatteryBox minecartBatteryBox = new MinecartBatteryBox(level, blockPos.getX() + .5,
                    blockPos.getY() + .0625 + yOffset, blockPos.getZ() + .5);
            if(itemStack.has(DataComponents.CUSTOM_NAME))
                minecartBatteryBox.setCustomName(itemStack.getHoverName());

            level.addFreshEntity(minecartBatteryBox);
            level.gameEvent(GameEvent.ENTITY_PLACE, blockPos, GameEvent.Context.of(useOnContext.getPlayer(),
                    level.getBlockState(blockPos.below())));
        }

        itemStack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}

package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.component.DimensionalPositionComponent;
import me.twheatking.enerjolt.component.EnerjoltDataComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TeleporterMatrixItem extends Item {
    public TeleporterMatrixItem(Properties props) {
        super(props);
    }

    public static boolean isLinked(ItemStack itemStack) {
        return itemStack.has(EnerjoltDataComponentTypes.DIMENSIONAL_POSITION);
    }

    public static BlockPos getBlockPos(Level level, ItemStack itemStack) {
        if(level.isClientSide)
            return null;

        if(!isLinked(itemStack))
            return null;


        DimensionalPositionComponent dimPos = itemStack.get(EnerjoltDataComponentTypes.DIMENSIONAL_POSITION);
        if(dimPos == null)
            return null;

        return new BlockPos(dimPos.x(), dimPos.y(), dimPos.z());
    }

    public static Level getDimension(Level level, ItemStack itemStack) {
        if(level.isClientSide || !(level instanceof ServerLevel))
            return null;

        if(!isLinked(itemStack))
            return null;

        DimensionalPositionComponent dimPos = itemStack.get(EnerjoltDataComponentTypes.DIMENSIONAL_POSITION);
        if(dimPos == null)
            return null;

        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, dimPos.dimensionId());
        return level.getServer().getLevel(dimensionKey);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide || !(level instanceof ServerLevel))
            return InteractionResult.SUCCESS;

        Player player = useOnContext.getPlayer();

        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState state = level.getBlockState(blockPos);

        ItemStack itemStack = useOnContext.getItemInHand();
        itemStack.set(EnerjoltDataComponentTypes.DIMENSIONAL_POSITION,
                new DimensionalPositionComponent(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        level.dimension().location()));

        if(state.is(EnerjoltBlocks.TELEPORTER.get())) {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.teleporter_matrix.set").
                                withStyle(ChatFormatting.GREEN)
                ));
            }
        }else {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.teleporter_matrix.set.warning").
                                withStyle(ChatFormatting.YELLOW)
                ));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide)
            return InteractionResultHolder.success(itemStack);

        if(itemStack.has(EnerjoltDataComponentTypes.DIMENSIONAL_POSITION))
            itemStack.remove(EnerjoltDataComponentTypes.DIMENSIONAL_POSITION);

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter_matrix.cleared").
                            withStyle(ChatFormatting.GREEN)
            ));
        }

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        DimensionalPositionComponent dimPos = itemStack.get(EnerjoltDataComponentTypes.DIMENSIONAL_POSITION);
        boolean linked = isLinked(itemStack) && dimPos != null;


        components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.status").withStyle(ChatFormatting.GRAY).
                append(Component.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).withStyle(linked?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(linked) {
            components.add(Component.empty());

           components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.location").
                   append(Component.literal(dimPos.x() + " " + dimPos.y() + " " + dimPos.z())));
           components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                   append(Component.literal(dimPos.dimensionId().toString())));
        }

        components.add(Component.empty());

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return getDescriptionId() + "." + (isLinked(itemStack)?"linked":"unlinked");
    }
}

package me.twheatking.enerjolt.event;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.input.ModKeyBindings;
import me.twheatking.enerjolt.networking.ModMessages;
import me.twheatking.enerjolt.networking.packet.UseTeleporterC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = EJOLTAPI.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if(ModKeyBindings.TELEPORTER_USE_KEY.consumeClick()) {
            Player player = Minecraft.getInstance().player;
            if(player == null)
                return;

            Level level = Minecraft.getInstance().level;
            if(level == null)
                return;

            BlockPos blockPos = player.getOnPos();
            BlockState state = level.getBlockState(blockPos);

            if(!state.is(EnerjoltBlocks.TELEPORTER.get()))
                return;

            ModMessages.sendToServer(new UseTeleporterC2SPacket(blockPos));
        }
    }
}

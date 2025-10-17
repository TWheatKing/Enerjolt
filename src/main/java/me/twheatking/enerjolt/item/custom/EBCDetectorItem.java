package me.twheatking.enerjolt.item.custom;

import me.twheatking.enerjolt.contamination.EBCContaminationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * E.B.C Detector - Shows player's current E.B.C contamination count
 * Similar to a Geiger counter for bio-contamination
 */
public class EBCDetectorItem extends Item {

    public EBCDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            // Get player's E.B.C data
            int ebcCount = EBCContaminationManager.getEBCCount(player);
            int stage = EBCContaminationManager.getEBCStage(player);

            // Create messages based on contamination level
            player.sendSystemMessage(Component.literal("=== E.B.C DETECTOR ===").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

            // Display current count
            ChatFormatting countColor = getCountColor(ebcCount);
            player.sendSystemMessage(Component.literal("E.B.C Count: " + ebcCount).withStyle(countColor, ChatFormatting.BOLD));

            // Display stage information
            String stageName = getStageName(stage);
            ChatFormatting stageColor = getStageColor(stage);
            player.sendSystemMessage(Component.literal("Status: " + stageName).withStyle(stageColor));

            // Display warnings/info based on stage
            displayStageInfo(player, stage, ebcCount);

            player.sendSystemMessage(Component.empty());
        }

        return InteractionResultHolder.success(itemStack);
    }

    /**
     * Get color based on E.B.C count
     */
    private ChatFormatting getCountColor(int count) {
        if (count >= 100) {
            return ChatFormatting.DARK_RED;
        } else if (count >= 50) {
            return ChatFormatting.GOLD;
        } else if (count >= 25) {
            return ChatFormatting.YELLOW;
        } else {
            return ChatFormatting.GREEN;
        }
    }

    /**
     * Get stage name
     */
    private String getStageName(int stage) {
        return switch (stage) {
            case 0 -> "Uncontaminated";
            case 1 -> "E.B.C Contamination";
            case 2 -> "PLAGUED BY E.B.C";
            default -> "Unknown";
        };
    }

    /**
     * Get color based on stage
     */
    private ChatFormatting getStageColor(int stage) {
        return switch (stage) {
            case 0 -> ChatFormatting.GREEN;
            case 1 -> ChatFormatting.GOLD;
            case 2 -> ChatFormatting.DARK_RED;
            default -> ChatFormatting.GRAY;
        };
    }

    /**
     * Display stage-specific information
     */
    private void displayStageInfo(Player player, int stage, int count) {
        switch (stage) {
            case 0 -> {
                if (count == 0) {
                    player.sendSystemMessage(Component.literal("✓ No contamination detected").withStyle(ChatFormatting.GREEN));
                } else {
                    player.sendSystemMessage(Component.literal("⚠ Low contamination detected (" + count + "/50)").withStyle(ChatFormatting.YELLOW));
                    player.sendSystemMessage(Component.literal("  Effects begin at 50 counts").withStyle(ChatFormatting.GRAY));
                }
            }
            case 1 -> {
                player.sendSystemMessage(Component.literal("⚠ WARNING: Accelerated hunger depletion active").withStyle(ChatFormatting.GOLD));
                player.sendSystemMessage(Component.literal("  " + (100 - count) + " counts until Stage 3").withStyle(ChatFormatting.GRAY));
            }
            case 2 -> {
                player.sendSystemMessage(Component.literal("☠ CRITICAL: Taking damage over time!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal("  Use B.C.R Potion immediately!").withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        components.add(Component.literal("Right-click to scan your E.B.C levels").withStyle(ChatFormatting.GRAY));
        components.add(Component.literal("Shows bio-contamination count").withStyle(ChatFormatting.DARK_GRAY));
    }
}
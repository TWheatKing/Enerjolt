package me.twheatking.enerjolt.item.potion;

import me.twheatking.enerjolt.contamination.EBCContaminationManager;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Bio Contamination Resistance (B.C.R) Potion
 *
 * Removes 50 E.B.C counts when consumed.
 * This is the ONLY way to reduce E.B.C contamination.
 * Milk and other vanilla cures do NOT work.
 */
public class BCRPotionItem extends Item {

    private final int ebcReduction;

    /**
     * Create a B.C.R potion with default reduction (50 counts)
     */
    public BCRPotionItem(Properties properties) {
        this(properties, 50);
    }

    /**
     * Create a B.C.R potion with custom reduction amount
     * @param ebcReduction How many E.B.C counts to remove
     */
    public BCRPotionItem(Properties properties, int ebcReduction) {
        super(properties);
        this.ebcReduction = ebcReduction;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            // Apply B.C.R effect - remove E.B.C counts
            EBCContaminationManager.applyBCRPotion(player, ebcReduction);

            // Stats and advancements
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            }

            // Play drinking sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);

            // Shrink stack
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            // Return empty bottle
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            } else {
                if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                    player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
                }
                return stack;
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32; // Same as potion drinking time
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    /**
     * Get the E.B.C reduction amount for this potion
     */
    public int getEBCReduction() {
        return ebcReduction;
    }
}
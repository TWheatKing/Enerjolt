package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.item.upgrade.UpgradeModuleItem;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SpeedUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.SPEED, UpgradeModuleModifier.ENERGY_CONSUMPTION
    };

    private static final double SPEED_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_1_EFFECT.getValue();
    private static final double SPEED_1_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_1_ENERGY_CONSUMPTION_EFFECT.getValue();

    private static final double SPEED_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_2_EFFECT.getValue();
    private static final double SPEED_2_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_2_ENERGY_CONSUMPTION_EFFECT.getValue();

    private static final double SPEED_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_3_EFFECT.getValue();
    private static final double SPEED_3_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_3_ENERGY_CONSUMPTION_EFFECT.getValue();

    private static final double SPEED_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_4_EFFECT.getValue();
    private static final double SPEED_4_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_4_ENERGY_CONSUMPTION_EFFECT.getValue();

    private static final double SPEED_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_5_EFFECT.getValue();
    private static final double SPEED_5_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_5_ENERGY_CONSUMPTION_EFFECT.getValue();

    public SpeedUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.SPEED, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case SPEED -> switch(tier) {
                case 1 -> SPEED_1_EFFECT;
                case 2 -> SPEED_2_EFFECT;
                case 3 -> SPEED_3_EFFECT;
                case 4 -> SPEED_4_EFFECT;
                case 5 -> SPEED_5_EFFECT;

                default -> -1;
            };
            case ENERGY_CONSUMPTION -> switch(tier) {
                case 1 -> SPEED_1_ENERGY_CONSUMPTION_EFFECT;
                case 2 -> SPEED_2_ENERGY_CONSUMPTION_EFFECT;
                case 3 -> SPEED_3_ENERGY_CONSUMPTION_EFFECT;
                case 4 -> SPEED_4_ENERGY_CONSUMPTION_EFFECT;
                case 5 -> SPEED_5_ENERGY_CONSUMPTION_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case SPEED -> Component.literal(String.format(Locale.US, "• %.2f", value)).
                    withStyle(ChatFormatting.GREEN);
            case ENERGY_CONSUMPTION -> Component.literal(String.format(Locale.US, "%+.2f %%", 100 * value - 100)).
                    withStyle(ChatFormatting.RED);

            default -> Component.empty();
        };
    }
}

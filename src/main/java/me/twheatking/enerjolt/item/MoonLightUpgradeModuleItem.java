package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.item.upgrade.UpgradeModuleItem;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MoonLightUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.MOON_LIGHT
    };

    private static final double MOON_LIGHT_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_MOON_LIGHT_1_EFFECT.getValue();

    private static final double MOON_LIGHT_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_MOON_LIGHT_2_EFFECT.getValue();

    private static final double MOON_LIGHT_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_MOON_LIGHT_3_EFFECT.getValue();

    public MoonLightUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.MOON_LIGHT, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case MOON_LIGHT -> switch(tier) {
                case 1 -> MOON_LIGHT_1_EFFECT;
                case 2 -> MOON_LIGHT_2_EFFECT;
                case 3 -> MOON_LIGHT_3_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case MOON_LIGHT -> Component.literal(String.format(Locale.US, "%.2f %%", 100 * value)).
                    withStyle(ChatFormatting.GREEN);

            default -> Component.empty();
        };
    }
}

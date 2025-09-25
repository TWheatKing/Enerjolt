package me.twheatking.enerjolt.item;

import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.item.upgrade.UpgradeModuleItem;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ExtractionDepthUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.EXTRACTION_DEPTH
    };

    private static final double EXTRACTION_DEPTH_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_1_EFFECT.getValue();

    private static final double EXTRACTION_DEPTH_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_2_EFFECT.getValue();

    private static final double EXTRACTION_DEPTH_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_3_EFFECT.getValue();

    private static final double EXTRACTION_DEPTH_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_4_EFFECT.getValue();

    private static final double EXTRACTION_DEPTH_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_5_EFFECT.getValue();

    public ExtractionDepthUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.EXTRACTION_DEPTH, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case EXTRACTION_DEPTH -> switch(tier) {
                case 1 -> EXTRACTION_DEPTH_1_EFFECT;
                case 2 -> EXTRACTION_DEPTH_2_EFFECT;
                case 3 -> EXTRACTION_DEPTH_3_EFFECT;
                case 4 -> EXTRACTION_DEPTH_4_EFFECT;
                case 5 -> EXTRACTION_DEPTH_5_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case EXTRACTION_DEPTH -> Component.literal(String.format(Locale.ENGLISH, "+%d ", (int)value)).
                    append(Component.translatable("tooltip.energizedpower.upgrade_module_modifier.extraction_depth.unit")).
                    withStyle(ChatFormatting.GREEN);

            default -> Component.empty();
        };
    }
}

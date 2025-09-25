package me.twheatking.enerjolt.machine.tier;

import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.EnerjoltBlockEntities;
import me.twheatking.enerjolt.block.entity.TransformerBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.screen.EnerjoltMenuTypes;
import me.twheatking.enerjolt.screen.TransformerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public enum TransformerTier {
    LV, MV, HV, EHV;

    public MenuType<TransformerMenu> getMenuTypeFromTierAndType(TransformerType type) {
        return switch(this) {
            case LV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltMenuTypes.LV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EnerjoltMenuTypes.LV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EnerjoltMenuTypes.LV_TRANSFORMER_N_TO_1_MENU.get();
                case CONFIGURABLE -> EnerjoltMenuTypes.CONFIGURABLE_LV_TRANSFORMER_MENU.get();
            };
            case MV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltMenuTypes.MV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EnerjoltMenuTypes.MV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EnerjoltMenuTypes.MV_TRANSFORMER_N_TO_1_MENU.get();
                case CONFIGURABLE -> EnerjoltMenuTypes.CONFIGURABLE_MV_TRANSFORMER_MENU.get();
            };
            case HV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltMenuTypes.HV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EnerjoltMenuTypes.HV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EnerjoltMenuTypes.HV_TRANSFORMER_N_TO_1_MENU.get();
                case CONFIGURABLE -> EnerjoltMenuTypes.CONFIGURABLE_HV_TRANSFORMER_MENU.get();
            };
            case EHV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EnerjoltMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EnerjoltMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU.get();
                case CONFIGURABLE -> EnerjoltMenuTypes.CONFIGURABLE_EHV_TRANSFORMER_MENU.get();
            };
        };
    }

    public BlockEntityType<TransformerBlockEntity> getEntityTypeFromTierAndType(TransformerType type) {
        return switch(this) {
            case LV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltBlockEntities.LV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EnerjoltBlockEntities.LV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EnerjoltBlockEntities.LV_TRANSFORMER_N_TO_1_ENTITY.get();
                case CONFIGURABLE -> EnerjoltBlockEntities.CONFIGURABLE_LV_TRANSFORMER_ENTITY.get();
            };
            case MV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltBlockEntities.MV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EnerjoltBlockEntities.MV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EnerjoltBlockEntities.MV_TRANSFORMER_N_TO_1_ENTITY.get();
                case CONFIGURABLE -> EnerjoltBlockEntities.CONFIGURABLE_MV_TRANSFORMER_ENTITY.get();
            };
            case HV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltBlockEntities.HV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EnerjoltBlockEntities.HV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EnerjoltBlockEntities.HV_TRANSFORMER_N_TO_1_ENTITY.get();
                case CONFIGURABLE -> EnerjoltBlockEntities.CONFIGURABLE_HV_TRANSFORMER_ENTITY.get();
            };
            case EHV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltBlockEntities.EHV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EnerjoltBlockEntities.EHV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EnerjoltBlockEntities.EHV_TRANSFORMER_N_TO_1_ENTITY.get();
                case CONFIGURABLE -> EnerjoltBlockEntities.CONFIGURABLE_EHV_TRANSFORMER_ENTITY.get();
            };
        };
    }

    public Block getBlockFromTierAndType(TransformerType type) {
        return switch(this) {
            case LV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltBlocks.LV_TRANSFORMER_1_TO_N.get();
                case TYPE_3_TO_3 -> EnerjoltBlocks.LV_TRANSFORMER_3_TO_3.get();
                case TYPE_N_TO_1 -> EnerjoltBlocks.LV_TRANSFORMER_N_TO_1.get();
                case CONFIGURABLE -> EnerjoltBlocks.CONFIGURABLE_LV_TRANSFORMER.get();
            };
            case MV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltBlocks.MV_TRANSFORMER_1_TO_N.get();
                case TYPE_3_TO_3 -> EnerjoltBlocks.MV_TRANSFORMER_3_TO_3.get();
                case TYPE_N_TO_1 -> EnerjoltBlocks.MV_TRANSFORMER_N_TO_1.get();
                case CONFIGURABLE -> EnerjoltBlocks.CONFIGURABLE_MV_TRANSFORMER.get();
            };
            case HV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltBlocks.HV_TRANSFORMER_1_TO_N.get();
                case TYPE_3_TO_3 -> EnerjoltBlocks.HV_TRANSFORMER_3_TO_3.get();
                case TYPE_N_TO_1 -> EnerjoltBlocks.HV_TRANSFORMER_N_TO_1.get();
                case CONFIGURABLE -> EnerjoltBlocks.CONFIGURABLE_HV_TRANSFORMER.get();
            };
            case EHV -> switch(type) {
                case TYPE_1_TO_N -> EnerjoltBlocks.EHV_TRANSFORMER_1_TO_N.get();
                case TYPE_3_TO_3 -> EnerjoltBlocks.EHV_TRANSFORMER_3_TO_3.get();
                case TYPE_N_TO_1 -> EnerjoltBlocks.EHV_TRANSFORMER_N_TO_1.get();
                case CONFIGURABLE -> EnerjoltBlocks.CONFIGURABLE_EHV_TRANSFORMER.get();
            };
        };
    }

    public String getMachineNameFromTierAndType(TransformerType type) {
        return switch(this) {
            case LV -> switch(type) {
                case TYPE_1_TO_N -> "lv_transformer_1_to_n";
                case TYPE_3_TO_3 -> "lv_transformer_3_to_3";
                case TYPE_N_TO_1 -> "lv_transformer_n_to_1";
                case CONFIGURABLE -> "configurable_lv_transformer";
            };
            case MV -> switch(type) {
                case TYPE_1_TO_N -> "transformer_1_to_n";
                case TYPE_3_TO_3 -> "transformer_3_to_3";
                case TYPE_N_TO_1 -> "transformer_n_to_1";
                case CONFIGURABLE -> "configurable_mv_transformer";
            };
            case HV -> switch(type) {
                case TYPE_1_TO_N -> "hv_transformer_1_to_n";
                case TYPE_3_TO_3 -> "hv_transformer_3_to_3";
                case TYPE_N_TO_1 -> "hv_transformer_n_to_1";
                case CONFIGURABLE -> "configurable_hv_transformer";
            };
            case EHV -> switch(type) {
                case TYPE_1_TO_N -> "ehv_transformer_1_to_n";
                case TYPE_3_TO_3 -> "ehv_transformer_3_to_3";
                case TYPE_N_TO_1 -> "ehv_transformer_n_to_1";
                case CONFIGURABLE -> "configurable_ehv_transformer";
            };
        };
    }

    public int getMaxEnergyTransferFromTier() {
        return switch(this) {
            case LV -> ModConfigs.COMMON_LV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case MV -> ModConfigs.COMMON_MV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case HV -> ModConfigs.COMMON_HV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case EHV -> ModConfigs.COMMON_EHV_TRANSFORMERS_TRANSFER_RATE.getValue();
        };
    }
}

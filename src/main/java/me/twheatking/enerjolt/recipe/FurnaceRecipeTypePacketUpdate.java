package me.twheatking.enerjolt.recipe;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Used for SyncFurnaceRecipeTypeS2CPacket
 */
public interface FurnaceRecipeTypePacketUpdate {
    void setRecipeType(RecipeType<? extends AbstractCookingRecipe> recipeType);
}

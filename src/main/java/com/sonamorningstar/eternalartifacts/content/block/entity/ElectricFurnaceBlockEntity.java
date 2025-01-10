package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricFurnaceBlockEntity extends GenericMachineBlockEntity {
    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.ELECTRIC_FURNACE, pos, blockState);
        setEnergy(createDefaultEnergy());
        outputSlots.add(1);
        setInventory(createRecipeFinderInventory(2, outputSlots));
    }

    private final RecipeCache<BlastingRecipe, Container> blastingCache = new RecipeCache<>();
    private final RecipeCache<SmokingRecipe, Container> smokingCache = new RecipeCache<>();
    private final RecipeCache<CampfireCookingRecipe, Container> campfireCache = new RecipeCache<>();
    private final RecipeCache<SmeltingRecipe, Container> smeltingCache = new RecipeCache<>();
    private SimpleContainer recipeContainer = null;
    private Recipe<Container> recipe = null;
    private Recipe<Container> previousRecipe = null;

    @Override
    protected void findRecipe() {
        recipeContainer = new SimpleContainer(inventory.getStackInSlot(0));
        blastingCache.findRecipe(RecipeType.BLASTING, recipeContainer, level);
        if (blastingCache.getRecipe() != null) {
            recipe = blastingCache.getRecipe();
        } else {
            smokingCache.findRecipe(RecipeType.SMOKING, recipeContainer, level);
            if (smokingCache.getRecipe() != null) {
                recipe = smokingCache.getRecipe();
            } else {
                campfireCache.findRecipe(RecipeType.CAMPFIRE_COOKING, recipeContainer, level);
                if (campfireCache.getRecipe() != null) {
                    recipe = campfireCache.getRecipe();
                } else {
                    smeltingCache.findRecipe(RecipeType.SMELTING, recipeContainer, level);
                    if (smeltingCache.getRecipe() != null) {
                        recipe = smeltingCache.getRecipe();
                    }
                }
            }
        }
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        if (recipe == null) {
            progress = 0;
            return;
        }
        if (previousRecipe != recipe) {
            progress = 0;
        }
        previousRecipe = recipe;

        ItemStack result = recipeContainer != null ? recipe.assemble(recipeContainer, lvl.registryAccess()).copy() : ItemStack.EMPTY;
        int cost = recipe.getIngredients().get(0).getItems()[0].getCount();
        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots)
                .createCustomCondition(result::isEmpty)
                .tryInsertForced(result)
                .tryExtractForced(cost, 0);

        progress(condition::getResult, () -> {
            inventory.insertItemForced(1, result, false);
            inventory.extractItem(0, cost, false);
        }, energy);
    }
}

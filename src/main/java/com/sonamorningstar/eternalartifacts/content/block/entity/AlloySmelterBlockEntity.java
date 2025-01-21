package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.AlloyingRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AlloySmelterBlockEntity extends GenericMachineBlockEntity {
    public AlloySmelterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.ALLOY_SMELTER, pos, blockState);
        setEnergy(createDefaultEnergy());
        outputSlots.add(3);
        setInventory(createRecipeFinderInventory(4, outputSlots));
        screenInfo.setArrowXOffset(16);
    }

    @Override
    protected void findRecipe() {
        recipeCache.findRecipe(ModRecipes.ALLOYING.getType(), new SimpleContainer(inventory.getStackInSlot(0), inventory.getStackInSlot(1), inventory.getStackInSlot(2)), level);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);

        AlloyingRecipe recipe = ((AlloyingRecipe) recipeCache.getRecipe());
        if (recipe == null) {
            progress = 0;
            return;
        }

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots)
                .tryInsertForced(recipe.getResultItem(lvl.registryAccess()));

        progress(condition::getResult, () -> {
            inventory.insertItemForced(3, recipe.getResultItem(lvl.registryAccess()).copy(), false);
            for (int i = 0; i < 3; i++) {
                ItemStack inputItem = inventory.getStackInSlot(i);
                if (!inputItem.is(ModItems.SLOT_LOCK) && !inputItem.isEmpty()) {
                    for (SizedIngredient input : recipe.getInputs()) {
                        for (ItemStack item : input.getItems()) {
                            if (item.is(inputItem.getItem())) {
                                inventory.extractItem(i, item.getCount(), false);
                            }
                        }
                    }
                }
            }
        }, energy);
    }
}

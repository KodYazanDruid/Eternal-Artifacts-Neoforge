package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleCraftingContainer;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class AdvancedCrafterBlockEntity extends GenericMachineBlockEntity {
    private ItemStack blueprint = ItemStack.EMPTY;
    public AdvancedCrafterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.ADVANCED_CRAFTER, pos, blockState);
        setMaxProgress(10);
        setEnergy(createDefaultEnergy());
        setTank(createRecipeFinderTank(16000));
        outputSlots.add(9);
        setInventory(createRecipeFinderInventory(10, outputSlots));
        for (int i = 0; i < 9; i++) {
            screenInfo.setSlotPosition(44 + (i % 3) * 18, 18 + (i / 3) * 18, i);
        }
        screenInfo.setSlotPosition(126, 36, 9);
        screenInfo.setArrowYOffset(-4);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Blueprint", blueprint.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        blueprint = ItemStack.of(tag.getCompound("Blueprint"));
    }

    private final RecipeCache<CraftingRecipe, CraftingContainer> recipeCache = new RecipeCache<>();
    @Nullable
    private CraftingContainer craftingContainer;
    private CraftingRecipe previousRecipe = null;
    @Override
    protected void findRecipe() {
        craftingContainer = new SimpleCraftingContainer(inventory, outputSlots);
        recipeCache.findRecipe(RecipeType.CRAFTING, craftingContainer, level);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputFluids(lvl, pos, tank);
        CraftingRecipe recipe = recipeCache.getRecipe();
        if (recipe == null) {
            progress = 0;
            return;
        }
        if (previousRecipe != recipe) {
            progress = 0;
        }
        previousRecipe = recipe;

        ItemStack result = craftingContainer != null ? recipe.assemble(craftingContainer, lvl.registryAccess()) : ItemStack.EMPTY;
        result.onCraftedBySystem(lvl);
        NonNullList<ItemStack> remainders = craftingContainer != null ? lvl.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, craftingContainer, lvl) : NonNullList.create();
        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initInputTank(tank)
                .initOutputSlots(outputSlots)
                .createCustomCondition(result::isEmpty)
                .tryInsertForced(result);

        progress(condition::getResult, () -> {
            inventory.insertItemForced(outputSlots.get(0), result.copy(), false);
            for (int i = 0; i < 9; i++) {
                inventory.extractItem(i, 1, false);
            }
            for (int i = 0; i < remainders.size(); i++) {
                ItemStack remainder = remainders.get(i).copy();
                inventory.insertItemForced(i, remainder, false);
            }
        }, energy);
    }
}

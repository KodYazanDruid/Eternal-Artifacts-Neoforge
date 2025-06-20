package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.content.recipe.blueprint.BlueprintPattern;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleContainerCrafterWrapped;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AdvancedCrafter extends GenericMachine {
    @Nullable
    BlueprintPattern pattern = null;
    
    // 0-8: Inputs, 9: Output, 10: Blueprint
    public AdvancedCrafter(BlockPos pos, BlockState blockState) {
        super(ModMachines.ADVANCED_CRAFTER, pos, blockState);
        setMaxProgress(10);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createRecipeFinderTank(16000));
        outputSlots.add(9);
        setInventory(() -> new ModItemStorage(11) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot == 10 && !(stack.getItem() instanceof BlueprintItem)) return stack;
                if (stack.getItem() instanceof BlueprintItem) {
                    ItemStack remainder = super.insertItem(10, stack, simulate);
                    if (!remainder.isEmpty()) return super.insertItem(slot, remainder, simulate);
                    else return ItemStack.EMPTY;
                }
                return super.insertItem(slot, stack, simulate);
            }
            @Override
            public ItemStack insertItemForced(int slot, ItemStack stack, boolean simulate) {
                if (slot == 10 && !(stack.getItem() instanceof BlueprintItem)) return stack;
                if (stack.getItem() instanceof BlueprintItem) {
                    ItemStack remainder = super.insertItemForced(10, stack, simulate);
                    if (!remainder.isEmpty()) return super.insertItemForced(slot, remainder, simulate);
                    else return ItemStack.EMPTY;
                }
                return super.insertItemForced(slot, stack, simulate);
            }
            @Override
            protected void onContentsChanged(int slot) {
                if (!outputSlots.contains(slot) || slot != 10) findRecipe();
                if (slot == 10) {
                    if (getStackInSlot(10).getItem() instanceof BlueprintItem) {
                        pattern = BlueprintItem.getPattern(getStackInSlot(10));
                    } else pattern = null;
                }
                AdvancedCrafter.this.sendUpdate();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (slot == 10) return stack.getItem() instanceof BlueprintItem;
                ItemStack blueprint = getStackInSlot(10);
                if (slot < 9 && !blueprint.isEmpty() && pattern != null) {
                    return pattern.testForPattern(stack, slot);
                }
                return !outputSlots.contains(slot);
            }
        });
        setRecipeContainer(() -> new SimpleCraftingContainer(inventory, outputSlots));
        for (int i = 0; i < 9; i++) {
            screenInfo.setSlotPosition(44 + (i % 3) * 18, 18 + (i / 3) * 18, i);
        }
        screenInfo.setSlotPosition(126, 36, 9);
        screenInfo.setSlotPosition(104, 16, 10);
        screenInfo.setArrowYOffset(-4);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }
    
    private CraftingRecipe previousRecipe = null;
    @Override
    protected void findRecipe() {
        ItemStack blueprint = inventory.getStackInSlot(10);
        RecipeCache.clearRecipes(this);
        if (!blueprint.isEmpty()) {
            /*SimpleContainerCrafterWrapped container = new SimpleContainerCrafterWrapped(9);
            NonNullList<ItemStack> pattern = BlueprintItem.getFakeItems(blueprint);
            for (int i = 0; i < 9; i++) {
                ItemStack patternStack = pattern.get(i);
                IFluidHandlerItem patternFH = patternStack.getCapability(Capabilities.FluidHandler.ITEM);
                if (patternFH != null) {
                    FluidStack drained = patternFH.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                    if (drained.getAmount() == 1000) {
                        container.setItem(i, patternStack.copy());
                    }
                } else container.setItem(i, inventory.getStackInSlot(i));
            }
            recipeCache.findRecipe(RecipeType.CRAFTING, container, level);*/
            SimpleContainerCrafterWrapped container = new SimpleContainerCrafterWrapped(9);
            for (int i = 0; i < 9; i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                container.setItem(i, stack);
            }
            pattern = new BlueprintPattern(container);
            RecipeCache.findRecipeFor(this, RecipeType.CRAFTING, container, level, false);
        } else {
            super.findRecipe();
        }
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputFluids(lvl, pos);
        CraftingRecipe recipe = ((CraftingRecipe) RecipeCache.getCachedRecipe(this));
        if (recipe == null) {
            progress = 0;
            return;
        }
        if (previousRecipe != recipe) {
            progress = 0;
        }
        previousRecipe = recipe;

        ItemStack result = recipe.assemble((CraftingContainer) recipeContainer.get(), lvl.registryAccess());
        NonNullList<ItemStack> remainders = lvl.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, (CraftingContainer) recipeContainer.get(), lvl);
 
 
    }

    private List<ItemStack> getContainers() {
        List<ItemStack> containers = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (fluidHandler != null) containers.add(stack);
        }
        return containers;
    }
}

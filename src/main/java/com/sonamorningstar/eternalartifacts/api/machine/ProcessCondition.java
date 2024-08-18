package com.sonamorningstar.eternalartifacts.api.machine;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.capabilities.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ProcessCondition {
    private ModItemStorage inventory;
    private List<Integer> outputSlots = new ArrayList<>();
    private final List<Integer> flaggedSlots = new ArrayList<>();
    private final List<ItemStack> queuedStacks = new ArrayList<>();
    private AbstractFluidTank inputTank;
    private AbstractFluidTank outputTank;
    private final List<RecipeCache<?, ?>> recipeCacheList = new ArrayList<>();
    private BooleanSupplier supplier;

    //region Initializing Handlers
    public ProcessCondition initInventory(ModItemStorage inventory) {
        this.inventory = inventory;
        return this;
    }
    public ProcessCondition initOutputSlots(List<Integer> slots) {
        this.outputSlots.addAll(slots);
        return this;
    }
    public ProcessCondition initInputTank(AbstractFluidTank tank) {
        this.inputTank = tank;
        return this;
    }
    public ProcessCondition initOutputTank(AbstractFluidTank tank) {
        this.outputTank = tank;
        return this;
    }
    //endregion

    public ProcessCondition initRecipe(RecipeCache<?, ?> recipeCache) {
        this.recipeCacheList.add(recipeCache);
        return this;
    }
    public RecipeCache<?, ?> validCache() {
        for (RecipeCache<?, ?> recipeCache : recipeCacheList) {
            if (recipeCache.getRecipe() != null) return recipeCache;
        }
        return null;
    }

    public ProcessCondition queueInsertion(ItemStack... queuedStack) {
        queuedStacks.addAll(Arrays.stream(queuedStack).toList());
        return this;
    }

    //region Transfer Logics.
    public ProcessCondition tryInsertForced(ItemStack... stacks) {
        if(supplier == null || !supplier.getAsBoolean()) {
            outputSlots.removeAll(flaggedSlots);
            for(ItemStack stack : stacks) {
                Pair<ItemStack, List<Integer>> remainder = ItemHelper.insertItemStackedForced(inventory, stack, true, outputSlots.toArray(Integer[]::new));
                if (remainder.getSecond() == null) {
                    supplier = preventWorking();
                    return this;
                }
                if (remainder.getFirst().isEmpty() && Collections.disjoint(remainder.getSecond(), flaggedSlots)) {
                    supplier = noCondition();
                    if (remainder.getSecond() != null) flaggedSlots.addAll(remainder.getSecond());
                } else supplier = preventWorking();
            }
        }
        return this;
    }
    public ProcessCondition tryInsertForced(ItemStack stack, int slot) {
        if(!outputSlots.contains(slot) || flaggedSlots.contains(slot)) supplier = preventWorking();
        if(supplier == null || !supplier.getAsBoolean()) {
            ItemStack remainder = inventory.insertItemForced(slot, stack, true);
            if (remainder.isEmpty()) {
                supplier = noCondition();
                flaggedSlots.add(slot);
            }
        }
        return this;
    }

    public ProcessCondition tryExtractItemForced(int count) {
        if (supplier == null || !supplier.getAsBoolean()) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack extracted = inventory.extractItem(i, count, true);
                if (!extracted.isEmpty()) {
                    supplier = noCondition();
                    break;
                } else supplier = preventWorking();
            }
        }
        return this;
    }

    public ProcessCondition tryExtractForced(int count, int slot) {
        if (supplier == null || !supplier.getAsBoolean()) {
            ItemStack extracted = inventory.extractItem(slot, count, true);
            supplier = extracted::isEmpty;
        }
        return this;
    }
    public ProcessCondition tryInsertForced(FluidStack stack) {
        if(supplier == null || !supplier.getAsBoolean()) {
            int remainder = outputTank.fillForced(stack, IFluidHandler.FluidAction.SIMULATE);
            if (remainder > 0) supplier = preventWorking();
            else supplier = noCondition();
        }
        return this;
    }
    public ProcessCondition tryExtractForced(FluidStack stack) {
        if(supplier == null || !supplier.getAsBoolean()) {
            FluidStack drained = inputTank.drainForced(stack, IFluidHandler.FluidAction.SIMULATE);
            if(drained.isEmpty()) supplier = preventWorking();
            else supplier = noCondition();
        }
        return this;
    }
    public ProcessCondition tryExtractFluidForced(int fluidAmount) {
        if(supplier == null || !supplier.getAsBoolean()) {
            FluidStack drained = inputTank.drainForced(fluidAmount, IFluidHandler.FluidAction.SIMULATE);
            if (drained.getAmount() < fluidAmount) supplier = preventWorking();
            else supplier = noCondition();
        }
        return this;
    }
    //endregion

    public static BooleanSupplier noCondition() { return () -> false; }
    public static BooleanSupplier preventWorking() { return () -> true; }

    public boolean getResult() {
        boolean noRecipes = false;
        if (!recipeCacheList.isEmpty()) noRecipes = validCache() == null;
/*        if (!queuedStacks.isEmpty()) {
            for (ItemStack queuedStack : queuedStacks) {
                outputSlots.removeAll(flaggedSlots);
                Pair<ItemStack, List<Integer>> remainder = ItemHelper.insertItemStackedForced(inventory, queuedStack, true, outputSlots.toArray(Integer[]::new));
                if (remainder.getFirst().isEmpty() && remainder.getSecond() != null && !remainder.getSecond().isEmpty()) {
                    supplier = noCondition();
                    flaggedSlots.addAll(remainder.getSecond());
                } else supplier = preventWorking();
            }
        }*/
        return supplier == null || supplier.getAsBoolean() || noRecipes;
    }

}

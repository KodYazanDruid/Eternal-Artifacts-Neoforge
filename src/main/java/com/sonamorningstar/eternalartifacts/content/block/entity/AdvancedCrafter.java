package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.AdvancedCrafterMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.content.recipe.blueprint.BlueprintPattern;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleContainerCrafterWrapped;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleCraftingContainer;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AdvancedCrafter extends SidedTransferMachine<AdvancedCrafterMenu> {
    @Nullable
    BlueprintPattern pattern = null;
    
    // 0-8: Inputs, 9: Output, 10: Blueprint
    public AdvancedCrafter(BlockPos pos, BlockState blockState) {
        super(ModMachines.ADVANCED_CRAFTER.getBlockEntity(), pos, blockState,
            (a, b, c, d) -> new AdvancedCrafterMenu(ModMachines.ADVANCED_CRAFTER.getMenu(), a, b, c, d));
        setMaxProgress(10);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createRecipeFinderTank(16000));
        outputSlots.add(9);
        setInventory(() -> new ModItemStorage(11) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot == 10 && !(stack.getItem() instanceof BlueprintItem)) return stack;
                if (stack.getItem() instanceof BlueprintItem) {
                    ItemStack remainder = super.insertItem(10, stack, true);
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
                } else setProcessCondition(new ProcessCondition(AdvancedCrafter.this),
                    RecipeCache.getCachedRecipe(AdvancedCrafter.this));
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
        setRecipeTypeAndContainer(RecipeType.CRAFTING, () -> new SimpleCraftingContainer(inventory, outputSlots));
    }
    
    private CraftingRecipe previousRecipe = null;
    @Override
    protected void findRecipe() {
        ItemStack blueprint = inventory.getStackInSlot(10);
        if (!blueprint.isEmpty()) {
            SimpleContainerCrafterWrapped container = new SimpleContainerCrafterWrapped(9);
            for (int i = 0; i < 9; i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                container.setItem(i, stack);
            }
            findRecipeFor(recipeType, () -> container);
            pattern = BlueprintItem.getPattern(blueprint);
        } else {
            super.findRecipe();
        }
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @org.jetbrains.annotations.Nullable Recipe<?> recipe) {
        if (recipe instanceof CraftingRecipe craftingRecipe) {
            if (pattern != null) {
                pattern.findRecipe(level);
                condition.createCustomCondition(() -> pattern.getRecipe() == null || pattern.getRecipe() != recipe);
            }
            condition.queueImport(craftingRecipe.getResultItem(level.registryAccess()))
                .commitQueuedImports();
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputItems(lvl, pos);
        performAutoInputFluids(lvl, pos);
        performAutoOutputItems(lvl, pos);
        CraftingRecipe recipe = ((CraftingRecipe) RecipeCache.getCachedRecipe(this));
        
        if (recipe == null) {
            progress = 0;
            return;
        }
        if (previousRecipe != recipe) {
            progress = 0;
        }
        previousRecipe = recipe;
        
        progress(() -> {
            FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, lvl);
            fakePlayer.setYRot(st.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot());
            fakePlayer.setPosRaw(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
            ItemStack result = recipe.assemble((CraftingContainer) recipeContainer.get(), lvl.registryAccess());
            result.onCraftedBy(lvl, fakePlayer, result.getCount());
            NonNullList<ItemStack> remainders = lvl.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, (CraftingContainer) recipeContainer.get(), lvl);
            inventory.insertItemForced(9, result, false);
            for (int i = 0; i < 9; i++) {
                inventory.extractItem(i, 1, false);
            }
            for (int i = 0; i < remainders.size(); i++) {
                ItemStack remainder = remainders.get(i);
                if (!remainder.isEmpty()) {
                    inventory.setStackInSlot(i, remainder);
                }
            }
        });
 
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

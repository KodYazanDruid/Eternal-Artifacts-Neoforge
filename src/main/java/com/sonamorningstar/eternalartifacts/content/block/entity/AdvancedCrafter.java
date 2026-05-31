package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.AdvancedCrafterMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.content.recipe.blueprint.BlueprintPattern;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleCraftingContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.*;

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
            protected void onContentsChanged(int slot) {
                if (!outputSlots.contains(slot) || slot != 10) findRecipe();
                if (slot == 10) {
                    if (getStackInSlot(10).getItem() instanceof BlueprintItem) {
                        pattern = BlueprintItem.getPattern(getStackInSlot(10));
                        if (level != null) {
                            pattern.findRecipe(level);
                        }
                    } else pattern = null;
                } else updateProcessCondition();
                AdvancedCrafter.this.markDirty();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (slot == 10) return stack.getItem() instanceof BlueprintItem;
                ItemStack blueprint = getStackInSlot(10);
                if (slot < 9 && !blueprint.isEmpty() && pattern != null) {
                    boolean useIngredients = BlueprintItem.isUsingTags(blueprint);
                    if (useIngredients) {
                        Ingredient ingredientFromSlot = BlueprintItem.getIngredientFromSlot(blueprint, slot);
                        return ingredientFromSlot != null && ingredientFromSlot.test(stack);
                    } else {
                        return pattern.testForPattern(stack, slot);
                    }
                }
                return !(stack.getItem() instanceof BlueprintItem) && !outputSlots.contains(slot);
            }
        });
        setRecipeTypeAndContainer(RecipeType.CRAFTING, () -> new SimpleCraftingContainer(inventory, outputSlots));
    }
    
    /**
     * Blueprint pattern'den gereken kovaları ve sıvılarını hesaplar.
     * Slotta zaten kova varsa tanktan sıvı kullanılmaz.
     * @return Map&lt;slotIndex, FluidStack needed&gt;
     */
    private Map<Integer, FluidStack> getRequiredFluids() {
        Map<Integer, FluidStack> result = new HashMap<>();
        
        if (pattern == null) {
            return result;
        }
        
        Map<Integer, FluidIngredient> fluids =
            pattern.getFluidIngredients();
        
        for (Map.Entry<Integer, FluidIngredient> entry : fluids.entrySet()) {
            int slot = entry.getKey();
            
            if (!inventory.getStackInSlot(slot).isEmpty()) {
                continue;
            }
            
            /*result.put(
                slot,
                entry.getValue().getFluid()
            );*/
        }
        
        return result;
    }
    
    /**
     * Tankta yeterli sıvı olup olmadığını kontrol eder
     */
    private boolean hasSufficientFluidInTank(Map<Integer, FluidStack> requiredFluids) {
        if (requiredFluids.isEmpty()) return true;
        if (tank == null) return false;
        
        // Calculate total fluid required by type
        Map<Fluid, Integer> totalRequired = new HashMap<>();
        for (FluidStack fluidStack : requiredFluids.values()) {
            totalRequired.merge(fluidStack.getFluid(), fluidStack.getAmount(), Integer::sum);
        }
        
        // Check if tank has enough
        FluidStack tankFluid = tank.getFluid(0);
        for (Map.Entry<Fluid, Integer> entry : totalRequired.entrySet()) {
            if (tankFluid.isEmpty() || !tankFluid.getFluid().isSame(entry.getKey()) || tankFluid.getAmount() < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Tanktan sıvı çeker ve kova remainder'ı döndürür
     */
    private void drainFluidFromTank(Map<Integer, FluidStack> requiredFluids) {
        if (tank == null || requiredFluids.isEmpty()) return;
        
        for (FluidStack fluidStack : requiredFluids.values()) {
            tank.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
        }
    }
    
    /**
     * Tarif için CraftingContainer oluşturur, tanktan sıvı kullanımını destekler.
     * Blueprint pattern'deki kova item'ını sanal olarak slota koyar.
     */
    /*private CraftingContainer createCraftingContainer() {
        return new SimpleCraftingContainer(inventory, outputSlots) {
            @Override
            public ItemStack getItem(int slot) {
                ItemStack original = super.getItem(slot);
                FluidStack contained = FluidUtil.getFluidContained(original).orElse(FluidStack.EMPTY);
                if (!contained.isEmpty()) {
                    ItemStack bucketStack = Items.BUCKET.getDefaultInstance();
                    IFluidHandlerItem handler = FluidUtil.getFluidHandler(bucketStack).orElse(null);
                    if (handler != null) {
                        handler.fill(contained, IFluidHandler.FluidAction.EXECUTE);
                        bucketStack = handler.getContainer();
                        return bucketStack;
                    }
                }
                return original;
            }
            
            @Override
            public List<ItemStack> getItems() {
                List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    items.add(getItem(i));
                }
                return items;
            }
        };
    }*/
    
    private CraftingContainer createCraftingContainer() {
        Map<Integer, FluidIngredient> fluidIngredients =
            pattern != null
                ? pattern.getFluidIngredients()
                : Collections.emptyMap();
        
        return new SimpleCraftingContainer(inventory, outputSlots) {
            
            @Override
            public ItemStack getItem(int slot) {
                ItemStack stack = super.getItem(slot);
                
                if (!stack.isEmpty()) {
                    return stack;
                }
                
                FluidIngredient ingredient =
                    fluidIngredients.get(slot);
                
                if (ingredient == null) {
                    return ItemStack.EMPTY;
                }
                
                FluidStack tankFluid =
                    tank.getFluid(0);
                
                if (tankFluid.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                
                if (!ingredient.test(tankFluid)) {
                    return ItemStack.EMPTY;
                }
                
                return FluidUtil.getFilledBucket(tankFluid);
            }
            
            @Override
            public List<ItemStack> getItems() {
                List<ItemStack> list = new ArrayList<>();
                
                for (int i = 0; i < getContainerSize(); i++) {
                    list.add(getItem(i));
                }
                
                return list;
            }
        };
    }
    
    private CraftingRecipe previousRecipe = null;
    /*@Override
	public void findRecipe() {
        ItemStack blueprint = inventory.getStackInSlot(10);
        if (!blueprint.isEmpty()) {
            *//*SimpleContainerCrafterWrapped container = new SimpleContainerCrafterWrapped();
            for (int i = 0; i < 9; i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                container.setItem(i, stack);
            }
            findRecipeFor(recipeType, () -> container);*//*
            pattern = BlueprintItem.getPattern(blueprint);
            //CraftingContainer craftingContainer = createCraftingContainer();
            findRecipeFor(recipeType, recipeContainer);
        } else {
            super.findRecipe();
        }
    }*/
    
    @Override
    protected void configureProcessCondition(ProcessCondition condition, @org.jetbrains.annotations.Nullable Recipe<?> recipe) {
        if (recipe instanceof CraftingRecipe craftingRecipe) {
            if (pattern != null) {
                //pattern.findRecipe(level);
                condition.createCustomCondition(() -> pattern.getRecipe() == null || pattern.getRecipe() != recipe);
                
                // Check if tank has sufficient fluid for bucket requirements (pattern-based)
                /*Map<Integer, FluidStack> requiredFluids = getRequiredBucketFluids();
                condition.createCustomCondition(() -> !hasSufficientFluidInTank(requiredFluids));
                requiredFluids.forEach((slot, fluidStack) -> {
                    System.out.println("Slot " + slot + " requires " + fluidStack.getAmount() + "mb of " + fluidStack.getFluid().builtInRegistryHolder().key().location());
                });*/
            }
            
            ItemStack resultItem = craftingRecipe.assemble((CraftingContainer) recipeContainer.get(), level.registryAccess());
            resultItem.onCraftedBy(level, getFakePlayer(), resultItem.getCount());
            condition.queueImport(resultItem).commitQueuedImports();
        }
    }
    
    @Override
    public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
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
        
        // Get required fluids for bucket substitution (pattern-based)
        //Map<Integer, FluidStack> requiredFluids = getRequiredBucketFluids();
        
        progress(() -> {
            // Create crafting container that substitutes buckets with tank fluid
            /*CraftingContainer craftingContainer = requiredFluids.isEmpty()
                ? (CraftingContainer) recipeContainer.get()
                : createCraftingContainer();*/
            
            ItemStack result = recipe.assemble((CraftingContainer) recipeContainer.get(), lvl.registryAccess());
            result.onCraftedBy(lvl, getFakePlayer(), result.getCount());
            NonNullList<ItemStack> remainders = lvl.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, (CraftingContainer) recipeContainer.get(), lvl);
            inventory.insertItemForced(9, result, false);
            
            // Extract items from slots (skip slots that use tank fluid)
            /*for (int i = 0; i < 9; i++) {
                if (!requiredFluids.containsKey(i)) {
                    inventory.extractItem(i, 1, false);
                }
            }*/
            
            // Drain fluid from tank for bucket substitutions
            //drainFluidFromTank(requiredFluids);
            
            // Handle remainders (buckets from tank fluid become empty buckets)
            for (int i = 0; i < remainders.size(); i++) {
                ItemStack remainder = remainders.get(i);
                if (!remainder.isEmpty()) {
                    /*if (requiredFluids.containsKey(i)) {
                        // Remainder from tank fluid substitution - insert to output or drop
                        ItemStack leftover = inventory.insertItemForced(9, remainder, false);
                        if (!leftover.isEmpty()) {
                            // Try to insert in any available slot
                            for (int j = 0; j < 9; j++) {
                                if (inventory.getStackInSlot(j).isEmpty()) {
                                    inventory.setStackInSlot(j, leftover);
                                    break;
                                }
                            }
                        }
                    } else {*/
                        inventory.setStackInSlot(i, remainder);
                    //}
                }
            }
        });
 
    }
}

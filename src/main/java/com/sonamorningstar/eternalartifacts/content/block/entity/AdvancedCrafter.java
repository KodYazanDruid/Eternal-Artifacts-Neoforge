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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        // Find recipe for the new pattern
                        if (level != null && pattern != null) {
                            pattern.findRecipe(level);
                        }
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
                    // Check if blueprint uses ingredient mode (USE_TAGS)
                    boolean useIngredients = BlueprintItem.isUsingTags(blueprint);
                    if (useIngredients) {
                        // Ensure pattern has recipe for ingredient matching
                        if (pattern.getRecipe() == null && level != null) {
                            pattern.findRecipe(level);
                        }
                        // Use ingredient-based filtering
                        return pattern.testForIngredient(stack, slot);
                    } else {
                        // Use exact item matching
                        return pattern.testForPattern(stack, slot);
                    }
                }
                return !outputSlots.contains(slot);
            }
        });
        setRecipeTypeAndContainer(RecipeType.CRAFTING, () -> new SimpleCraftingContainer(inventory, outputSlots));
    }
    
    /**
     * Blueprint pattern'den gereken kovaları ve sıvılarını hesaplar.
     * Slotta zaten kova varsa tanktan sıvı kullanılmaz.
     * @return Map&lt;slotIndex, FluidStack needed&gt;
     */
    private Map<Integer, FluidStack> getRequiredBucketFluids() {
        Map<Integer, FluidStack> requiredFluids = new HashMap<>();
        if (pattern == null) return requiredFluids;
        
        Map<Integer, FluidStack> bucketSlots = pattern.getBucketFluidSlots();
        for (Map.Entry<Integer, FluidStack> entry : bucketSlots.entrySet()) {
            int slot = entry.getKey();
            // Check if we have a bucket in this slot already
            ItemStack slotStack = inventory.getStackInSlot(slot);
            if (slotStack.isEmpty()) {
                // No bucket in slot, need to use tank fluid
                requiredFluids.put(slot, entry.getValue());
            }
        }
        return requiredFluids;
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
     * Slotta kova yerine tanktan sıvı kullanılıp kullanılamayacağını kontrol eder
     */
    private boolean canUseTankFluidForSlot(int slot) {
        if (pattern == null || tank == null) return false;
        
        FluidStack requiredFluid = pattern.getRequiredFluidForSlot(slot);
        if (requiredFluid.isEmpty()) return false;
        
        FluidStack tankFluid = tank.getFluid(0);
        return !tankFluid.isEmpty()
            && tankFluid.getFluid().isSame(requiredFluid.getFluid())
            && tankFluid.getAmount() >= requiredFluid.getAmount();
    }
    
    /**
     * Tarif için CraftingContainer oluşturur, tanktan sıvı kullanımını destekler.
     * Blueprint pattern'deki kova item'ını sanal olarak slota koyar.
     */
    private CraftingContainer createCraftingContainer() {
        return new SimpleCraftingContainer(inventory, outputSlots) {
            @Override
            public ItemStack getItem(int slot) {
                ItemStack original = super.getItem(slot);
                // If slot is empty and pattern needs a bucket, check tank
                if (original.isEmpty() && canUseTankFluidForSlot(slot)) {
                    // Return the virtual bucket from pattern for crafting
                    return pattern.getBucketItemForSlot(slot);
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
                
                // Check if tank has sufficient fluid for bucket requirements (pattern-based)
                Map<Integer, FluidStack> requiredFluids = getRequiredBucketFluids();
                condition.createCustomCondition(() -> !hasSufficientFluidInTank(requiredFluids));
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
        
        // Get required fluids for bucket substitution (pattern-based)
        Map<Integer, FluidStack> requiredFluids = getRequiredBucketFluids();
        
        progress(() -> {
            FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, lvl);
            fakePlayer.setYRot(st.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot());
            fakePlayer.setPosRaw(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
            
            // Create crafting container that substitutes buckets with tank fluid
            CraftingContainer craftingContainer = requiredFluids.isEmpty()
                ? (CraftingContainer) recipeContainer.get()
                : createCraftingContainer();
            
            ItemStack result = recipe.assemble(craftingContainer, lvl.registryAccess());
            result.onCraftedBy(lvl, fakePlayer, result.getCount());
            NonNullList<ItemStack> remainders = lvl.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, craftingContainer, lvl);
            inventory.insertItemForced(9, result, false);
            
            // Extract items from slots (skip slots that use tank fluid)
            for (int i = 0; i < 9; i++) {
                if (!requiredFluids.containsKey(i)) {
                    inventory.extractItem(i, 1, false);
                }
            }
            
            // Drain fluid from tank for bucket substitutions
            drainFluidFromTank(requiredFluids);
            
            // Handle remainders (buckets from tank fluid become empty buckets)
            for (int i = 0; i < remainders.size(); i++) {
                ItemStack remainder = remainders.get(i);
                if (!remainder.isEmpty()) {
                    if (requiredFluids.containsKey(i)) {
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
                    } else {
                        inventory.setStackInSlot(i, remainder);
                    }
                }
            }
        });
 
    }
}

package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.HeatStorage;
import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class InductionFurnace extends MultiFurnace<InductionFurnaceMenu> {
    private final int heatKeepCost = 20;
    protected Supplier<? extends Container> recipeContainer2;
    public InductionFurnace(BlockPos pos, BlockState blockState) {
        super(ModMachines.INDUCTION_FURNACE.getBlockEntity(), pos, blockState, (a, b, c, d) -> new InductionFurnaceMenu(ModMachines.INDUCTION_FURNACE.getMenu(), a, b, c, d));
        outputSlots.add(2);
        outputSlots.add(3);
        setMaxProgress(500);
        setEnergy(this::createDefaultEnergy);
        setInventory(() -> createRecipeFinderInventory(4, outputSlots));
        setRecipeContainer(() -> new SimpleContainer(inventory.getStackInSlot(0)));
        recipeContainer2 = () -> new SimpleContainer(inventory.getStackInSlot(1));
        RecipeCache.setRecipeCount(this, 2);
    }

    public HeatStorage heat = new HeatStorage(20000) {
        @Override
        public void onChange() {
            sendUpdate();
        }
    };

    @Override
    protected void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
        tag.put("HeatValue", heat.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heat.deserializeNBT(tag.getCompound("HeatValue"));
        setMaxProgressForHeat();
    }

    @Override
    public void saveContents(CompoundTag additionalTag) {
        super.saveContents(additionalTag);
        additionalTag.put("HeatValue", heat.serializeNBT());
    }

    @Override
    public void loadContents(CompoundTag additionalTag) {
        super.loadContents(additionalTag);
        heat.deserializeNBT(additionalTag.getCompound("HeatValue"));
        setMaxProgressForHeat();
    }

    public double getHeatPercentage() {return heat.getHeat() * 100D / heat.getMaxHeat();}
    
    @Override
    public void setRecipeTypeId(short id) {
        recipeTypeId = id;
        findRecipe();
        setProcessCondition(new ProcessCondition(this), null);
        sendUpdate();
    }

    @Override
    protected void findRecipe() {
        recipeType = getSelectedRecipeType();
        findRecipeFor(recipeType, recipeContainer, 0, true);
        findRecipeFor(recipeType, recipeContainer2, 1, true);
    }

    private void setMaxProgressForHeat() {
        maxProgress = defaultMaxProgress - (int)(490 * getHeatPercentage() / 100D);
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        Recipe<Container> recipe0 = (Recipe<Container>) RecipeCache.getCachedRecipe(this, 0);
        Recipe<Container> recipe1 = (Recipe<Container>) RecipeCache.getCachedRecipe(this, 1);
        ItemStack result0;
        ItemStack result1;
        if (recipe0 != null) {
            result0 = recipe0.assemble(recipeContainer.get(), level.registryAccess());
            result0.onCraftedBySystem(level);
        } else result0 = ItemStack.EMPTY;
        if (recipe1 != null) {
            result1 = recipe1.assemble(recipeContainer2.get(), level.registryAccess());
            result1.onCraftedBySystem(level);
        } else result1 = ItemStack.EMPTY;
        condition.createCustomCondition(() -> result0.isEmpty() && result1.isEmpty());
        condition.createCustomCondition(() -> recipe0 != null && result0.isEmpty() && recipe1 == null);
        condition.createCustomCondition(() -> recipe0 == null && recipe1 != null && result1.isEmpty());
        condition.createCustomCondition(() -> {
            if (!result0.isEmpty()) {
                ItemStack remainder = ItemHelper.insertItemStackedForced(inventory, result0, true, outputSlots).getFirst();
                if (remainder.isEmpty()) return false;
            }
            if (!result1.isEmpty()) {
                ItemStack remainder = ItemHelper.insertItemStackedForced(inventory, result1, true, outputSlots).getFirst();
				return !remainder.isEmpty();
            }
            return true;
        });
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos);

        var recipes = RecipeCache.getCachedRecipes(this);
        Recipe<Container> recipe0 = recipes != null && !recipes.isEmpty() ? (Recipe<Container>) recipes.get(0) : null;
        Recipe<Container> recipe1 = recipes != null && recipes.size() > 1 ? (Recipe<Container>) recipes.get(1) : null;

        if (recipe0 == null && recipe1 == null) {
            progress = 0;
        }

        ItemStack resultItem0;
        ItemStack resultItem1;
        if (recipe0 != null) {
            resultItem0 = recipe0.assemble(recipeContainer.get(), lvl.registryAccess());
            resultItem0.onCraftedBySystem(lvl);
        } else resultItem0 = ItemStack.EMPTY;

        if (recipe1 != null) {
            resultItem1 = recipe1.assemble(recipeContainer2.get(), lvl.registryAccess());
            resultItem1.onCraftedBySystem(lvl);
        } else resultItem1 = ItemStack.EMPTY;

        setMaxProgressForHeat();

        AtomicBoolean shouldHeat = new AtomicBoolean();
        shouldHeat.set(false);
        if (processCondition != null) {
            progress(processCondition::getResult,
                () -> shouldHeat.set(true),
                () -> {
                    ItemStack remainder0 = ItemStack.EMPTY;
                    ItemStack remainder1 = ItemStack.EMPTY;
                    if (recipe0 != null) {
                        remainder0 = ItemHelper.insertItemStackedForced(inventory, resultItem0, false, outputSlots).getFirst();
                    }
                    if (recipe1 != null) {
                        remainder1 = ItemHelper.insertItemStackedForced(inventory, resultItem1, false, outputSlots).getFirst();
                    }
                    if (recipe0 != null && remainder0.isEmpty()) inventory.extractItem(0, 1, false);
                    if (recipe1 != null && remainder1.isEmpty()) inventory.extractItem(1, 1, false);
                }, energy);
        } else {
            progress = 0;
        }
        
        boolean isEnergyEnough = energy.getEnergyStored() >= heatKeepCost;
        int currentHeat = heat.getHeat();

        if (shouldHeat.get() && isEnergyEnough) {
            heat.heat(1, false);
            energy.extractEnergyForced(heatKeepCost, false);
        } else {
            if (isThereACoil(lvl, pos) && isEnergyEnough && currentHeat > 0) {
                int energyToExtract = (int)(heatKeepCost * (currentHeat / (float)heat.getMaxHeat()));
                energy.extractEnergyForced(Math.max(1, energyToExtract), false);
            }
            else if (currentHeat > 0) {
                heat.cool(1, false);
            }
        }
    }

    private boolean isThereACoil(Level level, BlockPos pos) {
        for(Direction dir : Direction.values()) {
            BlockState state = level.getBlockState(pos.relative(dir));
            if (state.is(Blocks.LIGHTNING_ROD)) {
                Direction stateFacing = state.getValue(BlockStateProperties.FACING);
                if (stateFacing == dir) return true;
            }
        }
        return false;
    }
}

package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.HeatStorage;
import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class InductionFurnaceBlockEntity extends SidedTransferMachineBlockEntity<InductionFurnaceMenu> {
    private int heatKeepCost = 10;
    protected Supplier<? extends Container> recipeContainer2;
    public InductionFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.INDUCTION_FURNACE.getBlockEntity(), pos, blockState, (a, b, c, d) -> new InductionFurnaceMenu(ModMachines.INDUCTION_FURNACE.getMenu(), a, b, c, d));
        outputSlots.add(2);
        outputSlots.add(3);
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
        tag.putShort("RecipeType", recipeTypeId);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heat.deserializeNBT(tag.getCompound("HeatValue"));
        setMaxProgressForHeat();
        recipeTypeId = tag.getShort("RecipeType");
    }

    @Override
    public void saveContents(CompoundTag additionalTag) {
        super.saveContents(additionalTag);
        additionalTag.put("HeatValue", heat.serializeNBT());
        additionalTag.putShort("RecipeType", recipeTypeId);
    }

    @Override
    public void loadContents(CompoundTag additionalTag) {
        super.loadContents(additionalTag);
        heat.deserializeNBT(additionalTag.getCompound("HeatValue"));
        setMaxProgressForHeat();
        recipeTypeId = additionalTag.getShort("RecipeType");
    }

    public double getHeatPercentage() {return heat.getHeat() * 100D / heat.getMaxHeat();}
    
    public short recipeTypeId = 0;
    public RecipeType<? extends Recipe<? extends Container>> getSelectedRecipeType() {
        return switch (recipeTypeId) {
            case 1 -> RecipeType.BLASTING;
            case 2 -> RecipeType.SMOKING;
            case 3 -> RecipeType.CAMPFIRE_COOKING;
            default -> RecipeType.SMELTING;
        };
    }
    
    public void setRecipeTypeId(short id) {
        recipeTypeId = id;
        sendUpdate();
        findRecipe();
    }

    @Override
    protected void findRecipe() {
        recipeType = getSelectedRecipeType();
        findRecipeFor(recipeType, recipeContainer, 0, true);
        findRecipeFor(recipeType, recipeContainer2, 1, true);
    }

    private void setMaxProgressForHeat() {
        setMaxProgress(500 - (int)(490 * getHeatPercentage() / 100D));
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos);

        var recipes = RecipeCache.getCachedRecipes(this);
        Recipe<Container> recipe0 = recipes != null && !recipes.isEmpty() ? (Recipe<Container>) recipes.get(0) : null;
        Recipe<Container> recipe1 = recipes != null && recipes.size() > 1 ? (Recipe<Container>) recipes.get(1) : null;

        if (recipe0 == null && recipe1 == null) {
            progress = 0;
        }

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots);

        ItemStack resultItem0;
        ItemStack resultItem1;
        if (recipe0 != null) {
            resultItem0 = recipe0.assemble(recipeContainer.get(), lvl.registryAccess());
            resultItem0.onCraftedBySystem(lvl);
            condition.queueItemStack(resultItem0);
        } else resultItem0 = ItemStack.EMPTY;

        if (recipe1 != null) {
            resultItem1 = recipe1.assemble(recipeContainer2.get(), lvl.registryAccess());
            resultItem1.onCraftedBySystem(lvl);
            condition.queueItemStack(resultItem1);
        } else resultItem1 = ItemStack.EMPTY;

        condition.commitQueuedItemStacks().createCustomCondition(() -> resultItem0.isEmpty() && resultItem1.isEmpty());

        setMaxProgressForHeat();

        AtomicBoolean shouldHeat = new AtomicBoolean();
        shouldHeat.set(false);
        progress(
                //Condition for machine to run
                condition::getResult,
                //This executes while machine running.
                () -> shouldHeat.set(true),
                //This executes when progress reaches max progress.
                () ->  {
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

        boolean isThereACoil = isThereACoil(lvl, pos);
        boolean isEnergyEnough = energy.getEnergyStored() >= heatKeepCost;

        if(isEnergyEnough) {
            if (shouldHeat.get()) {
                heat.heat(1, false);
                energy.extractEnergyForced(heatKeepCost, false);
            } else {
                if (isThereACoil) energy.extractEnergyForced(heatKeepCost, false);
                else heat.cool(1, false);
            }
        } else {
            if (heat.getHeat() > 0) heat.cool(1, false);
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

package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import lombok.Setter;
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

public class InductionFurnaceBlockEntity extends SidedTransferMachineBlockEntity<InductionFurnaceMenu> {
    private int heat = 0;
    @Setter
    private int MAX_HEAT = 20000;
    private int heatKeepCost = 10;
    //private boolean isWorking = false;
    public InductionFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.INDUCTION_FURNACE.getBlockEntity(), pos, blockState, InductionFurnaceMenu::new);
        outputSlots.add(2);
        outputSlots.add(3);
        setEnergy(createDefaultEnergy());
        setInventory(new ModItemStorage(4) {
            @Override
            protected void onContentsChanged(int slot) {
                if(!outputSlots.contains(slot)){
                    progress = 0;
                    InductionFurnaceBlockEntity.this.sendUpdate();
                    //Find recipe for slot 0.
                    if(slot == 0){
                        blastingCache0.findRecipe(RecipeType.BLASTING, new SimpleContainer(inventory.getStackInSlot(0)), level);
                        if (blastingCache0.getRecipe() == null) {
                            smeltingCache0.findRecipe(RecipeType.SMELTING, new SimpleContainer(inventory.getStackInSlot(0)), level);
                            if (smeltingCache0.getRecipe() == null) {
                                smokingCache0.findRecipe(RecipeType.SMOKING, new SimpleContainer(inventory.getStackInSlot(0)), level);
                            }
                        }
                    }
                    //Find recipe for slot 1.
                    if(slot == 1){
                        blastingCache1.findRecipe(RecipeType.BLASTING, new SimpleContainer(inventory.getStackInSlot(1)), level);
                        if (blastingCache1.getRecipe() == null) {
                            smeltingCache1.findRecipe(RecipeType.SMELTING, new SimpleContainer(inventory.getStackInSlot(1)), level);
                            if (smeltingCache1.getRecipe() == null) {
                                smokingCache1.findRecipe(RecipeType.SMOKING, new SimpleContainer(inventory.getStackInSlot(1)), level);
                            }
                        }
                    }
                }
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {return !outputSlots.contains(slot);}
        });
    }

    @Override
    protected void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
        tag.putInt("Heat", heat);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heat = tag.getInt("Heat");
        setMaxProgressForHeat();
    }

    public double getHeatPercentage() {
        return heat * 100D / MAX_HEAT;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        //setMaxProgressForHeat();
        //Find recipes for both slots on load.
        blastingCache0.findRecipe(RecipeType.BLASTING, new SimpleContainer(inventory.getStackInSlot(0)), level);
        if (blastingCache0.getRecipe() == null) {
            smeltingCache0.findRecipe(RecipeType.SMELTING, new SimpleContainer(inventory.getStackInSlot(0)), level);
            if (smeltingCache0.getRecipe() == null) {
                smokingCache0.findRecipe(RecipeType.SMOKING, new SimpleContainer(inventory.getStackInSlot(0)), level);
            }
        }
        blastingCache1.findRecipe(RecipeType.BLASTING, new SimpleContainer(inventory.getStackInSlot(1)), level);
        if (blastingCache1.getRecipe() == null) {
            smeltingCache1.findRecipe(RecipeType.SMELTING, new SimpleContainer(inventory.getStackInSlot(1)), level);
            if (smeltingCache1.getRecipe() == null) {
                smokingCache1.findRecipe(RecipeType.SMOKING, new SimpleContainer(inventory.getStackInSlot(1)), level);
            }
        }
    }

    private void setMaxProgressForHeat() {
        setMaxProgress(500 - (int)(490 * getHeatPercentage() / 100D));
    }

    //putting these on map might be better IDK.
    //Make them optional for handling nulls easier.
    private final RecipeCache<SmeltingRecipe, Container> smeltingCache0 = new RecipeCache<>();
    private final RecipeCache<SmeltingRecipe, Container> smeltingCache1 = new RecipeCache<>();
    private final RecipeCache<BlastingRecipe, Container> blastingCache0 = new RecipeCache<>();
    private final RecipeCache<BlastingRecipe, Container> blastingCache1 = new RecipeCache<>();
    private final RecipeCache<SmokingRecipe, Container> smokingCache0 = new RecipeCache<>();
    private final RecipeCache<SmokingRecipe, Container> smokingCache1 = new RecipeCache<>();

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInput(lvl, pos, inventory);
        performAutoOutput(lvl, pos, inventory, outputSlots.toArray(Integer[]::new));

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots)
                .initRecipe(smeltingCache0)
                .initRecipe(smeltingCache1)
                .initRecipe(blastingCache0)
                .initRecipe(blastingCache1)
                .initRecipe(smokingCache0)
                .initRecipe(smokingCache1);

        Recipe<?> recipe0 = getValidRecipe(0);
        Recipe<?> recipe1 = getValidRecipe(1);

        if (recipe0 != null) condition.tryInsertForced(recipe0.getResultItem(lvl.registryAccess()));
        if (recipe1 != null) condition.tryInsertForced(recipe1.getResultItem(lvl.registryAccess()));

        boolean result = condition.getResult();
        setMaxProgressForHeat();
        AtomicBoolean shouldHeat = new AtomicBoolean(false);
        if (!result) shouldHeat.set(true);
        progress(()-> result, () ->  {
            if (recipe0 != null) ItemHelper.insertItemStackedForced(inventory, recipe0.getResultItem(lvl.registryAccess()).copy(), false, outputSlots.toArray(Integer[]::new));
            if (recipe1 != null) ItemHelper.insertItemStackedForced(inventory, recipe1.getResultItem(lvl.registryAccess()).copy(), false, outputSlots.toArray(Integer[]::new));
            if (recipe0 != null) inventory.extractItem(0, 1, false);
            if (recipe1 != null) inventory.extractItem(1, 1, false);
        }, energy);

        boolean isThereACoil = isThereACoil(pos);
        if (energy.getEnergyStored() >= heatKeepCost && shouldHeat.get()) {
            energy.extractEnergyForced(heatKeepCost, false);
            if (heat < MAX_HEAT) {
                heat++;
                sendUpdate();
            }
        } else if (heat > 0 && (!isThereACoil || energy.getEnergyStored() < heatKeepCost)) {
            heat--;
            sendUpdate();
        }
        if (isThereACoil && energy.getEnergyStored() >= heatKeepCost && !shouldHeat.get() && heat > 0)
            energy.extractEnergyForced(heatKeepCost, false);
    }

    private boolean isThereACoil(BlockPos pos) {
        for(Direction dir : Direction.values()) {
            BlockState state = level != null ? level.getBlockState(pos.relative(dir)) : Blocks.AIR.defaultBlockState();
            if (state.is(Blocks.LIGHTNING_ROD)) {
                Direction stateFacing = state.getValue(BlockStateProperties.FACING);
                return stateFacing == dir;
            }
        }
        return false;
    }

    private Recipe<?> getValidRecipe(int slot) {
        switch (slot) {
            case 0 -> {
                if (blastingCache0.getRecipe() != null) return blastingCache0.getRecipe();
                else if(smeltingCache0.getRecipe() != null) return smeltingCache0.getRecipe();
                else if(smokingCache0.getRecipe() != null) return smokingCache0.getRecipe();
            }
            case 1 -> {
                if (blastingCache1.getRecipe() != null) return blastingCache1.getRecipe();
                else if(smeltingCache1.getRecipe() != null) return smeltingCache1.getRecipe();
                else if(smokingCache1.getRecipe() != null) return smokingCache1.getRecipe();
            }
        }
        return null;
    }
}

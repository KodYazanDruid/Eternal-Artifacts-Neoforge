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

public class InductionFurnaceBlockEntity extends SidedTransferMachineBlockEntity<InductionFurnaceMenu> {
    private int heatKeepCost = 10;
    public InductionFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.INDUCTION_FURNACE.getBlockEntity(), pos, blockState, (a, b, c, d) -> new InductionFurnaceMenu(ModMachines.INDUCTION_FURNACE.getMenu(), a, b, c, d));
        outputSlots.add(2);
        outputSlots.add(3);
        setEnergy(createDefaultEnergy());
        setInventory(createRecipeFinderInventory(4, outputSlots));
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
    protected void findRecipe() {
        recipeCon0 = new SimpleContainer(inventory.getStackInSlot(0));
        blastingCache0.findRecipe(RecipeType.BLASTING, recipeCon0, level);
        if (blastingCache0.getRecipe() == null) {
            smokingCache0.findRecipe(RecipeType.SMOKING, recipeCon0, level);
            if (smokingCache0.getRecipe() == null) {
                campfireCookingCache0.findRecipe(RecipeType.CAMPFIRE_COOKING, recipeCon0, level);
                if (campfireCookingCache0.getRecipe() == null) {
                    smeltingCache0.findRecipe(RecipeType.SMELTING, recipeCon0, level);
                }
            }
        }
        recipeCon1 = new SimpleContainer(inventory.getStackInSlot(1));
        blastingCache1.findRecipe(RecipeType.BLASTING, recipeCon1, level);
        if (blastingCache1.getRecipe() == null) {
            smokingCache1.findRecipe(RecipeType.SMOKING, recipeCon1, level);
            if (smokingCache1.getRecipe() == null) {
                campfireCookingCache1.findRecipe(RecipeType.CAMPFIRE_COOKING, recipeCon1, level);
                if (campfireCookingCache1.getRecipe() == null) {
                    smeltingCache1.findRecipe(RecipeType.SMELTING, recipeCon1, level);
                }
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
    private final RecipeCache<CampfireCookingRecipe, Container> campfireCookingCache0 = new RecipeCache<>();
    private final RecipeCache<CampfireCookingRecipe, Container> campfireCookingCache1 = new RecipeCache<>();
    private SimpleContainer recipeCon0 = null;
    private SimpleContainer recipeCon1 = null;

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos);

        Recipe<Container> recipe0 = getValidRecipe(0);
        Recipe<Container> recipe1 = getValidRecipe(1);

        if (recipe0 == null && recipe1 == null) {
            progress = 0;
        }

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots);

        ItemStack resultItem0;
        ItemStack resultItem1;
        if (recipe0 != null) {
            resultItem0 = recipe0.assemble(recipeCon0, lvl.registryAccess());
            condition.queueItemStack(resultItem0);
        } else resultItem0 = ItemStack.EMPTY;

        if (recipe1 != null) {
            resultItem1 = recipe1.assemble(recipeCon1, lvl.registryAccess());
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
                remainder0 = ItemHelper.insertItemStackedForced(inventory, resultItem0.copy(), false, outputSlots).getFirst();
            }
            if (recipe1 != null) {
                remainder1 = ItemHelper.insertItemStackedForced(inventory, resultItem1.copy(), false, outputSlots).getFirst();
            }
            if (recipe0 != null && remainder0.isEmpty()) inventory.extractItem(0, 1, false);
            if (recipe1 != null && remainder1.isEmpty()) inventory.extractItem(1, 1, false);
        }, energy);

        boolean isThereACoil = isThereACoil(pos);
        boolean isEnergyEnough = energy.getEnergyStored() >= heatKeepCost;

        if(isEnergyEnough) {
            if (shouldHeat.get()) {
                heat.heat(1, false);
                energy.extractEnergyForced(heatKeepCost, false);
            } else {
                if (isThereACoil) energy.extractEnergyForced(heatKeepCost, false);
                else heat.cool(1, false);
            }
        }else {
            if (heat.getHeat() > 0) heat.cool(1, false);
        }
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

    private Recipe<Container> getValidRecipe(int slot) {
        switch (slot) {
            case 0 -> {
                if (blastingCache0.getRecipe() != null) return blastingCache0.getRecipe();
                else if(smokingCache0.getRecipe() != null) return smokingCache0.getRecipe();
                else if(campfireCookingCache0.getRecipe() != null) return campfireCookingCache0.getRecipe();
                else if(smeltingCache0.getRecipe() != null) return smeltingCache0.getRecipe();
            }
            case 1 -> {
                if (blastingCache1.getRecipe() != null) return blastingCache1.getRecipe();
                else if(smokingCache1.getRecipe() != null) return smokingCache1.getRecipe();
                else if(campfireCookingCache1.getRecipe() != null) return campfireCookingCache1.getRecipe();
                else if(smeltingCache1.getRecipe() != null) return smeltingCache1.getRecipe();
            }
        }
        return null;
    }
}

package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.google.common.base.Predicates;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.capabilities.MultiFluidTank;
import com.sonamorningstar.eternalartifacts.container.OilRefineryMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.OilRefineryRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Random;

public class OilRefineryBlockEntity extends SidedTransferMachineBlockEntity<OilRefineryMenu> {
    public OilRefineryBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.OIL_REFINERY.get(), pos, blockState, OilRefineryMenu::new);
        setMaxProgress(20);
    }

    RecipeCache<OilRefineryRecipe, SimpleFluidContainer> recipeCache = new RecipeCache<>();

    public MultiFluidTank tanks = new MultiFluidTank(
            new ModFluidStorage(16000, fs -> fs.is(ModTags.Fluids.CRUDE_OIL)) {
                @Override
                public FluidStack drain(int maxDrain, FluidAction action) {
                    return FluidStack.EMPTY;
                }
                @Override
                protected void onContentsChanged() {
                    OilRefineryBlockEntity.this.sendUpdate();
                    recipeCache.findRecipe(ModRecipes.OIL_REFINERY_TYPE.get(), new SimpleFluidContainer(tanks.getFluidInTank(0)), level);
                }
            },
            createBasicTank(16000, Predicates.alwaysFalse(), true),
            createBasicTank(16000, Predicates.alwaysFalse(), true)
    );

    public ModEnergyStorage energy = new ModEnergyStorage(50000, 5000) {
        @Override
        public void onEnergyChanged() {
            OilRefineryBlockEntity.this.sendUpdate();
        }
    };

    public ModItemStorage inventory = new ModItemStorage(3) {
        @Override
        protected void onContentsChanged(int slot) {
            OilRefineryBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

    };

    @Override
    public void onLoad() {
        super.onLoad();
        recipeCache.findRecipe(ModRecipes.OIL_REFINERY_TYPE.get(), new SimpleFluidContainer(tanks.getFluidInTank(0)), level);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        energy.deserializeNBT(pTag.get("Energy"));
        tanks.readFromNBT(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", energy.serializeNBT());
        tanks.writeToNBT(pTag);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputFluids(lvl, pos, tanks);
        performAutoOutputFluids(lvl, pos, tanks);
        performAutoOutput(lvl, pos, inventory, 0, 1, 2);

        if (recipeCache.getRecipe() != null) {
            //NonNullList<ItemStack> outputItems = recipeCache.getRecipe().getItemOutputs();
            //NonNullList<Float> chances = recipeCache.getRecipe().getChances();
            progress(()-> {
                int inserted = ((ModFluidStorage) tanks.get(1)).fillForced(recipeCache.getRecipe().getOutput(), IFluidHandler.FluidAction.SIMULATE);
                int insertedSecondary = ((ModFluidStorage) tanks.get(2)).fillForced(recipeCache.getRecipe().getSecondaryOutput(), IFluidHandler.FluidAction.SIMULATE);
                return inserted <= 0 || insertedSecondary <= 0;
            }, ()-> {
                ((ModFluidStorage) tanks.get(1)).fillForced(recipeCache.getRecipe().getOutput(), IFluidHandler.FluidAction.EXECUTE);
                ((ModFluidStorage) tanks.get(2)).fillForced(recipeCache.getRecipe().getSecondaryOutput(), IFluidHandler.FluidAction.EXECUTE);

                //TODO: Item outputs.

                /*try {
                    Random random = new Random();
                    for (int i = 0; i < outputItems.size(); i++) {
                        if (random.nextFloat() <= chances.get(i)) {
                            ItemHandlerHelper.insertItemStacked(inventory, outputItems.get(i).copy(), false);
                        }
                    }
                }catch (IndexOutOfBoundsException e) {
                    EternalArtifacts.LOGGER.error("Chances list and item output list for {} should be same size. Error in recipe {}", OilRefineryRecipe.class, recipeCache.getRecipe());
                }*/

                ((ModFluidStorage) tanks.get(0)).drainForced(50, IFluidHandler.FluidAction.EXECUTE);
            }, energy);
        }
    }
}

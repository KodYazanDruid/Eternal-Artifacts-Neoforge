package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SludgeRefiner extends GenericMachine {
	public static final List<ResourceLocation> SLUDGE_RESULTS = new ArrayList<>();
	public static final String CACHED_TABLE_ID_KEY = "CachedTableId";
	private ResourceLocation cachedTableId = null;
	private boolean shouldUpdateCache = false;
	
	public SludgeRefiner(BlockPos pos, BlockState blockState) {
		super(ModMachines.SLUDGE_REFINER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		setTank(() -> createRecipeFinderTank(16000, (fs) -> fs.is(ModFluids.SLUDGE.getFluid()),false, true));
		for (int i = 0; i < 8; i++) {
			outputSlots.add(i);
		}
		setInventory(() -> createBasicInventory(8, outputSlots, (slot, stack) -> false, s -> {}));
		screenInfo.setArrowPos(51, 45);
		for (int i = 0; i < 8; i++) {
			int x = i % 4;
			int y = i / 4;
			screenInfo.setSlotPosition(84 + x * 18, 35 + y * 18, i);
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (cachedTableId != null) {
			tag.putString(CACHED_TABLE_ID_KEY, cachedTableId.toString());
		}
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(CACHED_TABLE_ID_KEY)) {
			cachedTableId = ResourceLocation.tryParse(tag.getString(CACHED_TABLE_ID_KEY));
		}
	}
	
	@Override
	protected void findRecipe() {
		if (getLevel() instanceof ServerLevel sLevel && shouldUpdateCache) {
			FluidStack fluid = tank.getFluidInTank(0);
			if (fluid.getAmount() >= 500 && fluid.is(ModFluids.SLUDGE.getFluid())) {
				cachedTableId = SLUDGE_RESULTS.get(sLevel.random.nextInt(SLUDGE_RESULTS.size()));
			}
		}
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		if (cachedTableId != null && getLevel() instanceof ServerLevel sLevel) {
			Map<Item, Pair<Float, Float>> itemsWithCounts = LootTableHelper.getItemsWithCounts(sLevel, cachedTableId);
			itemsWithCounts.forEach((item, countPair) -> {
				condition.queueImport(new ItemStack(item, Math.max(1, Math.round(countPair.getSecond()))));
			});
			condition.commitQueuedImports();
		}
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		performAutoInputFluids(lvl, pos);
		
		if (cachedTableId == null || tank.getFluidInTank(0).getAmount() < 500 || !tank.getFluidInTank(0).is(ModFluids.SLUDGE.getFluid())) {
			progress = 0;
			return;
		}
		
		progress(() -> {
			ServerLevel sLevel = (ServerLevel) lvl;
			LootTable table = LootTableHelper.getTable(sLevel, cachedTableId);
			ObjectArrayList<ItemStack> items = table.getRandomItems(new LootParams.Builder(sLevel).create(LootContextParamSets.EMPTY));
			for (ItemStack stack : items) {
				ItemHelper.insertItemStackedForced(inventory, stack, false, outputSlots);
			}
			shouldUpdateCache = true;
			tank.drainForced(500, IFluidHandler.FluidAction.EXECUTE);
		});
	}
}

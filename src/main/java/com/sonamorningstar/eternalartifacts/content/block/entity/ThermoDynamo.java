package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.api.caches.InfiniteDynamoProcessCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.ItemDynamoMenu;
import com.sonamorningstar.eternalartifacts.container.base.DynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.function.Predicate;

public class ThermoDynamo extends AbstractDynamo<DynamoMenu> {
	private BlockState heatSourceState = null;
	private FluidStack coolant = FluidStack.EMPTY;
	private int heatValue = 0;
	private int coolantValue = 0;
	private boolean updateSourceState = false;
	public static final List<Pair<Predicate<BlockState>, Integer>> HEAT_SOURCES = List.of(
		Pair.of(bs -> bs.is(Blocks.LAVA), 1000),
		Pair.of(bs -> bs.is(Blocks.MAGMA_BLOCK), 850)
	);
	//The amount is useless. I used FluidIngredient to include fluid tags and possibly fluids with NBT.
	public static final List<Pair<FluidIngredient, Integer>> COOLANTS = List.of(
		Pair.of(FluidIngredient.of(FluidTags.WATER, 1000), 1000)
	);
	public ThermoDynamo(BlockPos pos, BlockState blockState) {
		super(ModMachines.THERMO_DYNAMO, pos, blockState);
		setTank(() -> createBasicTank(16000, this::isValidCoolant, false, true,
			() -> {
				FluidStack fluidStack = tank.getFluidInTank(0);
				if (!fluidStack.isFluidEqual(coolant)) setCoolant(fluidStack);
			}
		));
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		updateSourceState = true;
		setCoolant(tank.getFluidInTank(0));
	}
	
	@Override
	protected boolean canProcessRecipeless() {
		return true;
	}
	
	@Override
	public BlockState updateBlockState(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (direction == state.getValue(BlockStateProperties.FACING).getOpposite()) updateSourceState = true;
		return super.updateBlockState(state, direction, neighborState, level, pos, neighborPos);
	}
	
	protected void spendCoolant() {
		int drained = tank.drainForced(2, IFluidHandler.FluidAction.SIMULATE).getAmount();
		if (drained == 2) {
			tank.drainForced(2, IFluidHandler.FluidAction.EXECUTE);
		}
	}
	
	protected boolean isValidHeatSource(BlockState heatSource, BlockPos pos, ServerLevel lvl) {
		return HEAT_SOURCES.stream().anyMatch(p -> p.getFirst().test(heatSource));
	}
	
	protected boolean isValidCoolant(FluidStack coolant) {
		return COOLANTS.stream().anyMatch(p -> p.getFirst().testIgnoreAmount(coolant));
	}
	
	protected void setCoolant(FluidStack coolant) {
		if (isValidCoolant(coolant)) {
			this.coolant = coolant;
			coolantValue = COOLANTS.stream().filter(p -> p.getFirst().testIgnoreAmount(coolant)).map(Pair::getSecond).findFirst().orElse(0);
		} else {
			this.coolant = FluidStack.EMPTY;
			coolantValue = 0;
		}
		invalidateDynamoCache();
	}
	
	@Override
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		if (updateSourceState) {
			Direction facing = st.getValue(BlockStateProperties.FACING);
			heatSourceState = lvl.getBlockState(pos.relative(facing.getOpposite()));
			heatValue = HEAT_SOURCES.stream().filter(p -> p.getFirst().test(heatSourceState)).map(Pair::getSecond).findFirst().orElse(0);
			updateSourceState = false;
			invalidateDynamoCache();
		}
		if (heatSourceState == null || !isValidHeatSource(heatSourceState, pos, lvl)) invalidateDynamoCache();
		super.tickServer(lvl, pos, st);
	}
	
	@Override
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		if (heatSourceState != null && !heatSourceState.isAir() && !coolant.isEmpty()) {
			int delta = Math.abs(heatValue - coolantValue);
			int generation = defaultEnergyPerTick + (int) (Math.sqrt(delta) * 12.0F);
			setEnergyPerTick(generation * ((getEnchantmentLevel(ModEnchantments.CELERITY.get()) / 3) + 1));
			cacheGetter.apply(-1, energy, energyPerTick, this);
		}
	}
	
	@Override
	protected DynamoProcessCache createCache(int duration, ModEnergyStorage energyCap, int generation, AbstractDynamo<?> dynamo) {
		InfiniteDynamoProcessCache cache = new InfiniteDynamoProcessCache(energyCap, generation, dynamo) {
			@Override
			public boolean canContinueUse() {
				FluidStack fluidStack = tank.getFluidInTank(0);
				return !fluidStack.isEmpty() && fluidStack.getAmount() >= 2;
			}
		};
		cache.addOnProcessListener(ca -> spendCoolant());
		return cache;
	}
}

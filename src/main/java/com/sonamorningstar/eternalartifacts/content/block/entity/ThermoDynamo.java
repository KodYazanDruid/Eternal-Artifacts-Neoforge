package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.block_search.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.api.block_search.InfiniteDynamoProcessCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.base.DynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.content.datamaps.Coolant;
import com.sonamorningstar.eternalartifacts.content.datamaps.HeatSource;
import com.sonamorningstar.eternalartifacts.core.ModDataMaps;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class ThermoDynamo extends AbstractDynamo<DynamoMenu> {
	private BlockState heatSourceState = null;
	private FluidStack coolant = FluidStack.EMPTY;
	private int heatValue = 0;
	private int coolantValue = 0;
	private int coolantConsumption = 0;
	private boolean updateSourceState = false;
	
	public ThermoDynamo(BlockPos pos, BlockState blockState) {
		super(ModMachines.THERMO_DYNAMO, pos, blockState);
		setTank(() -> createBasicTank(16000, fs -> fs.getFluidHolder().getData(ModDataMaps.COOLANTS) != null, false, true,
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
	
	protected void setCoolant(FluidStack coolant) {
		Coolant data = coolant.getFluidHolder().getData(ModDataMaps.COOLANTS);
		if (data != null) {
			this.coolant = coolant;
			coolantValue = data.coolantValue();
			coolantConsumption = data.consumedPerTick();
		} else {
			this.coolant = FluidStack.EMPTY;
			coolantValue = 0;
			coolantConsumption = 0;
		}
		invalidateDynamoCache();
	}
	
	@Override
	public void tickServer(ServerLevel lvl, BlockPos pos, BlockState st) {
		if (updateSourceState) {
			Direction facing = st.getValue(BlockStateProperties.FACING);
			BlockPos back = pos.relative(facing.getOpposite());
			BlockState blockState = lvl.getBlockState(back);
			FluidState fluidState = lvl.getFluidState(back);
			BlockState fluidBlockState = fluidState.createLegacyBlock();
			HeatSource fluidHeatData = null;
			if (!fluidState.isEmpty() && !fluidBlockState.isAir()) {
				fluidHeatData = fluidBlockState.getBlockHolder().getData(ModDataMaps.HEAT_BLOCKS);
			}
			HeatSource blockHeatData = blockState.getBlockHolder().getData(ModDataMaps.HEAT_BLOCKS);
			
			BlockState state = fluidHeatData != null ? fluidBlockState : blockState;
			HeatSource heatSource = fluidHeatData != null ? fluidHeatData : blockHeatData;
			if (heatSource != null) {
				heatSourceState = state;
				heatValue = heatSource.heatValue();
			} else {
				heatSourceState = null;
				heatValue = 0;
			}
			updateSourceState = false;
			invalidateDynamoCache();
		}
		super.tickServer(lvl, pos, st);
	}
	
	@Override
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		if (heatSourceState != null && !heatSourceState.isAir() && !coolant.isEmpty()) {
			setEnergyPerTick(getEnergyGeneration() * ((getEnchantmentLevel(ModEnchantments.CELERITY.get()) / 3) + 1));
			cacheGetter.apply(-1, energy, energyPerTick, this);
		}
	}
	
	private int getEnergyGeneration() {
		int delta = Math.abs(heatValue - coolantValue);
		return defaultEnergyPerTick + (int) (Math.sqrt(delta) * 12.0F);
	}
	
	@Override
	protected DynamoProcessCache createCache(int duration, ModEnergyStorage energyCap, int generation, AbstractDynamo<?> dynamo) {
		InfiniteDynamoProcessCache cache = new InfiniteDynamoProcessCache(energyCap, generation, dynamo) {
			@Override
			public boolean canContinueUse() {
				FluidStack fluidStack = tank.getFluidInTank(0);
				return !fluidStack.isEmpty() && fluidStack.getAmount() >= coolantConsumption;
			}
		};
		cache.addOnProcessListener(ca -> tank.drainForced(coolantConsumption, IFluidHandler.FluidAction.EXECUTE));
		return cache;
	}
}

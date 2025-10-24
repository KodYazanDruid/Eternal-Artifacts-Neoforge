package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.base.DynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class AlchemicalDynamo extends AbstractDynamo<DynamoMenu> {
	public AlchemicalDynamo(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.ALCHEMICAL_DYNAMO.get(), pos, blockState, DynamoMenu::new);
		setTank(() -> createBasicTank(4000, false, true));
		setDefaultEnergyPerTick(80);
	}
	
	@Override
	protected boolean canProcessRecipeless() {
		FluidStack fluid = tank.getFluid(0);
		return !fluid.isEmpty() && fluid.is(ModTags.Fluids.POTION);
	}
	
	@Override
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		FluidStack fluid = tank.getFluid(0);
		if (!fluid.isEmpty() && fluid.getAmount() >= 250 && fluid.is(ModTags.Fluids.POTION)) {
			List<MobEffectInstance> effects = PotionUtils.getAllEffects(fluid.getTag());
			if (!effects.isEmpty()) {
				int totalDuration = 0;
				int generation = 0;
				for (MobEffectInstance effect : effects) {
					totalDuration += effect.getDuration();
					generation += (effect.getAmplifier() + 1) * defaultEnergyPerTick;
				}
				int celerity = getEnchantmentLevel(ModEnchantments.CELERITY.get());
				int efficiency = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
				setMaxProgress(totalDuration * ((efficiency / 5) + 1));
				setEnergyPerTick(generation * ((celerity / 3) + 1));
				tank.drainForced(250, IFluidHandler.FluidAction.EXECUTE);
				cacheGetter.apply(maxProgress, energy, energyPerTick, this);
			}
		}
	}
}

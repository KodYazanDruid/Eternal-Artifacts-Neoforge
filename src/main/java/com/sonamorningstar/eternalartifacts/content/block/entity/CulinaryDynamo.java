package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.ItemDynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;

public class CulinaryDynamo extends AbstractDynamo<ItemDynamoMenu> {
	public CulinaryDynamo(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.CULINARY_DYNAMO.get(), pos, blockState, ItemDynamoMenu::new);
		setInventory(() -> createBasicInventory(1, List.of(), (slot, stack) -> stack.isEdible()));
		setDefaultEnergyPerTick(80);
	}
	
	@Override
	protected boolean canProcessRecipeless() {
		ItemStack stack = inventory.getStackInSlot(0);
		return !stack.isEmpty() && stack.isEdible();
	}
	
	@Override
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		ItemStack stack = inventory.getStackInSlot(0);
		if (!stack.isEmpty() && stack.isEdible()) {
			int celerity = getEnchantmentLevel(ModEnchantments.CELERITY.get());
			int efficiency = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
			FoodProperties food = stack.getFoodProperties(null);
			int nutrition = food.getNutrition();
			float saturationMod = food.getSaturationModifier();
			float realSaturation = nutrition * saturationMod * 2.0F;
			int totalDuration = Mth.ceil((nutrition + realSaturation) * 20) * 2;
			setMaxProgress(totalDuration * ((efficiency / 5) + 1));
			setEnergyPerTick(defaultEnergyPerTick * ((celerity / 3) + 1));
			FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, level);
			fakePlayer.setYRot(getBlockState().getValue(BlockStateProperties.FACING).toYRot());
			fakePlayer.setPosRaw(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
			ItemStack remaining = stack.finishUsingItem(level, fakePlayer);
			inventory.setStackInSlot(0, remaining);
			cacheGetter.apply(maxProgress, energy, energyPerTick, this);
		}
	}
}

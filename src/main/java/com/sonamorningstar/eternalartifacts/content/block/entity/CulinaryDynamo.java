package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.block_search.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.ItemDynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.content.item.FeedingCanister;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;

public class CulinaryDynamo extends AbstractDynamo<ItemDynamoMenu> {
	
	public static final FoodData HUNGRY = Util.make(() -> {
		FoodData foodData = new FoodData() {
			@Override
			public void eat(int foodLevelModifier, float saturationLevelModifier) {}
		};
		foodData.setFoodLevel(0);
		foodData.setSaturation(0.0F);
		return foodData;
	});
	
	public CulinaryDynamo(BlockPos pos, BlockState blockState) {
		super(ModMachines.CULINARY_DYNAMO, pos, blockState);
		setInventory(() -> createBasicInventory(1, List.of(), (slot, stack) -> stack.isEdible(), slot -> {
			if (!level.isClientSide()) getFakePlayer().stopUsingItem();
		}));
		setDefaultEnergyPerTick(80);
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		if (!level.isClientSide()) getFakePlayer().setFoodData(HUNGRY);
	}
	
	@Override
	protected boolean canProcessRecipeless() {
		return true;
	}
	
	@Override
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		ItemStack stack = inventory.getStackInSlot(0);
		if (!stack.isEmpty() && stack.isEdible()) {
			FoodProperties food = stack.getFoodProperties(null);
			if (food != null) {
				float nutrition;
				float realSaturation;
				if (stack.getItem() instanceof FeedingCanister) {
					nutrition = Math.min(food.getNutrition(), 20);
					float saturationMod = food.getSaturationModifier();
					realSaturation = Math.min(nutrition * saturationMod * 2.0F, 20);
				} else {
					nutrition = food.getNutrition();
					float saturationMod = food.getSaturationModifier();
					realSaturation = nutrition * saturationMod * 2.0F;
				}
				defaultMaxProgress = Mth.ceil((nutrition + realSaturation) * 20) * 2;
				prepareDynamoEnergyAndDuration();
				getFakePlayer();
				if (!fakePlayer.isUsingItem()) {
					fakePlayer.setItemInHand(fakePlayer.getUsedItemHand(), stack);
					fakePlayer.startUsingItem(fakePlayer.getUsedItemHand());
					return;
				}
				
				boolean wasSameItem = ItemStack.isSameItemSameTags(fakePlayer.getUseItem(), stack);
				int remainingBefore = fakePlayer.getUseItemRemainingTicks();
				
				fakePlayer.updateUsingItem(fakePlayer.getUseItem());
				
				if (!fakePlayer.isUsingItem()) {
					boolean naturalCompletion = wasSameItem && remainingBefore <= 1;
					if (naturalCompletion) {
						cacheGetter.apply(maxProgress, energy, energyPerTick, this);
					}
				}
			}
		}
	}
}

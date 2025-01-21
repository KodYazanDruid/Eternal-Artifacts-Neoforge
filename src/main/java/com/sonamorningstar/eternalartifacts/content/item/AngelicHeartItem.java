package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AngelicHeartItem extends Item {
	public AngelicHeartItem(Properties props) {
		super(props);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		ItemStack itemStack = super.finishUsingItem(stack, level, livingEntity);
		CharmStorage.get(livingEntity).setWildcardNbt(true);
		return itemStack;
	}
}

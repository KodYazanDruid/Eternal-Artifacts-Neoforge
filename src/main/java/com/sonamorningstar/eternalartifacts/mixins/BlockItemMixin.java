package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.BlockItemCanPlace;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin implements BlockItemCanPlace {
	
	@Shadow protected abstract boolean canPlace(BlockPlaceContext context, BlockState state);
	
	@Override
	public boolean canPlaceBI(BlockPlaceContext context, BlockState state) {
		return this.canPlace(context, state);
	}
}

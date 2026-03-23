package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {
	
	@Shadow protected abstract BlockPos getStrikePosition();
	
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;powerLightningRod()V", ordinal = 0))
	private void onLightningStrike(CallbackInfo ci) {
		BlockPos strikePosition = getStrikePosition();
		for (BlockPos blockPos : BlockPos.betweenClosed(strikePosition.offset(-1, -1, -1), strikePosition.offset(1, 1, 1))) {
			LightningBolt bolt = (LightningBolt) (Object) this;
			Level level = bolt.level();
			BlockEntity be = level.getBlockEntity(blockPos);
			if (be instanceof BrewingStandBlockEntity stand) {
				for (int i = 0; i < 3; i++) {
					ItemStack item = stand.getItem(i);
					if (item.is(ModItems.REINFORCED_GLASS_BOTTLE)) {
						stand.setItem(i, ModItems.LIGHTNING_IN_A_BOTTLE.toStack(item.getCount()));
					}
				}
			}
		}
	}
}

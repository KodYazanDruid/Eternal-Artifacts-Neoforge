package com.sonamorningstar.eternalartifacts.mixins;

import com.sonamorningstar.eternalartifacts.content.block.entity.BlockBreaker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
	
	@Shadow @Final protected ServerPlayer player;
	
	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V"))
	private void destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (this.player instanceof FakePlayer fp) {
			BlockEntity be = this.player.level().getBlockEntity(fp.blockPosition());
			if (be instanceof BlockBreaker breaker) {
				breaker.destroyTickStart = -1;
			}
		}
	}
}

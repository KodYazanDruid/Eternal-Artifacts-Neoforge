package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
	
	@WrapOperation(method = "getSkin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerInfo;getSkin()Lnet/minecraft/client/resources/PlayerSkin;"))
	private PlayerSkin getSkin(PlayerInfo instance, Operation<PlayerSkin> original) {
		AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
		ItemStack head = PlayerCharmManager.findCharm(player, PlayerHeadItem.class);
		if (!head.isEmpty() && head.hasTag()) {
			GameProfile profile = NbtUtils.readGameProfile(head.getTag().getCompound("SkullOwner"));
			if (profile != null) {
				return PlayerInfo.createSkinLookup(profile).get();
			}
		}
		return original.call(instance);
	}
}

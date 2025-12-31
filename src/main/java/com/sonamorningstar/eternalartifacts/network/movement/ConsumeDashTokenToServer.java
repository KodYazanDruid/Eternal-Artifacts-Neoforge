package com.sonamorningstar.eternalartifacts.network.movement;

import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingDasher;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public class ConsumeDashTokenToServer implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "consume_dash_token_to_server");
	
	public static ConsumeDashTokenToServer create(FriendlyByteBuf buf) {
		return new ConsumeDashTokenToServer();
	}
	
	public static ConsumeDashTokenToServer create() {
		return new ConsumeDashTokenToServer();
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isServerbound()){
			ctx.workHandler().execute(() -> ctx.player().ifPresent(player -> {
				CompoundTag tag = player.getPersistentData();
				int tokens = tag.getInt(ILivingDasher.KEY);
				tag.putInt(ILivingDasher.KEY, Math.max(0, tokens - 1));
				if (player instanceof ILivingDasher dasher) {
					dasher.dashAir(player);
					player.causeFoodExhaustion(0.2F);
					dasher.setDashCooldown(10);
					Level level = player.level();
					level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.BREEZE_SLIDE, player.getSoundSource(), 1.0F, 1.0F);
					if (level instanceof ServerLevel sl) {
						sl.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(),
							10, 0.5, 0.5, 0.5, 0.1);
					}
				}
			}));
		}
	}
}

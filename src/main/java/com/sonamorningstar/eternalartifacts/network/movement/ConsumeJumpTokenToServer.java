package com.sonamorningstar.eternalartifacts.network.movement;

import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingJumper;
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
public record ConsumeJumpTokenToServer() implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "consume_jump_token_to_server");
	
	public static ConsumeJumpTokenToServer create(FriendlyByteBuf buf) {
		return new ConsumeJumpTokenToServer();
	}
	
	public static ConsumeJumpTokenToServer create() {
		return new ConsumeJumpTokenToServer();
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
				int tokens = tag.getInt(ILivingJumper.KEY);
				tag.putInt(ILivingJumper.KEY, Math.max(0, tokens - 1));
				player.jumpFromGround();
				if (player.isSprinting()) player.causeFoodExhaustion(0.2F);
				else player.causeFoodExhaustion(0.05F);
				player.noJumpDelay = 10;
				Level level = player.level();
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.BREEZE_JUMP, player.getSoundSource(), 1.0F, 1.0F);
				if (level instanceof ServerLevel sl) {
					sl.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(),
						10, 0.5, 0.5, 0.5, 0.1);
				}
			}));
		}
	}
}

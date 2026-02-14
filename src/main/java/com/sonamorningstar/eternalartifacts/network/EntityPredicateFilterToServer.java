package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.content.block.base.EntityFilterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.EntityInteractor;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record EntityPredicateFilterToServer(BlockPos pos, EntityPredicateEntry entry) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "entity_predicate_filter");
	
	public static EntityPredicateFilterToServer create(FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		EntityPredicateEntry entry = new EntityPredicateEntry();
		entry.fromNetwork(buf);
		return new EntityPredicateFilterToServer(pos, entry);
	}
	
	public static EntityPredicateFilterToServer create(BlockPos pos, EntityPredicateEntry entry) {
		return new EntityPredicateFilterToServer(pos, entry);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeBlockPos(pos);
		entry.toNetwork(buff);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ctx.workHandler().submitAsync(() -> ctx.player().ifPresent(player -> {
			if (player instanceof ServerPlayer serverPlayer) {
				Level level = serverPlayer.level();
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if (blockEntity instanceof EntityFilterable filterable) {
					filterable.setEntityFilter(entry);
					if (blockEntity instanceof ModBlockEntity mbe) mbe.sendUpdate();
					else blockEntity.setChanged();
				}
			}
		}));
	}
}

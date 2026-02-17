package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.filter.EntityFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.content.block.base.EntityFilterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.EntityInteractor;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
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
public record EntityPredicateFilterToServer(BlockPos pos, EntityPredicateEntry entry) implements ServerPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "entity_predicate_filter");
	
	public static EntityPredicateFilterToServer create(FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		EntityFilterEntry readEntry = EntityFilterEntry.readFromNetwork(buf);
		EntityPredicateEntry predicateEntry = readEntry instanceof EntityPredicateEntry e ? e : new EntityPredicateEntry();
		return new EntityPredicateFilterToServer(pos, predicateEntry);
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
	
	@Override
	public void handleOnServer(ServerPlayer player) {
		Level level = player.level();
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof EntityFilterable filterable) {
			filterable.setEntityFilter(entry);
			if (filterable instanceof ModBlockEntity mbe) mbe.sendUpdate();
		}
	}
	
}

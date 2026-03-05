package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.filter.EntityFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTagEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTypeEntry;
import com.sonamorningstar.eternalartifacts.content.block.base.EntityFilterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record EntityFilterEntryToServer(BlockPos pos, List<EntityFilterEntry> entries) implements ServerPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "entity_filter_entry");
	
	public static EntityFilterEntryToServer create(FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		var entries = buf.readList(EntityFilterEntry::readFromNetwork);
		return new EntityFilterEntryToServer(pos, entries);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeBlockPos(pos);
		buff.writeCollection(entries, (b, e) -> e.toNetwork(b));
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
			List<EntityTypeEntry> typeEntries = new ArrayList<>();
			List<EntityTagEntry> tagEntries = new ArrayList<>();
			for (EntityFilterEntry entry : entries) {
				if (entry instanceof EntityTypeEntry typeEntry) {
					typeEntries.add(typeEntry);
				} else if (entry instanceof EntityTagEntry tagEntry) {
					tagEntries.add(tagEntry);
				}
			}
			filterable.setEntityTypeEntries(typeEntries);
			filterable.setEntityTagEntries(tagEntries);
			if (filterable instanceof ModBlockEntity mbe) mbe.sendUpdate();
		}
	}
}

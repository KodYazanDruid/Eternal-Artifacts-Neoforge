package com.sonamorningstar.eternalartifacts.api.machine.tesseract;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Network<C> implements Comparable<Network<?>> {
	private final List<UUID> whitelistedPlayers = new ArrayList<>();
	@Setter
	private Access access = Access.PUBLIC;
	
	private final String name;
	private final UUID uuid;
	private final UUID ownerUUID;
	private final Class<C> capabilityClass;
	
	@Override
	public int compareTo(@NotNull Network<?> o) {
		int nameComparison = this.name.compareToIgnoreCase(o.getName());
		if (nameComparison != 0) {
			return nameComparison;
		}
		return this.uuid.compareTo(o.getUuid());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Network<?> network = (Network<?>) o;
		return Objects.equals(getUuid(), network.getUuid());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getUuid());
	}
	
	public void addWhiteList(Player player) {
		if (!player.getUUID().equals(ownerUUID)) whitelistedPlayers.add(player.getUUID());
	}
	
	public void writeToNBT(CompoundTag networkTag) {
		networkTag.putString("Name", name);
		networkTag.putUUID("UUID", uuid);
		networkTag.putUUID("OwnerUUID", ownerUUID);
		networkTag.putString("Access", access.name());
		ListTag whitelist = new ListTag();
		for (UUID playerId : whitelistedPlayers) {
			CompoundTag idTag = new CompoundTag();
			idTag.putUUID("PlayerUUID", playerId);
			whitelist.add(idTag);
		}
		networkTag.put("Whitelist", whitelist);
		networkTag.putString("CapabilityClass", capabilityClass.getName());
	}
	
	@Nullable
	public static Network<?> fromNBT(@Nullable CompoundTag networkTag) {
		if (networkTag == null) return null;
		Class<?> capClass = null;
		try {
			capClass = Class.forName(networkTag.getString("CapabilityClass"));
		} catch (ClassNotFoundException e) {
			EternalArtifacts.LOGGER.error("Failed to load capability class for network: {}.", networkTag.getString("Name"));
		}
		Network<?> network = new Network<>(
				networkTag.getString("Name"),
				networkTag.getUUID("UUID"),
				networkTag.getUUID("OwnerUUID"),
				capClass);
		network.setAccess(Access.valueOf(networkTag.getString("Access")));
		ListTag whitelist = networkTag.getList("Whitelist", 10);
		for (int i = 0; i < whitelist.size(); i++) {
			CompoundTag idTag = whitelist.getCompound(i);
			network.whitelistedPlayers.add(idTag.getUUID("PlayerUUID"));
		}
		return network;
	}
	
	public enum Access {
		PUBLIC,
		PROTECTED,
		PRIVATE
	}
}

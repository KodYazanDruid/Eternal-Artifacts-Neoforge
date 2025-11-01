package com.sonamorningstar.eternalartifacts.api.machine.tesseract;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;


/**
 * When you mutate this class (changing name, privacy or adding players to the whitelist), you are going to need to update the TesseractNetworks SavedData object that contains this TesseractNetwork.
 * Basically call {@link TesseractNetworks#setDirty()}
*/
@Getter
@RequiredArgsConstructor
public class TesseractNetwork<C> implements Comparable<TesseractNetwork<?>> {
	public static final Map<Class<?>, Component> CAPABILITY_NAMES = new TreeMap<>(
		Comparator.<Class<?>, String>comparing(Class::getSimpleName).thenComparing(Class::getName)
	);
	private final List<GameProfile> whitelistedPlayers = new ArrayList<>();
	private final List<String> pendingWhitelistPlayers = new ArrayList<>();
	@Setter
	private Access access = Access.PUBLIC;
	@Setter
	private CompoundTag savedData;
	
	private final String name;
	private final UUID uuid;
	private final GameProfile owner;
	private final Class<C> capabilityClass;
	
	@Override
	public int compareTo(@NotNull TesseractNetwork<?> o) {
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
		TesseractNetwork<?> tesseractNetwork = (TesseractNetwork<?>) o;
		return Objects.equals(getUuid(), tesseractNetwork.getUuid());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getUuid());
	}
	
	public void queuePlayerForWhitelist(String playerName) {
		if (!pendingWhitelistPlayers.contains(playerName)) {
			pendingWhitelistPlayers.add(playerName);
		}
	}
	
	public void addWhiteList(Player player) {
		if (!player.getUUID().equals(owner.getId())) whitelistedPlayers.add(player.getGameProfile());
	}
	
	public void writeToNBT(CompoundTag networkTag) {
		networkTag.putString("Name", name);
		networkTag.putUUID("UUID", uuid);
		CompoundTag gameProfile = new CompoundTag();
		NbtUtils.writeGameProfile(gameProfile, owner);
		networkTag.put("Owner", gameProfile);
		networkTag.putString("Access", access.name());
		ListTag whitelist = new ListTag();
		for (GameProfile profile : whitelistedPlayers) {
			CompoundTag idTag = new CompoundTag();
			NbtUtils.writeGameProfile(idTag, profile);
			whitelist.add(idTag);
		}
		networkTag.put("Whitelist", whitelist);
		ListTag pendingWhitelist = new ListTag();
		for (String playerName : pendingWhitelistPlayers) {
			CompoundTag idTag = new CompoundTag();
			idTag.putString("Name", playerName);
			pendingWhitelist.add(idTag);
		}
		networkTag.put("PendingWhitelist", pendingWhitelist);
		networkTag.putString("CapabilityClass", capabilityClass.getName());
		if (savedData != null) networkTag.put("CapData", savedData);
	}
	
	@Nullable
	public static TesseractNetwork<?> fromNBT(@Nullable CompoundTag networkTag) {
		if (networkTag == null) return null;
		Class<?> capClass = null;
		CompoundTag capData = null;
		try {
			capClass = Class.forName(networkTag.getString("CapabilityClass"));
		} catch (ClassNotFoundException e) {
			EternalArtifacts.LOGGER.error("Failed to load capability class for tesseractNetwork: {}.", networkTag.getString("Name"));
		}
		if (networkTag.contains("CapData")) capData = networkTag.getCompound("CapData");
		
		GameProfile gameProfile = NbtUtils.readGameProfile(networkTag.getCompound("Owner"));
		TesseractNetwork<?> tesseractNetwork = new TesseractNetwork<>(
				networkTag.getString("Name"),
				networkTag.getUUID("UUID"),
				gameProfile,
				capClass);
		if (capData != null) tesseractNetwork.setSavedData(capData);
		tesseractNetwork.setAccess(Access.valueOf(networkTag.getString("Access")));
		ListTag whitelist = networkTag.getList("Whitelist", 10);
		for (int i = 0; i < whitelist.size(); i++) {
			CompoundTag idTag = whitelist.getCompound(i);
			tesseractNetwork.whitelistedPlayers.add(NbtUtils.readGameProfile(idTag));
		}
		ListTag pendingWhitelist = networkTag.getList("PendingWhitelist", 10);
		for (int i = 0; i < pendingWhitelist.size(); i++) {
			CompoundTag idTag = pendingWhitelist.getCompound(i);
			tesseractNetwork.pendingWhitelistPlayers.add(idTag.getString("Name"));
		}
		return tesseractNetwork;
	}
	
	public enum Access {
		PUBLIC,
		PROTECTED,
		PRIVATE
	}
}

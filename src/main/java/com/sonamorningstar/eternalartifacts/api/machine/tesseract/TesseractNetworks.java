package com.sonamorningstar.eternalartifacts.api.machine.tesseract;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.*;

@Getter
public class TesseractNetworks extends SavedData {
	private final Set<Network<?>> networks = new HashSet<>();
	
	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag listTag = new ListTag();
		for (Network<?> network : networks) {
			CompoundTag networkTag = new CompoundTag();
			network.writeToNBT(networkTag);
			listTag.add(networkTag);
		}
		tag.put("TesseractNetworks", listTag);
		return tag;
	}
	
	public static TesseractNetworks load(CompoundTag tag) {
		TesseractNetworks tesseractNetworks = new TesseractNetworks();
		tesseractNetworks.read(tag);
		return tesseractNetworks;
	}
	
	private void read(CompoundTag tag) {
		ListTag listTag = tag.getList("TesseractNetworks", 10);
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag networkTag = listTag.getCompound(i);
			Network<?> network = Network.fromNBT(networkTag);
			networks.add(network);
		}
	}
	
	public boolean addNetwork(Network<?> network) {
		boolean ret = networks.add(network);
		setDirty();
		return ret;
	}
	
	public boolean removeNetwork(Network<?> network) {
		boolean ret = networks.remove(network);
		setDirty();
		return ret;
	}
	
	public boolean removeNetwork(UUID uuid) {
		boolean ret = networks.removeIf(network -> network.getUuid().equals(uuid));
		setDirty();
		return ret;
	}
	
	@Nullable
	public static Network<?> getNetwork(String name, LevelAccessor levelAcc) {
		return get(levelAcc).networks.stream().filter(network -> network.getName().equals(name)).findFirst().orElse(null);
	}
	
	@Nullable
	public static Network<?> getNetwork(UUID uuid, LevelAccessor levelAcc) {
		return get(levelAcc).networks.stream().filter(network -> network.getUuid().equals(uuid)).findFirst().orElse(null);
	}
	
	public List<Network<?>> getNetworksForPlayer(Player player) {
		return networks.stream().filter(network -> {
			UUID ownerUUID = network.getOwner().getId();
			if (ownerUUID.equals(player.getUUID())) return true;
			var whitelist = network.getWhitelistedPlayers();
			return whitelist.contains(ownerUUID);
		}).toList();
	}
	
	public static TesseractNetworks get(LevelAccessor levelAcc) {
		if (levelAcc instanceof ServerLevelAccessor severLevelAcc) {
			return Objects.requireNonNull(severLevelAcc.getServer()).overworld().getDataStorage().computeIfAbsent(
				new SavedData.Factory<>(TesseractNetworks::new, TesseractNetworks::load, DataFixTypes.LEVEL),
				"tesseract_networks"
			);
		} else return null;
	}
}

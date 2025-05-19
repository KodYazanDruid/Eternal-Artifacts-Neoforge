package com.sonamorningstar.eternalartifacts.api.machine.tesseract;

import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
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
	private final Map<Network<?>, Set<Tesseract>> tesseracts = new HashMap<>();
	
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
	
	public void removeTesseractFromNetwork(Tesseract tesseract) {
		for (Network<?> network : networks) {
			if (tesseracts.containsKey(network)) {
				var tesseractSet = tesseracts.get(network);
				if (tesseractSet != null) {
					tesseractSet.remove(tesseract);
					if (tesseractSet.isEmpty()) {
						tesseracts.remove(network);
					}
				}
			}
		}
	}
	
	public void changeNetwork(Tesseract tesseract, UUID oldId, UUID newId) {
		Network<?> oldNetwork = TesseractNetworks.getNetwork(oldId, tesseract.getLevel());
		Network<?> newNetwork = TesseractNetworks.getNetwork(newId, tesseract.getLevel());
		
		if (oldNetwork != null) {
			var oldSet = tesseracts.get(oldNetwork);
			if (oldSet != null) {
				oldSet.remove(tesseract);
				if (oldSet.isEmpty()) {
					tesseracts.remove(oldNetwork);
				}
			}
		}
		
		if (newNetwork == null) return;
		var newSet = tesseracts.get(newNetwork);
		if (newSet != null) {
			newSet.add(tesseract);
		} else {
			Set<Tesseract> newTesseracts = new HashSet<>();
			newTesseracts.add(tesseract);
			tesseracts.put(newNetwork, newTesseracts);
		}
	}
	
	public Set<Tesseract> getTesseracts(Network<?> network) {
		return tesseracts.getOrDefault(network, new HashSet<>());
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

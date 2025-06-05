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
import java.util.function.Consumer;

@Getter
public class TesseractNetworks extends SavedData {
	private final Set<TesseractNetwork<?>> tesseractNetworks = new HashSet<>();
	private final Map<TesseractNetwork<?>, Set<Tesseract>> tesseracts = new HashMap<>();
	
	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag listTag = new ListTag();
		for (TesseractNetwork<?> tesseractNetwork : tesseractNetworks) {
			CompoundTag networkTag = new CompoundTag();
			tesseractNetwork.writeToNBT(networkTag);
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
			TesseractNetwork<?> tesseractNetwork = TesseractNetwork.fromNBT(networkTag);
			tesseractNetworks.add(tesseractNetwork);
		}
	}
	
	public void removeTesseractFromNetwork(Tesseract tesseract) {
		for (TesseractNetwork<?> tesseractNetwork : tesseractNetworks) {
			if (tesseracts.containsKey(tesseractNetwork)) {
				var tesseractSet = tesseracts.get(tesseractNetwork);
				if (tesseractSet != null) {
					tesseractSet.remove(tesseract);
					if (tesseractSet.isEmpty()) {
						tesseracts.remove(tesseractNetwork);
					}
				}
			}
		}
	}
	
	public void changeNetwork(Tesseract tesseract, UUID oldId, UUID newId) {
		TesseractNetwork<?> oldTesseractNetwork = TesseractNetworks.getNetwork(oldId, tesseract.getLevel());
		TesseractNetwork<?> newTesseractNetwork = TesseractNetworks.getNetwork(newId, tesseract.getLevel());
		
		if (oldTesseractNetwork != null) {
			var oldSet = tesseracts.get(oldTesseractNetwork);
			if (oldSet != null) {
				oldSet.remove(tesseract);
				if (oldSet.isEmpty()) {
					tesseracts.remove(oldTesseractNetwork);
				}
			}
		}
		
		if (newTesseractNetwork == null) return;
		var newSet = tesseracts.get(newTesseractNetwork);
		if (newSet != null) {
			newSet.add(tesseract);
		} else {
			Set<Tesseract> newTesseracts = new HashSet<>();
			newTesseracts.add(tesseract);
			tesseracts.put(newTesseractNetwork, newTesseracts);
		}
	}
	
	public Set<Tesseract> getTesseracts(TesseractNetwork<?> tesseractNetwork) {
		return tesseracts.getOrDefault(tesseractNetwork, new HashSet<>());
	}
	
	public boolean addNetwork(TesseractNetwork<?> tesseractNetwork) {
		boolean ret = tesseractNetworks.add(tesseractNetwork);
		setDirty();
		return ret;
	}
	
	public boolean removeNetwork(TesseractNetwork<?> tesseractNetwork) {
		boolean ret = tesseractNetworks.remove(tesseractNetwork);
		setDirty();
		return ret;
	}
	
	public boolean removeNetwork(UUID uuid) {
		boolean ret = tesseractNetworks.removeIf(network -> network.getUuid().equals(uuid));
		setDirty();
		return ret;
	}
	
	public void mutateNetwork(TesseractNetwork<?> tesseractNetwork, Consumer<TesseractNetwork<?>> consumer) {
		if (!tesseractNetworks.contains(tesseractNetwork)) return;
		consumer.accept(tesseractNetwork);
		setDirty();
	}
	public void mutateNetwork(int index, Consumer<TesseractNetwork<?>> consumer) {
		if (index < 0 || index >= tesseractNetworks.size()) return;
		consumer.accept(tesseractNetworks.stream().toList().get(index));
		setDirty();
	}
	
	@Nullable
	public static TesseractNetwork<?> getNetwork(String name, LevelAccessor levelAcc) {
		return get(levelAcc).tesseractNetworks.stream().filter(network -> network.getName().equals(name)).findFirst().orElse(null);
	}
	
	@Nullable
	public static TesseractNetwork<?> getNetwork(UUID uuid, LevelAccessor levelAcc) {
		return get(levelAcc).tesseractNetworks.stream().filter(network -> network.getUuid().equals(uuid)).findFirst().orElse(null);
	}
	
	public List<TesseractNetwork<?>> getNetworksForPlayer(Player player) {
		return tesseractNetworks.stream().filter(network -> {
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

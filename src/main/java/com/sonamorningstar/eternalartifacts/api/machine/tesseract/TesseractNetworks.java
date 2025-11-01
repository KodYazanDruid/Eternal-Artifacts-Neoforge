package com.sonamorningstar.eternalartifacts.api.machine.tesseract;

import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.tesseract.TesseractNetworksToClient;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class TesseractNetworks extends SavedData {
	private final Set<TesseractNetwork<?>> tesseractNetworks = ConcurrentHashMap.newKeySet();
	private final Map<TesseractNetwork<?>, Set<Tesseract>> tesseracts = new ConcurrentHashMap<>();
	private final Map<UUID, TesseractNetwork<?>> networkCache = new ConcurrentHashMap<>();
	private final Map<String, TesseractNetwork<?>> nameCache = new ConcurrentHashMap<>();
	
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
			addNetworkInternal(tesseractNetwork);
		}
	}
	
	private void addNetworkInternal(TesseractNetwork<?> network) {
		tesseractNetworks.add(network);
		networkCache.put(network.getUuid(), network);
		nameCache.put(network.getName(), network);
	}
	
	public void removeTesseractFromNetwork(Tesseract tesseract) {
		tesseracts.values().forEach(set -> set.remove(tesseract));
		tesseracts.values().removeIf(Set::isEmpty);
	}
	
	public void changeNetwork(Tesseract tesseract, @Nullable UUID oldId, @Nullable UUID newId) {
		if (oldId != null) {
			TesseractNetwork<?> oldNetwork = networkCache.get(oldId);
			if (oldNetwork != null) {
				var oldSet = tesseracts.get(oldNetwork);
				if (oldSet != null && oldSet.remove(tesseract) && oldSet.isEmpty()) {
					tesseracts.remove(oldNetwork);
				}
			}
		}
		
		if (newId == null) return;
		
		TesseractNetwork<?> newNetwork = networkCache.get(newId);
		if (newNetwork == null) return;
		
		tesseracts.computeIfAbsent(newNetwork, k -> ConcurrentHashMap.newKeySet()).add(tesseract);
	}
	
	public Set<Tesseract> getTesseracts(TesseractNetwork<?> tesseractNetwork) {
		return tesseracts.getOrDefault(tesseractNetwork, Collections.emptySet());
	}
	
	public boolean addNetwork(TesseractNetwork<?> tesseractNetwork) {
		boolean added = tesseractNetworks.add(tesseractNetwork);
		if (added) {
			addNetworkInternal(tesseractNetwork);
			setDirty();
		}
		return added;
	}
	
	public boolean removeNetwork(TesseractNetwork<?> tesseractNetwork) {
		boolean removed = tesseractNetworks.remove(tesseractNetwork);
		if (removed) {
			networkCache.remove(tesseractNetwork.getUuid());
			nameCache.remove(tesseractNetwork.getName());
			tesseracts.remove(tesseractNetwork);
			setDirty();
		}
		return removed;
	}
	
	public boolean removeNetwork(UUID uuid) {
		TesseractNetwork<?> network = networkCache.get(uuid);
		return network != null && removeNetwork(network);
	}
	
	public void mutateNetwork(TesseractNetwork<?> tesseractNetwork, Consumer<TesseractNetwork<?>> consumer) {
		if (!tesseractNetworks.contains(tesseractNetwork)) return;
		consumer.accept(tesseractNetwork);
		setDirty();
	}
	
	public void mutateNetwork(int index, Consumer<TesseractNetwork<?>> consumer) {
		List<TesseractNetwork<?>> list = tesseractNetworks.stream().toList();
		if (index < 0 || index >= list.size()) return;
		consumer.accept(list.get(index));
		setDirty();
	}
	
	@Nullable
	public static TesseractNetwork<?> getNetwork(String name, LevelAccessor levelAcc) {
		TesseractNetworks networks = get(levelAcc);
		return networks != null ? networks.nameCache.get(name) : null;
	}
	
	@Nullable
	public static TesseractNetwork<?> getNetwork(UUID uuid, LevelAccessor levelAcc) {
		TesseractNetworks networks = get(levelAcc);
		return networks != null ? networks.networkCache.get(uuid) : null;
	}
	
	public List<TesseractNetwork<?>> getNetworksForPlayer(Player player) {
		UUID playerUUID = player.getUUID();
		return tesseractNetworks.stream()
			.filter(network -> {
				UUID ownerUUID = network.getOwner().getId();
				if (ownerUUID.equals(playerUUID)) return true;
				return network.getWhitelistedPlayers().contains(playerUUID);
			})
			.toList();
	}
	
	@Nullable
	public static TesseractNetworks get(LevelAccessor levelAcc) {
		if (!(levelAcc instanceof ServerLevelAccessor severLevelAcc)) return null;
		var server = severLevelAcc.getServer();
		if (server == null) return null;
		return server.overworld().getDataStorage().computeIfAbsent(
			new SavedData.Factory<>(TesseractNetworks::new, TesseractNetworks::load, DataFixTypes.LEVEL),
			"tesseract_networks"
		);
	}
	
	public void syncToClient(TesseractNetwork<?> network, ServerPlayer player) {
		Channel.sendToPlayer(new TesseractNetworksToClient(network), player);
	}
}

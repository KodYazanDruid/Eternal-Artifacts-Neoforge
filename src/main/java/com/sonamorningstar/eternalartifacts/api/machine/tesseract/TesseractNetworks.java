package com.sonamorningstar.eternalartifacts.api.machine.tesseract;

import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import com.sonamorningstar.eternalartifacts.network.tesseract.TesseractNetworksToClient;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class TesseractNetworks extends SavedData {
	private final Set<TesseractNetwork<?>> tesseractNetworks = ConcurrentHashMap.newKeySet();
	private final Map<UUID, TesseractNetwork<?>> networkCache = new ConcurrentHashMap<>();
	private final Map<String, TesseractNetwork<?>> nameCache = new ConcurrentHashMap<>();
	
	//Empty on clients because client can't know information on different dimensions on the server.
	private final Map<TesseractNetwork<?>, Set<Tesseract>> tesseracts = new ConcurrentHashMap<>();
	
	private static final TesseractNetworks CLIENT_INSTANCE = new TesseractNetworks();
	
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
		syncToClient(tesseractNetworks.getTesseractNetworks());
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
			if (this != CLIENT_INSTANCE) syncToClient(tesseractNetworks);
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
			syncToClient(tesseractNetworks);
			setDirty();
		}
		return removed;
	}
	
	public boolean removeNetwork(UUID uuid) {
		TesseractNetwork<?> network = networkCache.get(uuid);
		boolean ret = network != null && removeNetwork(network);
		setDirty();
		return ret;
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
				if (network.getAccess() == TesseractNetwork.Access.PUBLIC) return true;
				UUID ownerUUID = network.getOwner().getId();
				if (ownerUUID.equals(playerUUID)) return true;
				return network.getWhitelistedPlayers().stream().anyMatch(p -> playerUUID.equals(p.getId()));
			}).toList();
	}
	
	@Nullable
	public static TesseractNetworks get(LevelAccessor levelAcc) {
		if (!(levelAcc instanceof ServerLevelAccessor severLevelAcc)) return CLIENT_INSTANCE;
		var server = severLevelAcc.getServer();
		if (server == null) return null;
		return server.overworld().getDataStorage().computeIfAbsent(
			new SavedData.Factory<>(TesseractNetworks::new, TesseractNetworks::load, DataFixTypes.LEVEL),
			"tesseract_networks"
		);
	}
	
	public static void syncToClient(Set<TesseractNetwork<?>> networks) {
		MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
		if (currentServer != null && currentServer.isRunning()) {
			currentServer.getPlayerList().getPlayers().forEach(player -> {
				Set<TesseractNetwork<?>> networksToSync = new HashSet<>();
				networks.stream()
					.filter(network -> {
						UUID ownerUUID = network.getOwner().getId();
						UUID playerUUID = player.getUUID();
						if (ownerUUID.equals(playerUUID)) return true;
						return network.getWhitelistedPlayers().stream().anyMatch(p -> playerUUID.equals(p.getId()));
					}).forEach(networksToSync::add);
				Channel.sendToPlayer(new TesseractNetworksToClient(networksToSync), player);
			});
		}
	}
	
	public static void applyOnClient(Set<TesseractNetwork<?>> networks) {
		CLIENT_INSTANCE.tesseractNetworks.clear();
		CLIENT_INSTANCE.networkCache.clear();
		CLIENT_INSTANCE.nameCache.clear();
		for (TesseractNetwork<?> network : networks) {
			CLIENT_INSTANCE.addNetwork(network);
		}
		ClientProxy.onTesseractNetworksUpdated(CLIENT_INSTANCE);
	}
}

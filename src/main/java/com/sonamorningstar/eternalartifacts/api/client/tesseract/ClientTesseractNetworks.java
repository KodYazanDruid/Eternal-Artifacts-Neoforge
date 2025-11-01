package com.sonamorningstar.eternalartifacts.api.client.tesseract;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.content.block.entity.Tesseract;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

@Getter
public class ClientTesseractNetworks {
	private static final Map<TesseractNetwork<?>, Set<Tesseract>> tesseractNetworks = new HashMap<>();
	
	public static void clear() {
		tesseractNetworks.clear();
	}
	
	public static void addNetwork(TesseractNetwork<?> network) {
		if (network == null) return;
		tesseractNetworks.putIfAbsent(network, new HashSet<>());
	}
	
	public static void removeNetwork(TesseractNetwork<?> network) {
		tesseractNetworks.remove(network);
	}
	
	public static void addTesseract(TesseractNetwork<?> network, Tesseract tesseract) {
		tesseractNetworks.computeIfAbsent(network, n -> new HashSet<>()).add(tesseract);
	}
	
	public static void removeTesseract(TesseractNetwork<?> network, Tesseract tesseract) {
		Set<Tesseract> set = tesseractNetworks.get(network);
		if (set != null) {
			set.remove(tesseract);
			//if (set.isEmpty()) tesseractNetworks.remove(network);
		}
	}
	
	public static Collection<Tesseract> getInWorld(ResourceKey<Level> levelKey) {
		List<Tesseract> result = new ArrayList<>();
		for (Set<Tesseract> list : tesseractNetworks.values()) {
			for (Tesseract t : list) {
				if (t.getLevel().dimension().equals(levelKey)) {
					result.add(t);
				}
			}
		}
		return result;
	}
	
	public static Set<Tesseract> getByNetwork(TesseractNetwork<?> network) {
		return tesseractNetworks.getOrDefault(network, Collections.emptySet());
	}
}

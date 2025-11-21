package com.sonamorningstar.eternalartifacts.core;

import com.mojang.blaze3d.platform.InputConstants;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

public class ModKeyMappings {
	public static List<KeyMapping> KEY_MAPPINGS = new ArrayList<>();
	
	public static final KeyMapping OPEN_MACHINE_CONFIG = registerKey("open_machine_config", InputConstants.Type.KEYSYM, InputConstants.KEY_C);
	
	private static KeyMapping registerKey(String name, InputConstants.Type type, int keyCode) {
		KeyMapping key = new KeyMapping(ModConstants.KEY.withSuffix(name), type, keyCode, ModConstants.KEY_ETAR_CATEGORY.toString());
		KEY_MAPPINGS.add(key);
		return key;
	}
}

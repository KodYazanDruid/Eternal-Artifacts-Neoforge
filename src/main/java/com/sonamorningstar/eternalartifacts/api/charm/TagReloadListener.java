package com.sonamorningstar.eternalartifacts.api.charm;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class TagReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        CharmType.itemCharmTypes.clear();
    }
}

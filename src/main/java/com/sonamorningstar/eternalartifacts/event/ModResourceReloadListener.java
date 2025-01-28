package com.sonamorningstar.eternalartifacts.event;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.client.gui.overlay.HammeringRecipeOverlay;
import com.sonamorningstar.eternalartifacts.content.item.HammerItem;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.HammeringModifier;
import com.sonamorningstar.eternalartifacts.util.LootTableHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModResourceReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        CharmType.itemCharmTypes.clear();
        RecipeCache.clearCache();

        ResourceLocation hammeringTags = new ResourceLocation(MODID, "loot_tables/hammering/tags");
        ResourceLocation hammeringBlocks = new ResourceLocation(MODID, "loot_tables/hammering/blocks");

        // Process tags
        Map<ResourceLocation, Resource> tagResources = manager.listResources(hammeringTags.getPath(), rl -> true);
        tagResources.forEach((rl, resource) -> {
            String path = rl.toString();
            String[] parts = path.split(":");
            String strippedPath = parts[1].substring("loot_tables/hammering/tags/".length(), parts[1].length() - ".json".length());
            String[] pathParts = strippedPath.split("/");
            String namespace = pathParts[0];
            String finalPath = strippedPath.substring(namespace.length() + 1);
            TagKey<Block> tagKey = BlockTags.create(new ResourceLocation(namespace, finalPath));
            HammerItem.gatheredTags.add(tagKey);
        });

        // Process blocks
        Map<ResourceLocation, Resource> blockResources = manager.listResources(hammeringBlocks.getPath(), rl -> true);
        blockResources.forEach((rl, resource) -> {
            String path = rl.toString();
            String[] parts = path.split(":");
            String strippedPath = parts[1].substring("loot_tables/hammering/blocks/".length(), parts[1].length() - ".json".length());
            String[] pathParts = strippedPath.split("/");
            String namespace = pathParts[0];
            String finalPath = strippedPath.substring(namespace.length() + 1);
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(namespace, finalPath));
            HammerItem.gatheredBlocks.add(block);
        });
    }
}

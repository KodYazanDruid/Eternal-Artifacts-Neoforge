package com.sonamorningstar.eternalartifacts.event;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import com.sonamorningstar.eternalartifacts.content.item.HammerItem;
import com.sonamorningstar.eternalartifacts.event.common.CommonEvents;
import net.minecraft.core.Holder;
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
        
        Harvester.hoe_tillables = BuiltInRegistries.ITEM.holders().map(Holder.Reference::value).filter(item -> Harvester.isCorrectTool(item.getDefaultInstance())).toArray(Item[]::new);
        Harvester.cachedLootTables.clear();
        BlockBreaker.cachedLootTables.clear();
        
        ResourceLocation hammeringTags = new ResourceLocation(MODID, "loot_tables/hammering/tags");
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

        ResourceLocation hammeringBlocks = new ResourceLocation(MODID, "loot_tables/hammering/blocks");
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
        
        ResourceLocation sludgeRefiner = new ResourceLocation(MODID, "loot_tables/sludge_refiner");
        Map<ResourceLocation, Resource> sludgeResources = manager.listResources(sludgeRefiner.getPath(), rl -> true);
        sludgeResources.forEach((rl, resource) -> {
            String path = rl.toString();
            String[] parts = path.split(":");
            String strippedPath = parts[1].substring("loot_tables/".length(), parts[1].length() - ".json".length());
            ResourceLocation finalRl = new ResourceLocation(parts[0], strippedPath);
            SludgeRefiner.SLUDGE_RESULTS.add(finalRl);
        });
        
        Recycler.isRecipeMapInitialized = false;
        Recycler.isBreakdownMapInitialized = false;
        Packer.isPackingMapInitialized = false;
        
        CommonEvents.setupTagBasedCauldronInteractions();
    }
}

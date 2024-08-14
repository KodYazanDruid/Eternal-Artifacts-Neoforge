package com.sonamorningstar.eternalartifacts.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModelProvider extends net.minecraft.data.models.ModelProvider {
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    public ModelProvider(PackOutput output) {
        super(output);
        this.blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<Block, BlockStateGenerator> map = Maps.newHashMap();
        Consumer<BlockStateGenerator> consumer = generator -> {
            Block block = generator.getBlock();
            BlockStateGenerator blockstategenerator = map.put(block, generator);
            if (blockstategenerator != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        };
        Map<ResourceLocation, Supplier<JsonElement>> map1 = Maps.newHashMap();
        Set<Item> set = Sets.newHashSet();
        BiConsumer<ResourceLocation, Supplier<JsonElement>> biconsumer = (resourceLocation, jsonElementSupplier) -> {
            Supplier<JsonElement> supplier = map1.put(resourceLocation, jsonElementSupplier);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
            }
        };
        Consumer<Item> consumer1 = set::add;
        new BlockModelGenerators(consumer, biconsumer, consumer1).run();
        new ItemModelGenerators(biconsumer).run();
        return CompletableFuture.allOf(
                this.saveCollection(output, map, block -> this.blockStatePathProvider.json(block.builtInRegistryHolder().key().location())),
                this.saveCollection(output, map1, this.modelPathProvider::json)
        );
    }

    private <T> CompletableFuture<?> saveCollection(CachedOutput pOutput, Map<T, ? extends Supplier<JsonElement>> objectToJsonMap, Function<T, Path> resolveObjectPath) {
        return CompletableFuture.allOf(objectToJsonMap.entrySet().stream().map(entry -> {
            Path path = resolveObjectPath.apply(entry.getKey());
            JsonElement jsonelement = entry.getValue().get();
            return DataProvider.saveStable(pOutput, jsonelement, path);
        }).toArray(CompletableFuture[]::new));
    }
}

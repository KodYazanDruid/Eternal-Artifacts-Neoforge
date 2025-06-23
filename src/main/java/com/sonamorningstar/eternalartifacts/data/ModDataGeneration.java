package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.data.loot.ModGlobalLootModifierProvider;
import com.sonamorningstar.eternalartifacts.data.loot.ModLootTableProvider;
import com.sonamorningstar.eternalartifacts.data.recipe.RecipeProvider;
import com.sonamorningstar.eternalartifacts.data.tag.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGeneration {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider = dataGenerator.getVanillaPack(true).addProvider(factory -> new BlockTagsProvider(packOutput, lookupProvider, existingFileHelper)).contentsGetter();
        
        /*DatapackBuiltinEntriesProvider datapackProv = new DatapackBuiltinEntriesProvider(packOutput, lookupProvider);
        dataGenerator.addProvider(event.includeServer(), datapackProv);*/
        //CompletableFuture<HolderLookup.Provider> datapackLookupProv = datapackProv.getRegistryProvider();
        dataGenerator.addProvider(true, new DatapackBuiltinEntriesProvider(packOutput, lookupProvider));
        
        dataGenerator.addProvider(event.includeClient(), new ModelProvider(packOutput));
        dataGenerator.addProvider(event.includeClient(), new BlockStateProvider(packOutput, existingFileHelper));
        dataGenerator.addProvider(event.includeClient(), new ItemModelProvider(packOutput, existingFileHelper));
        dataGenerator.addProvider(event.includeClient(), new LanguageProvider(packOutput, "en_us"));
        dataGenerator.addProvider(event.includeClient(), new LanguageProvider(packOutput, "tr_tr"));
        dataGenerator.addProvider(event.includeClient(), new SoundDefinitionsProvider(packOutput, existingFileHelper));
        dataGenerator.addProvider(event.includeClient(), new BlockModelProvider(packOutput, existingFileHelper));

        dataGenerator.addProvider(event.includeServer(), new ItemTagsProvider(packOutput, lookupProvider, blockTagProvider, existingFileHelper));
        dataGenerator.addProvider(event.includeServer(), new BlockTagsProvider(packOutput, lookupProvider, existingFileHelper));
        dataGenerator.addProvider(event.includeServer(), new FluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        dataGenerator.addProvider(event.includeServer(), new EntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        dataGenerator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput));
        dataGenerator.addProvider(event.includeServer(), new ModGlobalLootModifierProvider(packOutput));
        dataGenerator.addProvider(event.includeServer(), new RecipeProvider(packOutput));
        dataGenerator.addProvider(event.includeServer(), new DataMapProvider(packOutput, lookupProvider));
        dataGenerator.addProvider(event.includeServer(), new PaintingVariantTagsProvider(packOutput, lookupProvider, existingFileHelper));
        dataGenerator.addProvider(event.includeServer(), new SpellTagsProvider(packOutput, lookupProvider, existingFileHelper));
        dataGenerator.addProvider(event.includeServer(), new DamageTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
    }
}

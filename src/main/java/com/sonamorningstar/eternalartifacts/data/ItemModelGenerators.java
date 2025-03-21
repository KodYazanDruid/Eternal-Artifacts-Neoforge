package com.sonamorningstar.eternalartifacts.data;

import com.google.gson.JsonElement;
import com.sonamorningstar.eternalartifacts.content.item.base.SpellTomeItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModModelTemplates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemModelGenerators extends net.minecraft.data.models.ItemModelGenerators {
    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> output;

    public ItemModelGenerators(BiConsumer<ResourceLocation, Supplier<JsonElement>> output) {
        super(output);
        this.output = output;
    }

    @Override
    public void run() {
        createBEWLRTransforms(ModBlocks.JAR);
        createBEWLRTransforms(ModBlocks.NOUS_TANK);
        createBEWLRTransforms(ModBlocks.FLUID_COMBUSTION_DYNAMO);
        createBEWLRTransforms(ModMachines.OIL_REFINERY.getItem());
        createBEWLRTransforms(ModBlocks.ENERGY_DOCK);
        useParent(ModBlocks.DROWNED_HEAD, ModelTemplates.SKULL_INVENTORY);
        useParent(ModBlocks.HUSK_HEAD, ModelTemplates.SKULL_INVENTORY);
        useParent(ModBlocks.STRAY_SKULL, ModelTemplates.SKULL_INVENTORY);
        useParent(ModBlocks.BLAZE_HEAD, ModelTemplates.SKULL_INVENTORY);

        createBEWLRTransforms(ModItems.FIREBALL_TOME);
        createSpellTome(ModItems.EVOKERS_TOME);
        createSpellTome(ModItems.TORNADO_TOME);
    }

    private void createBEWLRTransforms(ItemLike itemLike) {
        ResourceLocation texture = BuiltInRegistries.ITEM.getKey(itemLike.asItem()).withPrefix("item/");
        ModModelTemplates.ENTITY_RENDER_TRANSFORMS.create(ModelLocationUtils.getModelLocation(itemLike.asItem()), TextureMapping.defaultTexture(texture), output);
    }
    
    private void useParent(ItemLike itemLike, ModelTemplate parent) {
        ResourceLocation texture = BuiltInRegistries.ITEM.getKey(itemLike.asItem()).withPrefix("item/");
        parent.create(ModelLocationUtils.getModelLocation(itemLike.asItem()), TextureMapping.defaultTexture(texture), output);
    }

    private void createSpellTome(DeferredHolder<Item, ? extends SpellTomeItem<?>> holder) {
        ResourceLocation texture = BuiltInRegistries.ITEM.getKey(holder.get()).withPrefix("item/");
        ModModelTemplates.SPELL_TOME.create(ModelLocationUtils.getModelLocation(holder.get()), TextureMapping.defaultTexture(texture), output);
    }
}

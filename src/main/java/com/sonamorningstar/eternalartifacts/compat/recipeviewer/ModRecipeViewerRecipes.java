package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

import com.sonamorningstar.eternalartifacts.compat.recipeviewer.recipes.CauldronRecipe;
import com.sonamorningstar.eternalartifacts.compat.recipeviewer.recipes.InWorldRecipe;
import com.sonamorningstar.eternalartifacts.content.block.BluePlasticCauldronBlock;
import com.sonamorningstar.eternalartifacts.content.block.NaphthaCauldronBlock;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.Tags;

import java.util.stream.Stream;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

/**
 * Provides recipes for recipe viewers.
 * Add your in-world and cauldron recipes here.
 */
public final class ModRecipeViewerRecipes {
    
    private ModRecipeViewerRecipes() {}
    
    /**
     * Register all recipes to the registry.
     * Call this after categories are registered.
     */
    public static void registerAll() {
        registerInWorldRecipes();
        registerCauldronRecipes();
    }
    
    private static void registerInWorldRecipes() {
        Ingredient metalIngots = Ingredient.fromValues(Stream.of(
            new Ingredient.TagValue(Tags.Items.INGOTS_IRON),
            new Ingredient.TagValue(ModTags.Items.INGOTS_MANGANESE)
        ));
        
        RecipeViewerRegistry.registerRecipe(
            InWorldRecipe.throwCombineInContainer(
                new ResourceLocation(MODID, "in_world/steel_crafting_coal_dust"),
                ModRecipeViewerCategories.IN_WORLD_ID,
                Blocks.CAULDRON.defaultBlockState(),
                ModItems.STEEL_INGOT.get().getDefaultInstance(),
                metalIngots,
                SizedIngredient.of(ModTags.Items.DUSTS_COAL, 2).toIngredient()
            ).requireBlockAt(
                InWorldRecipe.ConditionType.BLOCK_BELOW,
                Component.translatable("recipe."+MODID+".in_world.steel_crafting"),
                Blocks.BLAST_FURNACE.defaultBlockState().setValue(BlockStateProperties.LIT, true),
                Blocks.MAGMA_BLOCK.defaultBlockState()
            )
        );
        
        RecipeViewerRegistry.registerRecipe(
            InWorldRecipe.throwCombineInContainer(
                new ResourceLocation(MODID, "in_world/steel_crafting_charcoal_dust"),
                ModRecipeViewerCategories.IN_WORLD_ID,
                Blocks.CAULDRON.defaultBlockState(),
                ModItems.STEEL_INGOT.get().getDefaultInstance(),
                metalIngots,
                SizedIngredient.of(ModTags.Items.DUSTS_CHARCOAL, 4).toIngredient()
            ).requireBlockAt(
                InWorldRecipe.ConditionType.BLOCK_BELOW,
                Component.translatable("recipe."+MODID+".in_world.steel_crafting"),
                Blocks.BLAST_FURNACE.defaultBlockState().setValue(BlockStateProperties.LIT, true),
                Blocks.MAGMA_BLOCK.defaultBlockState()
            )
        );
        
        RecipeViewerRegistry.registerRecipe(
            InWorldRecipe.throwCombineInContainer(
                new ResourceLocation(MODID, "in_world/steel_crafting_sugar_charcoal_dust"),
                ModRecipeViewerCategories.IN_WORLD_ID,
                Blocks.CAULDRON.defaultBlockState(),
                ModItems.STEEL_INGOT.get().getDefaultInstance(),
                metalIngots,
                SizedIngredient.of(ModTags.Items.DUSTS_SUGAR_CHARCOAL, 8).toIngredient()
            ).requireBlockAt(
                InWorldRecipe.ConditionType.BLOCK_BELOW,
                Component.translatable("recipe."+MODID+".in_world.steel_crafting"),
                Blocks.BLAST_FURNACE.defaultBlockState().setValue(BlockStateProperties.LIT, true),
                Blocks.MAGMA_BLOCK.defaultBlockState()
            )
        );
        
        RecipeViewerRegistry.registerRecipe(
            InWorldRecipe.throwCombineInContainer(
                new ResourceLocation(MODID, "in_world/demon_ingot"),
                ModRecipeViewerCategories.IN_WORLD_ID,
                Blocks.LAVA.defaultBlockState(),
                ModItems.DEMON_INGOT.get().getDefaultInstance(),
                Ingredient.of(Tags.Items.INGOTS_GOLD)
            ).requireBlockAt(InWorldRecipe.ConditionType.BLOCK_SURROUNDING,
                Component.translatable("recipe."+MODID+".in_world.demon_ingot"),
                Blocks.NETHER_BRICKS.defaultBlockState())
        );
        RecipeViewerRegistry.registerRecipe(
            InWorldRecipe.throwCombineInContainer(
                new ResourceLocation(MODID, "in_world/demon_block"),
                ModRecipeViewerCategories.IN_WORLD_ID,
                Blocks.LAVA.defaultBlockState(),
                ModBlocks.DEMON_BLOCK.get().asItem().getDefaultInstance(),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD)
            ).requireBlockAt(InWorldRecipe.ConditionType.BLOCK_SURROUNDING,
                Component.translatable("recipe."+MODID+".in_world.demon_ingot"),
                Blocks.NETHER_BRICKS.defaultBlockState())
        );
    }
    
    private static void registerCauldronRecipes() {
        RecipeViewerRegistry.registerRecipe(CauldronRecipe.transformCauldron(
            new ResourceLocation(MODID, "cauldron/naphtha_to_plastic"),
            ModRecipeViewerCategories.CAULDRON_ID,
            SizedIngredient.of(Tags.Items.SAND, 16).toIngredient(),
            ModBlocks.NAPHTHA_CAULDRON.get().defaultBlockState().setValue(NaphthaCauldronBlock.LEVEL, NaphthaCauldronBlock.MAX_LEVEL),
            ModBlocks.PLASTIC_CAULDRON.get().defaultBlockState()
        ));
        RecipeViewerRegistry.registerRecipe(CauldronRecipe.transformCauldron(
            new ResourceLocation(MODID, "cauldron/dye_blue"),
            ModRecipeViewerCategories.CAULDRON_ID,
            Ingredient.of(Tags.Items.DYES_BLUE),
            ModBlocks.PLASTIC_CAULDRON.get().defaultBlockState(),
            ModBlocks.BLUE_PLASTIC_CAULDRON.get().defaultBlockState().setValue(BluePlasticCauldronBlock.LEVEL, BluePlasticCauldronBlock.MAX_LEVEL)
        ));
    }
}

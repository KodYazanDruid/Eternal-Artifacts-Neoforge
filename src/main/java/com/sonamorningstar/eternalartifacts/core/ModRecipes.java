package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.recipe.*;
import com.sonamorningstar.eternalartifacts.content.recipe.container.ItemFluidContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleEntityContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.registrar.RecipeDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.RecipeDeferredRegister;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModRecipes {
    public static final RecipeDeferredRegister RECIPES = new RecipeDeferredRegister(MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedRetexturedRecipe>> SHAPED_RETEXTURED_SERIALIZER =
            RECIPES.registerSerializer("shaped_retextured_recipe", ShapedRetexturedRecipe.Serializer::new);

    public static final RecipeDeferredHolder<SimpleContainer, MeatShredderRecipe> MEAT_SHREDDING = RECIPES.register("meat_shredding", MeatShredderRecipe.Serializer::new);
    public static final RecipeDeferredHolder<SimpleEntityContainer, MobLiquifierRecipe> MOB_LIQUIFYING = RECIPES.register("mob_liquifying", MobLiquifierRecipe.Serializer::new);
    public static final RecipeDeferredHolder<SimpleFluidContainer, FluidCombustionRecipe> FLUID_COMBUSTING = RECIPES.register("fluid_combusting", FluidCombustionRecipe.Serializer::new);
    public static final RecipeDeferredHolder<ItemFluidContainer, FluidInfuserRecipe> FLUID_INFUSING = RECIPES.register("fluid_infusing", FluidInfuserRecipe.Serializer::new);
    public static final RecipeDeferredHolder<SimpleContainer, MeltingRecipe> MELTING = RECIPES.register("melting", MeltingRecipe.Serializer::new);
    public static final RecipeDeferredHolder<SimpleContainer, MaceratingRecipe> MACERATING = RECIPES.register("macerating", MaceratingRecipe.Serializer::new);
    public static final RecipeDeferredHolder<SimpleContainer, SqueezingRecipe> SQUEEZING = RECIPES.register("squeezing", SqueezingRecipe.Serializer::new);
    public static final RecipeDeferredHolder<SimpleContainer, AlloyingRecipe> ALLOYING = RECIPES.register("alloying", AlloyingRecipe.Serializer::new);

}
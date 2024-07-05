package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModIngredientTypes {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, MODID);
    
}

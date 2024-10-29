package com.sonamorningstar.eternalartifacts.content.recipe.ingredient;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class SizedIngredient extends Ingredient {
    protected SizedIngredient(Stream<? extends Value> pValues) {
        super(pValues);
    }
}

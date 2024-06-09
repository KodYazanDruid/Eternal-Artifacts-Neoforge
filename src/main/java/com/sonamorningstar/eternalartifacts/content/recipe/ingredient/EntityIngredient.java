package com.sonamorningstar.eternalartifacts.content.recipe.ingredient;

import net.minecraft.world.entity.EntityType;

import java.util.function.Predicate;

public class EntityIngredient implements Predicate<EntityType<?>> {
    @Override
    public boolean test(EntityType<?> entityType) {
        return false;
    }
}

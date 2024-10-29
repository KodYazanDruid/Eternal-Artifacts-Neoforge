package com.sonamorningstar.eternalartifacts.compat.jei;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public final class ModTypes {
    public static final IIngredientTypeWithSubtypes<EntityType<?>, LivingEntity> ENTITY = new IIngredientTypeWithSubtypes<>() {
        @Override
        public Class<? extends LivingEntity> getIngredientClass() {
            return LivingEntity.class;
        }

        @Override
        public Class<? extends EntityType<?>> getIngredientBaseClass() {return (Class<EntityType<?>>) (Object) EntityType.class;}

        @Override
        public EntityType<?> getBase(LivingEntity living) {
            return living.getType();
        }
    };

    public static final IIngredientType<LivingEntity> LIVING_ENTITY = () -> LivingEntity.class;
}

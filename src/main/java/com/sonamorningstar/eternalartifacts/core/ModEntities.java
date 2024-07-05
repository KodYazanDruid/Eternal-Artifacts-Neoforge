package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.entity.DemonEyeEntity;
import com.sonamorningstar.eternalartifacts.content.entity.DuckEntity;
import com.sonamorningstar.eternalartifacts.content.entity.MagicalBookEntity;
import com.sonamorningstar.eternalartifacts.content.entity.PinkyEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<DemonEyeEntity>> DEMON_EYE = ENTITY_TYPES.register("demon_eye",
            ()-> EntityType.Builder.of(DemonEyeEntity::new, MobCategory.MONSTER).sized(0.5f, 0.5f).build("demon_eye"));
    public static final DeferredHolder<EntityType<?>, EntityType<PinkyEntity>> PINKY = ENTITY_TYPES.register("pinky",
            ()-> EntityType.Builder.of(PinkyEntity::new, MobCategory.MONSTER).sized(0.5f, 0.5f).build("pinky"));
    public static final DeferredHolder<EntityType<?>, EntityType<MagicalBookEntity>> MAGICAL_BOOK = ENTITY_TYPES.register("magical_book",
            ()-> EntityType.Builder.of(MagicalBookEntity::new, MobCategory.MONSTER).sized(0.75f, 0.75f).build("magical_book"));

    public static final DeferredHolder<EntityType<?>, EntityType<DuckEntity>> DUCK = ENTITY_TYPES.register("duck",
            ()-> EntityType.Builder.of(DuckEntity::new, MobCategory.CREATURE).sized(0.4f, 0.7f).clientTrackingRange(10).build("duck"));

}

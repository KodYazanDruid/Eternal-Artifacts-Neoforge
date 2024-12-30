package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.entity.*;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Meteorite;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Missile;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Tornado;
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
public static final DeferredHolder<EntityType<?>, EntityType<ChargedSheepEntity>> CHARGED_SHEEP = ENTITY_TYPES.register("charged_sheep",
            ()-> EntityType.Builder.of(ChargedSheepEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10).build("charged_sheep"));

    public static final DeferredHolder<EntityType<?>, EntityType<PrimedBlockEntity>> PRIMED_BLOCK = ENTITY_TYPES.register("primed_drum",
            ()-> EntityType.Builder.<PrimedBlockEntity>of(PrimedBlockEntity::new, MobCategory.MISC).fireImmune().sized(0.98f, 0.98f).clientTrackingRange(10).updateInterval(10).build("primed_drum"));

    //Projectiles
    public static final DeferredHolder<EntityType<?>, EntityType<Tornado>> TORNADO = ENTITY_TYPES.register("tornado",
            ()-> EntityType.Builder.<Tornado>of(Tornado::new, MobCategory.MISC).fireImmune().sized(1.0f, 2.0f).clientTrackingRange(4).updateInterval(10).build("tornado"));
    public static final DeferredHolder<EntityType<?>, EntityType<Meteorite>> METEORITE = ENTITY_TYPES.register("meteorite",
            ()-> EntityType.Builder.<Meteorite>of(Meteorite::new, MobCategory.MISC).fireImmune().sized(2.0f, 2.0f).canSpawnFarFromPlayer().clientTrackingRange(1).updateInterval(10).build("meteorite"));
    public static final DeferredHolder<EntityType<?>, EntityType<Missile>> MISSILE = ENTITY_TYPES.register("missile",
            ()-> EntityType.Builder.<Missile>of(Missile::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).clientTrackingRange(1).updateInterval(10).build("meteorite"));

}

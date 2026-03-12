package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.entity.*;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.*;
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
    public static final DeferredHolder<EntityType<?>, EntityType<HoneySlime>> HONEY_SLIME = ENTITY_TYPES.register("honey_slime",
        ()-> EntityType.Builder.of(HoneySlime::new, MobCategory.MONSTER).sized(2.04F, 2.04F).build("honey_slime"));
    
    public static final DeferredHolder<EntityType<?>, EntityType<DuckEntity>> DUCK = ENTITY_TYPES.register("duck",
        ()-> EntityType.Builder.of(DuckEntity::new, MobCategory.CREATURE).sized(0.4f, 0.7f).clientTrackingRange(10).build("duck"));
    public static final DeferredHolder<EntityType<?>, EntityType<ChargedSheepEntity>> CHARGED_SHEEP = ENTITY_TYPES.register("charged_sheep",
        ()-> EntityType.Builder.of(ChargedSheepEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10).build("charged_sheep"));

    public static final DeferredHolder<EntityType<?>, EntityType<PrimedBlockEntity>> PRIMED_BLOCK = ENTITY_TYPES.register("primed_drum",
        ()-> EntityType.Builder.<PrimedBlockEntity>of(PrimedBlockEntity::new, MobCategory.MISC).fireImmune().sized(0.98f, 0.98f).clientTrackingRange(10).updateInterval(10).build("primed_drum"));
    
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownLightningInABottleItem>> THROWN_LIGHTNING_IN_A_BOTTLE = ENTITY_TYPES.register("thrown_lightning_in_a_bottle",
            ()-> EntityType.Builder.<ThrownLightningInABottleItem>of(ThrownLightningInABottleItem::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("primed_drum"));

    //Projectiles
    public static final DeferredHolder<EntityType<?>, EntityType<Tornado>> TORNADO = ENTITY_TYPES.register("tornado",
            ()-> EntityType.Builder.<Tornado>of(Tornado::new, MobCategory.MISC).fireImmune().sized(1.0f, 2.0f).clientTrackingRange(4).updateInterval(20).build("tornado"));
    public static final DeferredHolder<EntityType<?>, EntityType<Meteorite>> METEORITE = ENTITY_TYPES.register("meteorite",
            ()-> EntityType.Builder.<Meteorite>of(Meteorite::new, MobCategory.MISC).fireImmune().sized(2.0f, 2.0f).canSpawnFarFromPlayer().clientTrackingRange(4).updateInterval(20).build("meteorite"));
    public static final DeferredHolder<EntityType<?>, EntityType<Missile>> MISSILE = ENTITY_TYPES.register("missile",
            ()-> EntityType.Builder.<Missile>of(Missile::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build("missile"));
    public static final DeferredHolder<EntityType<?>, EntityType<SpellSmallFireball>> SPELL_SMALL_FIREBALL = ENTITY_TYPES.register("spell_small_fireball",
            ()-> EntityType.Builder.<SpellSmallFireball>of(SpellSmallFireball::new, MobCategory.MISC).fireImmune().sized(0.3125f, 0.3125f).clientTrackingRange(4).updateInterval(10).build("spell_small_fireball"));
    public static final DeferredHolder<EntityType<?>, EntityType<SpellShulkerBullet>> SPELL_SHULKER_BULLET = ENTITY_TYPES.register("spell_shulker_bullet",
            ()-> EntityType.Builder.<SpellShulkerBullet>of(SpellShulkerBullet::new, MobCategory.MISC).fireImmune().sized(0.3125f, 0.3125f).clientTrackingRange(8).updateInterval(3).build("spell_shulker_bullet"));
    public static final DeferredHolder<EntityType<?>, EntityType<SpellEvokerFangs>> SPELL_EVOKER_FANGS = ENTITY_TYPES.register("spell_evoker_fangs",
            ()-> EntityType.Builder.<SpellEvokerFangs>of(SpellEvokerFangs::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.8f).clientTrackingRange(6).updateInterval(2).build("spell_evoker_fangs"));
    public static final DeferredHolder<EntityType<?>, EntityType<SpellProjectile>> SPELL_PROJECTILE = ENTITY_TYPES.register("spell_projectile",
        ()-> EntityType.Builder.of(SpellProjectile::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build("spell_projectile"));
    public static final DeferredHolder<EntityType<?>, EntityType<AmethystArrow>> AMETHYST_ARROW = ENTITY_TYPES.register("amethyst_arrow",
        ()-> EntityType.Builder.<AmethystArrow>of(AmethystArrow::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build("amethyst_arrow"));
    public static final DeferredHolder<EntityType<?>, EntityType<PrismarineArrow>> PRISMARINE_ARROW = ENTITY_TYPES.register("prismarine_arrow",
        ()-> EntityType.Builder.<PrismarineArrow>of(PrismarineArrow::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build("prismarine_arrow"));
    public static final DeferredHolder<EntityType<?>, EntityType<SonicBoomSpellEntity>> SPELL_SONIC_BOOM = ENTITY_TYPES.register("spell_sonic_boom",
        ()-> EntityType.Builder.<SonicBoomSpellEntity>of(SonicBoomSpellEntity::new, MobCategory.MISC).fireImmune().sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(2).build("spell_sonic_boom"));
    public static final DeferredHolder<EntityType<?>, EntityType<MagicMissileEntity>> MAGIC_MISSILE = ENTITY_TYPES.register("magic_missile",
        ()-> EntityType.Builder.<MagicMissileEntity>of(MagicMissileEntity::new, MobCategory.MISC).fireImmune().sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("magic_missile"));
    public static final DeferredHolder<EntityType<?>, EntityType<PrismBeamEntity>> PRISM_BEAM = ENTITY_TYPES.register("prism_beam",
        ()-> EntityType.Builder.<PrismBeamEntity>of(PrismBeamEntity::new, MobCategory.MISC).fireImmune().sized(0.25f, 0.25f).clientTrackingRange(10).updateInterval(1).build("prism_beam"));
    public static final DeferredHolder<EntityType<?>, EntityType<LightningStrikeProjectile>> LIGHTNING_STRIKE_PROJECTILE = ENTITY_TYPES.register("lightning_strike_projectile",
        ()-> EntityType.Builder.<LightningStrikeProjectile>of(LightningStrikeProjectile::new, MobCategory.MISC).fireImmune().sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("lightning_strike_projectile"));

}

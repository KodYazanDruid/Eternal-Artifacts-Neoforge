package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.spell.*;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell.DamageCategory;
import com.sonamorningstar.eternalartifacts.content.spell.WitherSkullSpell;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModSpells {
    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(ModRegistries.Keys.SPELL, MODID);

    public static final DeferredHolder<Spell, FireballSpell> FIREBALL = register("fireball",
        () -> new FireballSpell(new Spell.Properties().rarity(Rarity.COMMON).cooldown(20).baseDamage(1.5F)));
    public static final DeferredHolder<Spell, EvokerFangsSpell> EVOKER_FANGS = register("evoker_fangs",
        () -> new EvokerFangsSpell(new Spell.Properties().rarity(Rarity.EPIC).cooldown(40).baseDamage(8.0F)));
    public static final DeferredHolder<Spell, TornadoSpell> TORNADO = register("tornado",
        () -> new TornadoSpell(new Spell.Properties().rarity(Rarity.RARE).cooldown(30).baseDamage(2.0F)));
    public static final DeferredHolder<Spell, ShulkerBulletsSpell> SHULKER_BULLETS = register("shulker_bullets",
        () -> new ShulkerBulletsSpell(new Spell.Properties().rarity(Rarity.RARE).cooldown(40).baseDamage(3.0F)));
    public static final DeferredHolder<Spell, MeteoriteSpell> METEORITE = register("meteorite",
        () -> new MeteoriteSpell(new Spell.Properties().rarity(Rarity.EPIC).cooldown(200).baseDamage(20.0F)));
    public static final DeferredHolder<Spell, SonicBoomSpell> SONIC_BOOM = register("sonic_boom",
        () -> new SonicBoomSpell(new Spell.Properties().rarity(Rarity.EPIC).cooldown(60).baseDamage(10.0F).damageCategory(DamageCategory.ARMOR_PIERCING)));
    public static final DeferredHolder<Spell, MagicMissileSpell> MAGIC_MISSILE = register("magic_missile",
        () -> new MagicMissileSpell(new Spell.Properties().rarity(Rarity.COMMON).cooldown(40).baseDamage(1.0F)));
    public static final DeferredHolder<Spell, PrismBeamSpell> PRISM_BEAM = register("prism_beam",
        () -> new PrismBeamSpell(new Spell.Properties().rarity(Rarity.EPIC).cooldown(100).baseDamage(2.0F)));
    public static final DeferredHolder<Spell, LightningStrikeSpell> LIGHTNING_STRIKE = register("lightning_strike",
        () -> new LightningStrikeSpell(new Spell.Properties().rarity(Rarity.RARE).cooldown(50).baseDamage(4.0F)));
    public static final DeferredHolder<Spell, DivineProtectionSpell> DIVINE_PROTECTION = register("divine_protection",
        () -> new DivineProtectionSpell(new Spell.Properties().rarity(Rarity.RARE).cooldown(600).baseHealing(8.0f).healingRadius(5.0f)));
    public static final DeferredHolder<Spell, WitherSkullSpell> WITHER_SKULL = register("wither_skull",
        () -> new WitherSkullSpell(new Spell.Properties().rarity(Rarity.UNCOMMON).cooldown(20).baseDamage(4.0F)));

    public static final DeferredHolder<Spell, BlackHoleSpell> BLACK_HOLE = register("black_hole",
        () -> new BlackHoleSpell(new Spell.Properties().rarity(Rarity.EPIC).cooldown(300).baseDamage(4.0F)));
    
    private static <S extends Spell> DeferredHolder<Spell, S> register(String name, Function<Spell.Properties, S> props, float damage) {
        return register(name, () -> props.apply(new Spell.Properties().baseDamage(damage)));
    }
    private static <S extends Spell> DeferredHolder<Spell, S> register(String name, Function<Spell.Properties, S> props) {
        return SPELLS.register(name, () -> props.apply(new Spell.Properties()));
    }
    private static <S extends Spell> DeferredHolder<Spell, S> register(String name, Supplier<S> supp) {
        return SPELLS.register(name, supp);
    }
}

package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.spell.*;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModSpells {
    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(ModRegistries.Keys.SPELL, MODID);

    public static final DeferredHolder<Spell, FireballSpell> FIREBALL = register("fireball", FireballSpell::new, 6.0F);
    public static final DeferredHolder<Spell, EvokerFangsSpell> EVOKER_FANGS = register("evoker_fangs",
            () -> new EvokerFangsSpell(new Spell.Properties().rarity(Rarity.EPIC).cooldown(40).baseDamage(8.0F)));
    public static final DeferredHolder<Spell, TornadoSpell> TORNADO = register("tornado", TornadoSpell::new, 2.0F);
    public static final DeferredHolder<Spell, ShulkerBulletsSpell> SHULKER_BULLETS = register("shulker_bullets", ShulkerBulletsSpell::new, 2.0F);
    public static final DeferredHolder<Spell, MeteoriteSpell> METEORITE = register("meteorite",
            () -> new MeteoriteSpell(new Spell.Properties().rarity(Rarity.EPIC).cooldown(200).baseDamage(20.0F)));


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

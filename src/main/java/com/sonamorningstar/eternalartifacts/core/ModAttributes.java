package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, MODID);

    public static final DeferredHolder<Attribute, Attribute> SPELL_DAMAGE = register("spell_damage", 100D, 0D, 2048D);
    public static final DeferredHolder<Attribute, Attribute> COOLDOWN_REDUCTION = register("cooldown_reduction", 0D, 0D, 100D);

    private static DeferredHolder<Attribute, Attribute> register(String name, double defaultValue, double min, double max) {
        return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute."+MODID+"."+name, defaultValue, min, max).setSyncable(true));
    }
}
package com.sonamorningstar.eternalartifacts.content.spell.base;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.core.ModAttributes;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Spell {
    public static final Codec<Spell> CODEC = ModRegistries.SPELL.byNameCodec();
    public static final AttributeModifier spellDamage = new AttributeModifier(UUID.fromString("92347df7-fa0d-489a-b38b-0e0911746b5c"), ModConstants.withId("dummy_spell_damage"), 10, AttributeModifier.Operation.ADDITION);

    @Getter
    private final Supplier<ResourceKey<Spell>> key = Suppliers.memoize(() -> ModRegistries.SPELL.getResourceKey(this).get());

    public final Rarity rarity;
    public final int maxCharges;
    public final int cooldown;
    public final float baseDamage;

    public Spell(Spell.Properties props) {
        this.rarity = props.rarity;
        this.maxCharges = props.maxCharges;
        this.cooldown = props.cooldown;
        this.baseDamage = props.baseDamage;
    }

    /**
     * Casts a spell using the provided item stack, level, caster.
     *
     * @param stack      The item stack used to cast the spell.
     * @param level      The level where the spell is cast.
     * @param caster     The caster who casts the spell.
     * @return {@code true} if the spell was successfully cast, {@code false} otherwise.
     */
    public boolean cast(ItemStack stack, Level level, LivingEntity caster, RandomSource random, float amplifiedDamage) {
        return false;
    }

    /**
     * Calculates the amplified damage for the spell based on the caster's attributes.
     *
     * @param caster The entity casting the spell.
     * @return The amplified damage value.
     * @deprecated This method is deprecated because you shouldn't be calling it.
     *             Instead, use the argument from the {@link Spell#cast(ItemStack, Level, LivingEntity, float)} method.
     *             It is fine to override this method if you need to change the spell damage calculation logic.
     */
    @Deprecated
    public float getAmplifiedDamage(LivingEntity caster) {
        AttributeInstance spellDamage = caster.getAttribute(ModAttributes.SPELL_DAMAGE.get());
        if (spellDamage == null) return baseDamage;
        double amp = spellDamage.getValue();
        return (float) (baseDamage * amp / 100);
    }

    public boolean is(Spell spell) {
        return this == spell;
    }
    public boolean is(TagKey<Spell> spell) {
        Optional<Holder.Reference<Spell>> holder = ModRegistries.SPELL.getHolder(key.get());
        return holder.isPresent() && holder.get().is(spell);
    }
    public boolean is(Predicate<Spell> spell) {
        return spell.test(this);
    }
    public boolean is(DeferredHolder<Spell, ? extends Spell> spell) {
        return this == spell.get();
    }

    public static class Properties {
        Rarity rarity = Rarity.COMMON;
        int maxCharges = 1;
        int cooldown = 0;
        float baseDamage = 0;

        public Properties rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Properties maxCharges(int maxCharges) {
            this.maxCharges = maxCharges;
            return this;
        }

        public Properties cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public Properties baseDamage(float baseDamage) {
            this.baseDamage = baseDamage;
            return this;
        }
    }
}

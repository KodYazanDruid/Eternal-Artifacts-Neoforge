package com.sonamorningstar.eternalartifacts.content.spell.base;

import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.core.ModAttributes;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class Spell {
    public static final Codec<Spell> CODEC = ModRegistries.SPELL.byNameCodec();
    public static final AttributeModifier spellDamage = new AttributeModifier(UUID.fromString("92347df7-fa0d-489a-b38b-0e0911746b5c"), ModConstants.withId("dummy_spell_damage"), 10, AttributeModifier.Operation.ADDITION);

    public final Rarity rarity;
    public final int maxCharges;
    public final int cooldown;

    public Spell(Spell.Properties props) {
        this.rarity = props.rarity;
        this.maxCharges = props.maxCharges;
        this.cooldown = props.cooldown;;
    }

    /**
     * Casts a spell using the provided item stack, level, caster.
     *
     * @param stack      The item stack used to cast the spell.
     * @param level      The level where the spell is cast.
     * @param caster     The caster who casts the spell.
     * @return {@code true} if the spell was successfully cast, {@code false} otherwise.
     */
    public boolean cast(ItemStack stack, Level level, LivingEntity caster) {
        return false;
    }

    public float applySpellDamage(LivingEntity caster, float baseDamage) {
        AttributeInstance spellDamage = caster.getAttribute(ModAttributes.SPELL_DAMAGE.get());
        if (spellDamage == null) return baseDamage;
        double amp = spellDamage.getValue();
        return (float) (baseDamage * amp / 100);
    }

    public static class Properties {
        Rarity rarity = Rarity.COMMON;
        int maxCharges = 1;
        int cooldown = 0;

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
    }
}

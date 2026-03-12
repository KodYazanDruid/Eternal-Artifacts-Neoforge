package com.sonamorningstar.eternalartifacts.content.spell.base;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.core.ModAttributes;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Spell {
    public static final Codec<Spell> CODEC = ModRegistries.SPELL.byNameCodec();

    @Getter
    private final Supplier<ResourceKey<Spell>> key = Suppliers.memoize(() -> ModRegistries.SPELL.getResourceKey(this).get());
    @Getter
    private final Supplier<String> descriptionId = Suppliers.memoize(() -> {
        ResourceLocation loc = key.get().location();
        return "spell." + loc.getNamespace() + "." + loc.getPath();
    });

    public final Rarity rarity;
    public final int maxCharges;
    public final int cooldown;
    public final float baseDamage;
    public final DamageCategory damageCategory;

    public Spell(Spell.Properties props) {
        this.rarity = props.rarity;
        this.maxCharges = props.maxCharges;
        this.cooldown = props.cooldown;
        this.baseDamage = props.baseDamage;
        this.damageCategory = props.damageCategory;
    }

    /**
     * Casts a spell using the provided item stack, level, caster.
     *
     * @param tome    The tome used to cast the spell.
     * @param caster  The caster who casts the spell.
     * @param hand    The hand used to cast the spell.
     * @param level   The level where the spell is cast.
     * @param random  The random source used to generate random numbers.
     * @param amplifiedDamage The damage value calculated based on casters spell damage.
     *
     * @return {@code true} if the spell was successfully cast, {@code false} otherwise.
     */
    public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
        return false;
    }

    /**
     * Calculates the amplified damage for the spell based on the caster's attributes.
     *
     * @param caster The entity casting the spell.
     * @return The amplified damage value.
     * @deprecated This method is deprecated because you shouldn't be calling it.
     *             Instead, use the argument from the {@link Spell#cast(ItemStack, LivingEntity, InteractionHand, Level, RandomSource, float)} method.
     *             It is fine to override this method if you need to change the spell damage calculation logic.
     */
    @Deprecated()
    public float getAmplifiedDamage(LivingEntity caster) {
        AttributeInstance spellDamage = caster.getAttribute(ModAttributes.SPELL_POWER.get());
        if (spellDamage == null) return baseDamage;
        double amp = spellDamage.getValue();
        return (float) (baseDamage * amp / 100);
    }
    
    /**
     * Calculates the decreased cooldown for the spell based on the caster's attributes.
     *
     * @param caster The entity casting the spell.
     * @return The decreased cooldown value in ticks.
     */
    public int getDecreasedCooldown(LivingEntity caster) {
        AttributeInstance cooldownReduction = caster.getAttribute(ModAttributes.SPELL_COOLDOWN_REDUCTION.get());
        if (cooldownReduction == null) return cooldown;
        double reduction = cooldownReduction.getValue();
        return (int) (cooldown * (1 - reduction / 100));
    }

    public static boolean checkCooldown(LivingEntity caster, Item tome) {
        if (caster instanceof Player player) return player.getCooldowns().isOnCooldown(tome);
        return false;
    }

    public MutableComponent getName() {
        return Component.translatable(descriptionId.get());
    }

    public MutableComponent getSpellDescription() {
        return Component.translatable(descriptionId.get() + ".desc");
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
        DamageCategory damageCategory = DamageCategory.MAGIC;

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

        public Properties damageCategory(DamageCategory damageCategory) {
            this.damageCategory = damageCategory;
            return this;
        }
    }

    @Getter
    public enum DamageCategory {
        MAGIC("magic", 0x00b0f0),
        PHYSICAL("physical", 0xff8c34),
        ARMOR_PIERCING("armor_piercing", 0xE0E8FF);

        private final String id;
        private final ChatFormatting chatFormatting;
        private final int color;

        DamageCategory(String id, ChatFormatting chatFormatting) {
            this.id = id;
            this.chatFormatting = chatFormatting;
            this.color = -1;
        }

        DamageCategory(String id, int color) {
            this.id = id;
            this.chatFormatting = null;
            this.color = color;
        }

        public MutableComponent applyStyle(MutableComponent component) {
            if (chatFormatting != null) return component.withStyle(chatFormatting);
            return component.withColor(color);
        }

        public String getTranslationKey() {
            return "tooltip.eternalartifacts.spell.damage_type." + id;
        }

        public MutableComponent getDisplayName() {
            return applyStyle(Component.translatable(getTranslationKey()));
        }
    }
}

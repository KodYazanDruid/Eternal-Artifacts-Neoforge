package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import javax.annotation.Nullable;

@Getter
@Setter
public class SpellCastEvent extends Event implements ICancellableEvent {
    private final @Nullable LivingEntity caster;
    private final Spell spell;
    private final Level level;
    private final ItemStack tome;
    private final InteractionHand hand;
    private float amplifiedDamage;
    private int cooldown;

    public SpellCastEvent(@Nullable LivingEntity caster, Level level, ItemStack tome, InteractionHand hand, float amplifiedDamage, Spell spell) {
        this.caster = caster;
        this.level = level;
        this.tome = tome;
        this.hand = hand;
        this.spell = spell;
        this.amplifiedDamage = amplifiedDamage;
        this.cooldown = spell.cooldown;
    }

}

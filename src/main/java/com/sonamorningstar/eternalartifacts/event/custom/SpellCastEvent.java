package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import lombok.Getter;
import lombok.Setter;
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
    private float amplifiedDamage;
    private int cooldown;

    public SpellCastEvent(@Nullable LivingEntity caster, Level level, ItemStack tome, float amplifiedDamage, Spell spell) {
        this.caster = caster;
        this.level = level;
        this.tome = tome;
        this.spell = spell;
        this.amplifiedDamage = amplifiedDamage;
        this.cooldown = spell.cooldown;
    }

}

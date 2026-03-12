package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.core.ModDamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;

/**
 * Common spell damage utilities to avoid repetitive indirectMagic/magic damage code
 * across spell projectile entities.
 */
public final class SpellDamageHelper {
    private SpellDamageHelper() {}

    /**
     * Deals indirect magic damage to a target from a projectile.
     * Uses {@code indirectMagic} if the projectile has an owner, otherwise falls back to {@code magic}.
     *
     * @param projectile The projectile dealing damage.
     * @param target     The entity being hit.
     * @param damage     The amount of damage to deal.
     * @return {@code true} if the damage was successfully applied.
     */
    public static boolean hurtWithSpellDamage(Projectile projectile, Entity target, float damage) {
        Entity owner = projectile.getOwner();
        if (owner != null) {
            return target.hurt(projectile.damageSources().indirectMagic(projectile, owner), damage);
        }
        return target.hurt(projectile.damageSources().magic(), damage);
    }

    /**
     * Deals indirect magic damage from any entity (not necessarily a Projectile).
     * Useful for non-projectile spell entities like EvokerFangs.
     *
     * @param source The entity dealing damage (e.g. fangs, beam).
     * @param owner  The caster who owns the source entity, or {@code null}.
     * @param target The entity being hit.
     * @param damage The amount of damage to deal.
     * @return {@code true} if the damage was successfully applied.
     */
    public static boolean hurtWithSpellDamage(Entity source, Entity owner, Entity target, float damage) {
        if (owner != null) {
            return target.hurt(source.damageSources().indirectMagic(source, owner), damage);
        }
        return target.hurt(source.damageSources().magic(), damage);
    }

    /**
     * Deals magic damage that bypasses invulnerability frames from a projectile.
     * Uses the {@code MAGIC_BYPASS_IFRAME} damage type which has {@code BYPASSES_COOLDOWN} tag.
     *
     * @param projectile The projectile dealing damage.
     * @param target     The entity being hit.
     * @param damage     The amount of damage to deal.
     * @return {@code true} if the damage was successfully applied.
     */
    public static boolean hurtWithSpellDamageBypassIFrame(Projectile projectile, Entity target, float damage) {
        ModDamageSources sources = ModDamageSources.INSTANCES.get(projectile.level());
        if (sources != null) {
            return target.hurt(sources.magicBypassIFrame(projectile, projectile.getOwner()), damage);
        }
        return hurtWithSpellDamage(projectile, target, damage);
    }

    /**
     * Deals magic damage that bypasses invulnerability frames from any entity.
     * Uses the {@code MAGIC_BYPASS_IFRAME} damage type which has {@code BYPASSES_COOLDOWN} tag.
     *
     * @param source The entity dealing damage (e.g. beam).
     * @param owner  The caster who owns the source entity, or {@code null}.
     * @param target The entity being hit.
     * @param damage The amount of damage to deal.
     * @return {@code true} if the damage was successfully applied.
     */
    public static boolean hurtWithSpellDamageBypassIFrame(Entity source, Entity owner, Entity target, float damage) {
        ModDamageSources sources = ModDamageSources.INSTANCES.get(source.level());
        if (sources != null) {
            return target.hurt(sources.magicBypassIFrame(source, owner), damage);
        }
        return hurtWithSpellDamage(source, owner, target, damage);
    }
}

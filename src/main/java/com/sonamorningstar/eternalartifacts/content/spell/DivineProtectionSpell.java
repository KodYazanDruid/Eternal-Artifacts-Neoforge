package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DivineProtectionSpell extends Spell {
    public DivineProtectionSpell(Properties properties) {
        super(properties);
    }

    @Override
    public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
        if (level.isClientSide()) return false;

        Vec3 casterPos = caster.position();
        float healingAmount = getAmplifiedHealing(caster);
        
        AABB searchArea = AABB.ofSize(casterPos, healingRadius * 2, healingRadius * 2, healingRadius * 2);
        
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchArea, entity -> {
            if (entity == null) return false;
            if (entity == caster) return true;
            if (entity instanceof Player) return true;
            if (entity instanceof OwnableEntity ownable && ownable.getOwner() == caster) return true;
            
            if (entity.getType() == EntityType.WOLF || entity.getType() == EntityType.CAT ||
                entity.getType() == EntityType.PARROT || entity.getType() == EntityType.HORSE ||
                entity.getType() == EntityType.DONKEY || entity.getType() == EntityType.MULE ||
                entity.getType() == EntityType.LLAMA) {
                return entity instanceof OwnableEntity ownable && ownable.getOwner() == caster;
            }
            return false;
        });

        for (LivingEntity entity : nearbyEntities) {
            entity.heal(healingAmount);
            entity.addEffect(new MobEffectInstance(ModEffects.DIVINE_PROTECTION.get(), 400, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        }

        // Visual/sound effects could be added here
        // level.playSound(null, caster.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 1.0f);

        return true;
    }
}

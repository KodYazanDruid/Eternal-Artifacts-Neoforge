package com.sonamorningstar.eternalartifacts.content.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

public class PinkyEntity extends Slime {
    public PinkyEntity(EntityType<? extends Slime> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    protected void dealDamage(LivingEntity pLivingEntity) {
        if (this.isAlive()) {
            if (this.distanceToSqr(pLivingEntity) < 1.5
                    && this.hasLineOfSight(pLivingEntity)
                    && pLivingEntity.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, pLivingEntity);
            }
        }
    }

    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.CHERRY_LEAVES;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return new EntityDimensions(0.5f, 0.5f, true);
    }

    @Override
    protected boolean isDealsDamage() {
        return isEffectiveAi();
    }

    @Override
    public void setSize(int pSize, boolean pResetHealth) {
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isTiny() {
        return true;
    }
}

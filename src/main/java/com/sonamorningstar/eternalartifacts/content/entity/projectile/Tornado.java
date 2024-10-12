package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public class Tornado extends AbstractHurtingProjectile {
    private float damage;
    private boolean inGround;
    @Getter
    @Setter
    private SoundEvent soundEvent = defaultSoundEvent();
    public Tornado(
            Level level,
            LivingEntity shooter,
            double offsetX,
            double offsetY,
            double offsetZ,
            float damage
    ) {
        super(ModEntities.TORNADO.get(), shooter, offsetX, offsetY, offsetZ, level);
        this.damage = damage;
    }

    public Tornado(EntityType<? extends Tornado> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide()) {
            Entity hit = result.getEntity();
            Entity owner = this.getOwner();
            hit.hurt(this.damageSources().mobProjectile(this, owner instanceof LivingEntity living ? living : null), damage);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        Direction dir = result.getDirection();
        if (dir != Direction.UP) {
            discard();
        }
    }

    //region Serialization stuff.
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("damage", damage);
        tag.putBoolean("inGround", inGround);
        tag.putString("SoundEvent", BuiltInRegistries.SOUND_EVENT.getKey(soundEvent).toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat("damage");
        inGround = tag.getBoolean("inGround");
        if (tag.contains("SoundEvent", 8)) {
            this.soundEvent = BuiltInRegistries.SOUND_EVENT
                    .getOptional(new ResourceLocation(tag.getString("SoundEvent")))
                    .orElse(defaultSoundEvent());
        }
    }
    //endregion


    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        if (pType != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    private boolean shouldFall() {
        return this.inGround && this.level().noCollision(new AABB(this.position(), this.position()).inflate(0.06));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(
                vec3.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F)
        );
    }

    private SoundEvent defaultSoundEvent() {return SoundEvents.CAT_PURR;}
    @Override
    public boolean canCollideWith(Entity entity) {return !(entity instanceof Tornado) && super.canCollideWith(entity);}
    @Override
    protected boolean canHitEntity(Entity target) {return !(target instanceof Tornado) && super.canHitEntity(target);}
    @Override
    public boolean isPickable() {return false;}
    @Override
    public boolean hurt(DamageSource source, float amount) {return false;}
    @Override
    protected ParticleOptions getTrailParticle() {return ParticleTypes.DUST_PLUME;}
    @Override
    protected boolean shouldBurn() {return false;}
}

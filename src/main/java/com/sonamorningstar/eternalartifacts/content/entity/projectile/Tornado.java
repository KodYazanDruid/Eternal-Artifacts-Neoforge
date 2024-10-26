package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

    @Override
    protected void onInsideBlock(BlockState state) {
        if (state.is(BlockTags.FIRE)) {
            BlockPos posToDestroy = this.getOnPos(0);
            if (level().getBlockState(posToDestroy).is(BlockTags.FIRE))
                this.level().destroyBlock(posToDestroy, false, this);
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
    public void tick() {
        super.tick();
        Vec3 deltaMovement = getDeltaMovement();
        double y = !this.isNoGravity() && shouldFall() ? -0.2 : 0;
        setDeltaMovement(deltaMovement.x, y, deltaMovement.z);
    }

    private boolean shouldFall() {
        return this.level().noCollision(new AABB(this.position(), this.position()).inflate(0.6));
    }

    //region Bloatware.
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
    //endregion
}
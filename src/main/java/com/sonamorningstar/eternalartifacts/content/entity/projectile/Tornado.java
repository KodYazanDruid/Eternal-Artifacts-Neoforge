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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

public class Tornado extends AbstractHurtingProjectile {
    private float damage;
    private boolean inGround;
	private int age;
    @Getter
    @Setter
    private SoundEvent soundEvent = defaultSoundEvent();
	private static final int MAX_AGE = 200;
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
            hit.hurtMarked = true;
            hit.setDeltaMovement(hit.getDeltaMovement().add(0, 0.2, 0));
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
		tag.putInt("age", age);
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
		age = tag.getInt("age");
    }
    //endregion

    @Override
    public void tick() {
        super.tick();
		this.age++;
		if (this.age >= MAX_AGE) {
			this.discard();
		}
        Vec3 deltaMovement = getDeltaMovement();
        double y = !this.isNoGravity() && shouldFall() ? -0.5 : 0;
        setDeltaMovement(deltaMovement.x, y, deltaMovement.z);
    }

    private boolean shouldFall() {
        AABB size = this.getBoundingBox();
        return this.level().noCollision(
                new AABB(this.position(), this.position().add(size.getXsize(), size.getYsize() + 0.6, size.getZsize()))
                .move(0, -0.6, 0)

        );
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

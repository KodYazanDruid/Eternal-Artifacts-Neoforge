package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BlackHoleEntity extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_LIFETIME_ID = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.INT);
    private int lifeTime = 200;

    public BlackHoleEntity(EntityType<? extends BlackHoleEntity> type, Level level) {
        super(type, level);
    }

    public BlackHoleEntity(Level level, Player player) {
        super(ModEntities.BLACK_HOLE.get(), level);
        setOwner(player);
        setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_LIFETIME_ID, lifeTime);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            lifeTime--;
            entityData.set(DATA_LIFETIME_ID, lifeTime);
            if (lifeTime <= 0) {
                discard();
                return;
            }

            double radius = 10.0;
            List<Entity> entities = level().getEntities(this, getBoundingBox().inflate(radius), e -> e instanceof LivingEntity && e != getOwner() || e instanceof ItemEntity || e instanceof ExperienceOrb);

            for (Entity entity : entities) {
                if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) {
                    entity.discard();
                    continue;
                }

                Vec3 direction = position().subtract(entity.position()).normalize();
                double distance = entity.position().distanceTo(position());
                double force = Math.max(0, (radius - distance) / radius);

                entity.setDeltaMovement(entity.getDeltaMovement().add(direction.scale(force * 0.5)));

                if (distance < 1.5) {
                    entity.hurt(damageSources().magic(), 4.0f);
                }
            }
        } else {
            lifeTime = entityData.get(DATA_LIFETIME_ID);
            if (random.nextBoolean()) {
                level().addParticle(ParticleTypes.PORTAL, getX() + random.nextGaussian() * 0.5, getY(), getZ() + random.nextGaussian() * 0.5, 0, 0, 0);
            }
        }
        //if(tickCount % 20 == 0) level().playSound(null, this.blockPosition(), ModSoundEvents.BLACK_HOLE_AMBIENT.get(), SoundSource.NEUTRAL, 0.5f, 1.0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("LifeTime")) {
            lifeTime = compound.getInt("LifeTime");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LifeTime", lifeTime);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
}

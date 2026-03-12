package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SpellShulkerBullet extends ShulkerBullet {
    private float damage;
    private boolean straightShot = false;
    private Vec3 shootDirection;

    public SpellShulkerBullet(Level level, LivingEntity shooter, @Nullable Entity target, Direction.Axis axis, float damage) {
        super(level, shooter, target, axis);
        this.damage = damage;
    }

    /**
     * Creates a straight-flying shulker bullet with no homing target.
     */
    public SpellShulkerBullet(Level level, LivingEntity shooter, Vec3 direction, float damage) {
        super(level, shooter, null, Direction.Axis.X);
        this.damage = damage;
        this.straightShot = true;
        this.shootDirection = direction.normalize();
    }

    public SpellShulkerBullet(EntityType<? extends SpellShulkerBullet> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        if (straightShot && shootDirection != null) {
            // Bypass vanilla ShulkerBullet tick which stops movement when target is null
            tickStraight();
        } else {
            super.tick();
        }
    }

    private void tickStraight() {
        if (!this.level().isClientSide()) {
            // Check collisions
            Entity owner = this.getOwner();
            var entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()),
                    e -> e != owner && e.isPickable());
            for (Entity entity : entities) {
                this.onHitEntity(new EntityHitResult(entity));
                return;
            }
        }

        Vec3 pos = this.position();
        double speed = 0.65;
        Vec3 velocity = shootDirection.scale(speed);
        this.setDeltaMovement(velocity);
        this.setPos(pos.x + velocity.x, pos.y + velocity.y, pos.z + velocity.z);

        // Spawn trail particle
        if (this.level().isClientSide()) {
            this.level().addParticle(ParticleTypes.END_ROD,
                    pos.x - velocity.x * 0.25, pos.y - velocity.y * 0.25, pos.z - velocity.z * 0.25,
                    0, 0, 0);
        }

        // Discard if too old or hit a block
        if (this.tickCount > 150) {
            this.discard();
            return;
        }
        var blockHit = this.level().clip(new net.minecraft.world.level.ClipContext(
                pos, pos.add(velocity), net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE, this));
        if (blockHit.getType() != BlockHitResult.Type.MISS) {
            this.onHitBlock(blockHit);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide()) {
            Entity hit = result.getEntity();
            boolean hurt = SpellDamageHelper.hurtWithSpellDamage(this, hit, damage);
            if (hurt && hit instanceof LivingEntity livingHit) {
                livingHit.setDeltaMovement(livingHit.getDeltaMovement().add(0, 0.2, 0));
            }
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("SpellDamage", damage);
        tag.putBoolean("StraightShot", straightShot);
        if (shootDirection != null) {
            tag.putDouble("ShootDirX", shootDirection.x);
            tag.putDouble("ShootDirY", shootDirection.y);
            tag.putDouble("ShootDirZ", shootDirection.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat("SpellDamage");
        straightShot = tag.getBoolean("StraightShot");
        if (tag.contains("ShootDirX")) {
            shootDirection = new Vec3(tag.getDouble("ShootDirX"), tag.getDouble("ShootDirY"), tag.getDouble("ShootDirZ"));
        }
    }
}

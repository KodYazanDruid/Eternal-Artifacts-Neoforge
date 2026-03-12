package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class SonicBoomSpellEntity extends AbstractSpellEntity {
    private static final int MAX_LIFETIME = 40;
    private static final double KNOCKBACK_STRENGTH = 2.5;
    private static final double BEAM_LENGTH = 32.0;
    private static final double BEAM_RADIUS = 1.0;

    private static final EntityDataAccessor<Float> DATA_START_X = SynchedEntityData.defineId(SonicBoomSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_START_Y = SynchedEntityData.defineId(SonicBoomSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_START_Z = SynchedEntityData.defineId(SonicBoomSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIR_X = SynchedEntityData.defineId(SonicBoomSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIR_Y = SynchedEntityData.defineId(SonicBoomSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIR_Z = SynchedEntityData.defineId(SonicBoomSpellEntity.class, EntityDataSerializers.FLOAT);

    private boolean hasDamaged = false;
    private boolean spawnedParticles = false;
    private final Set<UUID> damagedEntities = new HashSet<>();

    public SonicBoomSpellEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public SonicBoomSpellEntity(Level level, LivingEntity caster, float damage) {
        super(ModEntities.SPELL_SONIC_BOOM.get(), level);
        this.setOwner(caster);
        this.setDamage(damage);
        Vec3 start = caster.getEyePosition();
        Vec3 dir = caster.getLookAngle().normalize();
        setStartPos(start);
        setBeamDirection(dir);
        this.setPos(start.x, start.y, start.z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_START_X, 0.0F);
        this.entityData.define(DATA_START_Y, 0.0F);
        this.entityData.define(DATA_START_Z, 0.0F);
        this.entityData.define(DATA_DIR_X, 0.0F);
        this.entityData.define(DATA_DIR_Y, 0.0F);
        this.entityData.define(DATA_DIR_Z, 0.0F);
    }

    private void setStartPos(Vec3 pos) {
        this.entityData.set(DATA_START_X, (float) pos.x);
        this.entityData.set(DATA_START_Y, (float) pos.y);
        this.entityData.set(DATA_START_Z, (float) pos.z);
    }

    private Vec3 getStartPos() {
        return new Vec3(this.entityData.get(DATA_START_X), this.entityData.get(DATA_START_Y), this.entityData.get(DATA_START_Z));
    }

    private void setBeamDirection(Vec3 dir) {
        this.entityData.set(DATA_DIR_X, (float) dir.x);
        this.entityData.set(DATA_DIR_Y, (float) dir.y);
        this.entityData.set(DATA_DIR_Z, (float) dir.z);
    }

    private Vec3 getBeamDirection() {
        return new Vec3(this.entityData.get(DATA_DIR_X), this.entityData.get(DATA_DIR_Y), this.entityData.get(DATA_DIR_Z));
    }

    @Override
    protected int getMaxLifetime() {
        return MAX_LIFETIME;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide() && !spawnedParticles) {
            spawnParticles();
        }

        if (!this.level().isClientSide() && !hasDamaged && getAge() >= 8) {
            dealDamage();
        }
    }
    
    private void spawnParticles() {
        spawnedParticles = true;

        Vec3 startPos = getStartPos();
        Vec3 direction = getBeamDirection();

        int particleCount = (int) (BEAM_LENGTH / 2.0);
        for (int i = 0; i <= particleCount; i++) {
            double t = (double) i / particleCount;
            double px = startPos.x + direction.x * BEAM_LENGTH * t;
            double py = startPos.y + direction.y * BEAM_LENGTH * t;
            double pz = startPos.z + direction.z * BEAM_LENGTH * t;
            this.level().addParticle(ParticleTypes.SONIC_BOOM, px, py, pz, 0, 0, 0);
        }
    }

    private void dealDamage() {
        hasDamaged = true;
        Vec3 startPos = getStartPos();
        Vec3 direction = getBeamDirection();

        LivingEntity owner = getOwner();
        Vec3 endPos = startPos.add(direction.scale(BEAM_LENGTH));

        AABB beamBounds = new AABB(startPos, endPos).inflate(BEAM_RADIUS);

        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, beamBounds, entity ->
                entity.isAlive() && !entity.isInvulnerable() && entity != owner && !damagedEntities.contains(entity.getUUID())
        );

        for (LivingEntity target : entities) {
            if (distanceToBeam(startPos, direction, target.position().add(0, target.getBbHeight() / 2.0, 0)) <= BEAM_RADIUS + target.getBbWidth() / 2.0) {
				target.hurt(this.damageSources().sonicBoom(Objects.requireNonNullElse(owner, this)), getDamage());
                
                double knockbackResistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                double knockback = KNOCKBACK_STRENGTH * (1.0 - knockbackResistance);
                target.push(
                        knockback * 0.5 * direction.x,
                        knockback * 0.1,
                        knockback * 0.5 * direction.z
                );
                target.hurtMarked = true;
                damagedEntities.add(target.getUUID());
            }
        }
    
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0F, 1.0F);
        

    }
    
    private double distanceToBeam(Vec3 startPos, Vec3 direction, Vec3 point) {
        Vec3 beamEnd = startPos.add(direction.scale(BEAM_LENGTH));
        Vec3 startToPoint = point.subtract(startPos);
        Vec3 startToEnd = beamEnd.subtract(startPos);
        double beamLengthSq = startToEnd.lengthSqr();

        double t = Math.max(0, Math.min(1, startToPoint.dot(startToEnd) / beamLengthSq));

        Vec3 closestPoint = startPos.add(startToEnd.scale(t));
        return point.distanceTo(closestPoint);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasDamaged", hasDamaged);
        Vec3 start = getStartPos();
        tag.putDouble("StartX", start.x);
        tag.putDouble("StartY", start.y);
        tag.putDouble("StartZ", start.z);
        Vec3 dir = getBeamDirection();
        tag.putDouble("DirX", dir.x);
        tag.putDouble("DirY", dir.y);
        tag.putDouble("DirZ", dir.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hasDamaged = tag.getBoolean("HasDamaged");
        if (tag.contains("StartX")) {
            setStartPos(new Vec3(tag.getDouble("StartX"), tag.getDouble("StartY"), tag.getDouble("StartZ")));
        }
        if (tag.contains("DirX")) {
            setBeamDirection(new Vec3(tag.getDouble("DirX"), tag.getDouble("DirY"), tag.getDouble("DirZ")));
        }
    }
}

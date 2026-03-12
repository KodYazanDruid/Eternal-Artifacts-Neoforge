package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Abstract base class for spell entities that deal damage via raycast or area effects
 * rather than projectile collision. These entities are typically invisible and rely on
 * particles for visual feedback. Their lifetime is tied to the spell animation duration.
 */
@Setter
@Getter
public abstract class AbstractSpellEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private int age;

    public AbstractSpellEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DAMAGE, 0.0F);
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
    }

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DATA_DAMAGE, damage);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.entityData.set(DATA_OWNER_UUID, owner != null ? Optional.of(owner.getUUID()) : Optional.empty());
    }

    @Nullable
    public LivingEntity getOwner() {
        Optional<UUID> uuid = this.entityData.get(DATA_OWNER_UUID);
        if (uuid.isPresent() && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid.get());
            return entity instanceof LivingEntity living ? living : null;
        }
        return null;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }
    
    protected abstract int getMaxLifetime();

    @Override
    public void tick() {
        this.age++;
        if (this.age >= getMaxLifetime()) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("SpellDamage", getDamage());
        tag.putInt("Age", this.age);
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        setDamage(tag.getFloat("SpellDamage"));
        this.age = tag.getInt("Age");
        if (tag.hasUUID("Owner")) {
            this.entityData.set(DATA_OWNER_UUID, Optional.of(tag.getUUID("Owner")));
        }
    }
    @Override
    public boolean isNoGravity() {
        return true;
    }
}

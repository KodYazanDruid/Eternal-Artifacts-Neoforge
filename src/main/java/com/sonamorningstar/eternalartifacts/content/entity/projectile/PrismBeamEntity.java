package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nullable;
import java.util.*;

public class PrismBeamEntity extends AbstractSpellEntity {
    private static final double BEAM_LENGTH = 48.0;
    private static final double BEAM_RADIUS = 0.8;
    private static final float LERP_SPEED = 0.15F;

    private static final EntityDataAccessor<Float> DATA_DIR_X = SynchedEntityData.defineId(PrismBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIR_Y = SynchedEntityData.defineId(PrismBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIR_Z = SynchedEntityData.defineId(PrismBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_EFFECTIVE_LENGTH = SynchedEntityData.defineId(PrismBeamEntity.class, EntityDataSerializers.FLOAT);
    
    private final Long2ObjectMap<BlockEntityTicker<?>> tickAcceleratedBlocks = new Long2ObjectLinkedOpenHashMap<>();

    public Long2ObjectMap<BlockEntityTicker<?>> getTickAcceleratedBlocks() {
        return tickAcceleratedBlocks;
    }

    private Vec3 prevBeamDirection = Vec3.ZERO;


    public PrismBeamEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public PrismBeamEntity(Level level, LivingEntity caster, float damage) {
        super(ModEntities.PRISM_BEAM.get(), level);
        this.setOwner(caster);
        this.setDamage(damage);
        Vec3 start = caster.getEyePosition();
        Vec3 dir = caster.getLookAngle().normalize();
        setBeamDirection(dir);
        this.setPos(start.x, start.y, start.z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DIR_X, 0.0F);
        this.entityData.define(DATA_DIR_Y, 0.0F);
        this.entityData.define(DATA_DIR_Z, 0.0F);
        this.entityData.define(DATA_EFFECTIVE_LENGTH, (float) BEAM_LENGTH);
    }

    public void setBeamDirection(Vec3 dir) {
        this.entityData.set(DATA_DIR_X, (float) dir.x);
        this.entityData.set(DATA_DIR_Y, (float) dir.y);
        this.entityData.set(DATA_DIR_Z, (float) dir.z);
    }

    public Vec3 getBeamDirection() {
        return new Vec3(this.entityData.get(DATA_DIR_X), this.entityData.get(DATA_DIR_Y), this.entityData.get(DATA_DIR_Z));
    }
    
    public Vec3 getBeamDirection(float partialTick) {
        Vec3 current = getBeamDirection();
        if (prevBeamDirection.lengthSqr() < 0.001) return current;
        return lerpDirection(prevBeamDirection, current, partialTick);
    }

    public Vec3 getPrevBeamDirection() {
        return prevBeamDirection;
    }

    public double getBeamLength() {
        return this.entityData.get(DATA_EFFECTIVE_LENGTH);
    }

    public double getBeamRadius() {
        return BEAM_RADIUS;
    }
    
    @Nullable
    @Override
    public LivingEntity getOwner() {
        LivingEntity owner = super.getOwner();
        if (owner != null) return owner;
        UUID uuid = getOwnerUUID();
        if (uuid != null && this.level() instanceof ClientLevel clientLevel) {
            for (Entity entity : clientLevel.entitiesForRendering()) {
                if (entity.getUUID().equals(uuid) && entity instanceof LivingEntity living) {
                    return living;
                }
            }
        }
        return null;
    }

    @Override
    protected int getMaxLifetime() {
        return 72000;
    }
    
    @Override
    public void tick() {
        super.tick();
        tickAcceleratedBlocks.clear();

        LivingEntity owner = getOwner();
        if (owner == null || !owner.isAlive()) {
            if (getAge() > 5) {
                this.discard();
            }
            return;
        }
        
        if (owner instanceof Player player && !player.isUsingItem()) {
            this.discard();
            return;
        }

        Vec3 eyePos = owner.getEyePosition();
        this.setPos(eyePos.x, eyePos.y, eyePos.z);
        
        prevBeamDirection = getBeamDirection();
        
        Vec3 targetDir = owner.getLookAngle().normalize();
        Vec3 currentDir = getBeamDirection();
        if (currentDir.lengthSqr() < 0.001) {
            setBeamDirection(targetDir);
        } else {
            Vec3 lerpedDir = lerpDirection(currentDir, targetDir, LERP_SPEED);
            setBeamDirection(lerpedDir);
        }
        
        BlockHitResult hitResult = getHitResult(eyePos);
        
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.entityData.set(DATA_EFFECTIVE_LENGTH, (float) eyePos.distanceTo(hitResult.getLocation()));
            tickBlockEntities(hitResult.getBlockPos());
        } else {
            this.entityData.set(DATA_EFFECTIVE_LENGTH, (float) BEAM_LENGTH);
        }

        if (!this.level().isClientSide()) {
            dealDamage();
        }
        
        if (getAge() % 20 == 0) {
            this.level().playSound(owner instanceof Player p ? p : null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.5F, 1.5F);
        }
    }
    
    private void tickBlockEntities(BlockPos hitPos) {
        for (BlockPos pos : BlockPos.betweenClosed(hitPos.offset(-1, -1, -1), hitPos.offset(1, 1, 1))) {
            BlockEntity be = this.level().getBlockEntity(pos);
            if (be != null) {
                BlockEntityTicker<?> ticker = be.getBlockState().getTicker(this.level(), be.getType());
                if (ticker != null) {
                    ((BlockEntityTicker<BlockEntity>) ticker).tick(this.level(), pos, be.getBlockState(), be);
                    tickAcceleratedBlocks.put(pos.asLong(), ticker);
                }
            }
        }
    }
    
    public BlockHitResult getHitResult(Vec3 startPos) {
        Vec3 dir = getBeamDirection();
        Vec3 beamEnd = startPos.add(dir.scale(BEAM_LENGTH));
        
        BlockHitResult result = this.level().clip(new ClipContext(
            startPos, beamEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this
        ));
        
        while (result.getType() == HitResult.Type.BLOCK) {
            BlockState hitState = this.level().getBlockState(result.getBlockPos());
            if (hitState.is(Tags.Blocks.GLASS) || hitState.is(Tags.Blocks.GLASS_PANES)) {
                Vec3 newStart = result.getLocation().add(dir.scale(0.05));
                if (newStart.distanceToSqr(beamEnd) < 0.01) break;
                result = this.level().clip(new ClipContext(
                    newStart, beamEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this
                ));
            } else {
                break;
            }
        }
        
        return result;
    }
    

    private void dealDamage() {
        Vec3 startPos = this.position();
        Vec3 direction = getBeamDirection();
        LivingEntity owner = getOwner();
        double effectiveLength = getBeamLength();

        Vec3 endPos = startPos.add(direction.scale(effectiveLength));
        AABB beamBounds = new AABB(startPos, endPos).inflate(BEAM_RADIUS);

        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, beamBounds, entity ->
            entity.isAlive() && !entity.isInvulnerable() && entity != owner
        );

        for (LivingEntity target : entities) {
            Vec3 targetCenter = target.position().add(0, target.getBbHeight() / 2.0, 0);
            if (distanceToBeam(startPos, direction, effectiveLength, targetCenter) <= BEAM_RADIUS + target.getBbWidth() / 2.0) {
                SpellDamageHelper.hurtWithSpellDamageBypassIFrame(this, owner, target, getDamage());
            }
        }
    }

    private double distanceToBeam(Vec3 startPos, Vec3 direction, double effectiveLength, Vec3 point) {
        Vec3 beamEnd = startPos.add(direction.scale(effectiveLength));
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
        Vec3 dir = getBeamDirection();
        tag.putDouble("DirX", dir.x);
        tag.putDouble("DirY", dir.y);
        tag.putDouble("DirZ", dir.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("DirX")) {
            setBeamDirection(new Vec3(tag.getDouble("DirX"), tag.getDouble("DirY"), tag.getDouble("DirZ")));
        }
    }
    
    private static Vec3 lerpDirection(Vec3 from, Vec3 to, float t) {
        double x = from.x + (to.x - from.x) * t;
        double y = from.y + (to.y - from.y) * t;
        double z = from.z + (to.z - from.z) * t;
        Vec3 result = new Vec3(x, y, z);
        double len = result.length();
        return len < 1.0E-6 ? from : result.scale(1.0 / len);
    }
}

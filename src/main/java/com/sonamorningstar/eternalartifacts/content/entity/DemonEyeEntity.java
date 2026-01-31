package com.sonamorningstar.eternalartifacts.content.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DemonEyeEntity extends FlyingMob implements Enemy {

    public DemonEyeEntity(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new DemonEyeMoveControl(this);
        this.lookControl = new DemoneEyeLookControl(this);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public final AnimationState idleState = new AnimationState();
    private int idleAnimationTimeout = 0;
    
    private int attackCooldownTimer = 0;
    private static final int ATTACK_COOLDOWN = 20;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new FlyTowardsTargetGoal(this));
        goalSelector.addGoal(3, new FloatGoal(this));
        goalSelector.addGoal(4, new RandomFloatAroundGoal(this));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return pSize.height / 2;
    }
    
    @Override
    public void tick() {
        super.tick();

        if(level().isClientSide) {
            setupAnimationStates();
        }
        
        attackCooldownTimer = Math.max(0, attackCooldownTimer - 1);
    }

    private void setupAnimationStates() {
        if (idleAnimationTimeout <= 0) {
            idleAnimationTimeout = random.nextInt(40) + 80;
            idleState.start(tickCount);
        } else {
            --idleAnimationTimeout;
        }
    }
    
    @Override
    public void playerTouch(Player player) {
        if (this.isAlive() && attackCooldownTimer == 0 && this.hasLineOfSight(player) && player.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
            attackCooldownTimer = ATTACK_COOLDOWN;
            playSound(SoundEvents.PLAYER_ATTACK_WEAK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            doEnchantDamageEffects(this, player);
            
        }
    }
    
    protected float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }
    
    static class DemoneEyeLookControl extends LookControl {
        private final DemonEyeEntity demonEye;
        
        public DemoneEyeLookControl(DemonEyeEntity demonEye) {
            super(demonEye);
            this.demonEye = demonEye;
        }
        
        @Override
        public void tick() {
            super.tick();
            this.demonEye.setYRot(-((float) Mth.atan2(this.demonEye.getDeltaMovement().x,
                this.demonEye.getDeltaMovement().z)) * (180F / (float)Math.PI));
            this.demonEye.setXRot(-((float) Mth.atan2(this.demonEye.getDeltaMovement().y,
                Math.sqrt(this.demonEye.getDeltaMovement().x * this.demonEye.getDeltaMovement().x + this.demonEye.getDeltaMovement().z * this.demonEye.getDeltaMovement().z))) * (180F / (float)Math.PI));
        }
    }
    
    static class DemonEyeMoveControl extends MoveControl {
        private final DemonEyeEntity demonEye;
        private int floatDuration;
        
        public DemonEyeMoveControl(DemonEyeEntity demonEye) {
            super(demonEye);
            this.demonEye = demonEye;
        }
        
        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.demonEye.getRandom().nextInt(5) + 2;
                    Vec3 vec3 = new Vec3(this.wantedX - this.demonEye.getX(), this.wantedY - this.demonEye.getY(), this.wantedZ - this.demonEye.getZ());
                    double d0 = vec3.length();
                    vec3 = vec3.normalize();
                    if (this.canReach(vec3, Mth.ceil(d0))) {
                        this.demonEye.setDeltaMovement(this.demonEye.getDeltaMovement().add(vec3.scale(0.1)));
                    } else {
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }
            }
        }
        
        private boolean canReach(Vec3 pPos, int pLength) {
            AABB aabb = this.demonEye.getBoundingBox();
            
            for(int i = 1; i < pLength; ++i) {
                aabb = aabb.move(pPos);
                if (!this.demonEye.level().noCollision(this.demonEye, aabb)) {
                    return false;
                }
            }
            
            return true;
        }
    }

    static class RandomFloatAroundGoal extends Goal {
        private final DemonEyeEntity demonEye;

        public RandomFloatAroundGoal(DemonEyeEntity demonEye) {
            this.demonEye = demonEye;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }


        @Override
        public boolean canUse() {
            MoveControl movecontrol = this.demonEye.getMoveControl();
            if (!movecontrol.hasWanted()) {
                return true;
            } else {
                double d0 = movecontrol.getWantedX() - this.demonEye.getX();
                double d1 = movecontrol.getWantedY() - this.demonEye.getY();
                double d2 = movecontrol.getWantedZ() - this.demonEye.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0 || d3 > 3600.0;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            RandomSource randomsource = this.demonEye.getRandom();
            double d0 = this.demonEye.getX() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 13.0F);
            double d1 = this.demonEye.getY() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 13.0F);
            double d2 = this.demonEye.getZ() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 13.0F);
            this.demonEye.getMoveControl().setWantedPosition(d0, d1, d2, 1.0);
        }
    }

    static class FlyTowardsTargetGoal extends Goal {
        private final DemonEyeEntity demonEye;

        FlyTowardsTargetGoal(DemonEyeEntity demonEye) {
            this.demonEye = demonEye;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return demonEye.getTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            LivingEntity target = demonEye.getTarget();
            if (target != null)
                demonEye.getMoveControl().setWantedPosition(target.getX(), target.getEyeY(), target.getZ(), 1F);
        }
    }
}

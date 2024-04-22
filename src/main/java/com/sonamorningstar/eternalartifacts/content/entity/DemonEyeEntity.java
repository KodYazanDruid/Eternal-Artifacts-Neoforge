package com.sonamorningstar.eternalartifacts.content.entity;

import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

//This entity is dumb. Need fixing.
public class DemonEyeEntity extends FlyingMob implements Enemy {

    public DemonEyeEntity(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        //moveControl = new DemonEyeMoveControl(this);
        //lookControl = new DemonEyeLookControl(this);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
    }

    public final AnimationState idleState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new RandomFloatAroundGoal(this));
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        goalSelector.addGoal(3, new FlyTowardsTargetGoal(this));
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
    }

    private void setupAnimationStates() {
        if (idleAnimationTimeout <= 0) {
            idleAnimationTimeout = random.nextInt(40) + 80;
            idleState.start(tickCount);
        } else {
            --idleAnimationTimeout;
        }
    }

    class RandomFloatAroundGoal extends Goal {
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

    class FlyTowardsTargetGoal extends Goal {
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
                demonEye.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.5F);
        }
    }

    class DemonEyeLookControl extends LookControl {
        public DemonEyeLookControl(Mob pMob) {
            super(pMob);
        }

        @Override
        public void tick() {
        }
    }

    class DemonEyeMoveControl extends MoveControl {
        private float speed = 0.1F;

        public DemonEyeMoveControl(Mob pMob) {
            super(pMob);
        }

        @Override
        public void tick() {
            if (DemonEyeEntity.this.horizontalCollision) {
                DemonEyeEntity.this.setYRot(DemonEyeEntity.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = this.wantedX - DemonEyeEntity.this.getX();
            double d1 = this.wantedY - DemonEyeEntity.this.getY();
            double d2 = this.wantedZ - DemonEyeEntity.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > 1.0E-5F) {
                double d4 = 1.0 - Math.abs(d1 * 0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = DemonEyeEntity.this.getYRot();
                float f1 = (float)Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(DemonEyeEntity.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180.0F / (float)Math.PI));
                DemonEyeEntity.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                DemonEyeEntity.this.yBodyRot = DemonEyeEntity.this.getYRot();

                if (Mth.degreesDifferenceAbs(f, DemonEyeEntity.this.getYRot()) < 3.0F) this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                else this.speed = Mth.approach(this.speed, 0.2F, 0.025F);


                float f4 = (float)(-(Mth.atan2(-d1, d3) * 180.0F / (float)Math.PI));
                DemonEyeEntity.this.setXRot(f4);

                DemonEyeEntity.this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(d0, d1, d2));
                /*float f5 = DemonEyeEntity.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * (float) (Math.PI / 180.0))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * (float) (Math.PI / 180.0))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * (float) (Math.PI / 180.0))) * Math.abs(d1 / d5);
                Vec3 vec3 = DemonEyeEntity.this.getDeltaMovement();
                DemonEyeEntity.this.setDeltaMovement(vec3.add(new Vec3(d6, d8, d7).subtract(vec3).scale(0.2)));*/
            }
        }
    }

}

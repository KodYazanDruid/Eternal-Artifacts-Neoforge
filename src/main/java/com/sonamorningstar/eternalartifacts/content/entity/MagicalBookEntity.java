package com.sonamorningstar.eternalartifacts.content.entity;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MagicalBookEntity extends FlyingMob implements Enemy {
    public MagicalBookEntity(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        moveControl = new MagicalBookEntity.BookMoveControl(this);
        lookControl = new MagicalBookEntity.BookLookControl(this);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(5, new FloatGoal(this));
        goalSelector.addGoal(1, new BookAttackGoal(this));
        goalSelector.addGoal(5, new RandomFloatAroundGoal(this));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {return true;}

    @Override
    public boolean canAttackType(EntityType<?> pType) {
        return super.canAttackType(pType);
    }

    public float getBookOpenAmount(float partialTick) {
        return Mth.lerp((1.0F - Mth.cos((tickCount + partialTick) * 0.25F)) / 2F, 0.0F, 1.0F);
    }
    
    static class BookLookControl extends LookControl {
        private final MagicalBookEntity book;

        public BookLookControl(MagicalBookEntity book) {
            super(book);
            this.book = book;
        }
        
        @Override
        public void tick() {
            super.tick();
            this.book.setYRot(-((float) Mth.atan2(this.book.getDeltaMovement().x, this.book.getDeltaMovement().z)) * (180F / (float)Math.PI));
         }
    }

    static class BookMoveControl extends MoveControl {
        private final MagicalBookEntity book;
        private int floatDuration;

        public BookMoveControl(MagicalBookEntity book) {
            super(book);
            this.book = book;
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.book.getRandom().nextInt(5) + 2;
                    Vec3 vec3 = new Vec3(this.wantedX - this.book.getX(), this.wantedY - this.book.getY(), this.wantedZ - this.book.getZ());
                    double d0 = vec3.length();
                    vec3 = vec3.normalize();
                    if (this.canReach(vec3, Mth.ceil(d0))) {
                        this.book.setDeltaMovement(this.book.getDeltaMovement().add(vec3.scale(0.1)));
                    } else {
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }
            }
        }

        private boolean canReach(Vec3 pPos, int pLength) {
            AABB aabb = this.book.getBoundingBox();

            for(int i = 1; i < pLength; ++i) {
                aabb = aabb.move(pPos);
                if (!this.book.level().noCollision(this.book, aabb)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class RandomFloatAroundGoal extends Goal {
        private final MagicalBookEntity book;

        public RandomFloatAroundGoal(MagicalBookEntity book) {
            this.book = book;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MoveControl movecontrol = this.book.getMoveControl();
            if (!movecontrol.hasWanted()) {
                return true;
            } else {
                double d0 = movecontrol.getWantedX() - this.book.getX();
                double d1 = movecontrol.getWantedY() - this.book.getY();
                double d2 = movecontrol.getWantedZ() - this.book.getZ();
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
            RandomSource randomsource = this.book.getRandom();
            double d0 = this.book.getX() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.book.getY() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.book.getZ() + (double)((randomsource.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.book.getMoveControl().setWantedPosition(d0, d1, d2, 1.0);
        }
    }

    static class BookAttackGoal extends Goal {
        private final MagicalBookEntity book;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public BookAttackGoal(MagicalBookEntity book) {
            this.book = book;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.book.getTarget();
            return livingentity != null && livingentity.isAlive() && this.book.canAttack(livingentity);
        }

        @Override
        public void start() {
            this.attackStep = 0;
        }

        @Override
        public void stop() {
            this.lastSeen = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            --this.attackTime;
            LivingEntity livingentity = this.book.getTarget();
            if (livingentity != null) {
                boolean flag = this.book.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.book.distanceToSqr(livingentity);
                if (d0 < 4.0) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.book.doHurtTarget(livingentity);
                    }

                    this.book.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = livingentity.getX() - this.book.getX();
                    double d2 = livingentity.getY(0.5) - this.book.getY(0.5);
                    double d3 = livingentity.getZ() - this.book.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                        }

                        if (this.attackStep > 1) {
                            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5;
                            if (!this.book.isSilent()) {
                                this.book.level().levelEvent(null, 1018, this.book.blockPosition(), 0);
                            }

                            for(int i = 0; i < 1; ++i) {
                                SmallFireball smallfireball = new SmallFireball(
                                        this.book.level(),
                                        this.book,
                                        this.book.getRandom().triangle(d1, 2.297 * d4),
                                        d2,
                                        this.book.getRandom().triangle(d3, 2.297 * d4)
                                );
                                smallfireball.setPos(smallfireball.getX(), this.book.getY(0.5) + 0.5, smallfireball.getZ());
                                this.book.level().addFreshEntity(smallfireball);
                            }
                        }
                    }

                    this.book.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.book.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.book.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}

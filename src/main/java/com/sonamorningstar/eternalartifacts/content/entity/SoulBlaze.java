package com.sonamorningstar.eternalartifacts.content.entity;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.SmallSoulFireball;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class SoulBlaze extends Blaze {
	public SoulBlaze(EntityType<? extends Blaze> entityType, Level level) {
		super(entityType, level);
	}
	
	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 30.0)
			.add(Attributes.ATTACK_DAMAGE, 8.0)
			.add(Attributes.MOVEMENT_SPEED, 0.46F)
			.add(Attributes.FOLLOW_RANGE, 64.0);
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.removeAllGoals(goal -> goal instanceof Blaze.BlazeAttackGoal);
		goalSelector.addGoal(4, new SoulBlazeAttackGoal(this));
	}
	
	//Returning false because i want to render blue flames on the renderer.
	@Override
	public boolean displayFireAnimation() {
		return false;
	}
	
	static class SoulBlazeAttackGoal extends Blaze.BlazeAttackGoal {
		public SoulBlazeAttackGoal(Blaze blaze) {
			super(blaze);
		}
		
		@Override
		public void tick() {
			--this.attackTime;
			LivingEntity target = this.blaze.getTarget();
			if (target != null) {
				boolean canSee = this.blaze.getSensing().hasLineOfSight(target);
				if (canSee) this.lastSeen = 0;
				else ++this.lastSeen;
				
				double d0 = this.blaze.distanceToSqr(target);
				if (d0 < 4.0) {
					if (!canSee) return;
					
					if (this.attackTime <= 0) {
						this.attackTime = 20;
						this.blaze.doHurtTarget(target);
					}
					
					this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
				} else if (d0 < this.getFollowDistance() * this.getFollowDistance() && canSee) {
					double deltaX = target.getX() - this.blaze.getX();
					double deltaY = target.getY(0.5) - this.blaze.getY(0.5);
					double deltaZ = target.getZ() - this.blaze.getZ();
					if (this.attackTime <= 0) {
						++this.attackStep;
						if (this.attackStep == 1) {
							this.attackTime = 60;
							this.blaze.setCharged(true);
						} else if (this.attackStep <= 4) {
							this.attackTime = 6;
						} else {
							this.attackTime = 100;
							this.attackStep = 0;
							this.blaze.setCharged(false);
						}
						
						if (this.attackStep > 1) {
							double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5;
							if (!this.blaze.isSilent()) {
								this.blaze.level().levelEvent(null, 1018, this.blaze.blockPosition(), 0);
							}
							
							for(int i = 0; i < 1; ++i) {
								SmallSoulFireball fireball = new SmallSoulFireball(
									this.blaze.level(), this.blaze,
									this.blaze.getRandom().triangle(deltaX, 2.297 * d4), deltaY,
									this.blaze.getRandom().triangle(deltaZ, 2.297 * d4)
								);
								fireball.setPos(fireball.getX(), this.blaze.getY(0.5) + 0.5, fireball.getZ());
								this.blaze.level().addFreshEntity(fireball);
							}
						}
					}
					
					this.blaze.getLookControl().setLookAt(target, 10.0F, 10.0F);
				} else if (this.lastSeen < 5) {
					this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
				}
				
				super.tick();
			}
		}
		
	}
}

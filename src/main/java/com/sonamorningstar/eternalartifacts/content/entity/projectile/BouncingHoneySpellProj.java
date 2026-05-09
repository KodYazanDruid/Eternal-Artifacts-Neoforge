package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class BouncingHoneySpellProj extends AbstractHurtingProjectile {
	public float damage;
	private int age;
	private static final float AIR_DRAG = 1;
	private static final double GRAVITY = 0.00;
	private static final double BOUNCE_DAMPING = 1;
	private static final double MIN_BOUNCE_SPEED = 0.1;
	
	private static final int MAX_LIFE = 200;
	
	public BouncingHoneySpellProj(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
		super(entityType, level);
	}
	
	public BouncingHoneySpellProj(Level level, LivingEntity caster, double x, double y, double z, float amplifiedDamage) {
		super(ModEntities.SPELL_BOUNCING_HONEY.get(), caster, 0,0,0, level);
		this.damage = amplifiedDamage;
		this.noPhysics = true;
	}
	
	@Override
	public void tick() {
		this.baseTick();
		age++;
		if (age > MAX_LIFE) {
			discard();
			return;
		}
		
		Vec3 velocity = getDeltaMovement();
		if (!isNoGravity()) {
			velocity = velocity.add(0, -GRAVITY, 0);
		}
		velocity = velocity.scale(AIR_DRAG);
		setDeltaMovement(velocity);
		moveWithSubSteps(velocity);
		if (this.onGround() && getDeltaMovement().length() < 0.05) {
			setDeltaMovement(Vec3.ZERO);
		}
	}
	
	private void moveWithSubSteps(Vec3 velocity) {
		double maxStep = 0.25;
		
		double length = velocity.length();
		int steps = Math.max(1, (int) Math.ceil(length / maxStep));
		
		Vec3 step = velocity.scale(1.0 / steps);
		
		for (int i = 0; i < steps; i++) {
			Vec3 start = this.position();
			Vec3 end = start.add(step);
			
			HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(
				this,
				this::canHitEntity,
				ClipContext.Block.COLLIDER
			);
			
			if (hitResult.getType() != HitResult.Type.MISS) {
				if (!EventHooks.onProjectileImpact(this, hitResult)) {
					this.onHit(hitResult);
				}
				return;
			}
			setPos(end);
		}
	}
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		Vec3 velocity = this.getDeltaMovement();
		Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
		Vec3 hitLoc = result.getLocation();
		
		double dot = velocity.dot(normal);
		Vec3 reflected = velocity.subtract(normal.scale(2 * dot));
		
		reflected = reflected.scale(BOUNCE_DAMPING);
		
		if (Math.abs(reflected.y) < 0.1) {
			reflected = new Vec3(reflected.x, 0, reflected.z);
		}
		
		if (reflected.length() < MIN_BOUNCE_SPEED) {
			reflected = Vec3.ZERO;
		}
		
		setDeltaMovement(reflected);
		
		double push = 0.1;
		setPos(
			hitLoc.x + normal.x * push,
			hitLoc.y + normal.y * push,
			hitLoc.z + normal.z * push
		);
		
		playSound(SoundEvents.HONEY_BLOCK_HIT, 0.5f, 1.0f);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		Entity target = result.getEntity();
		
		boolean isHurt = SpellDamageHelper.hurtWithSpellDamage(this, target, damage);
		if (isHurt && target instanceof LivingEntity living) {
			living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
		}
		
		if (!target.noPhysics) {
			Vec3 velocity = getDeltaMovement();
			Vec3 normal = position().subtract(target.position()).normalize();
			double dot = velocity.dot(normal);
			Vec3 reflected = velocity.subtract(normal.scale(2 * dot));
			reflected = reflected.scale(BOUNCE_DAMPING);
			setDeltaMovement(reflected);
			playSound(SoundEvents.HONEY_BLOCK_HIT, 0.5f, 0.8f);
		}
	}
	
	@Override
	protected boolean shouldBurn() {return false;}
	@Override
	public boolean isAttackable() {return false;}
	
	@Override
	protected float getInertia() {
		return AIR_DRAG;
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putFloat("damage", damage);
		tag.putInt("age", age);
	}
	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		damage = tag.getFloat("damage");
		age = tag.getInt("age");
	}
}

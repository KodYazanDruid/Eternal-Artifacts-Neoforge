package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.Tags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ShadowRaySpell extends Spell {
	private static final float LENGTH = 32.0F;
	private static final double EPSILON = 0.01D;
	private static final double PARTICLE_STEP = 0.1D;
	
	public ShadowRaySpell(Properties props) {
		super(props);
	}
	
	@Override
	public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
		if (checkCooldown(caster, tome.getItem())) return false;
		if (level.isClientSide) return true;
		
		Vec3 start = RayTraceHelper.getStartVec(caster);
		Vec3 dir = caster.getViewVector(1.0F).normalize();
		
		double remaining = LENGTH;
		
		Set<UUID> hitEntities = new HashSet<>();
		while (remaining > 0.0D) {
			Vec3 end = start.add(dir.scale(remaining));
			
			BlockHitResult blockHit = level.clip(new ClipContext(
				start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster
			));
			
			AABB sweepBox = new AABB(start, end).inflate(1.0D);
			List<Entity> candidates = level.getEntities(caster, sweepBox, e -> e instanceof LivingEntity && e.isPickable() && e.isAlive());
			
			EntityHitResult entityHit = null;
			double closestEntityDistSqr = Double.MAX_VALUE;
			for (Entity candidate : candidates) {
				AABB bb = candidate.getBoundingBox().inflate(0.2D);
				Vec3 clip = bb.clip(start, end).orElse(null);
				if (clip == null) continue;
				double d = start.distanceToSqr(clip);
				if (d < closestEntityDistSqr) {
					closestEntityDistSqr = d;
					entityHit = new EntityHitResult(candidate, clip);
				}
			}
			
			double blockDistSqr = blockHit.getType() == HitResult.Type.BLOCK ? start.distanceToSqr(blockHit.getLocation()) : Double.MAX_VALUE;
			
			if (entityHit != null && closestEntityDistSqr < blockDistSqr) {
				Entity target = entityHit.getEntity();
				
				if (hitEntities.add(target.getUUID())) {
					SpellDamageHelper.hurtWithSpellDamage(caster, caster, target, amplifiedDamage);
				}
				
				spawnBeamParticles(level, start, entityHit.getLocation());
				
				double used = Math.sqrt(closestEntityDistSqr);
				remaining -= used;
				if (remaining <= 0.0D) return true;
				
				start = entityHit.getLocation().add(dir.scale(EPSILON));
				continue;
			}
			
			if (blockHit.getType() != HitResult.Type.BLOCK) {
				spawnBeamParticles(level, start, end);
				return true;
			}
			
			BlockPos pos = blockHit.getBlockPos();
			BlockState state = level.getBlockState(pos);
			
			spawnBeamParticles(level, start, blockHit.getLocation());
			
			if (isPassThrough(state)) {
				double used = Math.sqrt(blockDistSqr);
				remaining -= used;
				start = blockHit.getLocation().add(dir.scale(EPSILON));
				continue;
			}
			
			double used = Math.sqrt(blockDistSqr);
			remaining -= used;
			if (remaining <= 0.0D) return true;
			
			dir = reflect(dir, blockHit.getDirection()).normalize();
			start = blockHit.getLocation().add(dir.scale(EPSILON));
		}
		return true;
	}
	
	private static boolean isPassThrough(BlockState state) {
		return !state.is(BlockTags.IMPERMEABLE) && (
			state.is(BlockTags.CANDLE_CAKES) || state.is(Tags.Blocks.GLASS)
		);
	}
	
	private static Vec3 reflect(Vec3 in, Direction hitFace) {
		Vec3 normal = switch (hitFace.getAxis()) {
			case X -> new Vec3(hitFace.getStepX(), 0, 0);
			case Y -> new Vec3(0, hitFace.getStepY(), 0);
			case Z -> new Vec3(0, 0, hitFace.getStepZ());
		};
		
		// r = d - 2(d.n)n
		double dot = in.dot(normal);
		return in.subtract(normal.scale(2.0D * dot));
	}
	
	private static void spawnBeamParticles(Level level, Vec3 from, Vec3 to) {
		if (!(level instanceof ServerLevel serverLevel)) return;
		
		Vec3 delta = to.subtract(from);
		double length = delta.length();
		if (length <= 1.0E-6D) return;
		
		Vec3 dir = delta.scale(1.0D / length);
		int count = Math.max(1, (int) Math.ceil(length / PARTICLE_STEP));
		
		for (int i = 0; i <= count; i++) {
			double t = Math.min(length, i * PARTICLE_STEP);
			Vec3 p = from.add(dir.scale(t));
			
			//serverLevel.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			serverLevel.sendParticles(ParticleTypes.PORTAL, p.x, p.y, p.z, 1, 0, 0,0, 0.0D);
		}
	}
}

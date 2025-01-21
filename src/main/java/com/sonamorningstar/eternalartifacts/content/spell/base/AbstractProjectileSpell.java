package com.sonamorningstar.eternalartifacts.content.spell.base;

import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class AbstractProjectileSpell extends Spell {
    public AbstractProjectileSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack tome, LivingEntity caster, InteractionHand hand, Level level, RandomSource random, float amplifiedDamage) {
        double reach = getReachDistance(caster);
        HitResult result = caster instanceof Player player ?
                getHitResultForPlayer(player, reach, getBlockClipType(), getFluidClipType()) :
                reach > -1 ? RayTraceHelper.retraceGeneric(caster) : RayTraceHelper.retraceGeneric(caster, reach);
        if (shouldCast(result)) {
            Vec3 startPos = getStartVector(caster);
            Vec3 shootVector = getShootVector(caster, startPos, result);
            Projectile projectile = createProjectile(level, caster, amplifiedDamage, result, shootVector);
            if (projectile != null) {
                projectile.shoot(shootVector.x, shootVector.y, shootVector.z, getVelocity(caster, projectile), getInaccuracy(caster, projectile));
                projectile.setPos(startPos.x, startPos.y, startPos.z);
                level.addFreshEntity(projectile);
                return true;
            }
        }
        return false;
    }

    @Nullable
    protected abstract Projectile createProjectile(Level level, LivingEntity caster, float amplifiedDamage, HitResult result, Vec3 shootVector);

    protected boolean shouldCast(HitResult result) {
        return (result instanceof EntityHitResult && ignoreEntities()) ||
                (result instanceof BlockHitResult && ignoreBlocks()) ||
                result.getType() == getHitType();
    }

    protected boolean ignoreEntities() {
        return true;
    }
    protected boolean ignoreBlocks() {
        return false;
    }

    protected HitResult getHitResultForPlayer(Player player, double reach, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        return reach == -1 ? RayTraceHelper.retraceGenericForPlayer(player, blockMode, fluidMode) :
                RayTraceHelper.retraceGenericForPlayer(player, reach, blockMode, fluidMode);
    }
    protected ClipContext.Block getBlockClipType() {
        return ClipContext.Block.COLLIDER;
    }
    protected ClipContext.Fluid getFluidClipType() {
        return ClipContext.Fluid.ANY;
    }
    protected HitResult.Type getHitType() {
        return HitResult.Type.MISS;
    }

    protected Vec3 getStartVector(LivingEntity caster) {
        return caster.getEyePosition();
    }
    protected Vec3 getShootVector(LivingEntity caster, Vec3 start, HitResult result) {
        return start.vectorTo(result.getLocation());
    }
    protected float getVelocity(LivingEntity caster, Projectile projectile) {
        return 1.5F;
    }
    protected float getInaccuracy(LivingEntity caster, Projectile projectile) {
        return 0.0F;
    }

    //-1 to use entity attributes
    protected double getReachDistance(LivingEntity caster) {
        return -1;
    }
}

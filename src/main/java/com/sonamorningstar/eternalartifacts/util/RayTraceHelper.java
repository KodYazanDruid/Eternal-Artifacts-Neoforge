package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;

public class RayTraceHelper {
    public static BlockHitResult retrace(LivingEntity entity) {
        return retrace(entity, getBlockReachDistance(entity), ClipContext.Fluid.ANY);
    }

    public static BlockHitResult retrace(LivingEntity entity, double reach) {
        return retrace(entity, reach, ClipContext.Fluid.ANY);
    }

    public static BlockHitResult retrace(LivingEntity entity, ClipContext.Fluid fluidMode) {
        return retrace(entity, ClipContext.Block.COLLIDER, fluidMode);
    }

    public static BlockHitResult retrace(LivingEntity entity, double reach, ClipContext.Fluid fluidMode) {
        return retrace(entity, reach, ClipContext.Block.COLLIDER, fluidMode);
    }

    public static BlockHitResult retrace(LivingEntity entity, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        return entity.level().clip(new ClipContext(getStartVec(entity), getEndVec(entity), blockMode, fluidMode, entity));
    }

    public static BlockHitResult retrace(LivingEntity entity, double reach, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        return entity.level().clip(new ClipContext(getStartVec(entity), getEndVec(entity, reach), blockMode, fluidMode, entity));
    }

    public static EntityHitResult retraceEntity(LivingEntity entity) {
        return ProjectileUtil.getEntityHitResult(entity.level(), entity, getStartVec(entity), getEndVec(entity), entity.getBoundingBox(),
                (e) -> !e.isSpectator(), (float) getBlockReachDistance(entity));
    }

    public static HitResult retraceGeneric(LivingEntity living) {
        return retraceGeneric(living, getEntityReachDistance(living));
    }

    public static HitResult retraceGeneric(LivingEntity living, double reach) {
        return ProjectileUtil.getHitResultOnViewVector(living, e -> !e.isSpectator(), reach);
    }

    public static HitResult retraceGenericForPlayer(Player player) {
        return retraceGenericForPlayer(player, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY);
    }

    public static HitResult retraceGenericForPlayer(Player player, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        double entityReach = getEntityReachDistance(player);
        double blockReach = getBlockReachDistance(player);
        HitResult entityHit = ProjectileUtil.getHitResultOnViewVector(player, e -> !e.isSpectator(), entityReach);
        if (entityHit.getType() != HitResult.Type.MISS) {
            return entityHit;
        }
        return player.level().clip(new ClipContext(getStartVec(player), getEndVec(player, blockReach), blockMode, fluidMode, player));
    }

    public static HitResult retraceGenericForPlayer(Player player, double reach, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        HitResult entityHit = ProjectileUtil.getHitResultOnViewVector(player, e -> !e.isSpectator(), reach);
        if (entityHit.getType() != HitResult.Type.MISS) {
            return entityHit;
        }
        return player.level().clip(new ClipContext(getStartVec(player), getEndVec(player, reach), blockMode, fluidMode, player));
    }


    public static Vec3 getStartVec(LivingEntity player) {
        return getCorrectedHeadVec(player);
    }

    public static Vec3 getEndVec(LivingEntity player) {
        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getViewVector(0.0F);
        double reach = getBlockReachDistance(player);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vec3 getEndVec(LivingEntity player, double reach) {
        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getViewVector(0.0F);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    public static Vec3 getCorrectedHeadVec(LivingEntity player) {
        return new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
    }

    public static double getBlockReachDistance(LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player instanceof ServerPlayer serverPlayer) {
                return getBlockReachDistanceServer(serverPlayer);
            } else return getBlockReachDistanceClient();
        }else return getBlockReachDistanceServer(entity);
    }


    private static double getBlockReachDistanceServer(LivingEntity entity) {
        double base = entity instanceof Player player ? player.isCreative() ? 5.0D : 4.5D : 5.0D;
        AttributeInstance attribute = entity.getAttribute(NeoForgeMod.BLOCK_REACH.value());
        return attribute == null ? base : attribute.getValue();
    }

    private static double getEntityReachDistance(LivingEntity entity) {
        double base = entity instanceof Player player ? player.isCreative() ? 5.0D : 4.5D : 5.0D;
        AttributeInstance attribute = entity.getAttribute(NeoForgeMod.ENTITY_REACH.value());
        return attribute == null ? base: attribute.getValue();
    }

    private static double getBlockReachDistanceClient() {
        Minecraft mc = Minecraft.getInstance();
        MultiPlayerGameMode gamemode = mc.gameMode;
        return gamemode == null ? mc.player.isCreative() ? 5.0 : 4.5D : gamemode.getPickRange();
    }
}

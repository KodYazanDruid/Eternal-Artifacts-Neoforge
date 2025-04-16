package com.sonamorningstar.eternalartifacts.api.morph;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.EntityExposer;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.LivingEntityExposer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class MobModelRenderer {
	private static final Minecraft mc = Minecraft.getInstance();
	
	@SubscribeEvent
	public static void playerRenderPre(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();
		
		if (mc.getConnection() == null ||
			mc.getConnection().getPlayerInfo(player.getUUID()) == null ||
			mc.getConnection().getPlayerInfo(player.getUUID()).getGameMode() == GameType.SPECTATOR)
			return;
		
		mc.getEntityRenderDispatcher().setRenderShadow(false);
		if (renderEntityModel(player, event)) {
			event.setCanceled(true);
			mc.getEntityRenderDispatcher().setRenderShadow(true);
		}
	}
	
	@Nullable
	public static LivingEntity dummy = null;
	private static boolean renderEntityModel(Player player, RenderPlayerEvent event) {
		if (dummy == null) return false;
		
		PoseStack pose = event.getPoseStack();
		MultiBufferSource buffer = event.getMultiBufferSource();
		int packedLight = event.getPackedLight();
		float partialTick = event.getPartialTick();
		
		dummy.tickCount = player.tickCount;
		dummy.deathTime = player.deathTime;
		dummy.xOld = player.xOld;
		dummy.yOld = player.yOld;
		dummy.zOld = player.zOld;
		dummy.xo = player.xo;
		dummy.yo = player.yo;
		dummy.zo = player.zo;
		dummy.yBodyRot = player.yBodyRot;
		dummy.yBodyRotO = player.yBodyRotO;
		dummy.yHeadRot = player.yHeadRot;
		dummy.yHeadRotO = player.yHeadRotO;
		dummy.xRotO = player.xRotO;
		dummy.yRotO = player.yRotO;
		dummy.hurtTime = player.hurtTime;
		dummy.swinging = player.swinging;
		dummy.swingTime = player.swingTime;
		dummy.oAttackAnim = player.oAttackAnim;
		dummy.attackAnim = player.attackAnim;
		dummy.swingingArm = player.swingingArm;
		dummy.flyDist = player.flyDist;
		dummy.fallDistance = player.fallDistance;
		dummy.walkDist = player.walkDist;
		dummy.walkDistO = player.walkDistO;
		dummy.moveDist = player.moveDist;
		dummy.nextStep = player.nextStep;
		dummy.setPosRaw(player.getX(), player.getY(), player.getZ());
		dummy.setPose(player.getPose());
		dummy.setDeltaMovement(player.getDeltaMovement());
		dummy.setXRot(player.getXRot());
		dummy.setYRot(player.getYRot());
		dummy.setOnGround(player.onGround());
		dummy.setSwimming(player.isSwimming());
		dummy.setSprinting(player.isSprinting());
		dummy.setArrowCount(player.getArrowCount());
		dummy.setCustomName(player.getCustomName());
		dummy.setOnGround(player.onGround());
		dummy.setIsInPowderSnow(player.isInPowderSnow);
		dummy.setInvisible(player.isInvisible());
		dummy.setInvulnerable(player.isInvulnerable());
		
		dummy.setItemSlot(EquipmentSlot.MAINHAND, player.getMainHandItem());
		dummy.setItemSlot(EquipmentSlot.OFFHAND, player.getOffhandItem());
		dummy.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD));
		dummy.setItemSlot(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST));
		dummy.setItemSlot(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS));
		dummy.setItemSlot(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET));
		for (int i = 0; i < CharmStorage.get(player).getSlots(); i++) {
			CharmStorage.get(dummy).setStackInSlot(i, CharmStorage.get(player).getStackInSlot(i));
		}
		
		Class<? extends LivingEntity> entityClass = dummy.getClass();
		try {
			Field dummyAnimField = entityClass.getField("walkAnimation");
			dummyAnimField.setAccessible(true);
			dummyAnimField.set(dummy, player.walkAnimation);
		} catch (NoSuchFieldException e) {
			EternalArtifacts.LOGGER.error("Could not find field walkAnimation in {}", entityClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		if (dummy instanceof LivingEntityExposer dummyExp && player instanceof LivingEntityExposer playerExp) {
			dummyExp.setSwimAmountExp(playerExp.getSwimAmountExp());
			dummyExp.setSwimAmount0Exp(playerExp.getSwimAmount0Exp());
			dummyExp.setAutoSpinAttackTicks(playerExp.getAutoSpinAttackTicks());
		}
		
		if (dummy instanceof LivingEntityExposer exp) {
			exp.setFallFlyTicks(player.getFallFlyingTicks());
			exp.setLivingEntityFlagExp(4, player.isAutoSpinAttack());
			exp.setLivingEntityFlagExp(1, player.isUsingItem());
			exp.setUseItemExp(player.getUseItem());
			exp.setUseItemRemainingTicksExp(player.getUseItemRemainingTicks());
			exp.updateUsingItemExp(dummy.getUseItem());
		}
		
		if (dummy instanceof EntityExposer exp) {
			exp.setWasTouchingWater(player.isInWater());
			exp.setSharedFlagExp(7, player.isFallFlying());
			exp.forceVehicle(player.getVehicle());
		}
		
		player.getSleepingPos().ifPresent(dummy::setSleepingPos);
		
		if (dummy instanceof Mob mob) {
			mob.setAggressive(player.isUsingItem());
		}
		
		dummy.getUseItem().onUseTick(player.level(), dummy, dummy.getUseItemRemainingTicks());
		
		dummy.hurtTime = player.hurtTime;
		
		if (player != mc.player) {
			dummy.setCustomName(player.getCustomName());
		}
		float yaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
		assert player instanceof AbstractClientPlayer;
		setupDummyRotations((AbstractClientPlayer) player, dummy, pose, partialTick);
		mc.getEntityRenderDispatcher().render(dummy, 0, 0, 0, yaw, partialTick, pose, buffer, packedLight);
		return true;
	}
	
	public static void setupDummyRotations(AbstractClientPlayer host, LivingEntity dummy, PoseStack pose, float partialTick) {
		float swimAmount = dummy.getSwimAmount(partialTick);
		float viewXRot = dummy.getViewXRot(partialTick);
		
		pose.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.rotLerp(partialTick, dummy.yBodyRotO, dummy.yBodyRot)));
		
		if (dummy.isFallFlying()) {
			float fallFlyTicks = (float) dummy.getFallFlyingTicks() + partialTick;
			float f3 = Mth.clamp(fallFlyTicks * fallFlyTicks / 100.0F, 0.0F, 1.0F);
			if (!dummy.isAutoSpinAttack()) {
				pose.mulPose(Axis.XP.rotationDegrees(f3 * (-90.0F - viewXRot)));
			}
			
			Vec3 movement = host.getDeltaMovementLerped(partialTick);
			Vec3 facing = dummy.getViewVector(partialTick);
			double moveDistSqr = movement.horizontalDistanceSqr();
			double facingDistSqr = facing.horizontalDistanceSqr();
			
			if (moveDistSqr > 0.0 && facingDistSqr > 0.0) {
				double cosAngle = (movement.x * facing.x + movement.z * facing.z) / Math.sqrt(moveDistSqr * facingDistSqr);
				double crossProduct = movement.x * facing.z - movement.z * facing.x;
				
				float rotationAdjustment = (float) (Math.signum(crossProduct) * Math.acos(cosAngle));
				pose.mulPose(Axis.YP.rotation(rotationAdjustment));
			}
			
		} else if (swimAmount > 0.0F) {
			float f4 = dummy.isInWater() || dummy.isInFluidType((fluidType, height) -> dummy.canSwimInFluidType(fluidType)) ? -90.0F - dummy.getXRot() : -90.0F;
			float f5 = Mth.lerp(swimAmount, 0.0F, f4);
			pose.mulPose(Axis.XP.rotationDegrees(f5));
			if (dummy.isVisuallySwimming()) {
				pose.translate(0.0F, -1.0F, 0.3F);
			}
		}
		
		pose.mulPose(Axis.YP.rotationDegrees(180.0F + Mth.rotLerp(partialTick, dummy.yBodyRotO, dummy.yBodyRot)));
	}
}
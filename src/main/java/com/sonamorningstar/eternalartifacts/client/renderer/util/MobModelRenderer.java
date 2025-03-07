package com.sonamorningstar.eternalartifacts.client.renderer.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.CutlassModifier;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.EntityExposer;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.LivingEntityExposer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class MobModelRenderer {
	private static final Minecraft mc = Minecraft.getInstance();
	private static final Collection<Item> HEADS = CutlassModifier.ENTITY_HEAD_MAP.values();
	
	@SubscribeEvent
	public static void playerRenderPre(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();
		
		if (mc.getConnection() == null ||
			mc.getConnection().getPlayerInfo(player.getUUID()) == null ||
			mc.getConnection().getPlayerInfo(player.getUUID()).getGameMode() == GameType.SPECTATOR)
			return;
		
		ItemStack headCharm = PlayerCharmManager.findCharm(player, st -> !st.is(Items.DRAGON_HEAD) && HEADS.contains(st.getItem()));
		if (!headCharm.isEmpty()) {
			EntityType<? extends LivingEntity> entityType = getEntityType(headCharm);
			
			if (entityType != null) {
				dummy = entityType.create(player.level());
				renderEntityModel(entityType, player, event);
				event.setCanceled(true);
			}
		}
	}
	
	private static @Nullable EntityType<? extends LivingEntity> getEntityType(ItemStack headCharm) {
		EntityType<? extends LivingEntity> entityType = null;
		for (Map.Entry<EntityType<? extends LivingEntity>, Item> entry : CutlassModifier.ENTITY_HEAD_MAP.entrySet()) {
			if (!headCharm.is(Items.DRAGON_HEAD) && headCharm.is(entry.getValue())) {
				entityType = entry.getKey();
			}
		}
		return entityType;
	}
	
	public static LivingEntity dummy = null;
	private static <T extends LivingEntity> void renderEntityModel(EntityType<T> entityType, Player player, RenderPlayerEvent event) {
		PoseStack pose = event.getPoseStack();
		MultiBufferSource buffer = event.getMultiBufferSource();
		int packedLight = event.getPackedLight();
		float partialTick = event.getPartialTick();
		
		if (dummy == null) dummy = entityType.create(player.level());
		if (dummy == null) return;
		Class<? extends LivingEntity> entityClass = dummy.getClass();
		
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
		dummy.setPos(player.getX(), player.getY(), player.getZ());
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
		
		dummy.setItemSlot(EquipmentSlot.MAINHAND, player.getMainHandItem());
		dummy.setItemSlot(EquipmentSlot.OFFHAND, player.getOffhandItem());
		dummy.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD));
		dummy.setItemSlot(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST));
		dummy.setItemSlot(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS));
		dummy.setItemSlot(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET));
		for (int i = 0; i < CharmStorage.get(player).getSlots(); i++) {
			CharmStorage.get(dummy).setStackInSlot(i, CharmStorage.get(player).getStackInSlot(i));
		}
		
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
		}
		
		player.getSleepingPos().ifPresent(dummy::setSleepingPos);
		if (player.getVehicle() != null) dummy.startRiding(player.getVehicle());
		else dummy.stopRiding();
		
		if (Minecraft.getInstance().player != null) {
			dummy.setInvisible(player.isInvisibleTo(Minecraft.getInstance().player));
		}
		
		if (dummy instanceof Mob mob) {
			mob.setAggressive(player.isUsingItem());
		}
		
		dummy.getUseItem().onUseTick(player.level(), dummy, dummy.getUseItemRemainingTicks());
		
		dummy.hurtTime = player.hurtTime;
		
		if (player != mc.player) {
			dummy.setCustomName(player.getCustomName());
		}
		float yaw = Mth.lerp(partialTick, player.yRotO, player.getYRot());
		pose.pushPose();
		assert player instanceof AbstractClientPlayer;
		setupRotations((AbstractClientPlayer) player, dummy, pose, partialTick);
		mc.getEntityRenderDispatcher().render(dummy, 0, 0, 0, yaw, partialTick, pose, buffer, packedLight);
		pose.popPose();
	}
	
	private static void setupRotations(AbstractClientPlayer host, LivingEntity dummy, PoseStack pose, float partialTick) {
		float swimAmount = dummy.getSwimAmount(partialTick);
		float viewXRot = dummy.getViewXRot(partialTick);
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
	}
}
package com.sonamorningstar.eternalartifacts.content.entity.client.flyingitem;

import com.mojang.math.Axis;
import net.minecraft.util.Mth;

public final class ProjectileOrientations {
	
	private ProjectileOrientations() {}
	
	public static ProjectileOrientation simpleSpin() {
		return (poseStack, entity, partialTicks) -> {
			float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
			float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
			
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
			poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
			
			float spin = entity.getSpinAngle(partialTicks);
			if (spin != 0.0F) {
				poseStack.mulPose(Axis.YP.rotationDegrees(spin));
			}
		};
	}
	
	public static ProjectileOrientation pointForward() {
		return (poseStack, entity, partialTicks) -> {
			poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
			poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
			
			float roll = entity.getSpinAngle(partialTicks);
			if (roll != 0.0F) {
				poseStack.mulPose(Axis.ZP.rotationDegrees(roll));
			}
		};
	}
	
	public static ProjectileOrientation tumbling() {
		return (poseStack, entity, partialTicks) -> {
			poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
			poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
			
			float roll = entity.getSpinAngle(partialTicks);
			if (roll != 0.0F) {
				poseStack.mulPose(Axis.XP.rotationDegrees(roll));
			}
		};
	}
}

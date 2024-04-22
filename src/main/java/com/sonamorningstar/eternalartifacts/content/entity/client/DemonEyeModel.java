package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.entity.DemonEyeEntity;
import com.sonamorningstar.eternalartifacts.content.entity.animations.ModAnimationDefinitions;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DemonEyeModel<T extends DemonEyeEntity> extends HierarchicalModel<T> {
	private final ModelPart demon_eye;
	private final ModelPart head;

	public DemonEyeModel(ModelPart root) {
		this.demon_eye = root.getChild("demon_eye");
		this.head = demon_eye;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition demon_eye = partdefinition.addOrReplaceChild("demon_eye", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition eye = demon_eye.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition iris = eye.addOrReplaceChild("iris", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 4).addBox(-1.0F, -1.0F, -0.75F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -4.0F));

		PartDefinition capillary = demon_eye.addOrReplaceChild("capillary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 4.0F));

		PartDefinition capillary1 = capillary.addOrReplaceChild("capillary1", CubeListBuilder.create(), PartPose.offset(4.0F, 0.0F, 0.0F));

		PartDefinition capillary1_r1 = capillary1.addOrReplaceChild("capillary1_r1", CubeListBuilder.create().texOffs(16, 8).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

		PartDefinition capillary2 = capillary.addOrReplaceChild("capillary2", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition capillary2_r1 = capillary2.addOrReplaceChild("capillary2_r1", CubeListBuilder.create().texOffs(-8, 24).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition capillary3 = capillary.addOrReplaceChild("capillary3", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, 0.0F));

		PartDefinition capillary3_r1 = capillary3.addOrReplaceChild("capillary3_r1", CubeListBuilder.create().texOffs(16, 16).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

		PartDefinition capillary4 = capillary.addOrReplaceChild("capillary4", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition capillary4_r1 = capillary4.addOrReplaceChild("capillary4_r1", CubeListBuilder.create().texOffs(-8, 16).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(DemonEyeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		root().getAllParts().forEach(ModelPart::resetPose);
		applyRotation(netHeadYaw, headPitch);

		animate(entity.idleState, ModAnimationDefinitions.IDLE, ageInTicks, 1);

	}

	private void applyRotation(float yaw, float pitch) {
		this.head.yRot = yaw * ((float)Math.PI / 180F);
		this.head.xRot = pitch * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		demon_eye.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return demon_eye;
	}
}
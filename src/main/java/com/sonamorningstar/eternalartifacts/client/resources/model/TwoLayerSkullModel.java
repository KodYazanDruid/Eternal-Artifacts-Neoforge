package com.sonamorningstar.eternalartifacts.client.resources.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.level.block.SkullBlock;

@Getter
public class TwoLayerSkullModel extends SkullModelBase {
	final SkullBlock.Type type;
	final ModelPart head;
	final ModelPart overlay;
	
	public TwoLayerSkullModel(SkullBlock.Type type, ModelPart head, ModelPart overlay) {
		this.type = type;
		this.head = head;
		this.overlay = overlay;
	}
	
	public static LayerDefinition createBaseLayer(int width, int height) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
			.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
		return LayerDefinition.create(meshdefinition, width, height);
	}
	
	public static LayerDefinition createOverlayLayer(int width, int height) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("overlay", CubeListBuilder.create().texOffs(0, 0)
			.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
		return LayerDefinition.create(meshdefinition, width, height);
	}
	
	@Override
	public void setupAnim(float pMouthAnimation, float pYRot, float pXRot) {
		this.head.yRot = pYRot * ((float)Math.PI / 180F);
		this.head.xRot = pXRot * ((float)Math.PI / 180F);
		this.overlay.yRot = this.head.yRot;
		this.overlay.xRot = this.head.xRot;
	}
	
	public void renderSeparateConsumers(PoseStack pose, VertexConsumer base, VertexConsumer overlay, int light, int packedOverlay, int r, int g, int b, int a) {
		this.head.render(pose, base, light, packedOverlay, r, g, b, a);
		this.overlay.render(pose, overlay, light, packedOverlay, r, g, b, a);
	}
	
	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
		this.head.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
		this.overlay.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
	}
	
	public SkullBlock.Type getSkullType() {
		return this.type;
	}
}

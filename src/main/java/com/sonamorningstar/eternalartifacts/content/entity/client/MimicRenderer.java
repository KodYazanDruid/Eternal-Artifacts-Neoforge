package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.content.entity.MimicEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Calendar;

public class MimicRenderer extends EntityRenderer<MimicEntity> {
    private final ModelPart bottom;
    private final ModelPart lid;
    private final ModelPart lock;
    private boolean christmas;
    
    public MimicRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26) {
            this.christmas = true;
        }
        
        ModelPart modelpart = ctx.bakeLayer(ModelLayers.CHEST);
        this.bottom = modelpart.getChild("bottom");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
    }
    
    @Override
    public void render(MimicEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.pushPose();
        float rotationYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YN.rotationDegrees(rotationYaw));
        poseStack.translate(-0.5, 0, -0.5);
        VertexConsumer consumer = getMaterial().buffer(buffer, RenderType::entityCutout);
        float openness = entity.getOpenness(partialTick);
        openness = 1.0f - openness;
        openness = 1.0f - openness * openness * openness;
        render(poseStack, consumer, lid, lock, bottom, openness, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0f));
        poseStack.popPose();
    }
    
    private void render(
        PoseStack poseStack,
        VertexConsumer consumer,
        ModelPart lidPart,
        ModelPart lockPart,
        ModelPart bottomPart,
        float lidAngle,
        int packedLight,
        int packedOverlay
    ) {
        lidPart.xRot = -(lidAngle * (float) (Math.PI / 2));
        lockPart.xRot = lidPart.xRot;
        lidPart.render(poseStack, consumer, packedLight, packedOverlay);
        lockPart.render(poseStack, consumer, packedLight, packedOverlay);
        bottomPart.render(poseStack, consumer, packedLight, packedOverlay);
    }
    
    protected Material getMaterial() {
        return christmas ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;
    }
    
    @Override
    public ResourceLocation getTextureLocation(MimicEntity mimic) {
        return new ResourceLocation("minecraft", "textures/entity/chest/normal.png");
    }
}

package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.content.entity.MagicalBookEntity;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.isEntityUpsideDown;

public class MagicalBookRenderer extends EntityRenderer<MagicalBookEntity> {

    private final BookModel model;

    public MagicalBookRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        model = new BookModel(ctx.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(MagicalBookEntity book, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buff, int light) {
        VertexConsumer consumer = buff.getBuffer(RenderType.entitySolid(getTextureLocation(book)));
        poseStack.pushPose();
        setupRotations(book, poseStack, partialTick, yaw);
        poseStack.translate(0, 0.5f, 0);
        model.setupAnim(book.tickCount + partialTick, 0.9F, 0.1F, book.getBookOpenAmount(partialTick));
        model.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
        super.render(book, yaw, partialTick, poseStack, buff, light);
    }

    private void setupRotations(MagicalBookEntity book, PoseStack poseStack, float partialTick, float yaw) {
        if (book.deathTime > 0) {
            float f = ((float)book.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) f = 1.0F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(f * 90.0F));
        } else if (isEntityUpsideDown(book)) {
            poseStack.translate(0.0F, book.getBbHeight() + 0.1F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        }
        //poseStack.mulPose(Axis.YP.rotationDegrees(book.getYRot() * (180.F / (float) Math.PI)));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.ZN.rotationDegrees(45F));
        poseStack.translate(-0.5F, -0.25F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicalBookEntity entity) {
        return new ResourceLocation("textures/entity/enchanting_table_book.png");
    }

}

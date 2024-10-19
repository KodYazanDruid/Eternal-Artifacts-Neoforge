package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Tornado;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TornadoRenderer extends EntityRenderer<Tornado> {
    private final TornadoModel model;
    public TornadoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        model = new TornadoModel(ctx.bakeLayer(ModModelLayers.TORNADO_LAYER));
    }

    @Override
    public void render(Tornado tornado, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buff, int light) {
        VertexConsumer consumer = buff.getBuffer(RenderType.entitySolid(getTextureLocation(tornado)));
        poseStack.pushPose();
        model.setupAnim(tornado.tickCount + partialTick);
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Tornado tornado) {
        return new ResourceLocation("textures/block/sand.png");
    }
}

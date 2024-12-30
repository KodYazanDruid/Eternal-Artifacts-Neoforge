package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Missile;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MissileRenderer extends EntityRenderer<Missile> {
    private final MissileModel model;
    public MissileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new MissileModel(ctx.bakeLayer(ModModelLayers.MISSILE_LAYER));
    }

    @Override
    public void render(Missile missile, float yaw, float delta, PoseStack pose, MultiBufferSource buff, int light) {
        VertexConsumer consumer = buff.getBuffer(RenderType.entitySolid(getTextureLocation(missile)));
        model.renderToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }

    @Override
    public ResourceLocation getTextureLocation(Missile entity) {
        return new ResourceLocation("textures/block/stone.png");
    }
}

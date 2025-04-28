package com.sonamorningstar.eternalartifacts.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ProtectiveAuraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation LAYER_LOCATION = new ResourceLocation(MODID,"textures/entity/transparent_gray.png");
    private final M model;

    public ProtectiveAuraLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.model = renderer.getModel();
    }

    @Override
    public void render(
            PoseStack pose, MultiBufferSource buff,
            int light, T livingEntity,
            float limbSwing, float limbSwingAmount,
            float partialTick, float age,
            float yaw, float pitch) {

        if (livingEntity.hasEffect(ModEffects.MALADY.get())){
            float f = (float) livingEntity.tickCount + partialTick;
            model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
            VertexConsumer vertexconsumer = buff.getBuffer(RenderType.energySwirl(LAYER_LOCATION, this.xOffset(f) % 1.0F, f * 0.01F % 1.0F));
            model.setupAnim(livingEntity, limbSwing, limbSwingAmount, age, yaw, pitch);
            getParentModel().copyPropertiesTo(model);
            model.renderToBuffer(pose, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 0.2F, 0.2F, 0.2F, 1F);
        }

    }

    protected float xOffset(float tick) {return tick * 0.01F;}
}

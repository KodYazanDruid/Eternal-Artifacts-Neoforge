package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.sonamorningstar.eternalartifacts.content.entity.DemonEyeEntity;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class DemonEyeRenderer extends MobRenderer<DemonEyeEntity, DemonEyeModel<DemonEyeEntity>> {
    public DemonEyeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new DemonEyeModel<>(pContext.bakeLayer(ModModelLayers.DEMON_EYE_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(DemonEyeEntity pEntity) {
        return new ResourceLocation(MODID, "textures/entity/demon_eye.png");
    }
}

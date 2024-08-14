package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.sonamorningstar.eternalartifacts.content.entity.DuckEntity;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class DuckRenderer extends MobRenderer<DuckEntity, DuckModel<DuckEntity>> {
    private static final ResourceLocation DUCK_LOCATION = new ResourceLocation(MODID, "textures/entity/duck.png");

    public DuckRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DuckModel<>(ctx.bakeLayer(ModModelLayers.DUCK_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(DuckEntity pEntity) {
        return DUCK_LOCATION;
    }

    protected float getBob(DuckEntity pLivingBase, float pPartialTicks) {
        float f = Mth.lerp(pPartialTicks, pLivingBase.oFlap, pLivingBase.flap);
        float f1 = Mth.lerp(pPartialTicks, pLivingBase.oFlapSpeed, pLivingBase.flapSpeed);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}

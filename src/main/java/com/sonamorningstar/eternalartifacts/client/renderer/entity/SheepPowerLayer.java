package com.sonamorningstar.eternalartifacts.client.renderer.entity;

import com.sonamorningstar.eternalartifacts.content.entity.ChargedSheepEntity;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;

public class SheepPowerLayer extends EnergySwirlLayer<ChargedSheepEntity, SheepModel<ChargedSheepEntity>> {
    private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    protected SheepModel<ChargedSheepEntity>  model;
    public SheepPowerLayer(RenderLayerParent<ChargedSheepEntity, SheepModel<ChargedSheepEntity>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new SheepModel<>(modelSet.bakeLayer(ModModelLayers.CHARGED_SHEEP_SWIRL));
    }

    @Override
    protected float xOffset(float tick) {
        return tick * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    @Override
    protected EntityModel<ChargedSheepEntity> model() {
        return model;
    }
}

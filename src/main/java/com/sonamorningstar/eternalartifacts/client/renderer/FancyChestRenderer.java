package com.sonamorningstar.eternalartifacts.client.renderer;

import com.sonamorningstar.eternalartifacts.content.block.entity.FancyChestBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.client.ModModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;

public class FancyChestRenderer extends ChestRenderer<FancyChestBlockEntity> {

    public FancyChestRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
        ModelPart modelPart = pContext.bakeLayer(ModModelLayers.FANCY_CHEST_LAYER);
        this.bottom = modelPart.getChild("bottom");
        this.lid = modelPart.getChild("lid");
        this.lock = modelPart.getChild("lock");
    }

    @Override
    protected Material getMaterial(FancyChestBlockEntity blockEntity, ChestType chestType) {
        return new Material(Sheets.CHEST_SHEET, new ResourceLocation(blockEntity.getTextureName()));
    }
}

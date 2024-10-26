package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Meteorite;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MeteoriteRenderer extends EntityRenderer<Meteorite> {
    private final MeteoriteModel model;
    public MeteoriteRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        model = new MeteoriteModel(ctx.bakeLayer(ModModelLayers.METEORITE_LAYER));
    }

    @Override
    public void render(Meteorite meteorite, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buff, int light) {
        //VertexConsumer consumer = buff.getBuffer(RenderType.entitySolid(getTextureLocation(meteorite)));
        VertexConsumer consumer = buff.getBuffer(Sheets.solidBlockSheet());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(getTextureLocation(meteorite));
        consumer = sprite.wrap(consumer);

        poseStack.pushPose();
        //model.setupAnim(meteorite.tickCount + partialTick);
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Meteorite pEntity) {
        //return new ResourceLocation(MODID, "textures/entity/meteorite.png");
        return new ResourceLocation("block/magma");
    }
}

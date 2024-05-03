package com.sonamorningstar.eternalartifacts.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.renderer.util.GhostVertexConsumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Original author: XFactHD
 * Source: https://github.com/XFactHD/FramedBlocks/blob/1.19.4/src/main/java/xfacthd/framedblocks/client/util/ItemRenderHelper.java
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRendererHelper {
    private static final RenderType TRANSLUCENT = RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS);

    public static void renderFakeItemTransparent(PoseStack pose, ItemStack stack, int x, int y, int alpha) {
        if(stack.isEmpty()) return;
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel baked = renderer.getModel(stack, null, Minecraft.getInstance().player, 0);
        renderItemModel(pose, stack, x, y, alpha, baked, renderer);
    }

    public static void renderItemModel(PoseStack pose, ItemStack stack, int x, int y, int alpha, BakedModel model, ItemRenderer renderer) {
        pose.pushPose();
        pose.translate(x, y, 100f);
        pose.translate(8d, 8d, 0d);
        pose.scale(1f, -1f, 1f);
        pose.scale(16f, 16f, 16f);

        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.mulPoseMatrix(pose.last().pose());
        RenderSystem.applyModelViewMatrix();

        boolean flatLight = !model.usesBlockLight();
        if(flatLight) Lighting.setupForFlatItems();

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderer.render(
                stack,
                ItemDisplayContext.GUI,
                false,
                new PoseStack(),
                wrapBuffer(buffer, alpha, alpha < 255),
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );
        buffer.endBatch();

        RenderSystem.enableDepthTest();

        if(flatLight) Lighting.setupFor3DItems();
        pose.popPose();
        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static MultiBufferSource wrapBuffer(MultiBufferSource.BufferSource buffer, int alpha, boolean b) {
        return renderType -> new GhostVertexConsumer(buffer.getBuffer(b ? TRANSLUCENT : renderType), alpha);
    }
}

package com.sonamorningstar.eternalartifacts.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import org.joml.Matrix4f;

public class MapOverlay extends ModGuiOverlay{
    private static final RenderType MAP_BACKGROUND = RenderType.text(new ResourceLocation("textures/map/map_background.png"));
    private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.text(new ResourceLocation("textures/map/map_background_checkerboard.png"));

    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;
        if (shouldReturn(gui.getMinecraft())) return;
        ItemStack map = PlayerCharmManager.findCharm(player, Items.FILLED_MAP);
        if (!map.isEmpty()) {
            gui.setupOverlayRenderState(true, true);
            int x = 15;
            int y = 50;
            int width = 92;
            int height = 92;
            renderMap(guiGraphics, map, player.level(), mc.gameRenderer.getMapRenderer(), x, y, width, height);
        }
    }

    private static void renderMap(GuiGraphics guiGraphics, ItemStack map, Level level, MapRenderer renderer, int x, int y, int width, int height) {
        Integer integer = MapItem.getMapId(map);
        MapItemSavedData mapitemsaveddata = MapItem.getSavedData(integer, level);
        if (mapitemsaveddata != null) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(x, y, 1);
            poseStack.scale(width / 128F, height / 128F, -10F);
            MultiBufferSource buff = guiGraphics.bufferSource();
            renderPaper(poseStack, buff, mapitemsaveddata, 15728880);
            renderer.render(poseStack, buff, integer, mapitemsaveddata, false, 15728880);
            poseStack.popPose();
        }
    }

    private static void renderPaper(PoseStack poseStack, MultiBufferSource buff, MapItemSavedData mapitemsaveddata, int combinedLight) {
        VertexConsumer vertexconsumer = buff.getBuffer(mapitemsaveddata == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix4f = poseStack.last().pose();
        vertexconsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(combinedLight).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(combinedLight).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(combinedLight).endVertex();
        vertexconsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(combinedLight).endVertex();
    }
}

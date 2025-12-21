package com.sonamorningstar.eternalartifacts.client.render.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class RendererHelper {

    public static void renderFluidCube(
            PoseStack poseStack, MultiBufferSource buff,
            IFluidHandler fluidHandler, RenderCubeInfo info,
            int light, int overlay,
            float xLen, float yLen, float zLen,
            float xOff, float yOff, float zOff) {
        FluidStack fluid = fluidHandler.getFluidInTank(0);
        float fill = (float) fluid.getAmount() / fluidHandler.getTankCapacity(0);
        if(fluid.isEmpty()) return;

        VertexConsumer vertexConsumer = buff.getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation still = fluidTypeExtensions.getStillTexture(fluid);

        if(still != null) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(still);
            int tintColor = fluidTypeExtensions.getTintColor(fluid);

            float x0 = xOff / 16f;
            float y0 = yOff / 16f;
            float z0 = zOff / 16f;
            float x1 = (xOff + xLen) / 16f;
            float y1 = (yOff + (yLen * fill)) / 16f;
            float z1 = (zOff + zLen) / 16f;

            float uTop0 = sprite.getU(x0);
            float vTop0 = sprite.getV(z0);
            float uTop1 = sprite.getU(x1);
            float vTop1 = sprite.getV(z1);

            float uSide0 = sprite.getU(x0);
            float vSide0 = sprite.getV(y0);
            float uSide1 = sprite.getU(x1);
            float vSide1 = sprite.getV(y1);
            
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            for (Direction value : Direction.values()) {
                if (value == Direction.UP) {
                    if((fill < 1 || info.forceRenderUp) && info.shouldRender(Direction.UP)) {
                        drawQuad(vertexConsumer, poseStack, value, x0, y1, z0, x1, y1, z1,
                                uTop0, vTop0, uTop1, vTop1,
                                tintColor, LightTexture.FULL_BRIGHT, overlay);
                    }
                } else if (info.shouldRender(value)) {
                    drawQuad(vertexConsumer, poseStack, value, x0, y0, z0, x1, y1, z1,
                            uSide0, vSide0, uSide1, vSide1,
                            tintColor, LightTexture.FULL_BRIGHT, overlay);
                }
            }
            RenderSystem.disableBlend();
        }
    }
    
    public static void drawCube(VertexConsumer consumer, PoseStack poseStack, float xLen, float yLen, float zLen, float xOff, float yOff, float zOff) {
        drawCube(consumer, poseStack, new RenderCubeInfo(RenderCubeInfo.all(), false), xLen, yLen, zLen, xOff, yOff, zOff, 0, 0, 0xFFFFFFFF, 0, OverlayTexture.NO_OVERLAY);
    }

    public static void drawCube(VertexConsumer consumer, PoseStack poseStack, RenderCubeInfo info,
                                float xLen, float yLen, float zLen, float xOff, float yOff, float zOff,
                                float u, float v, int tintColor, int light, int overlay) {
        float x0 = xOff / 16;
        float y0 = yOff / 16;
        float z0 = zOff / 16;
        float x1 = (xOff + xLen) / 16;
        float y1 = (yOff + yLen) / 16;
        float z1 = (zOff + zLen) / 16;
		
        for (Direction value : Direction.values()) {
            if (info.shouldRender(value)) drawQuad(consumer, poseStack, value, x0, y0, z0, x1, y1, z1, u, v, u, v, tintColor, light, overlay);
        }
    }
    

    public static void drawQuad(
            VertexConsumer v, PoseStack pose, Direction dir,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float u0, float v0,
            float u1, float v1,
            int color, int light, int overlay) {
        
        Matrix4f matrix = pose.last().pose();
        switch (dir) {
            case NORTH -> {
                v.vertex(matrix, x0, y0, z0).color(color).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x0, y1, z0).color(color).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y1, z0).color(color).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y0, z0).color(color).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
            }
            
            case SOUTH -> {
                v.vertex(matrix, x1, y0, z1).color(color).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y1, z1).color(color).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x0, y1, z1).color(color).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x0, y0, z1).color(color).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
            }
            
            case EAST -> {
                v.vertex(matrix, x1, y0, z0).color(color).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y1, z0).color(color).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y1, z1).color(color).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y0, z1).color(color).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
            }
            
            case WEST -> {
                v.vertex(matrix, x0, y0, z1).color(color).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x0, y1, z1).color(color).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x0, y1, z0).color(color).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x0, y0, z0).color(color).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
            }
            
            case UP -> {
                v.vertex(matrix, x0, y1, z0).color(color).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x0, y1, z1).color(color).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y1, z1).color(color).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y1, z0).color(color).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
            }
            
            case DOWN -> {
                v.vertex(matrix, x0, y0, z1).color(color).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x0, y0, z0).color(color).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y0, z0).color(color).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
                v.vertex(matrix, x1, y0, z1).color(color).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()).endVertex();
            }
        }
    }
    
    public static void renderFluidTile(FluidStack fluid, PoseStack pose, MultiBufferSource buff, int color, int overlay) {
        VertexConsumer vertexConsumer = buff.getBuffer(RenderType.translucent());
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation still = fluidTypeExtensions.getStillTexture(fluid);
        if (still != null) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(still);
            int tintColor = fluidTypeExtensions.getTintColor(fluid);
            drawQuad(vertexConsumer, pose, Direction.NORTH,
                0, 0, 0, 1, 1, 0,
                sprite.getU(0), sprite.getV(0), sprite.getU(1), sprite.getV(1),
                tintColor, color, overlay);
        }
    }
    
    public static void renderTextInWorld(String text, PoseStack pose, MultiBufferSource buff) {
        renderTextInWorld(Component.literal(text), pose, buff);
    }
    public static void renderTextInWorld(Component component, PoseStack pose, MultiBufferSource buff) {
        pose.pushPose();
        pose.scale(1/16f, 1/16f, 1/16f);
        pose.translate(16, 0, 0);
        pose.mulPose(Axis.ZP.rotationDegrees(180));
        Minecraft.getInstance().font.drawInBatch(component, 0, 0, 0xffffffff,
            false, pose.last().pose(), buff, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        pose.popPose();
    }

    public record RenderCubeInfo(ArrayList<Direction> renderedFaces, boolean forceRenderUp) {

        public boolean shouldRender(Direction face) {
            return renderedFaces.contains(face);
        }

        public static ArrayList<Direction> all() {
            return new ArrayList<>(List.of(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
        }

        public static ArrayList<Direction> allExcept(Direction... except) {
            ArrayList<Direction> directionList = all();
            directionList.removeAll(List.of(except));
            return directionList;
        }
    }
}

package com.sonamorningstar.eternalartifacts.client.renderer.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RendererHelper {

    public static void renderFluidCube(
            PoseStack poseStack, MultiBufferSource buff,
            IFluidHandler fluidHandler, FluidRenderCubeInfo info,
            int light, int overlay,
            float xLen, float yLen, float zLen,
            float xOff, float yOff, float zOff) {
        FluidStack fluid = fluidHandler.getFluidInTank(0);
        float fill = (float) fluid.getAmount() / fluidHandler.getTankCapacity(0);
        if(fluid.isEmpty()) return;

        VertexConsumer vertexConsumer = buff.getBuffer(Sheets.translucentCullBlockSheet());
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation still = fluidTypeExtensions.getStillTexture(fluid);

        if(still != null){
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

            //Draws top if not fully filled.
            if(fill < 1 && info.shouldRender(Direction.UP)){
                drawQuad(
                        vertexConsumer, poseStack,
                        x0, y1, z0, x1, y1, z1,
                        uTop0, vTop0, uTop1, vTop1,
                        tintColor, light, overlay, /*level, jar.getBlockPos().above(),*/
                        1, 1, 1 /*0, 1, 0*/, true
                );
            }

            //Until I fix the lightning these normals will remain 1, 1, 1
            //Draw sides
            if(info.shouldRender(Direction.NORTH)) drawQuad(vertexConsumer, poseStack, x0, y0, z0, x1, y1, z0, uSide0, vSide0, uSide1, vSide1, tintColor, light, overlay, /*level, jar.getBlockPos().north(),*/1, 1, 1 /*0, -1, 0*/, true);
            if(info.shouldRender(Direction.EAST)) drawQuad(vertexConsumer, poseStack, x1, y0, z0, x1, y1, z1, uSide0, vSide0, uSide1, vSide1, tintColor, light, overlay, /*level, jar.getBlockPos().east(), */1, 1, 1 /*1, 0, 0*/, false);
            if(info.shouldRender(Direction.SOUTH)) drawQuad(vertexConsumer, poseStack, x1, y0, z1, x0, y1, z1, uSide0, vSide0, uSide1, vSide1, tintColor, light, overlay, /*level, jar.getBlockPos().south(),*/1, 1, 1 /*0, 0, 1*/, true);
            if(info.shouldRender(Direction.WEST)) drawQuad(vertexConsumer, poseStack, x0, y0, z1, x0, y1, z0, uSide0, vSide0, uSide1, vSide1, tintColor, light, overlay, /*level, jar.getBlockPos().west(), */1, 1, 1 /*-1, 0, 0*/, false);

            //Draws bottom overlay,
            if(info.shouldRender(Direction.DOWN)) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.translate(0, -2 / 16f, -1);
                drawQuad(
                        vertexConsumer, poseStack,
                        x0, y0, z0, x1, y0, z1,
                        uTop0, vTop0, uTop1, vTop1,
                        tintColor, light, overlay, /*level, jar.getBlockPos().below(),*/
                        1, 1, 1 /*0, -1, 0*/, true
                );
                poseStack.popPose();
            }
        }
    }


    public static void drawQuad(
            VertexConsumer vertexConsumer,
            PoseStack poseStack,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float u0, float v0,
            float u1, float v1,
            int tintColor, int light, int overlay, /*Level level, BlockPos pos,*/
            float normalX, float normalY, float normalZ, boolean isNS){
        /*int combined = level.getRawBrightness(pos, 0);
        int lightMapU = combined >> 16 & 65535;
        int lightMapV = combined & 65535;*/

        if(isNS){
            vertexConsumer.vertex(poseStack.last().pose(), x0, y0, z0).color(tintColor).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(normalX, normalY, normalZ).endVertex();
            vertexConsumer.vertex(poseStack.last().pose(), x0, y1, z1).color(tintColor).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(normalX, normalY, normalZ).endVertex();
            vertexConsumer.vertex(poseStack.last().pose(), x1, y1, z1).color(tintColor).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normalX, normalY, normalZ).endVertex();
            vertexConsumer.vertex(poseStack.last().pose(), x1, y0, z0).color(tintColor).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(normalX, normalY, normalZ).endVertex();
        }else{
            vertexConsumer.vertex(poseStack.last().pose(), x0, y0, z0).color(tintColor).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(normalX, normalY, normalZ).endVertex();
            vertexConsumer.vertex(poseStack.last().pose(), x1, y1, z0).color(tintColor).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(normalX, normalY, normalZ).endVertex();
            vertexConsumer.vertex(poseStack.last().pose(), x1, y1, z1).color(tintColor).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normalX, normalY, normalZ).endVertex();
            vertexConsumer.vertex(poseStack.last().pose(), x0, y0, z1).color(tintColor).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(normalX, normalY, normalZ).endVertex();
        }

        /*vertexConsumer.vertex(pose.last().pose(), x0, y0, z0).color(tintColor).uv(u0, v0).overlayCoords(lightMapU, lightMapV).uv2(light).normal(normalX, normalY, normalZ).endVertex();
        vertexConsumer.vertex(pose.last().pose(), x0, y1, z1).color(tintColor).uv(u0, v1).overlayCoords(lightMapU, lightMapV).uv2(light).normal(normalX, normalY, normalZ).endVertex();
        vertexConsumer.vertex(pose.last().pose(), x1, y1, z1).color(tintColor).uv(u1, v1).overlayCoords(lightMapU, lightMapV).uv2(light).normal(normalX, normalY, normalZ).endVertex();
        vertexConsumer.vertex(pose.last().pose(), x1, y0, z0).color(tintColor).uv(u1, v0).overlayCoords(lightMapU, lightMapV).uv2(light).normal(normalX, normalY, normalZ).endVertex();
 */   }

    public record FluidRenderCubeInfo(ArrayList<Direction> renderedFaces) {

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

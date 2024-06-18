package com.sonamorningstar.eternalartifacts.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.content.block.entity.JarBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.client.ModModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class JarRenderer implements BlockEntityRenderer<JarBlockEntity> {

    public static final Material TEXTURE_JAR = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "block/jar"));

    private static ModelPart modelPart;
    public JarRenderer(BlockEntityRendererProvider.Context ctx) {
        modelPart = ctx.bakeLayer(ModModelLayers.JAR_LAYER);
    }


    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 0).addBox(4.0F, 0.0F, 4.0F, 8.0F, 11.0F, 8.0F),
                PartPose.ZERO);
        partdefinition.addOrReplaceChild("lid",
                CubeListBuilder.create().texOffs(0, 19).addBox(5.0F, 0, 5.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offset(0, 10, 0));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void render(JarBlockEntity jar, float tick, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
        renderWhole(jar, pose, buff, TEXTURE_JAR, light, overlay);
    }

    public static void renderWhole(JarBlockEntity jar, PoseStack pose, MultiBufferSource buff, Material material, int light, int overlay) {
        renderJar(pose, buff, material, light, overlay);
        renderFluid(jar, pose, buff, light, overlay);
    }

    public static void renderJar(PoseStack pose, MultiBufferSource buff, Material material, int light, int overlay) {
        pose.pushPose();
        VertexConsumer consumer = material.buffer(buff, RenderType::entityCutout);
        modelPart.render(pose, consumer, light, overlay);
        pose.popPose();
    }

    public static void renderFluid(JarBlockEntity jar, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
        FluidStack fluid = jar.tank.getFluid();
        float fill = (float) jar.tank.getFluidAmount() / jar.tank.getCapacity();
        if(fluid.getAmount() >= 0) return;

        VertexConsumer vertexConsumer = buff.getBuffer(Sheets.translucentCullBlockSheet());
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation still = fluidTypeExtensions.getStillTexture(fluid);

        if(still != null){
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(still);
            int tintColor = fluidTypeExtensions.getTintColor(fluid);

            float x0 = 5 / 16f;
            float y0 = 1 / 16f;
            float z0 = 5 / 16f;
            float x1 = 11 / 16f;
            float y1 = (10 * fill) / 16;
            float z1 = 11 / 16f;

            float uTop0 = sprite.getU(x0);
            float vTop0 = sprite.getV(z0);
            float uTop1 = sprite.getU(x1);
            float vTop1 = sprite.getV(z1);

            float uSide0 = sprite.getU(x0);
            float vSide0 = sprite.getV(y0);
            float uSide1 = sprite.getU(x1);
            float vSide1 = sprite.getV(y1);


            //Draws top if not fully filled.
            if(fill < 1){
                drawQuad(
                        vertexConsumer, pose,
                        x0, y1, z0, x1, y1, z1,
                        uTop0, vTop0, uTop1, vTop1,
                        tintColor, light, overlay
                );
                /*pose.pushPose();
                vertexConsumer.vertex(pose.last().pose(), x1, y1, z1).color(tintColor).uv(uTop0, vTop0).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
                vertexConsumer.vertex(pose.last().pose(), x1, y1, z0).color(tintColor).uv(uTop0, vTop1).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
                vertexConsumer.vertex(pose.last().pose(), x0, y1, z0).color(tintColor).uv(uTop1, vTop1).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
                vertexConsumer.vertex(pose.last().pose(), x0, y1, z1).color(tintColor).uv(uTop1, vTop0).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex();
                pose.popPose();*/
            }

            //Draws sides
            //North
            drawQuad(vertexConsumer, pose, x0, y0, z0, x1, y1, z0, uSide0, vSide0, uSide1, vSide1, tintColor, light, overlay);
            //pose.pushPose();
            /*vertexConsumer.vertex(pose.last().pose(), x1, y0, z0).color(tintColor).uv(uSide1, vSide0).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
            vertexConsumer.vertex(pose.last().pose(), x0, y0, z0).color(tintColor).uv(uSide0, vSide0).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
            vertexConsumer.vertex(pose.last().pose(), x0, y1, z0).color(tintColor).uv(uSide0, vSide1).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
            vertexConsumer.vertex(pose.last().pose(), x1, y1, z0).color(tintColor).uv(uSide1, vSide1).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();*/
            //pose.popPose();
            //East
            //drawQuad(vertexConsumer, pose, x1, y0, z1, x1, y1, z0, uSide0, vSide0, uSide1, vSide1, tintColor, light, overlay);
            //South
            //drawQuad(vertexConsumer, pose, x1, y0, z1, x0, y1, z1, uSide0, vSide0, uSide1, vSide1, tintColor, light, overlay);
            //West
            //drawQuad(vertexConsumer, pose, x0, y0, z0, x0, y1, z1, uSide0, vSide0, uSide1, vSide1, tintColor, light, overlay);

            //Draws bottom
            pose.pushPose();
            pose.mulPose(Axis.XP.rotationDegrees(180));
            pose.translate(0, -2/16f, -1);
            drawQuad(
                    vertexConsumer, pose,
                    x0, y0, z0, x1, y0, z1,
                    uTop0, vTop0, uTop1, vTop1,
                    tintColor, light, overlay
            );
            pose.popPose();

        }
    }

    private static void drawQuad(
            VertexConsumer vertexConsumer,
            PoseStack pose,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float u0, float v0,
            float u1, float v1,
            int tintColor, int light, int overlay){
        vertexConsumer.vertex(pose.last().pose(), x0, y0, z0).color(tintColor).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex();
        vertexConsumer.vertex(pose.last().pose(), x0, y1, z1).color(tintColor).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex();
        vertexConsumer.vertex(pose.last().pose(), x1, y1, z1).color(tintColor).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex();
        vertexConsumer.vertex(pose.last().pose(), x1, y0, z0).color(tintColor).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex();
    }

}

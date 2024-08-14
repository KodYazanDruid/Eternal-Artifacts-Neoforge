package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.client.renderer.util.RendererHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.JarBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class JarRenderer implements BlockEntityRenderer<JarBlockEntity> {

    public static final Material TEXTURE_JAR = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "block/jar"));

    private static ModelPart modelPart;
    private static ModelPart base;
    private static ModelPart lid;
    public JarRenderer(BlockEntityRendererProvider.Context ctx) {
        modelPart = ctx.bakeLayer(ModModelLayers.JAR_LAYER);
        base = modelPart.getChild("base");
        lid = modelPart.getChild("lid");
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
        renderWhole(jar, pose, buff, TEXTURE_JAR, light, overlay, jar.isOpen);
    }

    public static void renderWhole(JarBlockEntity jar, PoseStack pose, MultiBufferSource buff, Material material, int light, int overlay, boolean isOpen) {
        renderJar(pose, buff, material, light, overlay, isOpen);
        RendererHelper.renderFluidCube(pose, buff, jar.tank, new RendererHelper.FluidRenderCubeInfo(RendererHelper.FluidRenderCubeInfo.all(), isOpen),
                light, overlay, 6, 9, 6, 5, 1, 5);
    }

    public static void renderJar(PoseStack pose, MultiBufferSource buff, Material material, int light, int overlay, boolean isOpen) {
        pose.pushPose();
        VertexConsumer consumer = material.buffer(buff, RenderType::entityCutout);
        base.render(pose, consumer, light, overlay);
        if(!isOpen) lid.render(pose, consumer, light, overlay);
        pose.popPose();
    }

}

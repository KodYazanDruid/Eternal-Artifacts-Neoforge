package com.sonamorningstar.eternalartifacts.client.renderer;

import appeng.client.render.cablebus.CubeBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.block.entity.JarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class JarRenderer implements BlockEntityRenderer<JarBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;
    private final ModelManager modelManager;

    public JarRenderer(BlockEntityRendererProvider.Context ctx) {
        blockRenderer = ctx.getBlockRenderDispatcher();
        modelManager = blockRenderer.getBlockModelShaper().getModelManager();
    }

    public static final ResourceLocation JAR_MODEL = new ResourceLocation(MODID, "block/jar");

    @Override
    public void render(JarBlockEntity jar, float tick, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
        BakedModel jarBakedModel = modelManager.getModel(JAR_MODEL);

        FluidStack fluid = jar.tank.getFluid();
        float fill = (float) jar.tank.getFluidAmount() / jar.tank.getCapacity();

        VertexConsumer vertexConsumer = buff.getBuffer(RenderType.translucentMovingBlock());
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation still = fluidTypeExtensions.getStillTexture(fluid);

        if(still != null){
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(still);

            int tintColor = fluidTypeExtensions.getTintColor(fluid);
            //float alpha = ((tintColor >> 24) & 0xFF) / 255f;
            float red = ((tintColor >> 16) & 0xFF) / 255f;
            float green = ((tintColor >> 8) & 0xFF) / 255f;
            float blue = (tintColor & 0xFF) / 255f;

            CubeBuilder builder = new CubeBuilder();
            builder.setTexture(sprite);

            //Jar is 8x11x8 but fluid should be displayed 6x9x6 in the middle
            FluidType attributes = fluid.getFluid().getFluidType();
            if(attributes.isLighterThanAir()) builder.addCube(5, 10 - 9 * fill, 5, 11, 10, 11);
            else builder.addCube(5, 1, 5, 11, 1 + 9 * fill, 11);

            for (BakedQuad bakedQuad : builder.getOutput()) {
                vertexConsumer.putBulkData(pose.last(), bakedQuad, red, green, blue, light, overlay);
            }
        }

        /*blockRenderer.getModelRenderer().tesselateWithAO(
                jar.getLevel(),
                jarBakedModel,
                jar.getBlockState(),
                jar.getBlockPos().above(),
                pose,
                vertexConsumer,
                false,
                RandomSource.create(),
                jar.getBlockState().getSeed(jar.getBlockPos()),
                overlay);*/
    }

}

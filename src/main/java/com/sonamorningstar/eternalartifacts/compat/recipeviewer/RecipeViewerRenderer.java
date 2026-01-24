package com.sonamorningstar.eternalartifacts.compat.recipeviewer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Quaternionf;


import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

/**
 * Common rendering utilities for recipe viewers.
 * These can be used by both EMI and JEI implementations.
 */
public final class RecipeViewerRenderer {
    
    // Common textures
    public static final ResourceLocation ARROW_TEXTURE = new ResourceLocation(MODID, "textures/gui/sprites/widget/arrow.png");
    public static final ResourceLocation ARROW_FILLED_TEXTURE = new ResourceLocation(MODID, "textures/gui/sprites/widget/arrow_filled.png");
    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("container/slot");
    public static final ResourceLocation CAULDRON_TEXTURE = new ResourceLocation(MODID, "textures/gui/sprites/widget/cauldron.png");
    public static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation(MODID, "textures/gui/sprites/widget/lightning.png");
    public static final ResourceLocation FIRE_TEXTURE = new ResourceLocation(MODID, "textures/gui/sprites/widget/fire.png");
    
    private RecipeViewerRenderer() {}
    
    /**
     * Renders an arrow indicating processing direction
     */
    public static void renderArrow(GuiGraphics gui, int x, int y, boolean filled) {
        gui.blit(filled ? ARROW_FILLED_TEXTURE : ARROW_TEXTURE, x, y, 0, 0, 22, 15, 22, 15);
    }
    
    /**
     * Renders an animated arrow showing progress
     */
    public static void renderAnimatedArrow(GuiGraphics gui, int x, int y, float progress) {
        int width = (int) (22 * progress);
        gui.blit(ARROW_TEXTURE, x, y, 0, 0, 22, 15, 22, 15);
        gui.blit(ARROW_FILLED_TEXTURE, x, y, 0, 0, width, 15, 22, 15);
    }
    
    /**
     * Renders a block state in the GUI
     */
    public static void renderBlock(GuiGraphics gui, BlockState state, int x, int y, float scale) {
        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(x + 8, y + 8, 100);
        pose.scale(scale * 16, -scale * 16, scale * 16);
        pose.mulPose(new Quaternionf().rotationXYZ(
            (float) Math.toRadians(30),
            (float) Math.toRadians(225),
            0
        ));
        
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        dispatcher.renderSingleBlock(state, pose, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
            ModelData.EMPTY, RenderType.solid());
        buffer.endBatch();
        
        pose.popPose();
    }
    
    /**
     * Checks if a block state is a fluid block
     */
    public static boolean isFluidBlock(BlockState state) {
        return !state.getFluidState().isEmpty();
    }
    
    /**
     * Renders a block state, automatically choosing the correct render method for fluids
     */
    public static void renderBlockAuto(GuiGraphics gui, BlockState state, int x, int y, float scale) {
        if (isFluidBlock(state)) {
            //renderFluidBlock(gui, state, x, y, scale);
        } else {
            renderBlock(gui, state, x, y, scale);
        }
    }
    
    public static void renderFluid(GuiGraphics gui, FluidStack fluid, int x, int y, int width, int height) {
        if (fluid.isEmpty()) return;
        
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation stillTexture = props.getStillTexture(fluid);
        if (stillTexture == null) return;
        
        int color = props.getTintColor(fluid);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;
        
        RenderSystem.setShaderColor(r, g, b, a);
        gui.blit(stillTexture, x, y, 0, 0, width, height, width, height);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
    
    /**
     * Renders an entity in the GUI
     */
    public static void renderEntity(GuiGraphics gui, EntityType<?> entityType, int x, int y, float scale, float rotation) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        Entity entity = entityType.create(mc.level);
        if (entity == null) return;
        
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(x, y, 50);
        pose.scale(scale, scale, scale);
        pose.mulPose(new Quaternionf().rotationY((float) Math.toRadians(rotation)));
        
        dispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        dispatcher.render(entity, 0, 0, 0, 0, 1, pose, buffer, 0xF000F0);
        buffer.endBatch();
        dispatcher.setRenderShadow(true);
        
        pose.popPose();
    }
    
    /**
     * Renders text centered at the given position
     */
    public static void renderCenteredText(GuiGraphics gui, Component text, int x, int y, int color) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        gui.drawString(font, text, x - textWidth / 2, y, color, false);
    }
    
    /**
     * Renders a chance percentage below a slot
     */
    public static void renderChance(GuiGraphics gui, float chance, int x, int y) {
        if (chance >= 1.0f) return;
        
        Font font = Minecraft.getInstance().font;
        String text = String.format("%.0f%%", chance * 100);
        int textWidth = font.width(text);
        int color = chance < 0.5f ? 0xFFFF5555 : 0xFFFFFF55;
        gui.drawString(font, text, x + 9 - textWidth / 2, y + 18, color, true);
    }
    
    /**
     * Renders a simple slot background
     */
    public static void renderSlotBackground(GuiGraphics gui, int x, int y) {
        gui.blitSprite(SLOT_TEXTURE, x, y, 18, 18);
    }
    
    /**
     * Renders a processing time indicator
     */
    public static void renderProcessingTime(GuiGraphics gui, int ticks, int x, int y) {
        Font font = Minecraft.getInstance().font;
        float seconds = ticks / 20f;
        String text = String.format("%.1fs", seconds);
        gui.drawString(font, text, x, y, 0xFF808080, false);
    }
    
    /**
     * Renders an energy cost indicator
     */
    public static void renderEnergyCost(GuiGraphics gui, int energy, int x, int y) {
        Font font = Minecraft.getInstance().font;
        String text;
        if (energy >= 1000000) {
            text = String.format("%.1fM FE", energy / 1000000f);
        } else if (energy >= 1000) {
            text = String.format("%.1fk FE", energy / 1000f);
        } else {
            text = energy + " FE";
        }
        gui.drawString(font, text, x, y, 0xFFDD0000, false);
    }
}


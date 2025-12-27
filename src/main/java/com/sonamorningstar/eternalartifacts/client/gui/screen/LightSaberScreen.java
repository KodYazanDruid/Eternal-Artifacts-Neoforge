package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.ColorUtils;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.ItemStackScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.CleanButton;
import com.sonamorningstar.eternalartifacts.content.item.LightSaberItem;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public class LightSaberScreen extends ItemStackScreen {
    //ExtendedSlider alphaSlider;
    ExtendedSlider redSlider;
    ExtendedSlider greenSlider;
    ExtendedSlider blueSlider;
    CleanButton toggleGlintButton;

    public LightSaberScreen(ItemStack stack) {
        super(stack);
    }

    @Override
    protected void init() {
        imageWidth = 256;
        imageHeight = 192;
        super.init();
        int color = ColorUtils.getColorFromNBT(stack, 0);
        /*alphaSlider = new ExtendedSlider(
                leftPos + 40, topPos + 30, 150, 20,
                Component.empty(), Component.empty(), 0, 255, FastColor.ARGB32.alpha(color), true);*/
        redSlider = new ExtendedSlider(
                leftPos + 40, topPos + 30, 150, 20,
                Component.empty(), Component.empty(), 0, 255, FastColor.ARGB32.red(color), true);
        greenSlider = new ExtendedSlider(
                leftPos + 40, topPos + 55, 150, 20,
                Component.empty(), Component.empty(), 0, 255, FastColor.ARGB32.green(color), true);
        blueSlider = new ExtendedSlider(
                leftPos + 40, topPos + 80, 150, 20,
                Component.empty(), Component.empty(), 0, 255, FastColor.ARGB32.blue(color), true);
        CleanButton doneButton = CleanButton.builder(ModConstants.GUI.withSuffixTranslatable("done"),
                (button) -> changeColor(button, stack))
                .bounds(leftPos + 10, topPos + 105, 50, 20).build();
        toggleGlintButton = CleanButton.builder(ModConstants.GUI.withSuffixTranslatable("toggle_glint"),
                        (button) -> toggleGlint(stack))
                .bounds(leftPos + 70, topPos + 105, 50, 20).build();
        toggleGlintButton.visible = stack.isEnchanted();
        //addRenderableWidget(alphaSlider);
        addRenderableWidget(redSlider);
        addRenderableWidget(greenSlider);
        addRenderableWidget(blueSlider);
        addRenderableWidget(doneButton);
        addRenderableWidget(toggleGlintButton);
    }

    @Override
    public void renderBackground(GuiGraphics gui, int mx, int my, float partTick) {
        super.renderBackground(gui, mx, my, partTick);
        GuiDrawer.drawDefaultBackground(gui, leftPos, topPos, imageWidth, imageHeight);
        //gui.drawString(font, ModConstants.GUI.withSuffixTranslatable("alpha"), leftPos + 10, topPos + 38, alphaSlider.getValueInt() << 24, false);
        gui.drawString(font, ModConstants.GUI.withSuffixTranslatable("red"), leftPos + 10, topPos + 38, redSlider.getValueInt() << 16, false);
        gui.drawString(font, ModConstants.GUI.withSuffixTranslatable("green"), leftPos + 10, topPos + 63, greenSlider.getValueInt() << 8, false);
        gui.drawString(font, ModConstants.GUI.withSuffixTranslatable("blue"), leftPos + 10, topPos + 88, blueSlider.getValueInt(), false);
        //gui.fillGradient(leftPos + 10, topPos + 150, leftPos + 190, topPos + 200, getColor(), getColor());
    }

   private void changeColor(@Nullable AbstractButton button, ItemStack stack) {
        if (stack.getItem() instanceof LightSaberItem lsi) {
            lsi.changeColor(stack, getColor());
        }
        if (button != null) minecraft.setScreen(null);
   }

   private int getColor() {
        return FastColor.ARGB32.color(
                //alphaSlider.getValueInt(),
                0xFF,
                redSlider.getValueInt(),
                greenSlider.getValueInt(),
                blueSlider.getValueInt()
        );
   }

    private void toggleGlint(ItemStack stack) {
        if (stack.getItem() instanceof LightSaberItem lsi) {
            lsi.toggleGlint(stack);
        }
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partTick) {
        super.render(gui, mx, my, partTick);
        toggleGlintButton.visible = stack.isEnchanted();
        ItemStack rendered = stack.copy();
        changeColor(null, rendered);
        BakedModel model = mc.getItemRenderer().getModel(rendered, mc.level, mc.player, 0);
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(leftPos + 227, topPos + 136, 150.0F);
        pose.mulPoseMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        pose.scale(100.0F, 100.0F, 100.0F);
        pose.mulPose(Axis.XP.rotationDegrees(15.0F));
        pose.mulPose(Axis.YP.rotationDegrees((mc.player.tickCount + partTick) * 5.0F));
        /*boolean blockLight = !model.usesBlockLight();
        if (blockLight) Lighting.setupForFlatItems();*/
        Lighting.setupForEntityInInventory();
        mc.getItemRenderer().renderStatic(
            rendered, ItemDisplayContext.NONE,
            15728880, OverlayTexture.NO_OVERLAY,
            pose, gui.bufferSource(), mc.level,
            mc.player.getId() + ItemDisplayContext.NONE.ordinal()
        );
        //gui.flush();
        /*if (blockLight)*/ Lighting.setupFor3DItems();
        pose.popPose();
    }
}

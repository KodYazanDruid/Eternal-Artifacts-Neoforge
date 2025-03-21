package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.container.MachineWorkbenchMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class MachineWorkbenchScreen extends AbstractMachineScreen<MachineWorkbenchMenu> {
	public MachineWorkbenchScreen(MachineWorkbenchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
	}
	
	@Override
	protected void renderExtra(GuiGraphics gui, int mx, int my, float partialTick) {
		GuiDrawer.drawFramedBackground(gui, this.leftPos + 7, this.topPos + 17, 146, 60, 1,
			0xFF000000, 0xFF373737, 0xffffffff);
		var cap = getMenu().getBeInventory();
		if (cap != null) {
			ItemStack stack = cap.getStackInSlot(0);
			PoseStack pose = gui.pose();
			Minecraft mc = Minecraft.getInstance();
			pose.pushPose();
			pose.translate(this.leftPos + 80, this.topPos + 47, 150);
			Player player = mc.player;
			int ticks = player.tickCount;
			float xRot = 22.5F;
			float yRot = 180 + ((ticks + partialTick) / 10);
			pose.scale(100.0F, 100.0F, 100.0F);
			if (!stack.isEmpty()) {
				BakedModel model = mc.getItemRenderer().getItemModelShaper().getItemModel(stack);
				boolean light = !model.usesBlockLight();
				if (light) Lighting.setupForFlatItems();
				pose.mulPose(Axis.ZP.rotationDegrees(180));
				pose.mulPoseMatrix(new Matrix4f().rotateX(xRot).rotateY(yRot));
				pose.mulPoseMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
				int combinedLight = LightTexture.pack(15, 15);
				mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
					OverlayTexture.NO_OVERLAY, gui.pose(), gui.bufferSource(), player.level(),
					player.getId());
				gui.flush();
				if (light) Lighting.setupFor3DItems();
			}
			pose.popPose();
		}
	}
}

package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.renderer.util.RendererHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.TesseractBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.neoforged.neoforge.client.model.data.ModelData;

public class TesseractRenderer implements BlockEntityRenderer<TesseractBlockEntity> {
	@Override
	public void render(TesseractBlockEntity tesseract, float delta, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
		if (!tesseract.hasLevel()) {
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(ModBlocks.TESSERACT.get().defaultBlockState(),
				pose, buff, light, overlay, ModelData.EMPTY, RenderType.solid());
		}
		RendererHelper.drawCube(buff.getBuffer(RenderType.endPortal()), pose, 10, 10, 10, 3, 3, 3);
	}
}

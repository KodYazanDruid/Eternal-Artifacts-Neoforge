package com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock.base.MultiBlockRenderer;
import com.sonamorningstar.eternalartifacts.content.block.entity.GeneratorBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class GeneratorRenderer extends MultiBlockRenderer<GeneratorBlockEntity> {
	public GeneratorRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	protected void renderMultiblock(GeneratorBlockEntity master, PoseStack pose, MultiBufferSource buffer,
									float partialTick, int width, int height, int depth, int packedLight, int packedOverlay) {
		LevelRenderer.renderLineBox(
			pose, buffer.getBuffer(RenderType.lines()),
			0.0, 0.0, 0.0,
			width, height, depth,
			0.20f, 0.85f, 1.00f, 1.00f
		);
	}
}

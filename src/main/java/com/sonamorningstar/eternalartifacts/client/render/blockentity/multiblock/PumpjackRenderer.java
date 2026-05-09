package com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock.base.MultiBlockRenderer;
import com.sonamorningstar.eternalartifacts.content.block.entity.PumpjackBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;

public class PumpjackRenderer extends MultiBlockRenderer<PumpjackBlockEntity> {
	public PumpjackRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	protected void renderMultiblock(PumpjackBlockEntity master, PoseStack pose, MultiBufferSource buffer,
									int width, int height, int depth, int packedLight, int packedOverlay) {
		
		Minecraft mc = Minecraft.getInstance();
		mc.getBlockRenderer().renderSingleBlock(
			master.isWorking() ? Blocks.DIAMOND_BLOCK.defaultBlockState() : Blocks.IRON_BLOCK.defaultBlockState(),
			pose, buffer, packedLight, packedOverlay);
		LevelRenderer.renderLineBox(
			pose, buffer.getBuffer(RenderType.lines()),
			0.0, 0.0, 0.0,
			width, height, depth,
			0.20f, 0.85f, 1.00f, 1.00f
		);
	}
}

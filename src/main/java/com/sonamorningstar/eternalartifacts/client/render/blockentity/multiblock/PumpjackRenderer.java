package com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock.base.MultiBlockRenderer;
import com.sonamorningstar.eternalartifacts.content.block.entity.PumpjackBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackRenderer extends MultiBlockRenderer<PumpjackBlockEntity> {
	public static final float MAX_ANGLE = 22.5F;
	
	public PumpjackRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	protected void renderMultiblock(PumpjackBlockEntity master, PoseStack pose, MultiBufferSource buffer,
									float partialTick, int width, int height, int depth, int packedLight, int packedOverlay) {
		
		Minecraft mc = Minecraft.getInstance();
		BlockRenderDispatcher renderer = mc.getBlockRenderer();
		for (int w = 0; w < width; w++) {
			for (int d = 0; d < depth; d++) {
				pose.pushPose();
				pose.translate(w, 0, d);
				BlockState state = Blocks.POLISHED_DEEPSLATE_SLAB.defaultBlockState();
				if (w == 1 && d == 0) state = ModBlocks.FLUID_PORT.get().defaultBlockState();
				if (w == 1 && d == 4) state = ModBlocks.ENERGY_PORT.get().defaultBlockState();
				renderer.renderSingleBlock(state, pose, buffer, packedLight, packedOverlay);
				pose.popPose();
			}
		}
		pose.pushPose();
		pose.translate(0, 0, 2);
		for (int i = 0; i < 3; i++) {
			pose.pushPose();
			pose.translate(0, i, 0);
			renderer.renderSingleBlock(Blocks.POLISHED_DEEPSLATE_WALL.defaultBlockState(), pose, buffer, packedLight, packedOverlay);
			pose.popPose();
		}
		pose.translate(2, 0, 0);
		for (int i = 0; i < 3; i++) {
			pose.pushPose();
			pose.translate(0, i, 0);
			renderer.renderSingleBlock(Blocks.POLISHED_DEEPSLATE_WALL.defaultBlockState(), pose, buffer, packedLight, packedOverlay);
			pose.popPose();
		}
		pose.popPose();
		//Moving part.
		pose.pushPose();
		if (master.isWorking()) {
			float time = ((float)master.getWorkingTicks() + partialTick);
			float progress = (Mth.sin(time) + 1.0F) * 0.5F;
			float angle = Mth.lerp(progress, 0.0F, MAX_ANGLE);
			pose.rotateAround(Axis.XP.rotationDegrees(angle), 1.5F, 2.5F, 2.5F);
		}
		for (int i = 0; i < 3; i++) {
			pose.pushPose();
			pose.translate(1, i + 1, 0);
			renderer.renderSingleBlock(ModBlocks.STEEL_BLOCK.get().defaultBlockState(), pose, buffer, packedLight, packedOverlay);
			pose.popPose();
		}
		for (int i = 0; i < 4; i++) {
			pose.pushPose();
			pose.translate(1, 2, i + 1);
			renderer.renderSingleBlock(ModBlocks.STEEL_BLOCK.get().defaultBlockState(), pose, buffer, packedLight, packedOverlay);
			pose.popPose();
		}
		pose.popPose();
		LevelRenderer.renderLineBox(
			pose, buffer.getBuffer(RenderType.lines()),
			0.0, 0.0, 0.0,
			width, height, depth,
			0.20f, 0.85f, 1.00f, 1.00f
		);
	}
}

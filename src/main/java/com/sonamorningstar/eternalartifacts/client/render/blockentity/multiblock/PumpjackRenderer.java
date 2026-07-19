package com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
		final float pivotX = 1.5F, pivotY = 2.5F, pivotZ = 2.5F;
		float angle = Mth.lerp(partialTick, master.getPrevArmAngle(), master.getArmAngle());
		pose.rotateAround(Axis.XP.rotationDegrees(angle), pivotX, pivotY, pivotZ);
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
		
		float dx = 0.0F, dy = -1.5F, dz = -2.0F;
		double rad = Math.toRadians(angle);
		float rotY = (float) (dy * Math.cos(rad) - dz * Math.sin(rad));
		float rotZ = (float) (dy * Math.sin(rad) + dz * Math.cos(rad));
		
		float tipX = pivotX + dx;
		float tipY = pivotY + rotY;
		float tipZ = pivotZ + rotZ;
		
		float baseX = 1.5F, baseY = 1.0F, baseZ = 0.5F;
		
		VertexConsumer lineConsumer = buffer.getBuffer(RenderType.lines());
		PoseStack.Pose last = pose.last();
		lineConsumer.vertex(last.pose(), tipX, tipY, tipZ)
			.color(0.1F, 0.1F, 0.1F, 1.0F)
			.normal(last.normal(), 0, 1, 0)
			.endVertex();
		lineConsumer.vertex(last.pose(), baseX, baseY, baseZ)
			.color(0.1F, 0.1F, 0.1F, 1.0F)
			.normal(last.normal(), 0, 1, 0)
			.endVertex();
	}
}

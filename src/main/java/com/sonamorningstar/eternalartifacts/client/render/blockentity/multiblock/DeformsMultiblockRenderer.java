package com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock.base.MultiBlockRenderer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;


public class DeformsMultiblockRenderer<MB extends AbstractMultiblockBlockEntity> extends MultiBlockRenderer<MB> {
	public DeformsMultiblockRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	public void render(MB part, float pPartialTick, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		Minecraft MC = Minecraft.getInstance();
		ClientLevel level = MC.level;
		if (level == null) return;
		BlockRenderDispatcher blockRenderer = MC.getBlockRenderer();
		BlockState deformState = part.getDeformState();
		if (deformState != null) {
			BlockPos partPos = part.getBlockPos();
			int light = LevelRenderer.getLightColor(level, partPos);
			blockRenderer.renderSingleBlock(
				deformState, pose, pBuffer, light, pPackedOverlay, ModelData.EMPTY, null
			);
		}
	}
}

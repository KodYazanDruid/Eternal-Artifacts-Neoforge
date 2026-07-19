package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.content.block.entity.BeaconAgitator;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.block.PlayerHeadBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.UUID;

public class BeaconAgitatorRenderer implements BlockEntityRenderer<BeaconAgitator> {
	public BeaconAgitatorRenderer(BlockEntityRendererProvider.Context context) {}
	
	@Override
	public void render(BeaconAgitator agitator, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		UUID ownerUUID = agitator.ownerUUID;
		if (ownerUUID != null && agitator.hasLevel()) {
			Player owner = agitator.getLevel().getPlayerByUUID(ownerUUID);
			if (owner != null) {
				poseStack.pushPose();
				//poseStack.scale(0.5f, 0.5f, 0.5f);
				poseStack.translate(-0.5, -0.5, -0.5);
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				poseStack.mulPose(agitator.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getRotation());
				poseStack.translate(0.5, 0.5, 0.5);
				poseStack.translate(0, 0.5, 0);
				//PlayerHeadItem.setBlockEntityData();
				poseStack.popPose();
			}
		}
	}
}

package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.content.block.entity.MachineWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;

public class MachineWorkbenchRenderer implements BlockEntityRenderer<MachineWorkbench> {
	
	public MachineWorkbenchRenderer(BlockEntityRendererProvider.Context ctx) {
	
	}
	
	@Override
	public void render(MachineWorkbench workbench, float deltaTick, PoseStack pose,
					   MultiBufferSource buff, int light, int overlay) {
		IItemHandler inventory = workbench.inventory;
		if (inventory != null) {
			ItemStack stack = inventory.getStackInSlot(0);
			if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
				Direction facing = workbench.hasLevel() ? workbench.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
				float yRot = facing.toYRot() + 180;
				pose.pushPose();
				pose.translate(0.5F, 0.5F, 0.5F);
				pose.mulPose(Axis.YN.rotationDegrees(yRot));
				pose.translate(-0.5F, -0.5F, -0.5F);
				pose.translate(0.5F, 1.25F, 0.5F);
				BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack);
				var renderer = Minecraft.getInstance().getItemRenderer();
				renderer.render(stack, ItemDisplayContext.FIXED, false, pose, buff, light, overlay, model);
				pose.popPose();
			}
		}
	}
	
	@Override
	public AABB getRenderBoundingBox(MachineWorkbench blockEntity) {
		return new AABB(blockEntity.getBlockPos()).setMaxY(blockEntity.getBlockPos().getY() + 1.5);
	}
}

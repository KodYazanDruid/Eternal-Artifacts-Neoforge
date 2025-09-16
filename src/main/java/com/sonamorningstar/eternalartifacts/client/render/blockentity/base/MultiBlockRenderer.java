package com.sonamorningstar.eternalartifacts.client.render.blockentity.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.render.util.DirectionRotationCache;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MultiBlockRenderer<MB extends AbstractMultiblockBlockEntity> implements BlockEntityRenderer<MB> {
	
	public MultiBlockRenderer(BlockEntityRendererProvider.Context context) {
	
	}
	
	/*List<Block> examples = List.of(
		Blocks.DIAMOND_BLOCK,
		Blocks.IRON_BLOCK,
		Blocks.GOLD_BLOCK,
		Blocks.EMERALD_BLOCK,
		Blocks.REDSTONE_BLOCK,
		Blocks.LAPIS_BLOCK,
		Blocks.COAL_BLOCK,
		Blocks.COPPER_BLOCK,
		Blocks.NETHERITE_BLOCK,
		Blocks.COBBLESTONE,
		Blocks.STONE,
		Blocks.DIORITE,
		Blocks.ANDESITE,
		Blocks.GRANITE,
		Blocks.SANDSTONE,
		Blocks.BRICKS,
		Blocks.COBBLESTONE_WALL,
		
	);*/
	
	@Override
	public void render(MB mbBE, float pPartialTick, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		if (!mbBE.isMaster()) return;
		Multiblock multiblock = mbBE.getMultiblock();
		BlockPattern pattern = multiblock.getPattern();
		var renderer = Minecraft.getInstance().getBlockRenderer();
		int width = pattern.getWidth();
		int height = pattern.getHeight();
		int depth = pattern.getDepth();
		BlockPos frontLeftBottomPos = BlockPattern.translateAndRotate(
			mbBE.getFrontLeftPos(), mbBE.getForwards(), mbBE.getUpwards(),
			0, height - 1, 0
		);
		pose.pushPose();
		
		pose.translate(0.5, 0.5, 0.5);
		applyOrientation(pose, mbBE.getForwards(), mbBE.getUpwards());
		pose.translate(-0.5, -0.5, -0.5);
		pose.translate(
			-multiblock.getMasterPalmOffset(), frontLeftBottomPos.getY() - mbBE.getBlockPos().getY(),
			-multiblock.getMasterFingerOffset());
		
		var localUp = DirectionRotationCache.transform(
			mbBE.getUpwards(), mbBE.getForwards(), Direction.UP
		);
		
		if (localUp == Direction.DOWN || mbBE.getUpwards() == Direction.DOWN) {
			pose.translate(0, -height + 1, 0);
		}
		
		var examples = BuiltInRegistries.BLOCK.stream().toList();
		
		int idx = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < depth; k++) {
					pose.pushPose();
					pose.translate(i, j, k);
					
					if (idx < examples.size()) {
						renderer.renderSingleBlock(
							examples.get(idx).defaultBlockState(),
							pose,
							pBuffer,
							LightTexture.FULL_BRIGHT,
							OverlayTexture.NO_OVERLAY,
							ModelData.EMPTY,
							RenderType.solid()
						);
						idx++;
					}
					
					pose.popPose();
				}
			}
		}
		
		pose.popPose();
	}
	
	private void applyOrientation(PoseStack pose, Direction forwards, Direction upwards) {
		Vector3f f = new Vector3f(forwards.step());
		Vector3f u = new Vector3f(upwards.step());
		
		Vector3f r = new Vector3f();
		u.cross(f, r);
		
		if (r.lengthSquared() < 1e-6f) {
			if (Math.abs(f.y) < 0.999f) {
				u.set(0, 1, 0);
			} else {
				u.set(1, 0, 0);
			}
			u.cross(f, r);
		}
		
		r.normalize();
		f.normalize();
		u = new Vector3f(f).cross(r).normalize();
		
		// basis -> quaternion
		Quaternionf q = quatFromBasis(r, u, f);
		pose.mulPose(q);
	}
	
	private static Quaternionf quatFromBasis(Vector3f r, Vector3f u, Vector3f f) {
		float m00 = r.x, m01 = u.x, m02 = f.x;
		float m10 = r.y, m11 = u.y, m12 = f.y;
		float m20 = r.z, m21 = u.z, m22 = f.z;
		
		float trace = m00 + m11 + m22;
		Quaternionf q = new Quaternionf();
		
		if (trace > 0f) {
			float s = (float) Math.sqrt(trace + 1.0f) * 2f;
			q.w = 0.25f * s;
			q.x = (m21 - m12) / s;
			q.y = (m02 - m20) / s;
			q.z = (m10 - m01) / s;
		} else if (m00 > m11 && m00 > m22) {
			float s = (float) Math.sqrt(1.0f + m00 - m11 - m22) * 2f;
			q.w = (m21 - m12) / s;
			q.x = 0.25f * s;
			q.y = (m01 + m10) / s;
			q.z = (m02 + m20) / s;
		} else if (m11 > m22) {
			float s = (float) Math.sqrt(1.0f + m11 - m00 - m22) * 2f;
			q.w = (m02 - m20) / s;
			q.x = (m01 + m10) / s;
			q.y = 0.25f * s;
			q.z = (m12 + m21) / s;
		} else {
			float s = (float) Math.sqrt(1.0f + m22 - m00 - m11) * 2f;
			q.w = (m10 - m01) / s;
			q.x = (m02 + m20) / s;
			q.y = (m12 + m21) / s;
			q.z = 0.25f * s;
		}
		
		return q.normalize();
	}
	
	@Override
	public AABB getRenderBoundingBox(MB mbBe) {
		if (!mbBe.isMaster()) return BlockEntityRenderer.super.getRenderBoundingBox(mbBe);
		Multiblock multiblock = mbBe.getMultiblock();
		BlockPattern pattern = multiblock.getPattern();
		int width = pattern.getWidth();
		int height = pattern.getHeight();
		int depth = pattern.getDepth();
		return AABB.encapsulatingFullBlocks(mbBe.getFrontLeftPos(), BlockPattern.translateAndRotate(
			mbBe.getFrontLeftPos(), mbBe.getForwards(), mbBe.getUpwards(),
			width - 1, height - 1, depth - 1
		));
	}
}

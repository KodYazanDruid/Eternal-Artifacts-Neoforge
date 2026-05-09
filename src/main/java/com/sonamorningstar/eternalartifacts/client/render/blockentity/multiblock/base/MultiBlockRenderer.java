package com.sonamorningstar.eternalartifacts.client.render.blockentity.multiblock.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class MultiBlockRenderer<MB extends AbstractMultiblockBlockEntity> implements BlockEntityRenderer<MB> {
	
	public MultiBlockRenderer(BlockEntityRendererProvider.Context context) {
	
	}
	
	@Override
	public void render(MB master, float pPartialTick, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		if (!master.isMaster()) return;
		
		BlockPos frontLeftPos = master.getFrontLeftTopPos();
		if (frontLeftPos == null) return;
		BlockPos masterPos = master.getBlockPos();
		
		pose.pushPose();
		
		// Anchor: pattern local (0,0,0) -> world front-left of multiblock
		pose.translate(
			frontLeftPos.getX() - masterPos.getX(),
			frontLeftPos.getY() - masterPos.getY(),
			frontLeftPos.getZ() - masterPos.getZ()
		);
		
		// Rotate around block center to avoid half-block drift on non-default axes
		pose.translate(0.5, 0.5, 0.5);
		applyOrientation(pose, master.getForwards(), master.getUpwards());
		pose.translate(-0.5, -0.5, -0.5);
		
		int width = master.getMbWidth();
		int height = master.getMbHeight();
		int depth = master.getMbDepth();
		
		pose.translate(1 - width, 1 - height, 0);
		
		renderMultiblock(master, pose, pBuffer, width, height, depth, pPackedLight, pPackedOverlay);
		
		pose.popPose();
	}
	
	/**
	 *  Override this to render the actual multiblock. Origin point is at the front bottom left of the pattern.
	 */
	protected void renderMultiblock(MB master, PoseStack pose, MultiBufferSource buffer,
									int width, int height, int depth,
									int packedLight, int packedOverlay) {
	
	}
	
	private void applyOrientation(PoseStack pose, Direction forwards, Direction upwards) {
		Vector3f f = new Vector3f(forwards.step());
		Vector3f u = new Vector3f(upwards.step());
		
		if (Math.abs(f.dot(u)) >0.999f) {
			u = (Math.abs(f.y) <0.999f) ?
				new Vector3f(0,1,0) :
				new Vector3f(1,0,0);
		}
		
		Vector3f r = new Vector3f();
		u.cross(f, r).normalize();
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
		int width = mbBe.getMbWidth();
		int height = mbBe.getMbHeight();
		int depth = mbBe.getMbDepth();
		return AABB.encapsulatingFullBlocks(mbBe.getFrontLeftTopPos(), BlockPattern.translateAndRotate(
			mbBe.getFrontLeftTopPos(), mbBe.getForwards(), mbBe.getUpwards(),
			width - 1, height - 1, depth - 1
		));
	}
}

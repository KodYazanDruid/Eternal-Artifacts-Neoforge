package com.sonamorningstar.eternalartifacts.content.multiblock.base;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.content.block.base.MultiblockBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.function.Supplier;

@Getter
@Setter
public class Multiblock {
	public static final Multimap<Multiblock, BlockPattern> PATTERNS = HashMultimap.create();
	public static final Codec<Multiblock> CODEC = ModRegistries.MULTIBLOCK.byNameCodec();
	private final Supplier<ResourceKey<Multiblock>> key = Suppliers.memoize(() -> ModRegistries.MULTIBLOCK.getResourceKey(this).get());
	private final Supplier<MultiblockBlock> multiblockBlock;
	private final MultiblockCapabilityManager capabilityManager;
	private final int masterPalmOffset;
	private final int masterThumbOffset;
	private final int masterFingerOffset;
	private final int clickablePalmOffset;
	private final int clickableThumbOffset;
	private final int clickableFingerOffset;
	private final boolean isLockedHorizontally;
	
	private MultiblockShapeProvider shapeProvider = (part, ctx) -> part.getDeformState().getShape(part.getLevel(), part.getBlockPos(), ctx);
	private MultiblockShapeProvider visualShapeProvider = (part, ctx) -> part.getDeformState().getVisualShape(part.getLevel(), part.getBlockPos(), ctx);
	private MultiblockShapeProvider collisionShapeProvider  = (part, ctx) -> part.getDeformState().getCollisionShape(part.getLevel(), part.getBlockPos(), ctx);
	
	public Multiblock(Supplier<MultiblockBlock> multiblockBlock,
					  int masterPalmOffset, int masterThumbOffset, int masterFingerOffset,
					  MultiblockCapabilityManager capabilityManager, boolean isLockedHorizontally) {
		this(multiblockBlock, masterPalmOffset, masterThumbOffset, masterFingerOffset,
				masterPalmOffset, masterThumbOffset, masterFingerOffset, capabilityManager, isLockedHorizontally);
	}

	public Multiblock(Supplier<MultiblockBlock> multiblockBlock,
					  int masterPalmOffset, int masterThumbOffset, int masterFingerOffset,
					  int clickablePalmOffset, int clickableThumbOffset, int clickableFingerOffset,
					  MultiblockCapabilityManager capabilityManager, boolean isLockedHorizontally) {
		this.multiblockBlock = multiblockBlock;
		this.masterPalmOffset = masterPalmOffset;
		this.masterThumbOffset = masterThumbOffset;
		this.masterFingerOffset = masterFingerOffset;
		this.clickablePalmOffset = clickablePalmOffset;
		this.clickableThumbOffset = clickableThumbOffset;
		this.clickableFingerOffset = clickableFingerOffset;
		this.capabilityManager = capabilityManager;
		this.isLockedHorizontally = isLockedHorizontally;
	}

	public void setShapeProviders(MultiblockShapeProvider provider) {
		this.shapeProvider = provider;
		this.visualShapeProvider = provider;
		this.collisionShapeProvider = provider;
	}
	
	public void setFullShape(VoxelShape fullShape) {
		setShapeProviders((part, ctx) -> {
			AbstractMultiblockBlockEntity master = part.getMasterBlockEntity();
			if (master == null) return Shapes.block();
			
			BlockPos frontTop = master.getFrontLeftTopPos();
			if (frontTop == null) return Shapes.block();

			Vector3f F = new Vector3f(master.getForwards().step());
			Vector3f U = new Vector3f(master.getUpwards().step());
			Vector3f R = new Vector3f();
			U.cross(F, R);

			if (R.lengthSquared() < 1e-6f) {
				if (Math.abs(F.y) < 0.999f) U.set(0, 1, 0); else U.set(1, 0, 0);
				U.cross(F, R);
			}
			R.normalize();
			F.normalize();
			U = new Vector3f(F).cross(R).normalize();

			int width = master.getMbWidth();
			int height = master.getMbHeight();

			double cx = (0.5 - width) * R.x() + (0.5 - height) * U.x() + (-0.5) * F.x();
			double cy = (0.5 - width) * R.y() + (0.5 - height) * U.y() + (-0.5) * F.y();
			double cz = (0.5 - width) * R.z() + (0.5 - height) * U.z() + (-0.5) * F.z();

			double dx = frontTop.getX() - part.getBlockPos().getX();
			double dy = frontTop.getY() - part.getBlockPos().getY();
			double dz = frontTop.getZ() - part.getBlockPos().getZ();

			double tx = cx + 0.5 + dx;
			double ty = cy + 0.5 + dy;
			double tz = cz + 0.5 + dz;

			VoxelShape rotated = rotateShapeInternal(fullShape, R, U, F);
			VoxelShape shifted = rotated.move(tx, ty, tz);
			VoxelShape intersection = Shapes.join(shifted, Shapes.block(), BooleanOp.AND);
			return intersection.isEmpty() ? Shapes.empty() : intersection;
		});
	}

	private static VoxelShape rotateShapeInternal(VoxelShape shape, Vector3f R, Vector3f U, Vector3f F) {
		VoxelShape rotated = Shapes.empty();
		for (AABB aabb : shape.toAabbs()) {
			double minX = aabb.minX * R.x() + aabb.minY * U.x() + aabb.minZ * F.x();
			double minY = aabb.minX * R.y() + aabb.minY * U.y() + aabb.minZ * F.y();
			double minZ = aabb.minX * R.z() + aabb.minY * U.z() + aabb.minZ * F.z();

			double maxX = aabb.maxX * R.x() + aabb.maxY * U.x() + aabb.maxZ * F.x();
			double maxY = aabb.maxX * R.y() + aabb.maxY * U.y() + aabb.maxZ * F.y();
			double maxZ = aabb.maxX * R.z() + aabb.maxY * U.z() + aabb.maxZ * F.z();

			rotated = Shapes.or(rotated, Shapes.create(
				Math.min(minX, maxX), Math.min(minY, maxY), Math.min(minZ, maxZ),
				Math.max(minX, maxX), Math.max(minY, maxY), Math.max(minZ, maxZ)
			));
		}
		return rotated;
	}

	public interface MultiblockShapeProvider {
		VoxelShape getShape(AbstractMultiblockBlockEntity part, CollisionContext context);
	}
}

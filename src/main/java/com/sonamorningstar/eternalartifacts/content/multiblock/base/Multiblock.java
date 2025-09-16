package com.sonamorningstar.eternalartifacts.content.multiblock.base;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.content.block.base.MultiblockBlock;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.util.RelativeBlockPos;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Getter
public class Multiblock {
	public static final Codec<Multiblock> CODEC = ModRegistries.MULTIBLOCK.byNameCodec();
	private final Supplier<ResourceKey<Multiblock>> key = Suppliers.memoize(() -> ModRegistries.MULTIBLOCK.getResourceKey(this).get());
	public static final Map<Multiblock, BlockPattern> PATTERNS = new HashMap<>();
	private final Supplier<MultiblockBlock> multiblockBlock;
	private final int masterPalmOffset;
	private final int masterThumbOffset;
	private final int masterFingerOffset;
	private final boolean isLockedRotation;
	
	private final BlockPattern pattern;
	private final MultiblockCapabilityManager capabilityManager;
	
	public Multiblock(BlockPattern pattern, Supplier<MultiblockBlock> multiblockBlock,
					  int masterPalmOffset, int masterThumbOffset, int masterFingerOffset,
					  MultiblockCapabilityManager capabilityManager, boolean isLockedRotation) {
		this.pattern = pattern;
		this.multiblockBlock = multiblockBlock;
		this.masterPalmOffset = masterPalmOffset;
		this.masterThumbOffset = masterThumbOffset;
		this.masterFingerOffset = masterFingerOffset;
		this.capabilityManager = capabilityManager;
		this.isLockedRotation = isLockedRotation;
		PATTERNS.put(this, pattern);
	}
	
	public static RelativeBlockPos getMultiblockOffset(BlockPos frontLeftPos, BlockPos targetPos, Direction forwards, Direction upwards) {
		// Referans noktasından hedef noktasına göre göreceli pozisyonu hesapla
		int xOffset = Math.abs(targetPos.getX() - frontLeftPos.getX());
		int yOffset = Math.abs(targetPos.getY() - frontLeftPos.getY());
		int zOffset = Math.abs(targetPos.getZ() - frontLeftPos.getZ());
		return new RelativeBlockPos(xOffset, yOffset, zOffset);
		
		/*BlockPattern.translateAndRotate()
		// Yönlere göre göreceli pozisyonu ayarla
		if (forwards == Direction.NORTH) zOffset = -zOffset;
		else if (forwards == Direction.SOUTH) {
		}
		if (upwards == Direction.UP) yOffset = -yOffset;
		else if (upwards == Direction.DOWN) {
		}
		
		return new RelativeBlockPos(xOffset, yOffset, zOffset);*/
	}
	
}

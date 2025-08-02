package com.sonamorningstar.eternalartifacts.content.multiblock.base;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.sonamorningstar.eternalartifacts.content.block.base.AbstractMultiblockBlock;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import lombok.Getter;
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
	private final Supplier<AbstractMultiblockBlock> multiblockBlock;
	private final int masterPalmOffset;
	private final int masterThumbOffset;
	private final int masterFingerOffset;
	
	private final BlockPattern pattern;
	
	public Multiblock(BlockPattern pattern, Supplier<AbstractMultiblockBlock> multiblockBlock, int masterPalmOffset, int masterThumbOffset, int masterFingerOffset) {
		this.pattern = pattern;
		this.multiblockBlock = multiblockBlock;
		this.masterPalmOffset = masterPalmOffset;
		this.masterThumbOffset = masterThumbOffset;
		this.masterFingerOffset = masterFingerOffset;
		PATTERNS.put(this, pattern);
	}
	
}

package com.sonamorningstar.eternalartifacts.content.block.base;

import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class PortBlock extends Block {
	public static final Set<PortBlock> PORT_BLOCKS = new HashSet<>();
	public PortBlock(Properties properties) {
		super(properties);
		PORT_BLOCKS.add(this);
	}
}

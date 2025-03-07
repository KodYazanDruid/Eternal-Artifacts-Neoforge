package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.world.level.block.SkullBlock;

public enum ModSkullType implements SkullBlock.Type{
	DROWNED("drowned"),
	HUSK("husk"),
	STRAY("stray");
	
	private final String name;
	
	ModSkullType(String name) {
		this.name = name;
		TYPES.put(name, this);
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
}

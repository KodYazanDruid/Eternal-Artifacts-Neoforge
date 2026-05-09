package com.sonamorningstar.eternalartifacts.event.custom;

import com.google.common.collect.Multimap;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class RegisterMultiblockPatternsEvent extends Event implements IModBusEvent {
	private final Multimap<Multiblock, BlockPattern> patterns;
	
	public RegisterMultiblockPatternsEvent(Multimap<Multiblock, BlockPattern> patterns) {
		this.patterns = patterns;
	}
	
	public void register(Multiblock multiblock, BlockPattern pattern) {
		if (patterns.containsEntry(multiblock, pattern)) {
			throw new IllegalArgumentException("Multiblock pattern already registered for this multiblock: " + multiblock.getKey().get());
		}
		patterns.put(multiblock, pattern);
	}
}

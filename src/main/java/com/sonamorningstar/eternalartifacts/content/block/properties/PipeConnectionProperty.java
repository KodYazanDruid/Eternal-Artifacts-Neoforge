package com.sonamorningstar.eternalartifacts.content.block.properties;

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class PipeConnectionProperty extends Property<PipeConnectionProperty.PipeConnection> {
	
	private final String name;
	
	protected PipeConnectionProperty(String name) {
		super(name, PipeConnection.class);
		this.name = name;
	}
	
	public static PipeConnectionProperty create(String name) {
		return new PipeConnectionProperty(name);
	}
	
	@Override
	public Collection<PipeConnection> getPossibleValues() {
		return ImmutableSet.of(PipeConnection.FILTERED, PipeConnection.EXTRACT, PipeConnection.FREE, PipeConnection.NONE);
	}
	
	@Override
	public String getName(PipeConnection value) {
		return value.name;
	}
	
	@Override
	public Optional<PipeConnection> getValue(String value) {
		return Arrays.stream(PipeConnection.values()).filter(v -> v.getSerializedName().equals(value)).findFirst();
	}
	
	public enum PipeConnection implements StringRepresentable {
		FILTERED("filtered"),
		EXTRACT("extract"),
		FREE("free"),
		NONE("none");
		
		private final String name;
		
		PipeConnection(String name) {
			this.name = name;
		}
		
		@Override
		public String getSerializedName() {
			return name;
		}
	}
}

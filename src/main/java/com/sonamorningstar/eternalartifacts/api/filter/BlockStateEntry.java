package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.*;

@Setter
@Getter
public class BlockStateEntry implements BlockFilterEntry {
	@Nullable
	private BlockState filterState;
	private boolean ignoreProperties;
	private boolean isWhitelist = true;
	private Set<String> matchingProperties = new HashSet<>();
	
	public static final BlockStateEntry EMPTY = new BlockStateEntry(null, true);
	
	public BlockStateEntry(@Nullable BlockState filterState, boolean ignoreProperties) {
		this.filterState = filterState;
		this.ignoreProperties = ignoreProperties;
	}
	
	public BlockStateEntry(BlockState filterState, String... propertiesToMatch) {
		this.filterState = filterState;
		this.ignoreProperties = false;
		this.matchingProperties.addAll(Arrays.asList(propertiesToMatch));
	}
	
	public static BlockStateEntry matchAllProperties(BlockState state) {
		BlockStateEntry entry = new BlockStateEntry(state, false);
		for (Property<?> prop : state.getProperties()) {
			entry.matchingProperties.add(prop.getName());
		}
		return entry;
	}
	
	public static BlockStateEntry matchBlockOnly(BlockState state) {
		return new BlockStateEntry(state, true);
	}
	
	@Override
	public boolean matches(BlockState state) {
		if (filterState == null) return state.isAir() || state.isEmpty();
		if (state.getBlock() != filterState.getBlock()) return false;
		if (ignoreProperties) return true;
		if (matchingProperties.isEmpty()) return true;
		
		for (Property<?> prop : filterState.getProperties()) {
			if (matchingProperties.contains(prop.getName())) {
				if (!propertyMatches(state, filterState, prop)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private <T extends Comparable<T>> boolean propertyMatches(BlockState state, BlockState filter, Property<T> property) {
		if (!state.hasProperty(property)) {
			return false;
		}
		return state.getValue(property).equals(filter.getValue(property));
	}
	
	@Override
	public boolean isEmpty() {
		return filterState == null || filterState.isEmpty() || filterState.isAir();
	}
	
	@Override
	public Component getDisplayName() {
		return filterState != null ? filterState.getBlock().getName() : Component.literal("Empty");
	}
	
	@Override
	public boolean isIgnoreNBT() {
		return ignoreProperties;
	}
	
	@Override
	public void setIgnoreNBT(boolean ignoreProperties) {
		this.ignoreProperties = ignoreProperties;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Type", "State");
		
		if (filterState != null) {
			ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(filterState.getBlock());
			tag.putString("Block", blockId.toString());
			
			// Property değerlerini kaydet
			CompoundTag propsTag = new CompoundTag();
			for (Property<?> prop : filterState.getProperties()) {
				propsTag.putString(prop.getName(), getPropertyValueName(filterState, prop));
			}
			tag.put("Properties", propsTag);
			
			// Eşleştirilecek property isimlerini kaydet
			ListTag matchingList = new ListTag();
			for (String propName : matchingProperties) {
				matchingList.add(StringTag.valueOf(propName));
			}
			tag.put("MatchingProperties", matchingList);
		}
		
		tag.putBoolean("IgnoreProperties", ignoreProperties);
		tag.putBoolean("IsWhitelist", isWhitelist);
		return tag;
	}
	
	private <T extends Comparable<T>> String getPropertyValueName(BlockState state, Property<T> property) {
		return property.getName(state.getValue(property));
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.ignoreProperties = tag.getBoolean("IgnoreProperties");
		this.isWhitelist = tag.getBoolean("IsWhitelist");
		
		if (tag.contains("Block")) {
			ResourceLocation blockId = new ResourceLocation(tag.getString("Block"));
			Block block = BuiltInRegistries.BLOCK.get(blockId);
			
			if (block != Blocks.AIR) {
				this.filterState = block.defaultBlockState();
				
				// Property değerlerini oku ve uygula
				if (tag.contains("Properties")) {
					CompoundTag propsTag = tag.getCompound("Properties");
					for (Property<?> prop : filterState.getProperties()) {
						if (propsTag.contains(prop.getName())) {
							String valueName = propsTag.getString(prop.getName());
							this.filterState = setPropertyValue(filterState, prop, valueName);
						}
					}
				}
				
				// Eşleştirilecek property isimlerini oku
				this.matchingProperties.clear();
				if (tag.contains("MatchingProperties")) {
					ListTag matchingList = tag.getList("MatchingProperties", Tag.TAG_STRING);
					for (int i = 0; i < matchingList.size(); i++) {
						matchingProperties.add(matchingList.getString(i));
					}
				}
			}
		}
	}
	
	private <T extends Comparable<T>> BlockState setPropertyValue(BlockState state, Property<T> property, String valueName) {
		Optional<T> value = property.getValue(valueName);
		return value.map(t -> state.setValue(property, t)).orElse(state);
	}
	
	@Override
	public String toString() {
		return "BlockStateEntry{" +
			"filterState=" + filterState +
			", ignoreProperties=" + ignoreProperties +
			", matchingProperties=" + matchingProperties +
			", isWhitelist=" + isWhitelist +
			'}';
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buff) {
		buff.writeUtf("State");
		buff.writeBoolean(filterState != null);
		
		if (filterState != null) {
			buff.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(filterState.getBlock()));
			
			// Property değerlerini yaz
			Collection<Property<?>> props = filterState.getProperties();
			buff.writeVarInt(props.size());
			for (Property<?> prop : props) {
				buff.writeUtf(prop.getName());
				buff.writeUtf(getPropertyValueName(filterState, prop));
			}
			
			// Eşleştirilecek property isimlerini yaz
			buff.writeVarInt(matchingProperties.size());
			for (String propName : matchingProperties) {
				buff.writeUtf(propName);
			}
		}
		
		buff.writeBoolean(ignoreProperties);
		buff.writeBoolean(isWhitelist);
	}
	
	@Override
	public void fromNetwork(FriendlyByteBuf buff) {
		boolean hasState = buff.readBoolean();
		
		if (hasState) {
			ResourceLocation blockId = buff.readResourceLocation();
			Block block = BuiltInRegistries.BLOCK.get(blockId);
			this.filterState = block.defaultBlockState();
			
			// Property değerlerini oku
			int propCount = buff.readVarInt();
			for (int i = 0; i < propCount; i++) {
				String propName = buff.readUtf();
				String valueName = buff.readUtf();
				
				if (filterState != null) {
					Property<?> prop = filterState.getBlock().getStateDefinition().getProperty(propName);
					if (prop != null) {
						this.filterState = setPropertyValue(filterState, prop, valueName);
					}
				}
			}
			
			// Eşleştirilecek property isimlerini oku
			this.matchingProperties.clear();
			int matchingCount = buff.readVarInt();
			for (int i = 0; i < matchingCount; i++) {
				matchingProperties.add(buff.readUtf());
			}
		} else {
			this.filterState = null;
		}
		
		this.ignoreProperties = buff.readBoolean();
		this.isWhitelist = buff.readBoolean();
	}
	
	/**
	 * Belirli bir property'yi eşleştirme listesine ekler
	 */
	public void addMatchingProperty(String propertyName) {
		matchingProperties.add(propertyName);
	}
	
	/**
	 * Belirli bir property'yi eşleştirme listesinden çıkarır
	 */
	public void removeMatchingProperty(String propertyName) {
		matchingProperties.remove(propertyName);
	}
	
	/**
	 * Property'nin eşleştirme listesinde olup olmadığını kontrol eder
	 */
	public boolean isPropertyMatched(String propertyName) {
		return matchingProperties.contains(propertyName);
	}
	
	/**
	 * Tüm property'leri eşleştirme listesine ekler
	 */
	public void matchAllProperties() {
		if (filterState != null) {
			for (Property<?> prop : filterState.getProperties()) {
				matchingProperties.add(prop.getName());
			}
		}
	}
	
	/**
	 * Eşleştirme listesini temizler (sadece blok eşleşmesi)
	 */
	public void clearMatchingProperties() {
		matchingProperties.clear();
	}
}


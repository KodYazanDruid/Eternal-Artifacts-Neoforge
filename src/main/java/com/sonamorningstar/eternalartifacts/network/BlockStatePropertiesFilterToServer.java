package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.FilterSyncable;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.network.base.PacketHelper.*;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record BlockStatePropertiesFilterToServer(
	int containerId,
	int index,
	@Nullable BlockState blockState,
	Set<String> matchingProperties,
	Map<String, String> propertyValues
) implements ServerPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "block_state_properties_filter_to_server");
	
	public static BlockStatePropertiesFilterToServer create(FriendlyByteBuf buf) {
		int containerId = readContainerId(buf);
		int index = readIndex(buf);
		boolean hasState = buf.readBoolean();
		BlockState state = null;
		Set<String> matchingProps = new HashSet<>();
		Map<String, String> propValues = new HashMap<>();
		
		if (hasState) {
			ResourceLocation blockId = buf.readResourceLocation();
			Block block = BuiltInRegistries.BLOCK.get(blockId);
			state = block.defaultBlockState();
			
			// Property değerlerini oku ve uygula
			int propCount = buf.readVarInt();
			for (int i = 0; i < propCount; i++) {
				String propName = buf.readUtf();
				String propValue = buf.readUtf();
				propValues.put(propName, propValue);
				
				// Property değerini BlockState'e uygula
				for (Property<?> prop : state.getProperties()) {
					if (prop.getName().equals(propName)) {
						state = setPropertyValue(state, prop, propValue);
						break;
					}
				}
			}
			
			// Eşleştirilecek property isimlerini oku
			int matchingCount = buf.readVarInt();
			for (int i = 0; i < matchingCount; i++) {
				matchingProps.add(buf.readUtf());
			}
		}
		
		return new BlockStatePropertiesFilterToServer(containerId, index, state, matchingProps, propValues);
	}
	
	private static <T extends Comparable<T>> BlockState setPropertyValue(BlockState state, Property<T> property, String valueName) {
		Optional<T> value = property.getValue(valueName);
		return value.map(t -> state.setValue(property, t)).orElse(state);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		writeContainerId(buff, containerId);
		writeIndex(buff, index);
		boolean hasState = blockState != null && !blockState.isAir();
		buff.writeBoolean(hasState);
		
		if (hasState) {
			ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
			buff.writeResourceLocation(blockId);
			
			// Property değerlerini yaz
			buff.writeVarInt(propertyValues.size());
			for (Map.Entry<String, String> entry : propertyValues.entrySet()) {
				buff.writeUtf(entry.getKey());
				buff.writeUtf(entry.getValue());
			}
			
			// Eşleştirilecek property isimlerini yaz
			buff.writeVarInt(matchingProperties.size());
			for (String propName : matchingProperties) {
				buff.writeUtf(propName);
			}
		}
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	@Override
	public void handleOnServer(ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId == containerId && menu instanceof FilterSyncable syncable) {
			syncable.blockStatePropertiesFilterSync(this);
		}
	}
}


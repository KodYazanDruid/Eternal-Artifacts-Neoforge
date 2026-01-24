package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.api.filter.FilterEntry;
import com.sonamorningstar.eternalartifacts.network.*;
import net.minecraft.core.NonNullList;

/**
 * Interface for menus that can sync filter entries from packets
 */
public interface FilterSyncable {
	
	NonNullList<FilterEntry> getFilterEntries();
	
	void itemTagFilterSynch(ItemTagFilterToServer pkt);
	void fluidStackFilterSync(FluidStackFilterToServer pkt);
	void fluidTagFilterSync(FluidTagFilterToServer pkt);
	default void blockStateFilterSync(BlockStateFilterToServer pkt) {}
	default void blockTagFilterSync(BlockTagFilterToServer pkt) {}
	default void blockStatePropertiesFilterSync(BlockStatePropertiesFilterToServer pkt) {}
}

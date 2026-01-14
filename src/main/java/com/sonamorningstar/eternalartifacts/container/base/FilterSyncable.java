package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.network.*;

/**
 * Interface for menus that can sync filter entries from packets
 */
public interface FilterSyncable {
	void itemTagFilterSynch(ItemTagFilterToServer pkt);
	void fluidStackFilterSync(FluidStackFilterToServer pkt);
	void fluidTagFilterSync(FluidTagFilterToServer pkt);
	default void blockStateFilterSync(BlockStateFilterToServer pkt) {}
	default void blockTagFilterSync(BlockTagFilterToServer pkt) {}
	default void blockStatePropertiesFilterSync(BlockStatePropertiesFilterToServer pkt) {}
}

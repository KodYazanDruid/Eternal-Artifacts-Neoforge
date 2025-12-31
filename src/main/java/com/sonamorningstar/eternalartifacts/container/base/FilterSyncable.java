package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.network.FluidStackFilterToServer;
import com.sonamorningstar.eternalartifacts.network.FluidTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.ItemTagFilterToServer;

/**
 * Interface for menus that can sync filter entries from packets
 */
public interface FilterSyncable {
	void itemTagFilterSynch(ItemTagFilterToServer pkt);
	void fluidStackFilterSync(FluidStackFilterToServer pkt);
	void fluidTagFilterSync(FluidTagFilterToServer pkt);
}


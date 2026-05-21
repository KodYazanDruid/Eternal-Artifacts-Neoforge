package com.sonamorningstar.eternalartifacts.container.base;

import com.sonamorningstar.eternalartifacts.api.filter.ItemFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.FluidFilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.BlockFilterEntry;
import com.sonamorningstar.eternalartifacts.network.*;
import net.minecraft.core.NonNullList;

/**
 * Interface for menus that can sync filter entries from packets
 */
public interface FilterSyncable {
    
    default NonNullList<ItemFilterEntry> getItemFilterEntries() { return NonNullList.create(); }
    default NonNullList<FluidFilterEntry> getFluidFilterEntries() { return NonNullList.create(); }
    default NonNullList<BlockFilterEntry> getBlockFilterEntries() { return NonNullList.create(); }
    
    void itemTagFilterSynch(ItemTagFilterToServer pkt);
    void fluidStackFilterSync(FluidStackFilterToServer pkt);
    void fluidTagFilterSync(FluidTagFilterToServer pkt);
    default void blockStateFilterSync(BlockStateFilterToServer pkt) {}
    default void blockTagFilterSync(BlockTagFilterToServer pkt) {}
    default void blockStatePropertiesFilterSync(BlockStatePropertiesFilterToServer pkt) {}
}

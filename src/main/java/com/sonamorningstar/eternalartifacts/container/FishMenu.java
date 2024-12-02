package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class FishMenu extends TabMenu {
    public FishMenu(int id, Inventory inv, FriendlyByteBuf buff) {
        super(ModMenuTypes.FISH.get(), id);
        addPlayerInventoryAndHotbar(inv, 8, 66);
    }
}

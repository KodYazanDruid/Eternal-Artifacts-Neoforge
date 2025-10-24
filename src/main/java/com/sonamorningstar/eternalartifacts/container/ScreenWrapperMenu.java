package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ScreenWrapperMenu extends TabMenu {
    public ScreenWrapperMenu(int id, Inventory inv, FriendlyByteBuf buff) {
        super(ModMenuTypes.SCREEN_WRAPPER.get(), id, inv);
        addPlayerInventoryAndHotbar(inv, 8, 66);
    }
}

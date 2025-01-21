package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CharmsMenu extends TabMenu {
    public final Player player;
    public CharmsMenu(int id, Inventory inv, FriendlyByteBuf buff) {
        super(ModMenuTypes.CHARMS.get(), id);
        this.player = inv.player;
        addPlayerInventoryAndHotbar(inv, 8, 66);
        CharmStorage charms = CharmStorage.get(player);
        for (int i = 0; i < charms.getSlots(); i++) {
            if (i == 12 && CharmStorage.canHaveWildcard(player)) addSlot(new SlotItemHandler(charms, i, 150, 36));
            else if (i != 12) addSlot(new SlotItemHandler(charms, i, calculateX(i), calculateY(i)));
        }
    }

    private static int calculateX(int index) {
        int slide = index < 6 ? 0 : 87;
        return 27 + slide + (index % 2) * 18;
    }
    private static int calculateY(int index) {
        if (index % 6 == 0 || index % 6 == 1) return 18;
        else if (index % 6 == 2 || index % 6 == 3) return 36;
        else if (index % 6 == 4 || index % 6 == 5) return 54;
        return 0;
    }
}

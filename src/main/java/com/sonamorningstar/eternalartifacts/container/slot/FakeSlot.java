package com.sonamorningstar.eternalartifacts.container.slot;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Getter
public class FakeSlot extends Slot {
    private final boolean displayOnly;
    public FakeSlot(Container container, int index, int x, int y, boolean displayOnly) {
        super(container, index, x, y);
        this.displayOnly = displayOnly;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {return false;}
    @Override
    public boolean mayPickup(Player pPlayer) {return false;}

    @Override
    public boolean isFake() {return true;}

}

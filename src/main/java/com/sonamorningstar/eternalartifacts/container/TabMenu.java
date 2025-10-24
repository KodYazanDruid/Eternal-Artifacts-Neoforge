package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class TabMenu extends AbstractModContainerMenu {
    protected TabMenu(@Nullable MenuType<?> menuType, int id, Inventory inv) {
        super(menuType, id, inv);
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isDeadOrDying();
    }
}

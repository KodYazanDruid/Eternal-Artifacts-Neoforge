package com.sonamorningstar.eternalartifacts.content.tabs.base;

import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.registrar.TabType;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;

public abstract class AbstractInventoryTab implements MenuProvider {
    @Getter
    protected final TabType<?> type;
    protected final FriendlyByteBuf data;

    public AbstractInventoryTab(TabType<?> type, FriendlyByteBuf data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public Component getDisplayName() {
        String path = ModRegistries.TAB_TYPE.getKey(type).getPath();
        return ModConstants.INVENTORY_TAB.withSuffixTranslatable(path);
    }

}

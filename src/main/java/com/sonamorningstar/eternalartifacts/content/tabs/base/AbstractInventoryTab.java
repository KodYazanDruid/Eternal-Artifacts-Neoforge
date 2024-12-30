package com.sonamorningstar.eternalartifacts.content.tabs.base;

import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.registrar.TabType;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class AbstractInventoryTab implements MenuProvider {
    @Getter
    protected final TabType<?> type;
    protected final FriendlyByteBuf data;

    public AbstractInventoryTab(TabType<?> type, FriendlyByteBuf data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Writes data on the server side to be sent to the client.
     * <br><br>
     * Written data will be accessed on the client side.
     */
    @Nullable
    public Consumer<FriendlyByteBuf> getBytes(Player player) {
        return null;
    };

    @Override
    public Component getDisplayName() {
        String path = ModRegistries.TAB_TYPE.getKey(type).getPath();
        return ModConstants.INVENTORY_TAB.withSuffixTranslatable(path);
    }

}

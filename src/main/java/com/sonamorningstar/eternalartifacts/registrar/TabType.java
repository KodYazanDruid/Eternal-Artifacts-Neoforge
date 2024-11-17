package com.sonamorningstar.eternalartifacts.registrar;

import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class TabType<T extends AbstractInventoryTab> {
    private final TabType.TabSupplier<T> constructor;

    @Getter
    private final Supplier<Item> icon;

    public TabType(TabType.TabSupplier<T> constructor, Supplier<Item> icon) {
        this.constructor = constructor;
        this.icon = icon;
    }

    public static <Y extends AbstractInventoryTab> TabType<Y> create(TabType.TabSupplier<Y> constructor, Supplier<Item> icon) {
        return new TabType<>(constructor, icon);
    }

    public T create(FriendlyByteBuf data) {
        return this.constructor.create(data);
    }

    public T create() {
        return this.constructor.create(null);
    }

    public String getDescriptionId() {
        String path = ModRegistries.TAB_TYPE.getKey(this).getPath();
        return ModConstants.INVENTORY_TAB.withSuffix(path);
    }

    public interface TabSupplier<T extends AbstractInventoryTab> {
        T create(@Nullable FriendlyByteBuf data);
    }
}

package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.tabs.CatTab;
import com.sonamorningstar.eternalartifacts.content.tabs.CharmsTab;
import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.content.tabs.EnderChestTab;
import com.sonamorningstar.eternalartifacts.content.tabs.PlayerInventoryTab;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.registrar.TabType;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModInventoryTabs {
    public static final DeferredRegister<TabType<?>> INVENTORY_TABS = DeferredRegister.create(ModRegistries.TAB_TYPE, MODID);

    public static final DeferredHolder<TabType<?>, TabType<PlayerInventoryTab>> INVENTORY = register("inventory",
            TabType.create(PlayerInventoryTab::new, () -> Items.CHEST));
    public static final DeferredHolder<TabType<?>, TabType<AbstractInventoryTab>> CHARMS = register("charms",
            TabType.create(CharmsTab::new, () -> Items.DIAMOND_CHESTPLATE));
    public static final DeferredHolder<TabType<?>, TabType<EnderChestTab>> ENDER_KNAPSACK = register("ender_knapsack",
            TabType.create(EnderChestTab::new, () -> Items.ENDER_CHEST));
    public static final DeferredHolder<TabType<?>, TabType<CatTab>> CAT = register("cat",
            TabType.create(CatTab::new, ModItems.CHLOROPHYTE_INGOT));

    private static <I extends AbstractInventoryTab> DeferredHolder<TabType<?>, TabType<I>> register(String name, TabType<I> tab) {
        return INVENTORY_TABS.register(name, () -> tab);
    }
}

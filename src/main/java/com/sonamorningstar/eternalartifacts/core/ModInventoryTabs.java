package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.tabs.*;
import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
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
    public static final DeferredHolder<TabType<?>, TabType<KnapsackTab>> KNAPSACK = register("knapsack",
            TabType.create(KnapsackTab::new, ModItems.KNAPSACK));
    public static final DeferredHolder<TabType<?>, TabType<EnderChestTab>> ENDER_KNAPSACK = register("ender_knapsack",
            TabType.create(EnderChestTab::new, () -> Items.ENDER_CHEST));
    public static final DeferredHolder<TabType<?>, TabType<TankKnapsackTab>> TANK_KNAPSACK = register("tank_knapsack",
            TabType.create(TankKnapsackTab::new, ModItems.TANK_KNAPSACK));
    public static final DeferredHolder<TabType<?>, TabType<CrafterTab>> CRAFTER = register("crafter",
            TabType.create(CrafterTab::new, () -> Items.CRAFTING_TABLE));
    public static final DeferredHolder<TabType<?>, TabType<FishTab>> FISH_TAB = register("fish_tab",
            TabType.create(FishTab::new, () -> Items.COD));
    public static final DeferredHolder<TabType<?>, TabType<PortableBatteryTab>> PORTABLE_BATTERY = register("portable_battery",
            TabType.create(PortableBatteryTab::new, ModItems.PORTABLE_BATTERY));
    public static final DeferredHolder<TabType<?>, TabType<PortableFurnaceTab>> PORTABLE_FURNACE = register("portable_furnace",
        TabType.create(PortableFurnaceTab::new, () -> Items.FURNACE));

    public static final DeferredHolder<TabType<?>, TabType<CatTab>> CAT = register("cat",
            TabType.create(CatTab::new, ModItems.CHLOROPHYTE_INGOT));

    private static <I extends AbstractInventoryTab> DeferredHolder<TabType<?>, TabType<I>> register(String name, TabType<I> tab) {
        return INVENTORY_TABS.register(name, () -> tab);
    }
}

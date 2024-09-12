package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.container.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AnvilinatorMenu>> ANVILINATOR = MENUS.register("anvilinator",
            ()-> IMenuTypeExtension.create(AnvilinatorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<BioFurnaceMenu>> BIOFURNACE = MENUS.register("biofurnace",
            ()-> IMenuTypeExtension.create(BioFurnaceMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<BookDuplicatorMenu>> BOOK_DUPLICATOR = MENUS.register("book_duplicator",
            ()-> IMenuTypeExtension.create(BookDuplicatorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<BatteryBoxMenu>> BATTERY_BOX = MENUS.register("battery_box",
            ()-> IMenuTypeExtension.create(BatteryBoxMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<FluidCombustionMenu>> FLUID_COMBUSTION_MENU = MENUS.register("fluid_combustion",
            ()-> IMenuTypeExtension.create(FluidCombustionMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<NousTankMenu>> NOUS_TANK = MENUS.register("nous_tank",
            ()-> IMenuTypeExtension.create(NousTankMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<KnapsackMenu>> KNAPSACK = MENUS.register("knapsack",
            ()-> IMenuTypeExtension.create(KnapsackMenu::fromNetwork));
    public static final DeferredHolder<MenuType<?>, MenuType<TankKnapsackMenu>> TANK_KNAPSACK = MENUS.register("tank_knapsack",
            ()-> IMenuTypeExtension.create(TankKnapsackMenu::fromNetwork));
}

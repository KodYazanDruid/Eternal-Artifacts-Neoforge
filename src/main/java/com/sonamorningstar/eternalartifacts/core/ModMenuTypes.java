package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.container.AnvilinatorMenu;
import com.sonamorningstar.eternalartifacts.container.BioFurnaceMenu;
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
}

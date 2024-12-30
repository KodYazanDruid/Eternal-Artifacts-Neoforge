package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.capabilities.handler.IHeatHandler;
import com.sonamorningstar.eternalartifacts.capabilities.handler.IItemCooldown;
import com.sonamorningstar.eternalartifacts.capabilities.handler.INutritionHandler;
import com.sonamorningstar.eternalartifacts.capabilities.handler.IPersistentCooldown;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public final class ModCapabilities {
    public static final class NutritionStorage {
        public static final BlockCapability<INutritionHandler, @Nullable Direction> BLOCK = BlockCapability.createSided(create("nutrition"), INutritionHandler.class);
        public static final EntityCapability<INutritionHandler, @Nullable Direction> ENTITY = EntityCapability.createSided(create("nutrition"), INutritionHandler.class);
        public static final ItemCapability<INutritionHandler, Void> ITEM = ItemCapability.createVoid(create("nutrition"), INutritionHandler.class);
    }

    public static final class Heat {
        public static final BlockCapability<IHeatHandler, @Nullable Direction> BLOCK = BlockCapability.createSided(create("heat"), IHeatHandler.class);
        public static final EntityCapability<IHeatHandler, @Nullable Direction> ENTITY = EntityCapability.createSided(create("heat"), IHeatHandler.class);
        public static final ItemCapability<IHeatHandler, Void> ITEM = ItemCapability.createVoid(create("heat"), IHeatHandler.class);
    }

    public static final class ItemCooldown {
        public static final ItemCapability<IItemCooldown, Void> ITEM = ItemCapability.createVoid(create("item_cooldown"), IItemCooldown.class);
    }

    public static final class PersistentPlayerCooldown {
        public static final EntityCapability<IPersistentCooldown, Void> ENTITY = EntityCapability.createVoid(create("persistent_player_cooldown"), IPersistentCooldown.class);
    }

    public static final class Item {
        public static final EntityCapability<CharmStorage, Void> ENTITY_CHARMS = EntityCapability.createVoid(create("charms"), CharmStorage.class);
    }

    private static ResourceLocation create(String path) {
        return new ResourceLocation(MODID, path);
    }
}

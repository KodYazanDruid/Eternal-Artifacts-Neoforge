package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.capabilities.handler.IHeatHandler;
import com.sonamorningstar.eternalartifacts.capabilities.handler.INutritionHandler;
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

    private static ResourceLocation create(String path) {
        return new ResourceLocation(MODID, path);
    }
}

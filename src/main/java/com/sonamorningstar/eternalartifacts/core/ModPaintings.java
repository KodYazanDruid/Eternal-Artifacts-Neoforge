package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModPaintings {
    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(Registries.PAINTING_VARIANT, MODID);

    public static final DeferredHolder<PaintingVariant, PaintingVariant> THE_LAST_SUPPER = PAINTINGS.register("the_last_supper", ()-> new PaintingVariant(128, 64));

}

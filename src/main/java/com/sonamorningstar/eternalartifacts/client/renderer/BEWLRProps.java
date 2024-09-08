package com.sonamorningstar.eternalartifacts.client.renderer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class BEWLRProps implements IClientItemExtensions {
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return ModItemStackBEWLR.INSTANCE.get();
    }
}

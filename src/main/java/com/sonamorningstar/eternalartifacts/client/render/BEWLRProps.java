package com.sonamorningstar.eternalartifacts.client.render;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class BEWLRProps implements IClientItemExtensions {
    public static BEWLRProps INSTANCE = new BEWLRProps();
    
    private BEWLRProps() {}
    
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return ModItemStackBEWLR.INSTANCE.get();
    }
}

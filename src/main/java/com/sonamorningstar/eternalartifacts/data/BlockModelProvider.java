package com.sonamorningstar.eternalartifacts.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockModelProvider extends net.neoforged.neoforge.client.model.generators.BlockModelProvider {
    public BlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}

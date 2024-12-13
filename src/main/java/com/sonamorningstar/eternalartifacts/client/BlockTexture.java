package com.sonamorningstar.eternalartifacts.client;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class BlockTexture implements INBTSerializable<CompoundTag> {

    public BlockTexture(IAttachmentHolder holder) {

    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}

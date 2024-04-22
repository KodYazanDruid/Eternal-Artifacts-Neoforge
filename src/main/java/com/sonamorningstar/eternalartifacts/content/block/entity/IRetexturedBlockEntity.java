package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IRetexturedBlockEntity {
    CompoundTag getPersistentData();

    default String getTextureName() {
        return RetexturedHelper.getTextureName(getPersistentData());
    }

    default Block getTexture() {
        return RetexturedHelper.getBlock(getTextureName());
    }

    default void updateTexture(String name) {
        String oldName = getTextureName();
        RetexturedHelper.setTexture(getPersistentData(), name);
        if (!oldName.equals(name)) RetexturedHelper.onTextureUpdated((BlockEntity)this);
    }


}

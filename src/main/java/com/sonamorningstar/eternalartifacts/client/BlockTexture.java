package com.sonamorningstar.eternalartifacts.client;

import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

@Getter
public class BlockTexture implements INBTSerializable<CompoundTag> {
    
    @Nullable
    private Block texture = null;
    private final BlockEntity owner;
    
    public BlockTexture(IAttachmentHolder holder) {
        this.owner = (BlockEntity) holder;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        RetexturedHelper.setTexture(tag, texture == null ? "minecraft:air" : RetexturedHelper.getTextureName(texture));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        String textureName = RetexturedHelper.getTextureName(nbt);
        Block newTexture = RetexturedHelper.getBlock(textureName);
        if (this.texture != newTexture) {
            this.texture = newTexture;
            RetexturedHelper.onTextureUpdated(owner);
        }
    }
}

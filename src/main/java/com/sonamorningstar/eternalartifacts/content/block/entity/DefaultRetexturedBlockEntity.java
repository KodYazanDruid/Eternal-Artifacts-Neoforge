package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

import static com.sonamorningstar.eternalartifacts.util.RetexturedHelper.TEXTURE_TAG_KEY;

public class DefaultRetexturedBlockEntity extends ModBlockEntity implements IRetexturedBlockEntity {
    @Nonnull
    @Getter
    private Block texture = Blocks.TERRACOTTA;

    public DefaultRetexturedBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return RetexturedHelper.getModelData(texture);
    }

    @Override
    public String getTextureName() {
        return RetexturedHelper.getTextureName(texture);
    }

    @Override
    public void updateTexture(String name) {
        Block oldTexture = texture;
        texture = RetexturedHelper.getBlock(name);
        if(oldTexture != texture) {
            sendUpdate();
            RetexturedHelper.onTextureUpdated(this);
        }
    }

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    @Override
    protected void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
        if(texture != Blocks.AIR) tag.putString(TEXTURE_TAG_KEY, getTextureName());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains(TEXTURE_TAG_KEY, Tag.TAG_STRING)) {
            texture = RetexturedHelper.getBlock(tag.getString(TEXTURE_TAG_KEY));
            RetexturedHelper.onTextureUpdated(this);
        }
    }
}

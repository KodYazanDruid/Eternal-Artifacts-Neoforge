package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import static com.sonamorningstar.eternalartifacts.util.RetexturedHelper.TEXTURE_TAG_KEY;

public class FancyChestBlockEntity extends ChestBlockEntity implements IRetexturedBlockEntity{
    @Nonnull
    @Getter
    private Block texture = Blocks.BAMBOO_PLANKS;
    public FancyChestBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FANCY_CHEST.get(), pPos, pBlockState);
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    protected void sendUpdate(){
        setChanged();
        if(level != null && level.hasChunkAt(worldPosition)) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }


    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if(texture != Blocks.AIR) pTag.putString(TEXTURE_TAG_KEY, getTextureName());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        texture = RetexturedHelper.getBlock(tag.getString(TEXTURE_TAG_KEY));
        RetexturedHelper.onTextureUpdated(this);
        /*if(tag.contains(TEXTURE_TAG_KEY, Tag.TAG_STRING)) {
            texture = RetexturedHelper.getBlock(tag.getString(TEXTURE_TAG_KEY));
            RetexturedHelper.onTextureUpdated(this);
        }*/
    }
}

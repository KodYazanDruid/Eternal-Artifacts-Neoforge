package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.IRetexturedBlockEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RetexturedHelper {
    public static final String TEXTURE_TAG_KEY = "Texture";
    public static final ModelProperty<Block> PROPERTY = new ModelProperty<>(block -> block != Blocks.AIR);

    public static Block getBlock(String name) {
        name = Normalizer.normalize(name, Normalizer.Form.NFD);
        name = name.replaceAll("[^\\p{ASCII}]", "");
        return name.isEmpty() ? Blocks.AIR : BuiltInRegistries.BLOCK.get(new ResourceLocation(name.toLowerCase(Locale.ROOT)));
    }

    public static String getTextureName(CompoundTag tag) {
        return tag == null ? "" : tag.getString(TEXTURE_TAG_KEY);
    }

    public static String getTextureName(Block block) {
        return block == Blocks.AIR ? "minecraft:air" : Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public static void setTexture(CompoundTag tag, String texture) {
        if(tag != null) {
            if(texture.isEmpty()) tag.remove(TEXTURE_TAG_KEY);
            else tag.putString(TEXTURE_TAG_KEY, texture);
        }
    }

    public static void onTextureUpdated(BlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        if(level != null && level.isClientSide()) {
            blockEntity.requestModelDataUpdate();
            BlockState state = blockEntity.getBlockState();
            level.sendBlockUpdated(pos, state, state, 0);
        }
        if (level != null) {
            AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
            if (lightManager != null && blockEntity instanceof IRetexturedBlockEntity retext)
                lightManager.setLightAt(pos, retext.getTexture().defaultBlockState().getLightEmission());
        }
    }

    public static ModelData.Builder getModelDataBuilder(Block block) {
        if(block == Blocks.AIR) block = null;
        return ModelData.builder().with(PROPERTY, block);
    }

    public static ModelData getModelData(Block block) {
        return getModelDataBuilder(block).build();
    }
}

package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.PlayerTeleportToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

@Getter
public class Warp {
    @Setter
    private String label;
    private final ResourceKey<Level> dimension;
    private final BlockPos position;

    private final SpriteButton teleportButton;
    private final SpriteButton deleteButton;
    public volatile BiConsumer<SpriteButton, Integer> deleteLogic = (a, b) -> {};

    public Warp(String label, ResourceKey<Level> dimension, BlockPos position) {
        this.label = label;
        this.dimension = dimension;
        this.position = position;

        this.teleportButton = SpriteButton.builder(Component.empty(), this::onTeleportPress).build();
        this.deleteButton = SpriteButton.builder(Component.empty(), this::delete)
                .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("delete_warp").withStyle(ChatFormatting.DARK_RED)).build();
    }

    private void onTeleportPress(SpriteButton button, int key){teleport();}

    private void delete(SpriteButton button, int key) {
        deleteLogic.accept(button, key);
    }

    public void teleport() {
        Channel.sendToServer(new PlayerTeleportToServer(dimension, position));
    }

    @Override
    public String toString() {
        return "Warp: "+label+", " +
                "Dimension: " + StringUtils.prettyName(dimension.location().getPath()) +
                ", X: "+position.getX()+", Y: "+position.getY()+", Z: "+position.getZ();
    }

    public void writeToNBT(CompoundTag tag) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Name", label);
        nbt.putString("Dimension", dimension.location().toString());
        nbt.putInt("X", position.getX());
        nbt.putInt("Y", position.getY());
        nbt.putInt("Z", position.getZ());
        tag.put("Warp", nbt);
    }

    public static Warp readFromNBT(CompoundTag tag) {
        CompoundTag nbt = tag.getCompound("Warp");
        String label = nbt.getString("Name");
        ResourceKey<Level> dimension = nbt.contains("Dimension") ? ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("Dimension"))) : Level.OVERWORLD;
        BlockPos position = new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
        return new Warp(label, dimension, position);
    }
}

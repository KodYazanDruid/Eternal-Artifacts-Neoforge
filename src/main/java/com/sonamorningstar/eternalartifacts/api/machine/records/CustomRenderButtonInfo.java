package com.sonamorningstar.eternalartifacts.api.machine.records;

import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public record CustomRenderButtonInfo(int x, int y, int width, int height, ResourceLocation tex, Runnable onPress) { }

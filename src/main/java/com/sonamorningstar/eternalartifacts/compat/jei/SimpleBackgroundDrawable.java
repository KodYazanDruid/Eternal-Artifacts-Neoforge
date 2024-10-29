package com.sonamorningstar.eternalartifacts.compat.jei;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.util.function.TriFunction;
import lombok.Getter;
import lombok.Setter;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SimpleBackgroundDrawable implements IDrawable {
    private final int width;
    private final int height;

    private final List<Pair<Integer, Integer>> itemSlots = new ArrayList<>();
    private final List<Pair<Integer, Integer>> smallFluidSlots = new ArrayList<>();
    private final List<Pair<Integer, Integer>> bigFluidSlots = new ArrayList<>();
    private final List<TriFunction<GuiGraphics, Integer, Integer, Void>> drawables = new ArrayList<>();
    @Setter
    private Pair<Integer, Integer> arrow;

    public SimpleBackgroundDrawable(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addItemSlot(int x, int y) {
        itemSlots.add(Pair.of(x, y));
    }
    public void addSmallFluidSlot(int x, int y) {
        smallFluidSlots.add(Pair.of(x, y));
    }
    public void addBigFluidSlot(int x, int y) {
        bigFluidSlots.add(Pair.of(x, y));
    }
    public void draw(TriFunction<GuiGraphics, Integer, Integer, Void> ctx) {
        drawables.add(ctx);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOff, int yOff) {
        GuiDrawer.drawBackground(guiGraphics, xOff, yOff, getWidth(), getHeight());
        for (Pair<Integer, Integer> itemSlot : itemSlots) {
            GuiDrawer.drawItemSlot(guiGraphics, itemSlot.getFirst() + xOff, itemSlot.getSecond() + yOff);
        }
        for (Pair<Integer, Integer> fluidSlot : smallFluidSlots) {
            GuiDrawer.drawEmptySmallTank(guiGraphics, fluidSlot.getFirst() + xOff, fluidSlot.getSecond() + yOff);
        }
        for (Pair<Integer, Integer> fluidSlot : bigFluidSlots) {
            GuiDrawer.drawEmptyTank(guiGraphics, fluidSlot.getFirst() + xOff, fluidSlot.getSecond() + yOff);
        }
        if(arrow != null) GuiDrawer.drawEmptyArrow(guiGraphics, arrow.getFirst() + xOff, arrow.getSecond() + yOff);
        for (TriFunction<GuiGraphics, Integer, Integer, Void> drawable : drawables) {
            drawable.apply(guiGraphics, xOff, yOff);
        }
    }
}

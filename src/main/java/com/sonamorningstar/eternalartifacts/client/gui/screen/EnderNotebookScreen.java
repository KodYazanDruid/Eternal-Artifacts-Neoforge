package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.widget.CustomRenderButton;
import com.sonamorningstar.eternalartifacts.client.gui.widget.Warp;
import com.sonamorningstar.eternalartifacts.client.gui.widget.WarpPageHandler;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.EnderNotebookAddNbtToServer;
import com.sonamorningstar.eternalartifacts.network.EnderNotebookRemoveNbtToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.collections.MutableListBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class EnderNotebookScreen extends Screen {
    public static final ResourceLocation background = new ResourceLocation(MODID, "textures/gui/ender_notebook.png");
    private final WarpPageHandler pageHandler;
    private final int maxWarpAmount;
    private final ItemStack notebook;
    int x;
    int y;
    int margin = 20;
    int textColor = 16777215;
    private EditBox name;
    public EnderNotebookScreen(ItemStack notebook) {
        super(Component.empty());
        this.notebook = notebook;
        this.pageHandler = new WarpPageHandler(readWarps(notebook), 8);
        this.maxWarpAmount = 8 + (notebook.getEnchantmentLevel(ModEnchantments.VOLUME.get()) * 4);
    }

    @Override
    protected void init() {
        x = (width - 192) / 2;
        y = (height - 256) / 2;
        addRenderableWidget(Button.builder(ModConstants.TRANSLATE_BUTTON_PREFIX.withSuffixTranslatable("addwarp"), this::addWarpPress)
                .bounds(x + 107, y + 196, 85, 20).build());
        name = new EditBox(minecraft.font, x + 5, y + 196, 100, 20, Component.empty());
        name.setMaxLength(20);
        name.setCanLoseFocus(true);
        addRenderableWidget(name);
        generateButtons(this::addWidget);
        generateTurningButtons(this::addWidget);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    //region Render stuff
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        x = (width - 192) / 2;
        y = (height - 256) / 2;
        guiGraphics.blit(background, x, y, 0, 0, 192, 192);
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, ModConstants.WARPS.translatable().append(String.valueOf(pageHandler.getWarpList().size())).append("/"+maxWarpAmount), x + 13, y + 171, textColor);
        renderWarpInfo(guiGraphics);
    }
    private void renderWarpInfo(GuiGraphics gui) {
        List<Warp> currentWarps = pageHandler.getCurrentWarps();
        for (int i = 0; i < currentWarps.size(); i++) {
            Warp warp = currentWarps.get(i);
            String path = warp.getDimension().location().getPath().replace('_', ' ');
            //Capitilazing first letter of each word.
            String[] pathWords = path.split("\\s");
            StringBuilder prettyPath = new StringBuilder();
            for(String word : pathWords) prettyPath.append(Character.toTitleCase(word.charAt(0))).append(word.substring(1)).append(" ");
            BlockPos pos = warp.getPosition();
            Component info = Component.literal(prettyPath.toString().trim()).append(" ").append(pos.getX() + " " + pos.getY() + " " + pos.getZ());
            gui.drawString(font, warp.getLabel(), x + 16, y + 12 + (margin * i), textColor);
            gui.drawString(font, info, x + 16, y + 20 + (margin * i), textColor);
        }
    }
    //endregion

    //region Button press handlers
    private void addWarpPress(Button button) {
        if(minecraft != null && minecraft.player != null && pageHandler.getWarpList().size() < maxWarpAmount) {
            addWarp(new Warp(name.getValue(), minecraft.player.level().dimension(), minecraft.player.blockPosition()));
        }
    }
    private void turnRight(CustomRenderButton button, int key) {
        pageHandler.turnPageRight();
        rebuildWidgets();
    }
    private void turnLeft(CustomRenderButton button, int key) {
        pageHandler.turnPageLeft();
        rebuildWidgets();
    }
    //endregion

    //region Dynamic button generation.
    private void generateButtons(Function<CustomRenderButton, ?> func) {
        List<Warp> currentWarps = pageHandler.getCurrentWarps();
        for (int i = 0; i < currentWarps.size(); i++) {
            Warp warp = currentWarps.get(i);
            CustomRenderButton teleport = warp.getTeleportButton();
            CustomRenderButton delete = warp.getDeleteButton();
            teleport.setSize(147, 18);
            teleport.setPosition(x + 13, y + 11 + (margin * i));
            teleport.setAlpha(0.5F);
            teleport.setTextures(new ResourceLocation(MODID, "textures/gui/sprites/blank_ender.png"));
            delete.setSize(18, 18);
            delete.setPosition(x + 160, y + 11 + (margin * i));
            delete.setTextures(new ResourceLocation(MODID, "textures/gui/sprites/blank_red.png"), new ResourceLocation(MODID, "textures/gui/sprites/trash_can.png"));
            warp.deleteLogic = (a, b) -> removeWarp(warp);
            func.apply(teleport);
            func.apply(delete);
            renderables.add(teleport);
            renderables.add(delete);
        }
    }
    private void generateTurningButtons(Function<CustomRenderButton, ?> fun) {
        CustomRenderButton rightArrow = CustomRenderButton.builder(Component.empty(), this::turnRight, new ResourceLocation(MODID, "textures/gui/sprites/right_arrow.png"))
                .size(16, 9).pos(x + 160, y + 175).build();
        CustomRenderButton leftArrow = CustomRenderButton.builder(Component.empty(), this::turnLeft, new ResourceLocation(MODID, "textures/gui/sprites/left_arrow.png")
        ).size(16, 9).pos(x + 140, y + 175).build();
        rightArrow.visible = pageHandler.getPageSize() > pageHandler.getCurrentPage();
        leftArrow.visible = pageHandler.getCurrentPage() > 0;
        fun.apply(rightArrow);
        fun.apply(leftArrow);
        renderables.add(rightArrow);
        renderables.add(leftArrow);
    }
    //endregion

    //region Add-Remove warp
    private void addWarp(Warp warp) {
        Channel.sendToServer(new EnderNotebookAddNbtToServer(warp.getLabel(), warp.getDimension(), warp.getPosition(), notebook));
        pageHandler.addWarp(warp);
        rebuildWidgets();
    }
    private void removeWarp(Warp warp) {
        int index = pageHandler.getWarpList().indexOf(warp);
        Channel.sendToServer(new EnderNotebookRemoveNbtToServer(index, notebook));
        pageHandler.deleteWarp(warp);
        rebuildWidgets();
    }
    //endregion

    //region Reading warps from the tag.
    private static List<Warp> readWarps(ItemStack notebook) {
        CompoundTag tag = notebook.getTag();
        return tag != null ? loadWarps(tag) : new ArrayList<>();
    }
    private static List<Warp> loadWarps(CompoundTag tag) {
        MutableListBuilder<Warp> builder = new MutableListBuilder<>();
        loadWarps(tag, builder::add);
        return builder.build();
    }
    private static void loadWarps(CompoundTag tag, Consumer<Warp> consumer) {
        ListTag listTag = tag.getList("Warps", 10).copy();
        for (Tag value : listTag) {
            CompoundTag singleWarp = ((CompoundTag) value);
            String warpName = singleWarp.getString("Name");
            ResourceKey<Level> level = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(singleWarp.getString("Dimension")));
            BlockPos position = NbtUtils.readBlockPos(singleWarp);
            consumer.accept(new Warp(warpName, level, position));
        }
    }
    //endregion
}
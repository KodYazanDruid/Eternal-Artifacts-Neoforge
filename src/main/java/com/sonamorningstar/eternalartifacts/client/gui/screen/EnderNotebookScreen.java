package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.ItemStackScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.CleanButton;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.client.gui.widget.Warp;
import com.sonamorningstar.eternalartifacts.client.gui.widget.WarpPageHandler;
import com.sonamorningstar.eternalartifacts.content.item.EnderNotebookItem;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookAddNbtToServer;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookRemoveNbtToServer;
import com.sonamorningstar.eternalartifacts.network.endernotebook.EnderNotebookRenameWarpToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import com.sonamorningstar.eternalartifacts.util.collections.MutableListBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
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

public class EnderNotebookScreen extends ItemStackScreen {
    public static final ResourceLocation background = new ResourceLocation(MODID, "textures/gui/ender_notebook.png");
    public static final ResourceLocation buttons = new ResourceLocation(MODID, "textures/gui/buttons.png");
    private final List<AbstractWidget> warpWidgets = new ArrayList<>();
    private final WarpPageHandler pageHandler;
    private final int maxWarpAmount;
    int margin = 20;
    int textColor = 16777215;
    private EditBox name;
    private EditBox searchBox;
    private AbstractButton addWarpButton;
    private AbstractButton renameWarpButton;
    private boolean isRenaming = false;
    private int renamingWarpIndex;
    private int currentPage = 0;
    public EnderNotebookScreen(ItemStack notebook) {
        super(notebook);
        this.pageHandler = new WarpPageHandler(readWarps(notebook), 8);
        this.maxWarpAmount = EnderNotebookItem.calculateMaxWarpAmount(notebook, 8, 4);
    }

    @Override
    protected void init() {
        imageWidth = 192;
        imageHeight = 256;
        super.init();
        
        addWarpButton = CleanButton.builder(ModConstants.TRANSLATE_BUTTON_PREFIX.withSuffixTranslatable("add_warp"), this::addWarpPress)
                .bounds(leftPos + 107, topPos + 196, 85, 20).build();
        
        name = new EditBox(font, leftPos + 5, topPos + 196, 100, 20, Component.empty());
        name.setMaxLength(20);
        name.setCanLoseFocus(true);
        
        searchBox = new EditBox(font, leftPos + 5, topPos - 13, 185, 20, Component.empty());
        searchBox.setSuggestion(ModConstants.GUI.withSuffixTranslatable("search_warps").getString(175));
        searchBox.setResponder(value -> {
            if (!value.isBlank()) searchBox.setSuggestion("");
            else searchBox.setSuggestion(ModConstants.GUI.withSuffixTranslatable("search_warps").getString(175));
            currentPage = 0;
            resetRenamingState();
            refreshWarps();
        });
        searchBox.setMaxLength(20);
        searchBox.setCanLoseFocus(true);
        
        addRenderableWidget(name);
        addRenderableWidget(searchBox);
        addRenderableWidget(addWarpButton);
        
        warpWidgets.clear();
        generateButtons(warpWidgets::add);
        generateTurningButtons(warpWidgets::add);
        warpWidgets.forEach(this::addRenderableWidget);
    }
    
    private void refreshWarps() {
        warpWidgets.forEach(this::removeWidget);
        warpWidgets.clear();
        generateButtons(warpWidgets::add);
        generateTurningButtons(warpWidgets::add);
        warpWidgets.forEach(this::addRenderableWidget);
    }

    //region Render stuff
    @Override
    public void renderLabel(GuiGraphics gui) {}
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        leftPos = (width - 192) / 2;
        topPos = (height - 256) / 2;
        guiGraphics.blit(background, leftPos, topPos, 0, 0, 192, 192);
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, ModConstants.WARPS.translatable().append(String.valueOf(pageHandler.getWarpList().size())).append("/"+maxWarpAmount), leftPos + 13, topPos + 171, textColor);
        if (isRenaming) guiGraphics.drawString(font, ModConstants.GUI.withSuffixTranslatable("renaming").append(" #"+(renamingWarpIndex+1)), leftPos + 5, topPos + 220, textColor);

        renderWarpInfo(guiGraphics);
    }
    private void renderWarpInfo(GuiGraphics gui) {
        List<Warp> currentWarps;
        
        if (searchBox != null && !searchBox.getValue().isBlank()) currentWarps = pageHandler.getWarps(searchBox.getValue(), currentPage);
        else currentWarps = pageHandler.getWarps(currentPage);
        
        for (int i = 0; i < currentWarps.size(); i++) {
            Warp warp = currentWarps.get(i);
            String prettyName = StringUtils.prettyName(warp.getDimension().location().getPath());
            BlockPos pos = warp.getPosition();
            Component info = Component.literal(prettyName).append(" ").append(pos.getX() + " " + pos.getY() + " " + pos.getZ());
            gui.drawString(font, warp.getLabel(), leftPos + 16, topPos + 12 + (margin * i), textColor);
            gui.drawString(font, info, leftPos + 16, topPos + 20 + (margin * i), textColor);
        }
    }
    //endregion
    //region Button press handlers
    private void addWarpPress(AbstractButton button) {
        if(minecraft != null && minecraft.player != null && pageHandler.getWarpList().size() < maxWarpAmount) {
            addWarp(new Warp(name.getValue(), minecraft.player.level().dimension(), minecraft.player.blockPosition()));
        }
    }
    //Button on the warp. Created for each individual warp.
    private void rename(SpriteButton button, int key, int index) {
        if(!isRenaming){
            addWarpButton.visible = false;
            renameWarpButton.visible = true;
            isRenaming = true;
            renamingWarpIndex = index;
            Warp warp = pageHandler.getWarp(index);
            name.setValue(warp.getLabel());
            name.setFocused(true);
        } else resetRenamingState();
    }
    private void renameWarpButton(AbstractButton button) {
        Warp warp = pageHandler.getWarp(renamingWarpIndex);
        warp.setLabel(name.getValue());
        Channel.sendToServer(new EnderNotebookRenameWarpToServer(renamingWarpIndex, stack, name.getValue()));
        resetRenamingState();
    }
    private void resetRenamingState() {
        name.setFocused(false);
        name.setValue("");
        isRenaming = false;
        addWarpButton.visible = true;
        renameWarpButton.visible = false;
    }
    private void turnRight(SpriteButton button, int key) {
        currentPage = Math.min(currentPage + 1, searchBox.getValue().isBlank() ?
            pageHandler.getPageSize() :
            pageHandler.getPageSizeFiltered(searchBox.getValue()));
        refreshWarps();
    }
    private void turnLeft(SpriteButton button, int key) {
        currentPage = Math.max(currentPage - 1, 0);
        refreshWarps();
    }
    //endregion
    //region Dynamic button generation.
    private void generateButtons(Function<AbstractButton, ?> func) {
        List<Warp> currentWarps;
        
        if (searchBox != null && !searchBox.getValue().isBlank()) currentWarps = pageHandler.getWarps(searchBox.getValue(), currentPage);
        else currentWarps = pageHandler.getWarps(currentPage);
        
        for (int i = 0; i < currentWarps.size(); i++) {
            Warp warp = currentWarps.get(i);
            SpriteButton teleport = warp.getTeleportButton();
            SpriteButton rename = SpriteButton.builder(Component.empty(), (button, key) -> rename(button, key, pageHandler.getWarpList().indexOf(warp)))
                    .bounds(leftPos + 142, topPos + 11 + (margin * i), 18, 18)
                    .addSprite(buttons, 0, 16, 18, 18)
                    .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("rename_warp")).build();
            SpriteButton delete = warp.getDeleteButton();

            teleport.setSize(129, 18);
            teleport.setPosition(leftPos + 13, topPos + 11 + (margin * i));
            teleport.setAlpha(0.5F);
            teleport.setTextures(new ResourceLocation(MODID, "textures/gui/sprites/blank_ender.png"));
            delete.setSize(18, 18);
            delete.setPosition(leftPos + 160, topPos + 11 + (margin * i));
            delete.setTextures(new ResourceLocation(MODID, "textures/gui/sprites/blank_red.png"), new ResourceLocation(MODID, "textures/gui/sprites/trash_can.png"));
            warp.deleteLogic = (a, b) -> removeWarp(warp);
            func.apply(teleport);
            func.apply(rename);
            func.apply(delete);
        }
        renameWarpButton = CleanButton.builder(ModConstants.TRANSLATE_BUTTON_PREFIX.withSuffixTranslatable("rename_warp"), this::renameWarpButton)
                .bounds(leftPos + 107, topPos + 196, 85, 20).build();
        renameWarpButton.visible = false;
        func.apply(renameWarpButton);
    }
    private void generateTurningButtons(Function<SpriteButton, ?> fun) {
        SpriteButton rightArrow = SpriteButton.builder(Component.empty(), this::turnRight, new ResourceLocation(MODID, "textures/gui/sprites/right_arrow.png"))
                .size(16, 9).pos(leftPos + 160, topPos + 175).build();
        SpriteButton leftArrow = SpriteButton.builder(Component.empty(), this::turnLeft, new ResourceLocation(MODID, "textures/gui/sprites/left_arrow.png")
        ).size(16, 9).pos(leftPos + 140, topPos + 175).build();
        if (searchBox != null) {
            if(searchBox.getValue().isBlank()) rightArrow.visible = pageHandler.getPageSizeFloat() > currentPage + 1.0F;
            else rightArrow.visible = pageHandler.getPageSizeFilteredFloat(searchBox.getValue()) > currentPage + 1.0F;
        }
        leftArrow.visible = currentPage > 0;
        fun.apply(rightArrow);
        fun.apply(leftArrow);
    }
    //endregion
    //region Add-Remove warp
    private void addWarp(Warp warp) {
        Channel.sendToServer(new EnderNotebookAddNbtToServer(warp.getLabel(), warp.getDimension(), warp.getPosition(), stack));
        pageHandler.addWarp(warp);
        refreshWarps();
    }
    private void removeWarp(Warp warp) {
        int index = pageHandler.getWarpList().indexOf(warp);
        Channel.sendToServer(new EnderNotebookRemoveNbtToServer(index, stack));
        pageHandler.deleteWarp(warp);
        refreshWarps();
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
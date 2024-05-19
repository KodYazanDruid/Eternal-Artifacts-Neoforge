package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.client.gui.widget.CustomRenderButton;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.EnderNotebookAddNbtToServer;
import com.sonamorningstar.eternalartifacts.network.EnderNotebookRemoveNbtToServer;
import com.sonamorningstar.eternalartifacts.network.EnderNotebookTeleportToServer;
import lombok.Getter;
import net.minecraft.client.GameNarrator;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class EnderNotebookScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/ender_notebook.png");
    private Button addWarp;
    private EditBox name;
    private final List<Pair<Pair<String, ResourceKey<Level>>, BlockPos>> warps = Lists.newArrayList();
    private final List<Pair<Pair<CustomRenderButton, CustomRenderButton>, Pair<Pair<String, ResourceKey<Level>>, BlockPos>>> keyList = Lists.newArrayList();
    int x;
    int y;
    int margin = 20;
    int textColor = 16777215;
    public final EnderNotebookScreen.EnderNotebookAccess bookAccess;
    public EnderNotebookScreen(EnderNotebookScreen.EnderNotebookAccess bookAccess) {
        super(GameNarrator.NO_TITLE);
        this.bookAccess = bookAccess;
        CompoundTag compoundtag = bookAccess.getNotebook().getTag();
        if (compoundtag != null) {
            loadWarps(compoundtag, this.warps::add);
        }
    }

    @Override
    protected void init() {
        x = (width - 192) / 2;
        y = (height - 256) / 2;
        addWarp = addRenderableWidget(Button.builder(Component.translatable("button.addwarp"), this::onAddWarpPress).bounds(x + 7, y + 196, 98, 20).build());
        name = new EditBox(minecraft.font, x + 107, y + 196, 82, 20, Component.empty());
        name.setMaxLength(20);
        name.setCanLoseFocus(true);
        addRenderableWidget(this.name);
        for(int i = 0; i < warps.size(); i++) {
            generateButtonsInit(i, keyList, this::addWidget);
        }
    }

    private void onAddWarpPress(Button button) {
        if(minecraft != null && minecraft.player != null && warps.size() < 8) {
            addWarpSynced(Pair.of(Pair.of(name.getValue(), minecraft.player.level().dimension()), minecraft.player.blockPosition()));
        }
    }

    private void onRemoveWarpPress(Button button, int index) {
        warps.remove(index);
        keyList.remove(index);
        Channel.sendToServer(new EnderNotebookRemoveNbtToServer(index, bookAccess.getNotebook()));
        rebuildWidgets();
    }

    private void onTeleportWarpPress(Button button, int index) {
        Pair<Pair<String, ResourceKey<Level>>, BlockPos> warp = warps.get(index);
        Channel.sendToServer(new EnderNotebookTeleportToServer(warp.getFirst().getSecond(), warp.getSecond()));
    }

    private void generateButtonsInit(int i, List<Pair<Pair<CustomRenderButton, CustomRenderButton>, Pair<Pair<String, ResourceKey<Level>>, BlockPos>>> keyList, Function<Button, ?> func) {
        Pair<CustomRenderButton, CustomRenderButton> buttonPair = generateButtonPair(i);
        keyList.add(i, Pair.of(buttonPair, Pair.of(Pair.of(warps.get(i).getFirst().getFirst(), warps.get(i).getFirst().getSecond()), warps.get(i).getSecond())));
        func.apply(buttonPair.getFirst());
        func.apply(buttonPair.getSecond());
        renderables.add(buttonPair.getFirst());
        renderables.add(buttonPair.getSecond());
    }

    private void addWarpSynced(Pair<Pair<String, ResourceKey<Level>>, BlockPos> warp) {
        Channel.sendToServer(new EnderNotebookAddNbtToServer(warp.getFirst(), warp.getSecond(), bookAccess.getNotebook()));
        warps.add(Pair.of(warp.getFirst(), warp.getSecond()));
        int i = warps.size();
        keyList.add(Pair.of(generateButtonPair(i), warp));
        renderables.add(keyList.get(i - 1).getFirst().getFirst());
        renderables.add(keyList.get(i - 1).getFirst().getSecond());
        rebuildWidgets();
    }

    private Pair<CustomRenderButton, CustomRenderButton> generateButtonPair(int i) {
        CustomRenderButton delete = CustomRenderButton.builder(Component.empty(), button -> onRemoveWarpPress(button, i),
                        new ResourceLocation(MODID, "textures/gui/sprites/blank_red.png"), new ResourceLocation(MODID, "textures/gui/sprites/trash_can.png"))
                .bounds(x + 160, y + 11 + (margin * i), 18, 18).build();
        CustomRenderButton teleport = CustomRenderButton.builder(Component.empty(), button -> onTeleportWarpPress(button, i),
                        new ResourceLocation(MODID, "textures/gui/sprites/blank_ender.png"))
                .bounds(x + 13, y + 11 + (margin * i), 147, 18).build();
        teleport.setAlpha(0.5f);
        return Pair.of(delete, teleport);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        x = (width - 192) / 2;
        y = (height - 256) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, 192, 192);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderWarps(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, Component.literal("Warps:").append(String.valueOf(warps.size())).append("/8"), x + 130, y + 171, textColor);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void renderWarps(GuiGraphics gui, int mouseX, int mouseY, float tick) {
        for(int i = 0; i < warps.size(); i ++) {
            Pair<Pair<String, ResourceKey<Level>>, BlockPos> warp = warps.get(i);
            BlockPos pos = warp.getSecond();

            String path = warp.getFirst().getSecond().location().getPath().replace('_', ' ');
            //Capitilazing first letter of each word.
            String[] pathWords = path.split("\\s");
            StringBuilder prettyPath = new StringBuilder();
            for(String word : pathWords) prettyPath.append(Character.toTitleCase(word.charAt(0))).append(word.substring(1)).append(" ");

            Component info = Component.literal(prettyPath.toString().trim()).append(" ").append(pos.getX() + " " + pos.getY() + " " + pos.getZ());

            gui.drawString(font, warp.getFirst().getFirst(), x + 16, y + 12 + (margin * i), textColor);
            gui.drawString(font, info, x + 16, y + 20 + (margin * i), textColor);

            Pair<CustomRenderButton, CustomRenderButton> buttons = keyList.get(i).getFirst();

            /*buttons.getFirst().render(gui, mouseX, mouseY, tick);
            buttons.getSecond().render(gui, mouseX, mouseY, tick);*/
        }
    }

    static List<Pair<Pair<String, ResourceKey<Level>>, BlockPos>> loadWarps(CompoundTag tag) {
        ImmutableList.Builder<Pair<Pair<String, ResourceKey<Level>>, BlockPos>> builder = ImmutableList.builder();
        loadWarps(tag, builder::add);
        return builder.build();
    }

    public static void loadWarps(CompoundTag tag, Consumer<Pair<Pair<String, ResourceKey<Level>>, BlockPos>> consumer) {
        ListTag listTag = tag.getList("Warps", 10).copy();
        for (Tag value : listTag) {
            CompoundTag singleWarp = ((CompoundTag) value);
            String warpName = singleWarp.getString("Name");
            ResourceKey<Level> level = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(singleWarp.getString("Dimension")));
            BlockPos position = NbtUtils.readBlockPos(singleWarp);
            consumer.accept(Pair.of(Pair.of(warpName, level), position));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Deprecated
    public static class EnderNotebookAccess {
        @Getter
        private final List<Pair<Pair<String, ResourceKey<Level>>, BlockPos>> warps;
        @Getter
        private final ItemStack notebook;

        public EnderNotebookAccess(ItemStack notebook) {
            this.notebook = notebook;
            this.warps = readWarps(notebook);
        }

        private static List<Pair<Pair<String, ResourceKey<Level>>, BlockPos>> readWarps(ItemStack notebook) {
            CompoundTag tag = notebook.getTag();
            return tag != null ? EnderNotebookScreen.loadWarps(tag) :ImmutableList.of();
        }

    }

}

package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.network.*;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Getter
public abstract class AbstractMachineScreen<T extends AbstractMachineMenu> extends AbstractModContainerScreen<T> {
    protected static final ResourceLocation bars = new ResourceLocation(MODID, "textures/gui/bars.png");
    protected static final ResourceLocation buttons = new ResourceLocation(MODID, "textures/gui/buttons.png");
    private final Map<String, Integer> energyLoc = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> fluidLocs = new HashMap<>();
    protected static final int labelColor = 4210752;
    
    private int cachedMx, cachedMy;
    private boolean shouldRenderEnergyTooltip = false;
    private boolean shouldRenderFluidTooltip = false;
    private boolean shouldRenderProgressTooltip = false;
    protected boolean renderEPT = true;
    private int progressTooltipX, progressTooltipY, progressTooltipXLen, progressTooltipYLen, progressTooltipFirst, progressTooltipSecond;
    private String progressTooltipKey;
    
    @Nullable
    protected SimpleDraggablePanel tagList;
    @Nullable
    protected SimpleDraggablePanel itemFilterPanel;
    @Nullable
    protected SimpleDraggablePanel fluidFilterPanel;
    @Nullable
    protected SimpleDraggablePanel blockFilterPanel;
    @Nullable
    protected SimpleDraggablePanel propertyPanel;
    @Nullable
    protected FilterSlotWidget convertingSlot;

    public AbstractMachineScreen(T menu, Inventory pPlayerInventory, Component pTitle) {
        super(menu, pPlayerInventory, pTitle);
        if (getMenu().getBeTank() != null) inventoryLabelX = 46;
        else inventoryLabelX = 28;
    }

    @Override
    protected void init() {
        super.init();
        BlockEntity blockEntity = menu.getBlockEntity();
        if (blockEntity instanceof Filterable filterable) {
            if (filterable instanceof AbstractMultiblockBlockEntity part && !part.isMaster()) return;
            
            int btnX = leftPos + imageWidth - (enchantmentPanel != null ? 54 : 36);
            int btnY = topPos + 4;

            buildFilterPanel(0);
            buildFilterPanel(1);
            buildFilterPanel(2);
            
            if (filterable.hasBlockFilters()) {
                SpriteButton button = SpriteButton.builder(Component.empty(), (b, i) -> blockFilterPanel.toggle(),
                    new ResourceLocation(MODID, "textures/item/machine_item_filter.png"))
                .bounds(btnX, btnY, 16, 16)
                .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("block_filter").withColor(0x039eff)).build();
                addRenderableWidget(button);
                btnX -= 18;
            }

            if (filterable.hasFluidFilters()) {
                SpriteButton button = SpriteButton.builder(Component.empty(), (b, i) -> fluidFilterPanel.toggle(),
                    new ResourceLocation(MODID, "textures/item/machine_fluid_filter.png"))
                .bounds(btnX, btnY, 16, 16)
                .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("fluid_filter").withColor(0x039eff)).build();
                addRenderableWidget(button);
                btnX -= 18;
            }
            
            if (filterable.hasItemFilters()) {
                SpriteButton button = SpriteButton.builder(Component.empty(), (b, i) -> itemFilterPanel.toggle(),
                    new ResourceLocation(MODID, "textures/item/machine_item_filter.png"))
                .bounds(btnX, btnY, 16, 16)
                .addTooltipHover(ModConstants.GUI.withSuffixTranslatable("item_filter").withColor(0x039eff)).build();
                addRenderableWidget(button);
            }
        }
    }
    
    @Override
    protected void setupConfigWidgets() {
        BlockEntity be = menu.getBlockEntity();
        if (!(be instanceof AbstractMultiblockBlockEntity part) || part.isMaster()) {
            super.setupConfigWidgets();
        }
    }
    
    protected void buildFilterPanel(int type) {
        SimpleDraggablePanel targetPanel = null;
        if (type == 0) {
            itemFilterPanel = new SimpleDraggablePanel(ModConstants.GUI.withSuffixTranslatable("item_filter"), leftPos + 23, topPos + 8, 129, 75, SimpleDraggablePanel.Bounds.of(0, 0, width, height));
            targetPanel = itemFilterPanel;
            addUpperLayerChild(itemFilterPanel);
        } else if (type == 1) {
            fluidFilterPanel = new SimpleDraggablePanel(ModConstants.GUI.withSuffixTranslatable("fluid_filter"), leftPos + 23, topPos + 8, 129, 75, SimpleDraggablePanel.Bounds.of(0, 0, width, height));
            targetPanel = fluidFilterPanel;
            addUpperLayerChild(fluidFilterPanel);
        } else if (type == 2) {
            blockFilterPanel = new SimpleDraggablePanel(ModConstants.GUI.withSuffixTranslatable("block_filter"), leftPos + 23, topPos + 8, 129, 75, SimpleDraggablePanel.Bounds.of(0, 0, width, height));
            targetPanel = blockFilterPanel;
            addUpperLayerChild(blockFilterPanel);
        }
        if (targetPanel == null) return;
        
        targetPanel.active = false;
        targetPanel.visible = false;
        targetPanel.addClosingButton();
        
        for (int i = 0; i < AbstractMachineMenu.FILTER_SIZE; i++) {
            FilterSlotWidget slotWidget = getFilterSlotWidget(i, type);
            if (slotWidget != null) {
                int col = i % 3;
                int row = i / 3;
                targetPanel.addChildren((fx, fy, fW, fH) -> {
                    slotWidget.setPosition(fx + 10 + col * 18, fy + 15 + row * 18);
                    return slotWidget;
                });
            }
        }
        
        targetPanel.addChildren((fx, fy, fW, fH) -> {
            var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
                if (type == 0) menu.setItemWhitelist(!menu.isItemWhitelist());
                else if (type == 1) menu.setFluidWhitelist(!menu.isFluidWhitelist());
                else if (type == 2) menu.setBlockWhitelist(!menu.isBlockWhitelist());
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, type == 0 ? 0 : type == 1 ? 2 : 4);
                }
                setListIcon(type, b);
            }).bounds(fx + 68, fy + 10, 16, 16);
            bld.addTooltipHover(() -> (type == 0 ? menu.isItemWhitelist() : type == 1 ? menu.isFluidWhitelist() : menu.isBlockWhitelist()) ?
                ModConstants.GUI.withSuffixTranslatable("whitelist").withStyle(style -> style.withColor(0x55FF55)) :
                ModConstants.GUI.withSuffixTranslatable("blacklist").withStyle(style -> style.withColor(0xFF5555)));
            bld.addTooltipHover(() -> (type == 0 ? menu.isItemWhitelist() : type == 1 ? menu.isFluidWhitelist() : menu.isBlockWhitelist()) ?
                ModConstants.GUI.withSuffixTranslatable("pipe_filter_blacklist_swap").withStyle(style -> style.withColor(0xAAAAAA)) :
                ModConstants.GUI.withSuffixTranslatable("pipe_filter_whitelist_swap").withStyle(style -> style.withColor(0xAAAAAA)));
            var buton = bld.build();
            setListIcon(type, buton);
            return buton;
        });

        targetPanel.addChildren((fx, fy, fW, fH) -> {
            var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
                if (type == 0) menu.setItemIgnoresNbt(!menu.isItemIgnoresNbt());
                else if (type == 1) menu.setFluidIgnoresNbt(!menu.isFluidIgnoresNbt());
                else if (type == 2) menu.setBlockIgnoresProps(!menu.isBlockIgnoresProps());
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, type == 0 ? 1 : type == 1 ? 3 : 5);
                }
                setNbtIcon(type, b);
            }).bounds(fx + 68, fy + 28, 16, 16);
            bld.addTooltipHover(() -> (type == 0 ? menu.isItemIgnoresNbt() : type == 1 ? menu.isFluidIgnoresNbt() : menu.isBlockIgnoresProps()) ?
                (type == 2 ?
                    ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_properties").withStyle(style -> style.withColor(0x55FF55)) :
                    ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_nbt").withStyle(style -> style.withColor(0x55FF55))) :
                (type == 2 ?
                    ModConstants.GUI.withSuffixTranslatable("pipe_filter_properties_tolerant").withStyle(style -> style.withColor(0xFF5555)) :
                    ModConstants.GUI.withSuffixTranslatable("pipe_filter_nbt_tolerant").withStyle(style -> style.withColor(0xFF5555))));
            bld.addTooltipHover(() -> (type == 0 ? menu.isItemIgnoresNbt() : type == 1 ? menu.isFluidIgnoresNbt() : menu.isBlockIgnoresProps()) ?
                (type == 2 ?
                    ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_properties_swap").withStyle(style -> style.withColor(0xAAAAAA)) :
                    ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_nbt_swap").withStyle(style -> style.withColor(0xAAAAAA))) :
                (type == 2 ?
                    ModConstants.GUI.withSuffixTranslatable("pipe_filter_properties_tolerant_swap").withStyle(style -> style.withColor(0xAAAAAA)) :
                    ModConstants.GUI.withSuffixTranslatable("pipe_filter_nbt_tolerant_swap").withStyle(style -> style.withColor(0xAAAAAA))));
            var buton = bld.build();
            setNbtIcon(type, buton);
            return buton;
        });
        
        if (type == 2) {
            targetPanel.addChildren((fx, fy, fW, fH) -> {
                var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> openPropertySelectionForFilters())
                    .bounds(fx + 68, fy + 46, 16, 16);
                bld.addTooltipHover(ModConstants.GUI.withSuffixTranslatable("block_properties").withStyle(style -> style.withColor(0x55AAFF)));
                bld.addTooltipHover(ModConstants.GUI.withSuffixTranslatable("block_properties_desc").withStyle(style -> style.withColor(0xAAAAAA)));
                SpriteButton buton = bld.build();
                buton.setTextures(new ResourceLocation(MODID, "textures/item/wrench.png"));
                return buton;
            });
            targetPanel.addChildren((fx, fy, fW, fH) -> {
                var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
                    addTargetBlockToFilter();
                }).bounds(fx + 86, fy + 46, 16, 16);
                bld.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("add_target_block").withStyle(style -> style.withColor(0x55FF55)));
                bld.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("add_target_block_desc").withStyle(style -> style.withColor(0xAAAAAA)));
                var buton = bld.build();
                buton.setTextures(new ResourceLocation("textures/item/compass_00.png"));
                return buton;
            });
        }
    }
    
    private void setNbtIcon(int type, SpriteButton b) {
        if (type == 0) {
            b.setTextures(menu.isItemIgnoresNbt() ? new ResourceLocation(MODID, "textures/item/green_apple.png") : new ResourceLocation("textures/item/apple.png"));
        } else if (type == 1) {
            b.setTextures(menu.isFluidIgnoresNbt() ? new ResourceLocation(MODID, "textures/item/green_apple.png") : new ResourceLocation("textures/item/apple.png"));
        } else if (type == 2) {
            b.setTextures(menu.isBlockIgnoresProps() ? new ResourceLocation(MODID, "textures/item/green_apple.png") : new ResourceLocation("textures/item/apple.png"));
        }
    }
    
    private void setListIcon(int type, SpriteButton b) {
        if (type == 0) {
            b.setTextures(menu.isItemWhitelist() ? new ResourceLocation("textures/item/paper.png") : new ResourceLocation(MODID, "textures/item/carbon_paper.png"));
        } else if (type == 1) {
            b.setTextures(menu.isFluidWhitelist() ? new ResourceLocation("textures/item/paper.png") : new ResourceLocation(MODID, "textures/item/carbon_paper.png"));
        } else if (type == 2) {
            b.setTextures(menu.isBlockWhitelist() ? new ResourceLocation("textures/item/paper.png") : new ResourceLocation(MODID, "textures/item/carbon_paper.png"));
        }
    }
    
    private @Nullable FilterSlotWidget getFilterSlotWidget(int i, int type) {
        final int slotIndex = i;
        FilterSlotWidget slotWidget = null;
        if (type == 0 && i < menu.getItemFilterFakeSlots().size()) {
            slotWidget = new FilterSlotWidget(menu.getItemFilterFakeSlots().get(i), menu.getItemFilterEntries().get(slotIndex));
            slotWidget.setOnFilterChanged(entry -> menu.getItemFilterEntries().set(slotIndex, (ItemFilterEntry) entry));
            slotWidget.setOnRightClick(this::openTagPanel);
        } else if (type == 1 && i < menu.getFluidFilterFakeSlots().size()) {
            slotWidget = new FilterSlotWidget(menu.getFluidFilterFakeSlots().get(i), menu.getFluidFilterEntries().get(slotIndex));
            slotWidget.setOnFilterChanged(entry -> menu.getFluidFilterEntries().set(slotIndex, (FluidFilterEntry) entry));
            slotWidget.setOnRightClick(this::openTagPanel);
        } else if (type == 2 && i < menu.getBlockFilterFakeSlots().size()) {
            slotWidget = new FilterSlotWidget(menu.getBlockFilterFakeSlots().get(i), menu.getBlockFilterEntries().get(slotIndex));
            slotWidget.setOnFilterChanged(entry -> menu.getBlockFilterEntries().set(slotIndex, (BlockFilterEntry) entry));
            slotWidget.setOnRightClick(this::openBlockTagPanel);
        }
        return slotWidget;
    }
    
    protected void openBlockTagPanel(FilterSlotWidget slotWidget, Either<ItemStack, FluidStack> toConvert) {
        toConvert.ifLeft(itemStack -> {
            if (itemStack.getItem() instanceof BlockItem blockItem) {
                openBlockTagPanelForBlock(slotWidget, blockItem.getBlock());
            }
        });
        
        toConvert.ifRight(fluidStack -> openFluidTagPanelOnly(slotWidget, fluidStack));
    }
    
    protected void openBlockTagPanelForBlock(FilterSlotWidget slotWidget, Block block) {
        if (tagList != null) {
            removeWidget(tagList);
            tagList = null;
        }
        
        convertingSlot = slotWidget;
        slotWidget.setHighlighted(true);
        
        tagList = new SimpleDraggablePanel(
            Component.translatable("gui.eternalartifacts.pipe_filter.tags_for", block.getName()),
            leftPos + (imageWidth / 2) - 81, topPos + 10, 162, 96,
            SimpleDraggablePanel.Bounds.full(this)
        );
        tagList.addClosingButton();
        tagList.setColor(getGuiTint());
        tagList.addOnCloseListener(panel -> {
            if (convertingSlot != null) {
                convertingSlot.setHighlighted(false);
            }
            convertingSlot = null;
            removeWidget(tagList);
            tagList = null;
        });
        
        var innerList = new ScrollablePanel<ScrollablePanelComponent>(
            tagList.getX() + 4, tagList.getY() + 17, 146, 75, 10
        );
        
        var tags = BuiltInRegistries.BLOCK.getTagNames()
            .filter(key -> block.builtInRegistryHolder().is(key))
            .toList();
        
        for (int i = 0; i < tags.size(); i++) {
            int finalI = i;
            TagKey<Block> tag = tags.get(i);
            String tagLocation = tag.location().toString();
            innerList.addChild((x, y, width, height) -> {
                var comp = new ScrollablePanelComponent(
                    x, y + finalI * 18, width, 16, innerList,
                    (mx, my, btn, c) -> setBlockTagFilter(tag, slotWidget),
                    finalI, font, Component.literal(tagLocation),
                    0xff2C2F33, 0xff3C8DBC, 0xff68C8FA
                );
                comp.setRenderIcon(false);
                return comp;
            });
        }
        
        innerList.reCalcInnerHeight();
        tagList.addChildren((x, y, width, height) -> innerList);
        addUpperLayerChild(tagList);
    }
    
    protected void openFluidTagPanelOnly(FilterSlotWidget slotWidget, FluidStack fluidStack) {
        if (tagList != null) {
            removeWidget(tagList);
            tagList = null;
        }
        
        convertingSlot = slotWidget;
        slotWidget.setHighlighted(true);
        
        tagList = new SimpleDraggablePanel(
            Component.translatable("gui.eternalartifacts.pipe_filter.tags_for", fluidStack.getDisplayName()),
            leftPos + (imageWidth / 2) - 81, topPos + 10, 162, 96,
            SimpleDraggablePanel.Bounds.full(this)
        );
        tagList.addClosingButton();
        tagList.setColor(getGuiTint());
        tagList.addOnCloseListener(panel -> {
            if (convertingSlot != null) {
                convertingSlot.setHighlighted(false);
            }
            convertingSlot = null;
            removeWidget(tagList);
            tagList = null;
        });
        
        var innerList = new ScrollablePanel<ScrollablePanelComponent>(
            tagList.getX() + 4, tagList.getY() + 17, 146, 75, 10
        );
        
        Fluid fluid = fluidStack.getFluid();
        var tags = BuiltInRegistries.FLUID.getTagNames()
            .filter(key -> fluid.builtInRegistryHolder().is(key))
            .toList();
        
        for (int i = 0; i < tags.size(); i++) {
            int finalI = i;
            TagKey<Fluid> tag = tags.get(i);
            String tagLocation = tag.location().toString();
            innerList.addChild((x, y, width, height) -> {
                var comp = new ScrollablePanelComponent(
                    x, y + finalI * 18, width, 16, innerList,
                    (mx, my, btn, c) -> setFluidTagFilter(tag, slotWidget),
                    finalI, font, Component.literal(tagLocation),
                    0xff2C2F33, 0xff3C8DBC, 0xff68C8FA
                );
                comp.setRenderIcon(false);
                return comp;
            });
        }
        
        innerList.reCalcInnerHeight();
        tagList.addChildren((x, y, width, height) -> innerList);
        addUpperLayerChild(tagList);
    }
    
    protected void setBlockTagFilter(TagKey<Block> tag, FilterSlotWidget slotWidget) {
        int slotIndex = slotWidget.getFilterSlot().getSlotIndex();
        BlockTagEntry tagEntry = new BlockTagEntry(tag);
        
        slotWidget.getFilterSlot().setFilter(tagEntry);
        slotWidget.setFilterEntry(tagEntry);
        slotWidget.getFilterSlot().set(ItemStack.EMPTY);
        menu.getBlockFilterEntries().set(slotIndex, tagEntry);
        
        Channel.sendToServer(new BlockTagFilterToServer(menu.containerId, slotIndex, tag));
        
        closeTagPanel();
    }
    
    protected void openTagPanel(FilterSlotWidget slotWidget, Either<ItemStack, FluidStack> toConvert) {
        if (tagList != null) {
            removeWidget(tagList);
            tagList = null;
        }
        
        convertingSlot = slotWidget;
        slotWidget.setHighlighted(true);
        
        Component desc = toConvert.map(
            ItemStack::getHoverName,
            FluidStack::getDisplayName
        );
        
        tagList = new SimpleDraggablePanel(
            Component.translatable("gui.eternalartifacts.pipe_filter.tags_for", desc),
            leftPos + (imageWidth / 2) - 81, topPos + 10, 162, 96,
            SimpleDraggablePanel.Bounds.full(this)
        );
        tagList.addClosingButton();
        tagList.setColor(getGuiTint());
        tagList.addOnCloseListener(panel -> {
            if (convertingSlot != null) {
                convertingSlot.setHighlighted(false);
            }
            convertingSlot = null;
            removeWidget(tagList);
            tagList = null;
        });
        
        var innerList = new ScrollablePanel<ScrollablePanelComponent>(
            tagList.getX() + 4, tagList.getY() + 17, 146, 75, 10
        );
        
        toConvert.ifLeft(itemStack -> {
            Item item = itemStack.getItem();
            var tags = BuiltInRegistries.ITEM.getTagNames()
                .filter(key -> item.builtInRegistryHolder().is(key))
                .toList();
            
            for (int i = 0; i < tags.size(); i++) {
                int finalI = i;
                TagKey<Item> tag = tags.get(i);
                String tagLocation = tag.location().toString();
                innerList.addChild((x, y, width, height) -> {
                    var comp = new ScrollablePanelComponent(
                        x, y + finalI * 18, width, 16, innerList,
                        (mx, my, btn, c) -> setItemTagFilter(tag, slotWidget),
                        finalI, font, Component.literal(tagLocation),
                        0xff2C2F33, 0xff3C8DBC, 0xff68C8FA
                    );
                    comp.setRenderIcon(false);
                    return comp;
                });
            }
        });
        
        toConvert.ifRight(fluidStack -> {
            Fluid fluid = fluidStack.getFluid();
            var tags = BuiltInRegistries.FLUID.getTagNames()
                .filter(key -> fluid.builtInRegistryHolder().is(key))
                .toList();
            
            for (int i = 0; i < tags.size(); i++) {
                int finalI = i;
                TagKey<Fluid> tag = tags.get(i);
                String tagLocation = tag.location().toString();
                innerList.addChild((x, y, width, height) -> {
                    var comp = new ScrollablePanelComponent(
                        x, y + finalI * 18, width, 16, innerList,
                        (mx, my, btn, c) -> setFluidTagFilter(tag, slotWidget),
                        finalI, font, Component.literal(tagLocation),
                        0xff2C2F33, 0xff3C8DBC, 0xff68C8FA
                    );
                    comp.setRenderIcon(false);
                    return comp;
                });
            }
        });
        
        innerList.reCalcInnerHeight();
        tagList.addChildren((x, y, width, height) -> innerList);
        addUpperLayerChild(tagList);
    }
    
    protected void setItemTagFilter(TagKey<Item> tag, FilterSlotWidget slotWidget) {
        int slotIndex = slotWidget.getFilterSlot().getSlotIndex();
        ItemTagEntry tagEntry = new ItemTagEntry(tag);
        
        slotWidget.getFilterSlot().setFilter(tagEntry);
        slotWidget.setFilterEntry(tagEntry);
        slotWidget.getFilterSlot().set(ItemStack.EMPTY);
        menu.getItemFilterEntries().set(slotIndex, tagEntry);
        
        Channel.sendToServer(new ItemTagFilterToServer(menu.containerId, slotIndex, tag));
        
        closeTagPanel();
    }
    
    protected void setFluidTagFilter(TagKey<Fluid> tag, FilterSlotWidget slotWidget) {
        int slotIndex = slotWidget.getFilterSlot().getSlotIndex();
        FluidTagEntry tagEntry = new FluidTagEntry(tag);
        
        slotWidget.getFilterSlot().setFilter(tagEntry);
        slotWidget.setFilterEntry(tagEntry);
        slotWidget.getFilterSlot().set(ItemStack.EMPTY);
        menu.getFluidFilterEntries().set(slotIndex, tagEntry);
        
        Channel.sendToServer(new FluidTagFilterToServer(menu.containerId, slotIndex, tag));
        
        closeTagPanel();
    }
    
    protected void closeTagPanel() {
        if (tagList != null) {
            if (convertingSlot != null) {
                convertingSlot.setHighlighted(false);
            }
            removeWidget(tagList);
            tagList = null;
            convertingSlot = null;
        }
    }
    
    private double lastPropertyPanelScrollAmount = 0;
    private boolean propertyPanelWasOpen = false;
    private int lastPropertyPanelX = 0;
    private int lastPropertyPanelY = 0;
    protected void openPropertySelectionForFilters() {
        if (propertyPanel != null) {
            propertyPanelWasOpen = true;
            lastPropertyPanelX = propertyPanel.getX();
            lastPropertyPanelY = propertyPanel.getY();
            var children = propertyPanel.getChildren();
            for (var child : children) {
                if (child instanceof ScrollablePanel<?> scrollPanel) {
                    lastPropertyPanelScrollAmount = scrollPanel.scrollAmount();
                    break;
                }
            }
        }
        
        closePropertyPanel(!propertyPanelWasOpen);
        
        propertyPanel = new SimpleDraggablePanel(
            ModConstants.GUI.withSuffixTranslatable("block_properties"),
            leftPos - 45, topPos + 5, 180, 150,
            SimpleDraggablePanel.Bounds.full(this)
        );
        if (propertyPanelWasOpen) propertyPanel.setPosition(lastPropertyPanelX, lastPropertyPanelY);
        propertyPanel.setColor(getGuiTint());
        propertyPanel.addClosingButton();
        propertyPanel.addOnCloseListener(panel -> {
            closePropertyPanel(true);
            propertyPanelWasOpen = false;
        });
        
        var innerList = new ScrollablePanel<ScrollablePanelComponent> (
            propertyPanel.getX() + 4, propertyPanel.getY() + 17, 164, 100, 10
        );
        
        List<Integer> blockStateIndices = new ArrayList<>();
        for (int i = 0; i < menu.getBlockFilterEntries().size(); i++) {
            FilterEntry entry = menu.getBlockFilterEntries().get(i);
            if (entry instanceof BlockStateEntry bse && !bse.isEmpty() && bse.getFilterState() != null) {
                if (!bse.getFilterState().getProperties().isEmpty()) {
                    blockStateIndices.add(i);
                }
            }
        }
        
        if (blockStateIndices.isEmpty()) {
            return;
        }
        
        int componentIndex = 0;
        for (int slotIndex : blockStateIndices) {
            BlockStateEntry bse = (BlockStateEntry) menu.getBlockFilterEntries().get(slotIndex);
            BlockState state = bse.getFilterState();
            Block block = state.getBlock();
            
            final int headerIndex = componentIndex++;
            innerList.addChild((x, y, width, height) -> {
                var comp = new ScrollablePanelComponent(
                    x, y + headerIndex * 18, width, 16, innerList,
                    (mx, my, btn, c) -> {}, headerIndex, font,
                    Component.literal("§e" + block.getName().getString()),
                    0xff1a1a2e, 0xff1a1a2e, 0xff1a1a2e
                );
                comp.setCanClick(false);
                comp.setRenderIcon(false);
                return comp;
            });
            
            for (Property<?> prop : state.getProperties()) {
                final int propIndex = componentIndex++;
                final int finalSlotIndex = slotIndex;
                final String propName = prop.getName();
                final boolean isSelected = bse.getMatchingProperties().contains(propName);
                String valueStr = getPropertyValueString(state, prop);
                
                innerList.addChild((x, y, width, height) -> {
                    String prefix = isSelected ? "§a✓ " : "§7✗ ";
                    var comp = new ScrollablePanelComponent(
                        x, y + propIndex * 18, width, 16, innerList,
                        (mx, my, btn, c) -> toggleProperty(finalSlotIndex, propName),
                        propIndex, font,
                        Component.literal(prefix + propName + ": " + valueStr),
                        isSelected ? 0xff2d4a3e : 0xff2C2F33,
                        isSelected ? 0xff3d6a5e : 0xff3C4D56,
                        isSelected ? 0xff4d8a7e : 0xff4C6D76
                    );
                    comp.setRenderIcon(false);
                    return comp;
                });
            }
            
            final int cycleIndex = componentIndex++;
            final int finalSlotIdx = slotIndex;
            innerList.addChild((x, y, width, height) -> {
                var comp = new ScrollablePanelComponent(
                    x, y + cycleIndex * 18, width, 16, innerList,
                    (mx, my, btn, c) -> openPropertyValuePanel(finalSlotIdx),
                    cycleIndex, font,
                    Component.literal("§b⚙ " + ModConstants.GUI.withSuffixTranslatable("change_property_values").getString()),
                    0xff1e3a5f, 0xff2e5a8f, 0xff3e7abf
                );
                comp.setRenderIcon(false);
                return comp;
            });
            
            componentIndex++;
        }
        
        propertyPanel.addChildren((pX, pY, pW, pH) ->
            CleanButton.builder(ModConstants.GUI.withSuffixTranslatable("apply"), btn -> {
                applyPropertyChanges();
                closePropertyPanel(true);
                propertyPanelWasOpen = false;
            }).bounds(pX + 5, pY + pH - 28, pW - 18, 18).build()
        );
        
        innerList.reCalcInnerHeight();
        innerList.setScrollAmount(lastPropertyPanelScrollAmount);
        propertyPanel.addChildren((x, y, width, height) -> innerList);
        addUpperLayerChild(propertyPanel);
    }
    
    protected void toggleProperty(int slotIndex, String propertyName) {
        FilterEntry entry = menu.getBlockFilterEntries().get(slotIndex);
        if (entry instanceof BlockStateEntry bse) {
            Set<String> props = new HashSet<>(bse.getMatchingProperties());
            if (props.contains(propertyName)) {
                props.remove(propertyName);
            } else {
                props.add(propertyName);
            }
            bse.setMatchingProperties(props);
            
            openPropertySelectionForFilters();
        }
    }
    
    protected void openPropertyValuePanel(int slotIndex) {
        FilterEntry entry = menu.getBlockFilterEntries().get(slotIndex);
        if (!(entry instanceof BlockStateEntry bse) || bse.getFilterState() == null) return;
        
        BlockState state = bse.getFilterState();
        
        SimpleDraggablePanel valuePanel = new SimpleDraggablePanel(
            ModConstants.GUI.withSuffixTranslatable("property_values", state.getBlock().getName()),
            propertyPanel != null ? propertyPanel.getX() + propertyPanel.getWidth() :
                leftPos + 90, propertyPanel != null ? propertyPanel.getY() : topPos + 5,
            160, 120,
            SimpleDraggablePanel.Bounds.full(this)
        );
        valuePanel.setId("property_value_panel");
        valuePanel.setColor(getGuiTint());
        valuePanel.addClosingButton();
        valuePanel.addOnCloseListener(panel -> {
            removeWidget(valuePanel);
        });
        
        var innerList = new ScrollablePanel<ScrollablePanelComponent>(
            valuePanel.getX() + 4, valuePanel.getY() + 17, 144, 95, 10
        );
        
        int compIndex = 0;
        for (Property<?> prop : state.getProperties()) {
            final int idx = compIndex++;
            final String propName = prop.getName();
            String currentValue = getPropertyValueString(state, prop);
            
            innerList.addChild((x, y, width, height) -> {
                var comp = new ScrollablePanelComponent(
                    x, y + idx * 18, width, 16, innerList,
                    (mx, my, btn, c) -> cyclePropertyValue(slotIndex, propName, innerList),
                    idx, font,
                    Component.literal("↻ " + propName + ": §f" + currentValue),
                    0xff2C2F33, 0xff3C8DBC, 0xff68C8FA
                );
                comp.setRenderIcon(false);
                return comp;
            });
        }
        
        innerList.reCalcInnerHeight();
        valuePanel.addChildren((x, y, width, height) -> innerList);
        addUpperLayerChild(valuePanel);
    }
    
    protected <T extends Comparable<T>> void cyclePropertyValue(int slotIndex, String propertyName, ScrollablePanel<ScrollablePanelComponent> innerList) {
        FilterEntry entry = menu.getBlockFilterEntries().get(slotIndex);
        if (!(entry instanceof BlockStateEntry bse) || bse.getFilterState() == null) return;
        
        BlockState state = bse.getFilterState();
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().equals(propertyName)) {
                @SuppressWarnings("unchecked")
                Property<T> typedProp = (Property<T>) prop;
                BlockState newState = cycleProperty(state, typedProp);
                bse.setFilterState(newState);
                for (ScrollablePanelComponent child : innerList.getChildren()) {
                    if (child.getMessage().getString().startsWith("↻ " + propertyName + ":")) {
                        String newValueStr = getPropertyValueString(newState, typedProp);
                        child.setMessage(Component.literal("↻ " + propertyName + ": §f" + newValueStr));
                        break;
                    }
                }
                openPropertySelectionForFilters();
                break;
            }
        }
    }
    
    private <T extends Comparable<T>> BlockState cycleProperty(BlockState state, Property<T> property) {
        Collection<T> values = property.getPossibleValues();
        List<T> valueList = new ArrayList<>(values);
        T currentValue = state.getValue(property);
        int currentIndex = valueList.indexOf(currentValue);
        int nextIndex = (currentIndex + 1) % valueList.size();
        return state.setValue(property, valueList.get(nextIndex));
    }
    
    private <T extends Comparable<T>> String getPropertyValueString(BlockState state, Property<T> property) {
        T value = state.getValue(property);
        return property.getName(value);
    }
    
    protected void applyPropertyChanges() {
        for (int i = 0; i < menu.getBlockFilterEntries().size(); i++) {
            FilterEntry entry = menu.getBlockFilterEntries().get(i);
            if (entry instanceof BlockStateEntry bse && !bse.isEmpty() && bse.getFilterState() != null) {
                BlockState state = bse.getFilterState();
                Set<String> matchingProps = bse.getMatchingProperties();
                
                Map<String, String> propValues = new HashMap<>();
                for (Property<?> prop : state.getProperties()) {
                    propValues.put(prop.getName(), getPropertyValueString(state, prop));
                }
                
                Channel.sendToServer(new BlockStatePropertiesFilterToServer(
                    menu.containerId, i, state, matchingProps, propValues
                ));
            }
        }
    }
    
    protected void addTargetBlockToFilter() {
        if (minecraft == null || minecraft.level == null) return;
        if (!(menu.getBlockEntity() instanceof Filterable)) return;
        
        BlockPos machinePos = menu.getBlockEntity().getBlockPos();
        BlockState machineState = minecraft.level.getBlockState(machinePos);
        
        //This works for normal machines but useless for multiblocks for now.
        if (!machineState.hasProperty(BlockStateProperties.FACING)) return;
        
        BlockPos targetPos = machinePos.relative(machineState.getValue(BlockStateProperties.FACING));
        BlockState targetState = minecraft.level.getBlockState(targetPos);
        
        if (targetState.isAir()) return;
        
        int emptySlotIndex = -1;
        for (int i = 0; i < menu.getBlockFilterEntries().size(); i++) {
            FilterEntry entry = menu.getBlockFilterEntries().get(i);
            if (entry.isEmpty()) {
                emptySlotIndex = i;
                break;
            }
        }
        
        if (emptySlotIndex != -1) {
            BlockStateEntry blockStateEntry = BlockStateEntry.matchBlockOnly(targetState);
            blockStateEntry.setIgnoreNBT(menu.isBlockIgnoresProps());
            blockStateEntry.setWhitelist(menu.isBlockWhitelist());
            
            FilterFakeSlot filterFakeSlot = menu.getBlockFilterFakeSlots().get(emptySlotIndex);
            filterFakeSlot.setFilter(blockStateEntry);
            SlotWidget widgetForSlot = getWidgetForSlot(filterFakeSlot);
            if (widgetForSlot instanceof FilterSlotWidget filterSlotWidget) {
                filterSlotWidget.setFilterEntry(blockStateEntry);
            }
            menu.getBlockFilterEntries().set(emptySlotIndex, blockStateEntry);
            
            Channel.sendToServer(new BlockStateFilterToServer(menu.containerId, emptySlotIndex, targetState));
        }
    }
    
    protected void closePropertyPanel(boolean closeValuePanelsToo) {
        if (propertyPanel != null) {
            removeWidget(propertyPanel);
            propertyPanel = null;
        }
        if (closeValuePanelsToo) {
            List<SimpleDraggablePanel> toRemove = new ArrayList<>();
            for (GuiEventListener child : children) {
                if (child instanceof SimpleDraggablePanel panel && "property_value_panel".equals(panel.getId())) {
                    toRemove.add(panel);
                }
            }
            for (SimpleDraggablePanel panel : toRemove) {
                removeWidget(panel);
            }
        }
    }
    
    protected void renderExtra(GuiGraphics gui, int mx, int my, float partialTick) {}

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        cachedMx = mx;
        cachedMy = my;
        shouldRenderEnergyTooltip = menu.getBeEnergy() != null;
        shouldRenderFluidTooltip = menu.getBeTank() != null;
        renderExtra(gui, mx, my, partialTick);
    }
    
    /**
     * ClientEvents tarafından çağrılır - panellerden sonra tooltip'leri render eder
     */
    public void renderMachineTooltips(GuiGraphics gui, int tooltipZ) {
        if (shouldRenderEnergyTooltip) renderEnergyTooltipInternal(gui, cachedMx, cachedMy, tooltipZ);
        if (shouldRenderFluidTooltip) renderFluidTooltipInternal(gui, cachedMx, cachedMy, tooltipZ);
        if (shouldRenderProgressTooltip) renderProgressTooltipInternal(gui, cachedMx, cachedMy, tooltipZ);
        shouldRenderProgressTooltip = false;
        
    }
    
    private void renderEnergyTooltipInternal(GuiGraphics gui, int mx, int my, int tooltipZ) {
        if(!energyLoc.isEmpty() && isCursorInBounds(energyLoc.get("x"), energyLoc.get("y"), energyLoc.get("width"), energyLoc.get("height"), mx, my)) {
            Machine<?> machine = (Machine<?>) menu.getBlockEntity();
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(Component.translatable(ModConstants.GUI.withSuffix("energy")).append(": ")
                .append(String.valueOf(menu.getBeEnergy().getEnergyStored()))
                .append("/").append(String.valueOf(menu.getBeEnergy().getMaxEnergyStored())));
            if (renderEPT) {
                String prefix = machine.isGenerator() ? "produce": "consume";
                tooltips.add(Component.translatable(ModConstants.GUI.withSuffix(prefix+"_energy_per_tick"), machine.getEnergyPerTick()));
            }
            gui.pose().pushPose();
            gui.pose().translate(0, 0, tooltipZ);
            com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            gui.renderTooltip(font, tooltips, Optional.empty(), mx, my);
            com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
            gui.pose().popPose();
        }
    }
    
    private void renderFluidTooltipInternal(GuiGraphics gui, int mx, int my, int tooltipZ) {
        fluidLocs.forEach( (tank, fluidLoc) -> {
            if (!fluidLoc.isEmpty() && isCursorInBounds(fluidLoc.get("x"), fluidLoc.get("y"), fluidLoc.get("width"), fluidLoc.get("height"), mx, my)) {
                var fs = menu.getBeTank().getFluidInTank(tank);
                var tooltips = StringUtils.getTooltipFromContainerFluid(fs, minecraft.level,
                    minecraft.options.advancedItemTooltips);
                tooltips.add(Component.literal(String.valueOf(fs.getAmount())).append(" / ").append(String.valueOf(menu.getBeTank().getTankCapacity(tank))));
                gui.pose().pushPose();
                gui.pose().translate(0, 0, tooltipZ);
                com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
                gui.renderTooltip(font, tooltips, Optional.empty(), mx, my);
                com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
                gui.pose().popPose();
            }
        });
    }
    
    protected void renderProgressTooltip(GuiGraphics gui, int x, int y, int xLen, int yLen, int mx, int my, int progressTooltipFirst, int progressTooltipSecond, String key) {
        if(isCursorInBounds(x, y, xLen, yLen, mx, my)) {
            this.shouldRenderProgressTooltip = true;
            this.progressTooltipX = x;
            this.progressTooltipY = y;
            this.progressTooltipXLen = xLen;
            this.progressTooltipYLen = yLen;
            this.progressTooltipKey = key;
            this.progressTooltipFirst = progressTooltipFirst;
            this.progressTooltipSecond = progressTooltipSecond;
        }
    }
    
    private void renderProgressTooltipInternal(GuiGraphics gui, int mx, int my, int tooltipZ) {
        if(isCursorInBounds(progressTooltipX, progressTooltipY, progressTooltipXLen, progressTooltipYLen, mx, my)) {
            gui.pose().pushPose();
            gui.pose().translate(0, 0, tooltipZ);
            com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            gui.renderTooltip(font,
                Component.translatable(ModConstants.GUI.withSuffix(progressTooltipKey)).append(": ")
                    .append(String.valueOf(progressTooltipFirst)).append("/").append(String.valueOf(progressTooltipSecond)),
                mx, my);
            com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
            gui.pose().popPose();
        }
    }

    protected void renderDefaultEnergyAndFluidBar(GuiGraphics gui) {
        renderDefaultEnergyBar(gui);
        renderDefaultFluidBar(gui);
    }
    protected void renderDefaultEnergyBar(GuiGraphics gui) {
        renderEnergyBar(gui, leftPos + 5, topPos + 20);
    }

    protected void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(bars, x, y, 0, 0, 18, 56);
        guiGraphics.blit(bars, x + 3, y + 53 - menu.getEnergyProgress(), 18, 53 - menu.getEnergyProgress(), 12, menu.getEnergyProgress());
        energyLoc.put("x", x);
        energyLoc.put("y", y);
        energyLoc.put("width", 18);
        energyLoc.put("height", 56);
    }

    protected void renderDefaultFluidBar(GuiGraphics gui) { renderDefaultFluidBar(gui, 0); }
    protected void renderDefaultFluidBar(GuiGraphics gui, int tankSlot) { renderFluidBar(gui, leftPos + 24, topPos + 20, tankSlot); }
    protected void renderFluidBar(GuiGraphics guiGraphics, int x, int y) { renderFluidBar(guiGraphics,  x,  y, 0); }
    protected void renderFluidBar(GuiGraphics guiGraphics, int x, int y, int tankSlot) {
        Map<String, Integer> fluidLoc = new HashMap<>();
        fluidLoc.put("x", x);
        fluidLoc.put("y", y);
        fluidLoc.put("width", 18);
        fluidLoc.put("height", 56);
        fluidLocs.put(tankSlot, fluidLoc);

        IFluidHandler tank = menu.getBlockEntity().getLevel().getCapability(Capabilities.FluidHandler.BLOCK, menu.getBlockEntity().getBlockPos(), menu.getBlockEntity().getBlockState(), menu.getBlockEntity(), null);
        FluidStack stack = FluidStack.EMPTY;
        if(tank != null) stack = tank.getFluidInTank(tankSlot);
        GuiDrawer.drawFluidWithTank(guiGraphics, x, y, stack, menu.getFluidProgress(tankSlot, 50));
    }

    protected void renderBurn(GuiGraphics guiGraphics, int x, int y, int mx, int my) {
        guiGraphics.blit(bars, x + 1, y + 1, 48, 10, 13, 13);
        if(menu.isWorking()) guiGraphics.blit(bars, x, y + 14 - menu.getScaledProgress(14), 48,  37 - menu.getScaledProgress(14), 14, menu.getScaledProgress(14));
        renderProgressTooltip(guiGraphics, x, y, 13, 13, mx, my, menu.data.get(0), menu.data.get(1), "burn_time");
    }

    protected void renderProgressArrowWTooltips(GuiGraphics guiGraphics, int x, int y, int mx, int my) {
        renderProgressArrow(guiGraphics, x, y, mx, my);
        String key = menu.getBlockEntity() instanceof Machine<?> machine && machine.isChargeProgress() ? "charge_progress" : "progress";
        renderProgressTooltip(guiGraphics, x, y, 22, 15, mx, my, menu.data.get(0), menu.data.get(1), key);
    }
    protected void renderProgressArrow(GuiGraphics guiGraphics, int x, int y, int mx, int my) {
        guiGraphics.blit(bars, x, y, 0, 56, 22, 15);
        if(menu.isWorking()) guiGraphics.blit(bars, x, y, 22, 56, menu.getScaledProgress(22), 15);
    }
}

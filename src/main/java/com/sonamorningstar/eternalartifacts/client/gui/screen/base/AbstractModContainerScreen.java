package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import com.sonamorningstar.eternalartifacts.client.config.ConfigUIRegistry;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.*;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.Draggable;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.Overlapping;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.ParentalWidget;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import com.sonamorningstar.eternalartifacts.core.ModKeyMappings;
import com.sonamorningstar.eternalartifacts.event.custom.RenderEtarSlotEvent;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.FluidSlotTransferToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractModContainerScreen<T extends AbstractModContainerMenu> extends EffectRenderingInventoryScreen<T> {
    private static final ResourceLocation CONFIG_BUTTON_TEXTURE = new ResourceLocation(MODID, "textures/gui/sprites/widget/machine_config_button.png");
    private static final ResourceLocation ENCHANTMENT_BUTTON_TEXTURE = new ResourceLocation("textures/item/enchanted_book.png");
    public static final int PANEL_Z_INCREMENT = 50;
    public static final int BASE_PANEL_Z = 300;
    public static final int TOOLTIP_Z_OFFSET = 500;
    
    @Setter
    @Getter
    private int guiTint = 0xFFFFFFFF;
    protected boolean renderEffects = true;
    public final List<GuiEventListener> upperLayerChildren = new ArrayList<>();
    public final Queue<AbstractWidget> upperLayerUpdateQueue = new ArrayDeque<>();
    @Nullable
    protected SimpleDraggablePanel configPanel;
    @Nullable
    protected SimpleDraggablePanel enchantmentPanel;
    private int nextPanelZ = BASE_PANEL_Z;
    private final Set<Slot> widgetManagedSlots = new HashSet<>();
    
    public void registerWidgetManagedSlot(Slot slot) {widgetManagedSlots.add(slot);}
    public void unregisterWidgetManagedSlot(Slot slot) {
        widgetManagedSlots.remove(slot);
    }
    public boolean isWidgetManagedSlot(Slot slot) {
        return widgetManagedSlots.contains(slot);
    }

    public AbstractModContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Override
    protected void init() {
        super.init();
        if (getMenu().machineConfigs != null) setupConfigWidgets();
        setupEnchantmentWidgets();
    }
    
    @Override
    protected void containerTick() {
        super.containerTick();
        while (!upperLayerUpdateQueue.isEmpty()) {
            AbstractWidget widget = upperLayerUpdateQueue.poll();
            if (widget != null) {
                shiftElementsToUpperLayer(widget);
            }
        }
    }
    
    private void shiftElementsToUpperLayer(AbstractWidget widget) {
        if (upperLayerChildren.contains(widget)) {
            upperLayerChildren.remove(widget);
            children.remove(widget);
            narratables.remove(widget);
            renderables.remove(widget);
            addUpperLayerChild(widget);
        }
    }
    
    protected void setupConfigWidgets() {
        configPanel = new SimpleDraggablePanel(
            Component.empty(), leftPos + (imageWidth / 2) - 50, topPos + 10, 100, 75,
            SimpleDraggablePanel.Bounds.full(this)
        );
        
        configPanel.visible = false;
        configPanel.active = false;
        configPanel.addClosingButton();
        
        MachineConfiguration configs = getMenu().machineConfigs;
        if (configs == null) return;
        
        for (Config config : configs.getConfigs().values()) {
            createUIFor(config, new ConfigUIRegistry.ConfigUIContext(configPanel, this));
        }
        
        SpriteButton configButton = SpriteButton.builder(Component.empty(),
                (button, key) -> configPanel.toggle(), CONFIG_BUTTON_TEXTURE)
            .bounds(leftPos + imageWidth - 18, topPos + 4, 13, 13).build();
        
        addUpperLayerChild(configPanel);
        addRenderableWidget(configButton);
    }
    
    protected void setupEnchantmentWidgets() {
        if (!(getMenu() instanceof AbstractMachineMenu amm)) {return;}
        ModBlockEntity mbe = amm.getBlockEntity() instanceof ModBlockEntity m ? m : null;
        if (mbe == null) return;
        
        Object2IntMap<Enchantment> enchantments = mbe.enchantments;
        if (enchantments == null || enchantments.isEmpty()) return;
        
        enchantmentPanel = new SimpleDraggablePanel(
            ModConstants.GUI.withSuffixTranslatable("enchantments"),
            leftPos + imageWidth + 5, topPos + 5, 150, 120,
            SimpleDraggablePanel.Bounds.full(this)
        );
        
        enchantmentPanel.visible = false;
        enchantmentPanel.active = false;
        enchantmentPanel.addClosingButton();
        
        var innerList = new ScrollablePanel<ScrollablePanelComponent>(
            enchantmentPanel.getX() + 4, enchantmentPanel.getY() + 17, 134, 95, 10
        );
        
        int componentIndex = 0;
        for (Object2IntMap.Entry<Enchantment> entry : enchantments.object2IntEntrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getIntValue();
            final int idx = componentIndex++;
            
            int maxLevel = enchantment.getMaxLevel();
            int textColor;
            if (level > maxLevel) textColor = 0xFFFF5555;
            else if (level == maxLevel) textColor = 0xFFFFAA00;
            else textColor = 0xFFFFFFFF;
            
            Component enchantName = enchantment.getFullname(level).copy().withStyle(s -> s.withColor(textColor));
            int bgColor = 0xFF2C2F33;
            innerList.addChild((x, y, width, height) -> {
                var comp = new ScrollablePanelComponent(
                    x, y + idx * 18, width, 16, innerList,
                    (mx, my, btn) -> {}, idx, font, enchantName,
                    bgColor, bgColor, bgColor
                );
                comp.setRenderIcon(false);
                comp.setCanClick(false);
                return comp;
            });
        }
        
        innerList.reCalcInnerHeight();
        enchantmentPanel.addChildren((x, y, width, height) -> innerList);
        
        SpriteButton enchantmentButton = SpriteButton.builder(Component.empty(),
                (button, key) -> enchantmentPanel.toggle(), ENCHANTMENT_BUTTON_TEXTURE)
            .bounds(leftPos + imageWidth - 36, topPos + 4, 16, 16)
            .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("enchantments"))
            .build();
        
        addUpperLayerChild(enchantmentPanel);
        addRenderableWidget(enchantmentButton);
    }
    
    private <C extends Config> void createUIFor(C config, ConfigUIRegistry.ConfigUIContext ctx) {
        ConfigUIRegistry.ConfigUIFactory<C> factory = ConfigUIRegistry.get(config);
        
        if (factory != null) {
            factory.createWidgets(config, ctx);
        }
    }
    
    protected void setImageSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }
    
    public <G extends GuiEventListener & NarratableEntry> void addUpperLayerChild(G child) {
        children.add(0, child);
        narratables.add(0, child);
        upperLayerChildren.add(child);
        
        if (child instanceof SimpleDraggablePanel panel) {
            bringPanelToFront(panel);
        }
    }
    
    public void bringPanelToFront(SimpleDraggablePanel panel) {
        // Listedeki sırayı güncelle - en sona taşı (en üstte render edilecek)
        if (upperLayerChildren.contains(panel)) {
            upperLayerChildren.remove(panel);
            upperLayerChildren.add(panel);
        }
        
        // Z-indexleri normalize et ve paneli en üste çıkar
        normalizeZIndices();
    }
    
    /**
     * Tüm panellerin z-indexlerini upperLayerChildren listesindeki sıraya göre yeniden hesaplar.
     * Bu sayede z-indexler sürekli artmaz ve kontrollü kalır.
     */
    private void normalizeZIndices() {
        int currentZ = BASE_PANEL_Z;
        for (GuiEventListener child : upperLayerChildren) {
            if (child instanceof SimpleDraggablePanel p) {
                p.setZIndex(currentZ);
                currentZ += PANEL_Z_INCREMENT;
            }
        }
        nextPanelZ = currentZ;
    }
    
    public int getMaxPanelZ() {
        int maxZ = 0;
        for (GuiEventListener child : upperLayerChildren) {
            if (child instanceof SimpleDraggablePanel p) {
                maxZ = Math.max(maxZ, p.getZIndex());
            }
        }
        return maxZ;
    }
    
    public int getTooltipZ() {
        return getMaxPanelZ() + TOOLTIP_Z_OFFSET;
    }
    
    @Override
    protected <T extends GuiEventListener & NarratableEntry> T addWidget(T pListener) {
        T widget = super.addWidget(pListener);
        if (widget instanceof SlotWidget slotWidget) {
            slotWidget.registerToScreen(this);
        }
        return widget;
    }
    
    @Override
    protected void clearWidgets() {
        for (GuiEventListener child : children()) {
            if (child instanceof SlotWidget slotWidget) {
                slotWidget.unregisterFromScreen(this);
            }
            if (child instanceof ParentalWidget parent) {
                for (GuiEventListener grandChild : parent.getChildren()) {
                    if (grandChild instanceof SlotWidget slotWidget) {
                        slotWidget.unregisterFromScreen(this);
                    }
                }
            }
        }
        super.clearWidgets();
        upperLayerChildren.clear();
    }
    
    @Override
	public void removeWidget(GuiEventListener listener) {
        if (listener instanceof SlotWidget slotWidget) {
            slotWidget.unregisterFromScreen(this);
        }
        if (listener instanceof ParentalWidget parent) {
            for (GuiEventListener child : parent.getChildren()) {
                if (child instanceof SlotWidget slotWidget) {
                    slotWidget.unregisterFromScreen(this);
                }
            }
        }
        upperLayerChildren.remove(listener);
        super.removeWidget(listener);
        normalizeZIndices();
    }
    
    public List<FakeSlot> getAllFakeSlots() {
        List<FakeSlot> fakeSlots = new ArrayList<>();
        for (Slot slot : menu.slots) {
            if (slot instanceof FakeSlot fs) {
                fakeSlots.add(fs);
            }
        }
        for (GuiEventListener child : children) {
            if (child instanceof ParentalWidget parent) {
                for (GuiEventListener grandChild : parent.getChildren()) {
                    if (grandChild instanceof SlotWidget slotWidget) {
                        Slot slot = slotWidget.getSlot();
                        if (slot instanceof FakeSlot fs && !fakeSlots.contains(fs)) {
                            fakeSlots.add(fs);
                        }
                    }
                }
            }
        }
        return fakeSlots;
    }
    
    @Nullable
    public SlotWidget getWidgetForSlot(Slot slot) {
        for (GuiEventListener child : children) {
            if (child instanceof SlotWidget slotWidget) {
                if (slotWidget.getSlot() == slot) {
                    return slotWidget;
                }
            }
            if (child instanceof ParentalWidget parent) {
                for (GuiEventListener grandChild : parent.getChildren()) {
                    if (grandChild instanceof SlotWidget slotWidget) {
                        if (slotWidget.getSlot() == slot) {
                            return slotWidget;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Nullable
    public SimpleDraggablePanel getPanelForWidget(GuiEventListener widget) {
        for (GuiEventListener child : upperLayerChildren) {
            if (child instanceof SimpleDraggablePanel panel) {
                if (panel.getChildren().contains(widget)) {
                    return panel;
                }
            }
        }
        return null;
    }
    
    public boolean isAnyPanelOpen() {
        for (GuiEventListener child : upperLayerChildren) {
            if (child instanceof SimpleDraggablePanel panel) {
                if (panel.visible || panel.active) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected void drawExtraBg(GuiGraphics gui, float tickDelta, int x, int y) {
        applyGuiTint(gui);
        renderSlotBgs(gui);
        clearGuiTint(gui);
    }
    
    protected void renderSlotBgs(GuiGraphics gui) {
        for(Slot slot : menu.slots) {
            if (isWidgetManagedSlot(slot)) continue;
            renderSlotBg(gui, slot, new ResourceLocation("container/slot"));
        }
    }
    
    protected void renderSlotBg(GuiGraphics gui, Slot slot, ResourceLocation texture) {
        int xPos = leftPos + slot.x - 1;
        int yPos = topPos + slot.y - 1;
        var event = NeoForge.EVENT_BUS.post(new RenderEtarSlotEvent(
            this, gui, slot, texture, xPos, yPos, 0,18, 18, guiTint));
        if (!event.isCanceled()) {
            applyCustomGuiTint(event.getGui(), event.getGuiTint());
            event.getGui().blitSprite(
                event.getTexture(), event.getX(), event.getY(), event.getBlitOffset(), event.getWidth(), event.getHeight()
            );
            resetGuiTint(event.getGui());
        }
    }
    
    @Override
    protected void renderSlot(GuiGraphics gui, Slot slot) {
        if (!isWidgetManagedSlot(slot)) super.renderSlot(gui, slot);
    }
    
    @Override
    protected void renderSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick) {
        if (!isWidgetManagedSlot(slot)) super.renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isHovering(Slot slot, double mx, double my) {
        if (isWidgetManagedSlot(slot)) return false;
        return super.isHovering(slot, mx, my);
    }
    
    @Override
    public void renderTooltip(GuiGraphics gui, int mx, int my) {
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, getTooltipZ());
        RenderSystem.disableDepthTest();
        super.renderTooltip(gui, mx, my);
        RenderSystem.enableDepthTest();
        pose.popPose();
    }
    
    @Override
    protected void renderBg(GuiGraphics gui, float tickDelta, int mX, int mY) {
        applyGuiTint(gui);
        GuiDrawer.drawDefaultBackground(gui, leftPos, topPos, imageWidth, imageHeight);
        drawExtraBg(gui, tickDelta, leftPos, topPos);
        clearGuiTint(gui);
    }
    
    /**
     * Floating item rendering handled on
     * {@link com.sonamorningstar.eternalartifacts.event.client.ClientEvents#renderScreenLowPrio(ScreenEvent.Render.Post)}
     */
    @Override
    public void renderFloatingItem(GuiGraphics pGuiGraphics, ItemStack pStack, int pX, int pY, String pText) {}
    
    public void applyGuiTint(GuiGraphics gui) {
        gui.setColor(FastColor.ARGB32.red(guiTint) / 255.0F, FastColor.ARGB32.green(guiTint) / 255.0F,
            FastColor.ARGB32.blue(guiTint) / 255.0F, FastColor.ARGB32.alpha(guiTint) / 255.0F);
    }
    public void applyGuiTint(GuiGraphics gui, int alpha) {
        gui.setColor(FastColor.ARGB32.red(guiTint) / 255.0F, FastColor.ARGB32.green(guiTint) / 255.0F,
            FastColor.ARGB32.blue(guiTint) / 255.0F, alpha / 255.0F);
    }
    public static void applyCustomGuiTint(GuiGraphics gui, int color) {
        gui.setColor(FastColor.ARGB32.red(color) / 255.0F, FastColor.ARGB32.green(color) / 255.0F,
            FastColor.ARGB32.blue(color) / 255.0F, FastColor.ARGB32.alpha(color) / 255.0F);
    }
    public void resetGuiTint(GuiGraphics gui) {
        applyGuiTint(gui);
    }
    public static void clearGuiTint(GuiGraphics gui) {
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Override
    protected void renderEffects(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (renderEffects) super.renderEffects(pGuiGraphics, pMouseX, pMouseY);
    }
    
    protected void renderTankSlots(GuiGraphics gui, int x, int y, int mx, int my) {
        for (int i = 0; i < menu.fluidSlots.size(); i++) {
            FluidSlot slot = menu.getFluidSlot(i);
            FluidStack fluidStack = slot.getFluid();
            if (isHovering(slot.x + 1, slot.y + 1, 16, 16, mx, my)) {
                var tooltipComponents = Lists.<Component>newArrayList();
                if (!fluidStack.isEmpty()) {
                    tooltipComponents.addAll(StringUtils.getTooltipFromContainerFluid(fluidStack, minecraft.level,
                        minecraft.options.advancedItemTooltips));
                    tooltipComponents.add(Component.literal(fluidStack.getAmount() + " / " + slot.getMaxSize()));
                }
                if (!menu.getCarried().isEmpty()) {
                    IFluidHandlerItem carriedHandler = menu.getCarried().getCapability(Capabilities.FluidHandler.ITEM);
                    if (carriedHandler != null) {
                        if (!tooltipComponents.isEmpty()) tooltipComponents.add(CommonComponents.EMPTY);
                        tooltipComponents.add(ModConstants.GUI.withSuffixTranslatable("left_click_transfer").withStyle(ChatFormatting.BLUE));
                        tooltipComponents.add(ModConstants.GUI.withSuffixTranslatable("right_click_transfer").withStyle(ChatFormatting.BLUE));
                    }
                }
                gui.renderTooltip(font, tooltipComponents, Optional.empty(), mx, my);
            }
            renderTankSlot(gui, x, y, slot);
        }
    }

    protected void renderTankSlot(GuiGraphics gui, int x, int y, FluidSlot slot) {
        FluidStack stack = slot.getFluid();
        int percentage = stack.getAmount() * 12 / slot.getMaxSize();
        GuiDrawer.drawFluidWithSmallTank(gui, x + slot.x, y + slot.y, stack, percentage);
    }
    
    @Override
    public boolean mouseDragged(double mx, double my, int button, double dragX, double dragY) {
        // Fokuslanmış panel varsa önce onu kontrol et
        if (getFocused() instanceof SimpleDraggablePanel panel && isDragging() && button == 0) {
            return panel.mouseDragged(mx, my, button, dragX, dragY);
        }
        super.mouseDragged(mx, my, button, dragX, dragY);
        return getFocused() != null && isDragging() && button == 0 && getFocused().mouseDragged(mx, my, button, dragX, dragY);
    }
    
    @Override
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double mx, double my) {
        Optional<GuiEventListener> child = getChildAt(mx, my);
        if (child.isPresent()) {
            if (child.get() instanceof Overlapping) {
                return false;
            }
        }
        return super.isHovering(pX, pY, pWidth, pHeight, mx, my);
    }
    
    @Override
    protected boolean hasClickedOutside(double mx, double my, int guiLeft, int guiTop, int mouseButton) {
        Optional<GuiEventListener> child = getChildAt(mx, my);
        if (child.isPresent() && child.get() instanceof Overlapping) {
            return false;
        }
        return super.hasClickedOutside(mx, my, guiLeft, guiTop, mouseButton);
    }
    
    public boolean isCursorInBounds(int startX, int startY, int lengthX, int lengthY, double mx, double my) {
        Optional<GuiEventListener> child = getChildAt(mx, my);
        if (child.isPresent()) {
            if (child.get() instanceof Overlapping) {
                return false;
            }
        }
        return mx >= startX && mx <= startX + lengthX &&
            my >= startY && my <= startY + lengthY;
    }
    
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        // upperLayerChildren listesini ters sırada kontrol et (en sondaki en üstte)
        for (int i = upperLayerChildren.size() - 1; i >= 0; i--) {
            GuiEventListener child = upperLayerChildren.get(i);
            if (child instanceof SimpleDraggablePanel panel && panel.visible && panel.active) {
                boolean isOver = mx >= panel.getX() && mx < panel.getX() + panel.getWidth() &&
                    my >= panel.getY() && my < panel.getY() + panel.getHeight();
                if (isOver) {
                    bringPanelToFront(panel);
                    panel.mouseClicked(mx, my, button);
                    setFocused(panel);
                    if (button == 0) setDragging(true);
                    return true;
                }
            }
        }
        
        // Diğer Overlapping widgetları kontrol et (DropdownMenu vb.)
        for (GuiEventListener child : children()) {
            if (child instanceof Overlapping overlapping &&
                child instanceof AbstractWidget widget &&
                !(child instanceof SimpleDraggablePanel)) {
                boolean isOver = widget.active && widget.visible &&
                    mx >= widget.getX() && mx < widget.getX() + widget.getWidth() &&
                    my >= widget.getY() && my < widget.getY() + widget.getHeight();
                if (isOver) {
                    GuiEventListener elementUnder = overlapping.getElementUnderMouse(mx, my);
                    if (elementUnder != null) {
                        if (elementUnder.mouseClicked(mx, my, button)) {
                            setFocused(elementUnder);
                            return true;
                        }
                    }
                }
            }
        }
        
        boolean ret = super.mouseClicked(mx, my, button);
        
        FluidSlot slot = getTankUnderMouse(mx, my);
        if (slot != null) {
            menu.handleFluidTankTransfer(slot.index, button);
            Channel.sendToServer(new FluidSlotTransferToServer(slot.index, button));
            return true;
        }
        
        return ret;
    }
    
    @Nullable
    private FluidSlot getTankUnderMouse(double mx, double my) {
        for(int i = 0; i < this.menu.fluidSlots.size(); i++) {
            FluidSlot slot = this.menu.fluidSlots.get(i);
            if (isHovering(slot.x + 1, slot.y + 1, 16, 16, mx, my)) {
                return slot;
            }
        }
        return null;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int pScanCode, int pModifiers) {
        if (keyCode == 69) { // E key without modifiers
            if (getFocused() instanceof EditBox box && box.canConsumeInput()) return false; // Prevents closing the screen when typing in a text box
        }
        KeyMapping configMapping = ModKeyMappings.OPEN_MACHINE_CONFIG;
        if (keyCode == configMapping.getKey().getValue()) {
            if (configPanel != null) {
                configPanel.toggle();
                return true;
            }
        }
        return super.keyPressed(keyCode, pScanCode, pModifiers);
    }
    
    @Override
    public void render(GuiGraphics gui, int mx, int my, float delta) {
        // Önce normal renderı çağır
        super.render(gui, mx, my, delta);
        renderTankSlots(gui, leftPos, topPos, mx, my);
        
        // Hover durumunu güncelle - z-index sırasına göre (en üstteki önce)
        boolean isBlocked = false;
        
        // Önce upperLayerChildren'ı ters sırada kontrol et (en üstte olan en son eklenen)
        for (int i = upperLayerChildren.size() - 1; i >= 0; i--) {
            GuiEventListener child = upperLayerChildren.get(i);
            if (child instanceof AbstractWidget widget && child instanceof Overlapping overlapping) {
                if (widget.visible) {
                    boolean hovered = overlapping.updateHover(mx, my, isBlocked);
                    // Raw bounds check kullanarak recursive çağrıyı önle
                    boolean isOver = widget.active &&
                        mx >= widget.getX() && mx < widget.getX() + widget.getWidth() &&
                        my >= widget.getY() && my < widget.getY() + widget.getHeight();
                    if (hovered && isOver) {
                        isBlocked = true;
                    }
                }
            }
        }
        
        // Sonra normal children'daki Overlapping widgetları kontrol et
        for (GuiEventListener child : children) {
            if (child instanceof AbstractWidget widget &&
                child instanceof Overlapping overlapping &&
                !upperLayerChildren.contains(child)) {
                if (widget.visible) {
                    overlapping.updateHover(mx, my, isBlocked);
                }
            }
        }
        // NOT: Panel'ler ve tooltip'ler ClientEvents.renderScreenLowPrio'da render ediliyor
    }
}

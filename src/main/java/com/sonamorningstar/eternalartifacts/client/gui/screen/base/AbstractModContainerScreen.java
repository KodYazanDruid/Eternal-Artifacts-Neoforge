package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import com.sonamorningstar.eternalartifacts.client.config.ConfigUIRegistry;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import com.sonamorningstar.eternalartifacts.core.ModKeyMappings;
import com.sonamorningstar.eternalartifacts.event.custom.RenderEtarSlotEvent;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.FluidSlotTransferToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractModContainerScreen<T extends AbstractModContainerMenu> extends EffectRenderingInventoryScreen<T> {
    private static final ResourceLocation CONFIG_BUTTON_TEXTURE = new ResourceLocation(MODID, "textures/gui/sprites/widget/machine_config_button.png");
    public static final int PANEL_Z_INCREMENT = 50;
    public static final int BASE_PANEL_Z = 300; // Minecraft item render z-index'inin üstünde (150+)
    public static final int TOOLTIP_Z_OFFSET = 500;
    
    @Setter
    @Getter
    private int guiTint = 0xFFFFFFFF;
    protected boolean renderEffects = true;
    public final List<GuiEventListener> upperLayerChildren = new ArrayList<>();
    public final Queue<AbstractWidget> upperLayerUpdateQueue = new ArrayDeque<>();
    @Nullable
    private SimpleDraggablePanel configPanel;
    
    private int nextPanelZ = BASE_PANEL_Z;

    public AbstractModContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Override
    protected void init() {
        super.init();
        if (getMenu().machineConfigs != null) setupConfigWidgets();
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
            panel.setZIndex(nextPanelZ);
            nextPanelZ += PANEL_Z_INCREMENT;
        }
    }
    
    private void bringPanelToFront(SimpleDraggablePanel panel) {
        int maxZ = BASE_PANEL_Z;
        for (GuiEventListener child : upperLayerChildren) {
            if (child instanceof SimpleDraggablePanel p && p != panel) {
                maxZ = Math.max(maxZ, p.getZIndex());
            }
        }
        panel.setZIndex(maxZ + PANEL_Z_INCREMENT);
        nextPanelZ = Math.max(nextPanelZ, panel.getZIndex() + PANEL_Z_INCREMENT);
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
    
    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        upperLayerChildren.clear();
    }
    
    @Override
	public void removeWidget(GuiEventListener listener) {
        super.removeWidget(listener);
        upperLayerChildren.remove(listener);
    }
    
    protected void drawExtraBg(GuiGraphics gui, float tickDelta, int x, int y) {
        applyGuiTint(gui);
        renderSlots(gui);
        clearGuiTint(gui);
    }
    
    protected void renderSlots(GuiGraphics gui) {
        for(Slot slot : menu.slots) {
            renderSlot(gui, slot, new ResourceLocation("container/slot"));
        }
    }
    
    protected void renderSlot(GuiGraphics gui, Slot slot, ResourceLocation texture) {
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
    protected void renderTooltip(GuiGraphics gui, int mx, int my) {
        PoseStack pose = gui.pose();
        pose.pushPose();
        RenderSystem.disableDepthTest();
        int tooltipZ = getMaxPanelZ() + TOOLTIP_Z_OFFSET;
        pose.translate(0.0F, 0.0F, tooltipZ);
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
        boolean ret = super.mouseClicked(mx, my, button);
        Optional<GuiEventListener> child = getChildAt(mx, my);
        if (child.isPresent()) {
            if (child.get() instanceof Overlapping overlapping) {
                if (overlapping instanceof AbstractWidget widget) {
                    upperLayerUpdateQueue.add(widget);
                }
                if (child.get() instanceof SimpleDraggablePanel panel) {
                    bringPanelToFront(panel);
                }
                GuiEventListener listener = overlapping.getElementUnderMouse(mx, my);
                if (listener != null) setFocused(listener);
            }
        }
        
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
        
        // Hover durumunu güncelle
        boolean foundOpenMenu = false;
        for (GuiEventListener child : children) {
            if (child instanceof AbstractWidget widget &&
                widget instanceof Overlapping overlapping) {
                if (!foundOpenMenu && widget.isMouseOver(mx, my)) {
                    overlapping.updateHover(mx, my);
                    foundOpenMenu = true;
                } else {
                    overlapping.updateHover(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
                }
            }
        }
        // NOT: Panel'ler ve tooltip'ler ClientEvents.renderScreenLowPrio'da render ediliyor
    }
}

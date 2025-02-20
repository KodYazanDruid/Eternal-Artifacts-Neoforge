package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.Network;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.DropdownMenu;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.container.TesseractMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.UUID;

public class TesseractScreen extends AbstractModContainerScreen<TesseractMenu> {
	private ScrollablePanel<TesseractNetwork> panel;
	private DropdownMenu<AbstractButton> selector;
	public TesseractScreen(TesseractMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		setModular(true);
		setGuiTint(0x002f29);
	}
	
	@Override
	protected void init() {
		super.init();
		setupPanel();
		/*addRenderableWidget(Button.builder(Component.literal("add_random"), this::addNetwork)
			.bounds(leftPos + 10, panel.getY() + panel.getHeight(), imageWidth - 20, 25).build());*/
		addRenderableWidget(Button.builder(Component.literal("remove_last"), this::removeLast)
			.bounds(leftPos, topPos-25, imageWidth, 25).build());
		addRenderableWidget(Button.builder(Component.literal("clear_selected"), this::clearSelected)
			.bounds(leftPos, topPos-75, imageWidth, 25).build());
		selector = new DropdownMenu<>(leftPos + 10, panel.getY() + panel.getHeight(),
			imageWidth - 20, 25, 50, font);
		addRenderableWidget(panel);
		addRenderableWidget(selector);
	}
	
	public void rebuildNetworkPanel() {
		renderables.remove(panel);
		children.remove(panel);
		narratables.remove(panel);
		double scrollAmount = panel.scrollAmount();
		setupPanel();
		panel.setScrollAmount(scrollAmount);
		addRenderableWidget(panel);
	}
	
	private void setupPanel() {
		var networks = TesseractMenu.gatheredNetworks;
		int topMargin = menu.tesseract.getNetworkId() == null ? 0 : 23;
		panel = new ScrollablePanel<>(leftPos + 10, topPos + 10 + topMargin,
			imageWidth - 20, imageHeight - 20 - topMargin - 25,
			getContentHeight(networks), 10);
		for (int i = 0; i < networks.size(); i++) {
			Network<?> network = networks.get(i);
			int finalI = i;
			panel.addChild((x, y, width, height) ->
				new TesseractNetwork(network, finalI, this::selectNetwork, x + 1, y + 1 + (finalI * 23), width - 2, 22)
			);
		}
	}
	
	private void addNetwork(Button b) {
		minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
	}
	private void removeLast(Button b) {
		minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
	}
	private void clearSelected(Button button) {
		menu.tesseract.setNetworkId(null);
		minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 2);
	}
	private void selectNetwork(int index) {
		if (index < TesseractMenu.gatheredNetworks.size()) {
			List<TesseractNetwork> children = panel.getChildren();
			TesseractNetwork widget = children.get(index);
			if (widget != null && widget.isFocused()) {
				Network<?> network = TesseractMenu.gatheredNetworks.get(index);
				menu.tesseract.setNetworkId(network.getUuid());
				minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 50 + index);
			} else if (widget != null) {
				widget.setFocused(true);
				children.stream().filter(w -> w != widget).forEach(w -> w.setFocused(false));
			}
		}
	}
	
	@Override
	public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(gui, pMouseX, pMouseY, pPartialTick);
		UUID selectedNetwork = menu.tesseract.getNetworkId();
		if (selectedNetwork != null) {
			Network<?> network = TesseractMenu.gatheredNetworks.stream()
				.filter(n -> n.getUuid().equals(selectedNetwork))
				.findFirst().orElse(null);
			if (network == null) {
				gui.drawString(font, Component.literal("Couldn't find the network!"), leftPos + 10, topPos + 10, 0xff3232);
			} else {
				gui.drawString(font, Component.literal("Selected Network: " + network.getName()), leftPos + 10, topPos + 10, 0xFFFFFFFF);
			}
		}
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}
	
	private int getContentHeight(List<Network<?>> networks) {
		return (networks.size()) * 23;
	}
	
	@Override
	public boolean mouseDragged(double mx, double my, int button, double dragX, double dragY) {
		super.mouseDragged(mx, my, button, dragX, dragY);
		return getFocused() != null && isDragging() && button == 0 && getFocused().mouseDragged(mx, my, button, dragX, dragY);
	}
}

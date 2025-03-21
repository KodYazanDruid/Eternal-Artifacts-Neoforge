package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.Network;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.DropdownMenu;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanelComponent;
import com.sonamorningstar.eternalartifacts.client.gui.widget.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.container.TesseractMenu;
import com.sonamorningstar.eternalartifacts.network.AddTesseractNetworkToServer;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TesseractScreen extends AbstractModContainerScreen<TesseractMenu> {
	private ScrollablePanel<TesseractNetwork> panel;
	private EditBox networkName;
	private Button addNetwork;
	private TesseractNetwork selectedNetwork;
	private DropdownMenu<ScrollablePanelComponent> securityMenu;
	private DropdownMenu<ScrollablePanelComponent> capMenu;
	public TesseractScreen(TesseractMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		setImageSize(240, 200);
		setGuiTint(0x002f29);
		renderEffects = false;
	}
	
	@Override
	protected void init() {
		super.init();
		setupPanel();
		addRenderableWidget(Button.builder(Component.literal("clear_selected"), this::clearSelected)
			.bounds(leftPos, topPos-75, imageWidth, 25).build());
		List<Component> securities = new ArrayList<>();
		Network.Access[] values = Network.Access.values();
		for (Network.Access value : values) {
			securities.add(ModConstants.SCROLLABLE_PANEL_COMPONENT.withSuffixTranslatable(value.name().toLowerCase()));
		}
		securityMenu = new DropdownMenu<>(leftPos + 95, panel.getY() + panel.getHeight(),
			imageWidth - 105, 15, 50, font, builder -> {
			for (int i = 0; i < securities.size(); i++) {
				Component component = securities.get(i);
				ScrollablePanel<ScrollablePanelComponent> panel = builder.menu().getDropPanel();
				builder.add(new ScrollablePanelComponent(panel.getX(), panel.getY() + (i * 22), panel.getWidth(), 20,
					panel, index -> securityMenu.select(index), i,
					font, component, 0xFF3F3000,0xFF554200, 0xFF6B5200));
			}
		}, ModConstants.DROPDOWN_MENU.withSuffixTranslatable("unselected_security"));
		List<Component> capabilities = Network.CAPABILITY_NAMES.values().stream().toList();
		capMenu = new DropdownMenu<>(leftPos + 95, panel.getY() + panel.getHeight() + 15,
			imageWidth - 105, 15, 50, font, builder -> {
			for (int i = 0; i < capabilities.size(); i++) {
				Component component = capabilities.get(i);
				ScrollablePanel<ScrollablePanelComponent> panel = builder.menu().getDropPanel();
				builder.add(new ScrollablePanelComponent(panel.getX(), panel.getY() + (i * 22), panel.getWidth(), 20,
					panel, index -> capMenu.select(index), i,
					font, component, 0xFF2E1065, 0xFF3B0086, 0xFF4C0099));
			}
		}, ModConstants.DROPDOWN_MENU.withSuffixTranslatable("unselected_capability"));
		networkName = new EditBox(font, leftPos + 10, panel.getY() + panel.getHeight(), 85, 15, Component.empty());
		networkName.setMaxLength(20);
		addNetwork = Button.builder(ModConstants.GUI.withSuffixTranslatable("add"), this::addNetwork)
			.bounds(leftPos + 10, networkName.getY() + networkName.getHeight(), 60, 15).build();
		addNetwork.active = false;
		addRenderableWidget(panel);
		addWidget(securityMenu);
		addWidget(capMenu);
		addRenderableWidget(networkName);
		addRenderableWidget(addNetwork);
		UUID selectedId = menu.tesseract.getNetworkId();
		if (selectedId != null) {
			TesseractMenu.gatheredNetworks.stream()
				.filter(n -> n.getUuid().equals(selectedId))
				.findFirst().ifPresent(this::constructSelected);
		}
	}
	
	public void rebuildNetworkPanel() {
		removeWidget(panel);
		double scrollAmount = panel.scrollAmount();
		setupPanel();
		panel.setScrollAmount(scrollAmount);
		addRenderableWidget(panel);
	}
	
	private void setupPanel() {
		var networks = TesseractMenu.gatheredNetworks;
		int topMargin = menu.tesseract.getNetworkId() == null ? 0 : 28;
		panel = new ScrollablePanel<>(leftPos + 10, topPos + 10 + topMargin,
			imageWidth - 20, imageHeight - 20 - topMargin - 25, 10);
		int childHeight = 28;
		for (int i = 0; i < networks.size(); i++) {
			Network<?> network = networks.get(i);
			int finalI = i;
			panel.addChild((x, y, width, height) ->
				new TesseractNetwork(panel.getX(), panel.getY() + (finalI * (childHeight + 1)), panel.getWidth(), childHeight,
					network, panel, this::selectNetwork, finalI, font,
					Component.empty(), 0xFF007A6D, 0xFF005F54, 0xFF004A42)
			);
		}
		panel.reCalcInnerHeight();
	}
	
	private void addNetwork(Button b) {
		Channel.sendToServer(
			new AddTesseractNetworkToServer(networkName.getValue(), minecraft.player.getId(), securityMenu.getIndex(), capMenu.getIndex())
		);
	}
	private void clearSelected(Button button) {
		menu.tesseract.setNetworkId(null);
		if (selectedNetwork != null) {
			removeWidget(selectedNetwork);
		}
		minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 2000);
	}
	private void selectNetwork(int index) {
		if (index < TesseractMenu.gatheredNetworks.size()) {
			List<TesseractNetwork> children = panel.getChildren();
			TesseractNetwork widget = children.get(index);
			if (widget != null && widget.isFocused()) {
				Network<?> network = TesseractMenu.gatheredNetworks.get(index);
				menu.tesseract.setNetworkId(network.getUuid());
				constructSelected(network);
				minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 5000 + index);
			} else if (widget != null) {
				focusNetwork(widget);
			}
		}
	}
	
	private void focusNetwork(TesseractNetwork widget) {
		widget.setFocused(true);
		panel.getChildren().stream().filter(w -> w != widget).forEach(w -> w.setFocused(false));
	}
	
	private void constructSelected(Network<?> network) {
		if (selectedNetwork != null) {
			removeWidget(selectedNetwork);
		}
		selectedNetwork = new TesseractNetwork(leftPos + 10, topPos + 9, imageWidth - 40, 26, network,
			panel, b -> this.focusNetwork(selectedNetwork), -1, font, Component.empty(),
			0xFF007A6D, 0xFF005F54, 0xFF004A42);
		addRenderableWidget(selectedNetwork);
	}
	
	@Override
	public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(gui, pMouseX, pMouseY, pPartialTick);
		capMenu.render(gui, pMouseX, pMouseY, pPartialTick);
		securityMenu.render(gui, pMouseX, pMouseY, pPartialTick);
		UUID selectedNetwork = menu.tesseract.getNetworkId();
		addNetwork.active = !networkName.getValue().isEmpty() && capMenu.getValue() != null && securityMenu.getValue() != null;
		if (selectedNetwork != null) {
			Network<?> network = TesseractMenu.gatheredNetworks.stream()
				.filter(n -> n.getUuid().equals(selectedNetwork))
				.findFirst().orElse(null);
			if (network == null) {
				gui.drawString(font, Component.literal("Couldn't find the network!"), leftPos + 10, topPos + 10, 0xff3232);
			}
		}
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		//System.out.println("Key Pressed!: "+pKeyCode);
		if (pKeyCode == 261) {
			for (int i = 0; i < panel.getChildren().size(); i++) {
				TesseractNetwork tn = panel.getChildren().get(i);
				int index = tn.getIndex();
				if (tn.isFocused() && index != -1) {
					minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1000 + index);
					break;
				} else if (tn.isFocused() && index == -1) {
					clearSelected(null);
					break;
				}
			}
		}
		if (pKeyCode == 256) {
			minecraft.player.closeContainer();
		}
		
		return networkName.keyPressed(pKeyCode, pScanCode, pModifiers) || networkName.canConsumeInput() || super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}
}

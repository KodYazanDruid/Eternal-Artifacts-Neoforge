package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.*;
import com.sonamorningstar.eternalartifacts.container.TesseractMenu;
import com.sonamorningstar.eternalartifacts.network.tesseract.AddTesseractNetworkToServer;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.tesseract.TesseractNetworkWhitelistToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TesseractScreen extends AbstractModContainerScreen<TesseractMenu> {
	public static final Component WHITELIST_TITLE = ModConstants.GUI.withSuffixTranslatable("whitelist");
	public static final String WHITELIST_ID_PREFIX = "tesseract_whitelist_panel";
	private ScrollablePanel<AbstractScrollPanelComponent> panel;
	private EditBox networkName;
	private AbstractButton addNetwork;
	@Getter
	private TesseractNetworkWidget selectedNetwork;
	private ScrollablePanelComponent selectedWlButton;
	public SimpleDraggablePanel selectedWlPanel;
	private DropdownMenu<ScrollablePanelComponent> securityMenu;
	private DropdownMenu<ScrollablePanelComponent> capMenu;
	public TesseractScreen(TesseractMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		setImageSize(240, 200);
		setGuiTint(0xff002f29);
		renderEffects = false;
	}
	
	@Override
	protected void init() {
		super.init();
		setupPanel();
		addRenderableWidget(CleanButton.builder(Component.literal("clear_selected"), this::clearSelected)
			.bounds(leftPos, topPos-50, imageWidth, 25).build());
		addRenderableWidget(CleanButton.builder(Component.literal("cycle_transfer"), this::cycleTransfer)
			.bounds(leftPos, topPos-25, imageWidth, 25).build());
		List<Component> securities = new ArrayList<>();
		TesseractNetwork.Access[] values = TesseractNetwork.Access.values();
		for (TesseractNetwork.Access value : values) {
			securities.add(ModConstants.SCROLLABLE_PANEL_COMPONENT.withSuffixTranslatable(value.name().toLowerCase()));
		}
		
		securityMenu = new DropdownMenu<>(leftPos + 95, panel.getY() + panel.getHeight(),
			imageWidth - 105, 15, 50, font, builder -> {
			for (int i = 0; i < securities.size(); i++) {
				Component component = securities.get(i);
				ScrollablePanel<ScrollablePanelComponent> panel = builder.menu().getDropPanel();
				builder.add(new ScrollablePanelComponent(panel.getX(), panel.getY() + (i * 22), panel.getWidth(), 20,
					panel, (mx, my, index) -> securityMenu.select(index), i,
					font, component, 0xFF3F3000,0xFF554200, 0xFF6B5200));
			}
		}, ModConstants.DROPDOWN_MENU.withSuffixTranslatable("unselected_security"));
		List<Component> capabilities = TesseractNetwork.CAPABILITY_NAMES.values().stream().toList();
		capMenu = new DropdownMenu<>(leftPos + 95, panel.getY() + panel.getHeight() + 15,
			imageWidth - 105, 15, 50, font, builder -> {
			for (int i = 0; i < capabilities.size(); i++) {
				Component component = capabilities.get(i);
				ScrollablePanel<ScrollablePanelComponent> panel = builder.menu().getDropPanel();
				builder.add(new ScrollablePanelComponent(panel.getX(), panel.getY() + (i * 22), panel.getWidth(), 20,
					panel, (mx, my, index) -> capMenu.select(index), i,
					font, component, 0xFF2E1065, 0xFF3B0086, 0xFF4C0099));
			}
		}, ModConstants.DROPDOWN_MENU.withSuffixTranslatable("unselected_capability"));
		
		networkName = new EditBox(font, leftPos + 10, panel.getY() + panel.getHeight(), 85, 15, Component.empty());
		networkName.setMaxLength(20);
		addNetwork = CleanButton.builder(ModConstants.GUI.withSuffixTranslatable("add"), this::addNetwork)
			.bounds(leftPos + 10, networkName.getY() + networkName.getHeight(), 60, 15).build();
		addNetwork.active = false;
		addRenderableWidget(panel);
		addWidget(securityMenu);
		addWidget(capMenu);
		addRenderableWidget(networkName);
		addRenderableWidget(addNetwork);
		UUID selectedId = menu.tesseract.getNetworkId();
		if (selectedId != null) {
			TesseractMenu.gatheredTesseractNetworks.stream()
				.filter(n -> n.getUuid().equals(selectedId))
				.findFirst().ifPresent(this::constructSelected);
		}
	}
	
	@Override
	public void rebuildWidgets() {
		double scrollAmount = panel.scrollAmount();
		super.rebuildWidgets();
		panel.setScrollAmount(scrollAmount);
	}
	
	private void setupPanel() {
		var tNetworks = TesseractNetworks.get(minecraft.level);
		var networks = tNetworks.getTesseractNetworks().stream().toList();
		int topMargin = menu.tesseract.getNetworkId() == null ? 0 : 28;
		panel = new ScrollablePanel<>(leftPos + 10, topPos + 10 + topMargin,
			imageWidth - 20, imageHeight - 20 - topMargin - 25, 10);
		int childHeight = 28;
		AtomicInteger networkIndex = new AtomicInteger();
		for (int i = 0; i < networks.size(); i++) {
			TesseractNetwork<?> tesseractNetwork = networks.get(i);
			int finalI = i;
			
			panel.addChild((x, y, width, height) -> {
				int widgetX = panel.getX();
				int isShort = tesseractNetwork.getAccess() == TesseractNetwork.Access.PROTECTED ? 20 : 0;
				int widgetY = panel.getY() + (finalI * (childHeight + 1));
				return new TesseractNetworkWidget(widgetX, widgetY, panel.getWidth() - isShort, childHeight,
					tesseractNetwork, panel, (mx, my, index) -> selectNetwork(index)
					,networkIndex.getAndIncrement(), font, Component.empty(),
					0xFF007A6D, 0xFF005F54, 0xFF004A42);
			});
			
			if (tesseractNetwork.getAccess() == TesseractNetwork.Access.PROTECTED) {
				int widgetX = panel.getX() + panel.getWidth() - 20;
				int widgetY = panel.getY() + (finalI * (childHeight + 1));
				
				SimpleDraggablePanel dragPanel = new SimpleDraggablePanel(WHITELIST_TITLE,
					widgetX, widgetY, 200, 150,
					SimpleDraggablePanel.Bounds.full(this));
				dragPanel.setColor(getGuiTint());
				dragPanel.addClosingButton();
				dragPanel.setId(WHITELIST_ID_PREFIX + "_" + tesseractNetwork.getUuid());
				
				boolean isOwner = tesseractNetwork.getOwner().getId().equals(minecraft.getGameProfile().getId());
				if (isOwner) {
					dragPanel.addChildren((pX, pY, pW, pH) -> {
						var editBox = new EditBox(font, pX + 5, pY + 18, pW - 25, 15, Component.empty());
						editBox.setMaxLength(20);
						return editBox;
					});
					dragPanel.addChildren((pX, pY, pW, pH) ->
						CleanButton.builder(Component.literal("+"), b -> {
							String name = ((EditBox) dragPanel.getChildren().get(1)).getValue();
							if (!name.isEmpty()) {
								Channel.sendToServer(new TesseractNetworkWhitelistToServer(name, tesseractNetwork.getUuid()));
							}
						}).bounds(pX + pW - 20, pY + 18, 15, 15).build()
					);
				}
				
				ScrollablePanel<TesseractWhitelistComponentWidget> whitelistPanel =
					new ScrollablePanel<>(leftPos + 5, topPos + 34 - (isOwner ? 0 : 15),
						dragPanel.getWidth() - 19, dragPanel.getHeight() - 39 + (isOwner ? 0 : 15), 10);
				
				fillWhitelistPanel(tesseractNetwork, whitelistPanel, font);
				
				dragPanel.addChildren((pX, pY, pW, pH) -> {
					whitelistPanel.setX(pX + 5);
					whitelistPanel.setY(pY + (isOwner ? 34 : 26));
					whitelistPanel.setWidth(pW - 19);
					whitelistPanel.setHeight(pH - 39);
					return whitelistPanel;
				});
				dragPanel.visible = false;
				dragPanel.active = false;
				addUpperLayerChild(dragPanel);
				
				panel.addChild((x, y, width, height) -> new ScrollablePanelComponent(widgetX, widgetY, 20, childHeight,
					panel, (mx, my, index) -> {
					dragPanel.setX(widgetX);
					dragPanel.setY(widgetY - (int) panel.scrollAmount());
					dragPanel.toggle();
				}, finalI, font, Component.empty(), 0xFF2E1065, 0xFF3B0086, 0xFF4C0099));
			}
		}
		panel.reCalcInnerHeight();
	}
	
	public static void fillWhitelistPanel(TesseractNetwork<?> tesseractNetwork,
										  ScrollablePanel<TesseractWhitelistComponentWidget> whitelistPanel,
										  Font font) {
		int names = 0;
		for (int j = 0; j < tesseractNetwork.getWhitelistedPlayers().size(); j++) {
			GameProfile whitelistedPlayer = tesseractNetwork.getWhitelistedPlayers().get(j);
			whitelistPanel.addChild(new TesseractWhitelistComponentWidget(
				whitelistPanel.getX(), whitelistPanel.getY() + (names * 16),
				whitelistPanel.getWidth(), 15,
				whitelistPanel, names++, font,
				Either.left(whitelistedPlayer), tesseractNetwork,
				0xFF1F2A24, 0xFF2E4238, 0xFF3B6F5A
			));
		}
		for (int j = 0; j < tesseractNetwork.getPendingWhitelistPlayers().size(); j++) {
			String whitelistedPlayer = tesseractNetwork.getPendingWhitelistPlayers().get(j);
			whitelistPanel.addChild(new TesseractWhitelistComponentWidget(
				whitelistPanel.getX(), whitelistPanel.getY() + (names * 16),
				whitelistPanel.getWidth(), 15,
				whitelistPanel, names++, font,
				Either.right(whitelistedPlayer), tesseractNetwork,
				0xFF2A2618, 0xFF3A3522, 0xFF5A4A28
			));
		}
		whitelistPanel.reCalcInnerHeight();
	}
	
	//region Buttons.
	private void addNetwork(AbstractButton b) {
		Channel.sendToServer(
			new AddTesseractNetworkToServer(networkName.getValue(), minecraft.player.getId(), securityMenu.getIndex(), capMenu.getIndex())
		);
	}
	private void clearSelected(AbstractButton button) {
		if (selectedNetwork != null) {
			removeWidget(selectedNetwork);
			rebuildWidgets();
		}
		minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 2000);
	}
	private void cycleTransfer(AbstractButton button) {
		minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 3000);
	}
	private void selectNetwork(int index) {
		if (index < TesseractMenu.gatheredTesseractNetworks.size()) {
			List<AbstractScrollPanelComponent> children = panel.getChildren().stream()
				.filter(c -> c instanceof TesseractNetworkWidget).toList();
			AbstractScrollPanelComponent widget = children.get(index);
			if (!(widget instanceof TesseractNetworkWidget tesseractNetworkWidget)) return;
			if (tesseractNetworkWidget.isFocused()) {
				TesseractNetwork<?> tesseractNetwork = TesseractMenu.gatheredTesseractNetworks.get(index);
				constructSelected(tesseractNetwork);
				menu.tesseract.setNetworkId(tesseractNetwork.getUuid());
				minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 5000 + index);
			}
		}
	}
	//endregion

	public void constructSelected(TesseractNetwork<?> tesseractNetwork) {
		if (selectedNetwork != null) removeWidget(selectedNetwork);
		if (selectedWlButton != null) removeWidget(selectedWlButton);
		if (selectedWlPanel != null) removeWidget(selectedWlPanel);
		
		selectedNetwork = new TesseractNetworkWidget(leftPos + 10, topPos + 9, imageWidth - 20, 26, tesseractNetwork,
			panel, (mx, my, b) -> this.setFocused(selectedNetwork), -1, font, Component.empty(),
			0xFF007A6D, 0xFF005F54, 0xFF004A42);
		if (tesseractNetwork.getAccess() == TesseractNetwork.Access.PROTECTED) {
			selectedNetwork.setWidth(imageWidth - 40);
			int dX = leftPos + selectedNetwork.getWidth() + 10;
			selectedWlPanel = new SimpleDraggablePanel(WHITELIST_TITLE,
				dX, selectedNetwork.getY(), 200, 150,
				SimpleDraggablePanel.Bounds.full(this));
			selectedWlPanel.setColor(getGuiTint());
			selectedWlPanel.addClosingButton();
			selectedWlPanel.setId(WHITELIST_ID_PREFIX + "_" + tesseractNetwork.getUuid());
			
			boolean isOwner = tesseractNetwork.getOwner().getId().equals(minecraft.getGameProfile().getId());
			if (isOwner) {
				selectedWlPanel.addChildren((pX, pY, pW, pH) -> {
					var editBox = new EditBox(font, pX + 5, pY + 18, pW - 25, 15, Component.empty());
					editBox.setMaxLength(20);
					return editBox;
				});
				
				selectedWlPanel.addChildren((pX, pY, pW, pH) ->
					CleanButton.builder(Component.literal("+"), b -> {
						String name = ((EditBox) selectedWlPanel.getChildren().get(1)).getValue();
						if (!name.isEmpty()) {
							Channel.sendToServer(new TesseractNetworkWhitelistToServer(name, tesseractNetwork.getUuid()));
						}
					}).bounds(pX + pW - 20, pY + 18, 15, 15).build()
				);
			}
			
			ScrollablePanel<TesseractWhitelistComponentWidget> whitelistPanel =
				new ScrollablePanel<>(leftPos + 5, topPos + 34 - (isOwner ? 0 : 15),
					selectedWlPanel.getWidth() - 19, selectedWlPanel.getHeight() - 39 + (isOwner ? 0 : 15), 10);
			
			fillWhitelistPanel(tesseractNetwork, whitelistPanel, font);
			
			selectedWlPanel.addChildren((pX, pY, pW, pH) -> {
				whitelistPanel.setX(pX + 5);
				whitelistPanel.setY(pY + (isOwner ? 34 : 26));
				whitelistPanel.setWidth(pW - 19);
				whitelistPanel.setHeight(pH - 39);
				return whitelistPanel;
			});
			
			selectedWlButton = new ScrollablePanelComponent(dX, selectedNetwork.getY(),
				20, selectedNetwork.getHeight(),
				panel, (mx, my, index) -> {
				selectedWlPanel.setX(dX);
				selectedWlPanel.setY(selectedNetwork.getY() - (int) panel.scrollAmount());
				selectedWlPanel.toggle();
			}, -1, font, Component.empty(), 0xFF2E1065, 0xFF3B0086, 0xFF4C0099);
			selectedWlPanel.visible = false;
			selectedWlPanel.active = false;
			addRenderableWidget(selectedWlButton);
			addUpperLayerChild(selectedWlPanel);
		}
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
			TesseractNetwork<?> tesseractNetwork = TesseractMenu.gatheredTesseractNetworks.stream()
				.filter(n -> n.getUuid().equals(selectedNetwork))
				.findFirst().orElse(null);
			if (tesseractNetwork == null) {
				gui.drawString(font, Component.literal("Couldn't find the tesseractNetwork!"), leftPos + 10, topPos + 10, 0xff3232);
			}
		}
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}
	
	@Override
	public boolean keyPressed(int keyCode, int pScanCode, int pModifiers) {
		//Delete key removes the selected network.
		if (keyCode == 261) {
			for (int i = 0; i < panel.getChildren().size(); i++) {
				AbstractScrollPanelComponent tn = panel.getChildren().get(i);
				if (!(tn instanceof TesseractNetworkWidget)) continue;
				int index = tn.getIndex();
				if (tn.isFocused() && index != -1) {
					minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1000 + index);
					return true;
				} else if (tn.isFocused() && index == -1) {
					clearSelected(null);
					return true;
				}
			}
		}
		// Escape key closes the screen.
		if (keyCode == 256) {
			minecraft.player.closeContainer();
			return true;
		}
		
		return super.keyPressed(keyCode, pScanCode, pModifiers);
	}
}

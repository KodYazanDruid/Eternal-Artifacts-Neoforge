package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanelComponent;
import com.sonamorningstar.eternalartifacts.container.EnergyDistributorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.EnergyDistributor;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EnergyDistributorScreen extends AbstractSidedMachineScreen<EnergyDistributorMenu> {
	private ScrollablePanel<ScrollablePanelComponent> targetsPanel;
	private LongList lastTargets;
	
	public EnergyDistributorScreen(EnergyDistributorMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.renderEPT = false;
	}
	
	@Override
	protected void init() {
		super.init();
		targetsPanel = new ScrollablePanel<>(leftPos + 25, topPos + 19, 141, 58, 10);
		addRenderableWidget(targetsPanel);
		rebuildTargetsList();
	}
	
	@Override
	public void containerTick() {
		super.containerTick();
		EnergyDistributor eDistributor = (EnergyDistributor) menu.getBlockEntity();
		if (eDistributor.targets != null && !eDistributor.targets.equals(lastTargets)) {
			rebuildTargetsList();
		}
	}
	
	private void rebuildTargetsList() {
		EnergyDistributor eDistributor = (EnergyDistributor) menu.getBlockEntity();
		if (eDistributor.targets == null) return;
		lastTargets = eDistributor.targets;
		
		targetsPanel.clearChildren();
		int componentHeight = 20;
		int i = 0;
		for (long target : eDistributor.targets) {
			BlockPos targetPos = BlockPos.of(target);
			BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(targetPos);
			if (blockEntity != null) {
				ResourceLocation resourceLocation = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
				String displayText = resourceLocation.toString() + " " + targetPos.toShortString();
				
				ScrollablePanelComponent component = new ScrollablePanelComponent(
					targetsPanel.getX(), targetsPanel.getY() + (i * componentHeight),
					targetsPanel.getWidth(), componentHeight,
					targetsPanel, (mx, my, idx, c) -> {}, i, font,
					Component.literal(displayText),
					0xff404040, 0xff606060, 0xff505050
				);
				component.setRenderIcon(false);
				targetsPanel.addChild(component);
				i++;
			}
		}
		targetsPanel.reCalcInnerHeight();
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyBar(gui);
	}
	
	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
	}
}

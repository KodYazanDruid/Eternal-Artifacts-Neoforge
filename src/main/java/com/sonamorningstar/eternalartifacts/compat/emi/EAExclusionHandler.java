package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.PipeFilterScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.Draggable;
import com.sonamorningstar.eternalartifacts.registrar.TabType;
import dev.emi.emi.api.EmiExclusionArea;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.function.Consumer;

public class EAExclusionHandler implements EmiExclusionArea<Screen> {
	@Override
	public void addExclusionArea(Screen screen, Consumer<Bounds> consumer) {
		if (screen instanceof AbstractContainerScreen<?> containerScreen) {
			TabHandler tabHandler = TabHandler.INSTANCE;
			if (tabHandler != null) {
				var tabs = tabHandler.activeTabs;
				for (int i = 0; i < tabs.size(); i++) {
					TabType<?> tab = tabs.get(i);
					int x = containerScreen.getGuiLeft() + tabHandler.xOff + i * 27;
					int y = containerScreen.getGuiTop() + tabHandler.yOff;
					int height = tab == tabHandler.currentTab ? 32 : 28;
					consumer.accept(new Bounds(x, y, 26, height));
				}
			}
		}
		
		if (screen instanceof AbstractModContainerScreen<?> modContainerScreen){
			var upperLayerChildren = modContainerScreen.upperLayerChildren;
			for (GuiEventListener upperLayerChild : upperLayerChildren) {
				if (upperLayerChild instanceof Draggable && upperLayerChild instanceof AbstractWidget widget) {
					consumer.accept(new Bounds(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight()));
				}
			}
		}
		
		if (screen instanceof PipeFilterScreen pipeScreen) {
			var tagList = pipeScreen.getTagList();
			if (tagList != null) consumer.accept(new Bounds(tagList.getX(), tagList.getY(), tagList.getWidth(), tagList.getHeight()));
		}
	}
}

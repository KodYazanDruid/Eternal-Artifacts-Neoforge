package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.InterfaceRemoteMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.compress.utils.Lists;

import java.util.Optional;

public class InterfaceRemoteScreen extends AbstractModContainerScreen<InterfaceRemoteMenu> {
	public InterfaceRemoteScreen(InterfaceRemoteMenu menu, Inventory pPlayerInventory, Component pTitle) {
		super(menu, pPlayerInventory, pTitle);
		int totalSlotSize = this.menu.slots.size() + this.menu.fluidSlots.size();
		setImageSize(imageWidth, Mth.ceil((float) totalSlotSize / 9) * 18 + 40);
		inventoryLabelY = this.menu.invYOff + 16;
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		//renderTankSlots(gui, leftPos, topPos, mx, my);
		renderTooltip(gui, mx, my);
	}
	
	/*private void renderTankSlots(GuiGraphics gui, int x, int y, int mx, int my) {
		for (int i = 0; i < menu.fluidSlots.size(); i++) {
			FluidSlot slot = menu.getFluidSlot(i);
			FluidStack fluidStack = slot.getFluid();
			if (isHovering(slot.x + 1, slot.y + 1, 16, 16, mx, my)) {
				var tooltipComponents = Lists.<Component>newArrayList();
				if (!fluidStack.isEmpty()) {
					tooltipComponents.addAll(TooltipHelper.getTooltipFromContainerFluid(fluidStack, minecraft.level,
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
	}*/
}

package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.FilterSlotWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanelComponent;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.container.BlockInteractorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.FluidTagFilterToServer;
import com.sonamorningstar.eternalartifacts.network.ItemTagFilterToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockInteractorScreen extends AbstractSidedMachineScreen<BlockInteractorMenu> {
	@Getter
	@Nullable
	protected SimpleDraggablePanel tagList;
	@Nullable
	protected SimpleDraggablePanel filterPanel;
	@Nullable
	protected FilterSlotWidget convertingSlot;
	
	public BlockInteractorScreen(BlockInteractorMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	
	@Override
	protected void init() {
		super.init();
		if (menu.getBlockEntity() instanceof Filterable) {
			filterPanel = new SimpleDraggablePanel(Component.empty(),
				leftPos + 23, topPos + 8, 129, 70,
				SimpleDraggablePanel.Bounds.of(0, 0, width, height));
			filterPanel.visible = false;
			filterPanel.active = false;
			filterPanel.addClosingButton();
			
			SpriteButton button = SpriteButton.builder(Component.empty(), (b, i) -> {
					filterPanel.visible = true;
					filterPanel.active = true;
				}, new ResourceLocation(MODID, "textures/item/machine_item_filter.png"))
				.bounds(leftPos + imageWidth - 41, topPos + 3, 18, 18).build();
			
			for (int i = 0; i < BlockInteractorMenu.FILTER_SIZE; i++) {
				final int slotIndex = i;
				FilterSlotWidget slotWidget = new FilterSlotWidget(
					menu.FAKE_FILTER_SLOTS.get(i),
					() -> menu.getFilterEntries().get(slotIndex)
				);
				slotWidget.setOnFilterChanged(entry -> menu.getFilterEntries().set(slotIndex, entry));
				slotWidget.setOnRightClick(this::openTagPanel);
				
				int col = i % 3;
				int row = i / 3;
				filterPanel.addChildren((fx, fy, fW, fH) -> {
					slotWidget.setPosition(fx + 10 + col * 18, fy + 10 + row * 18);
					return slotWidget;
				});
			}
			
			filterPanel.addChildren((fx, fy, fW, fH) -> {
				var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
					menu.setWhitelist(!menu.isWhitelist());
					if (minecraft != null && minecraft.gameMode != null) {
						minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
					}
					b.setTextures(getListIcon());
				}).bounds(fx + 68, fy + 10, 16, 16);
				bld.addTooltipHover(() -> menu.isWhitelist() ?
					ModConstants.GUI.withSuffixTranslatable("whitelist").withStyle(style -> style.withColor(0x55FF55)) :
					ModConstants.GUI.withSuffixTranslatable("blacklist").withStyle(style -> style.withColor(0xFF5555)));
				bld.addTooltipHover(() -> menu.isWhitelist() ?
					ModConstants.GUI.withSuffixTranslatable("pipe_filter_blacklist_swap").withStyle(style -> style.withColor(0xAAAAAA)) :
					ModConstants.GUI.withSuffixTranslatable("pipe_filter_whitelist_swap").withStyle(style -> style.withColor(0xAAAAAA)));
				var buton = bld.build();
				buton.setTextures(getListIcon());
				return buton;
			});
			
			filterPanel.addChildren((fx, fy, fW, fH) -> {
				var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
					menu.setIgnoresNbt(!menu.isIgnoresNbt());
					if (minecraft != null && minecraft.gameMode != null) {
						minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
					}
					b.setTextures(getNbtToleranceIcon());
				}).bounds(fx + 68, fy + 28, 16, 16);
				bld.addTooltipHover(() -> menu.isIgnoresNbt() ?
					ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_nbt").withStyle(style -> style.withColor(0x55FF55)) :
					ModConstants.GUI.withSuffixTranslatable("pipe_filter_nbt_tolerant").withStyle(style -> style.withColor(0xFF5555)));
				bld.addTooltipHover(() -> menu.isIgnoresNbt() ?
					ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_nbt_swap").withStyle(style -> style.withColor(0xAAAAAA)) :
					ModConstants.GUI.withSuffixTranslatable("pipe_filter_nbt_tolerant_swap").withStyle(style -> style.withColor(0xAAAAAA)));
				var buton = bld.build();
				buton.setTextures(getNbtToleranceIcon());
				return buton;
			});
			
			addRenderableWidget(button);
			addUpperLayerChild(filterPanel);
		}
	}
	
	private ResourceLocation getListIcon() {
		return menu.isWhitelist() ? new ResourceLocation("textures/item/paper.png") :
			new ResourceLocation(MODID, "textures/item/carbon_paper.png");
	}
	
	private ResourceLocation getNbtToleranceIcon() {
		return menu.isIgnoresNbt() ? new ResourceLocation(MODID, "textures/item/green_apple.png") :
			new ResourceLocation("textures/item/apple.png");
	}
	
	protected void openTagPanel(FilterSlotWidget slotWidget, Either<ItemStack, FluidStack> toConvert) {
		if (tagList != null) {
			removeWidget(tagList);
			tagList = null;
		}
		
		convertingSlot = slotWidget;
		slotWidget.setHighlighted(true); // Highlight'ı aç
		
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
				convertingSlot.setHighlighted(false); // Highlight'ı kapat
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
						(mx, my, btn) -> setItemTagFilter(tag, slotWidget),
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
						(mx, my, btn) -> setFluidTagFilter(tag, slotWidget),
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
		slotWidget.getFilterSlot().set(ItemStack.EMPTY);
		menu.getFilterEntries().set(slotIndex, tagEntry);
		
		Channel.sendToServer(new ItemTagFilterToServer(menu.containerId, slotIndex, tag));
		
		closeTagPanel();
	}
	
	protected void setFluidTagFilter(TagKey<Fluid> tag, FilterSlotWidget slotWidget) {
		int slotIndex = slotWidget.getFilterSlot().getSlotIndex();
		FluidTagEntry tagEntry = new FluidTagEntry(tag);
		
		slotWidget.getFilterSlot().setFilter(tagEntry);
		slotWidget.getFilterSlot().set(ItemStack.EMPTY);
		menu.getFilterEntries().set(slotIndex, tagEntry);
		
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
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyAndFluidBar(gui);
	}
}

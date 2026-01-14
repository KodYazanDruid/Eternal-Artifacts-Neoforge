package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.FilterSlotWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanelComponent;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.client.gui.widget.CleanButton;
import com.sonamorningstar.eternalartifacts.container.BlockInteractorMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.network.*;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockInteractorScreen extends AbstractSidedMachineScreen<BlockInteractorMenu> {
	@Getter
	@Nullable
	protected SimpleDraggablePanel tagList;
	@Nullable
	protected SimpleDraggablePanel filterPanel;
	@Nullable
	protected SimpleDraggablePanel propertyPanel;
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
			
			SpriteButton button = SpriteButton.builder(Component.empty(), (b, i) -> filterPanel.toggle()
				, new ResourceLocation(MODID, "textures/item/machine_item_filter.png"))
				.bounds(leftPos + imageWidth - (enchantmentPanel != null ? 54 : 36), topPos + 3, 16, 16).build();
			
			for (int i = 0; i < BlockInteractorMenu.FILTER_SIZE; i++) {
				final int slotIndex = i;
				FilterSlotWidget slotWidget = new FilterSlotWidget(
					menu.FAKE_FILTER_SLOTS.get(i),
					() -> menu.getFilterEntries().get(slotIndex)
				);
				slotWidget.setOnFilterChanged(entry -> menu.getFilterEntries().set(slotIndex, entry));
				slotWidget.setBlockFilter(menu.isBlockBreaker());
				
				if (menu.isBlockBreaker()) {
					slotWidget.setOnRightClick(this::openBlockTagPanel);
				} else {
					slotWidget.setOnRightClick(this::openTagPanel);
				}
				
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
					(menu.isBlockBreaker() ?
						ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_properties").withStyle(style -> style.withColor(0x55FF55)) :
						ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_nbt").withStyle(style -> style.withColor(0x55FF55))) :
					(menu.isBlockBreaker() ?
						ModConstants.GUI.withSuffixTranslatable("pipe_filter_properties_tolerant").withStyle(style -> style.withColor(0xFF5555)) :
						ModConstants.GUI.withSuffixTranslatable("pipe_filter_nbt_tolerant").withStyle(style -> style.withColor(0xFF5555))));
				bld.addTooltipHover(() -> menu.isIgnoresNbt() ?
					(menu.isBlockBreaker() ?
						ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_properties_swap").withStyle(style -> style.withColor(0xAAAAAA)) :
						ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_nbt_swap").withStyle(style -> style.withColor(0xAAAAAA))) :
					(menu.isBlockBreaker() ?
						ModConstants.GUI.withSuffixTranslatable("pipe_filter_properties_tolerant_swap").withStyle(style -> style.withColor(0xAAAAAA)) :
						ModConstants.GUI.withSuffixTranslatable("pipe_filter_nbt_tolerant_swap").withStyle(style -> style.withColor(0xAAAAAA))));
				var buton = bld.build();
				buton.setTextures(getNbtToleranceIcon());
				return buton;
			});
			
			// Property seçim paneli açma butonu - sadece BlockBreaker için
			if (menu.isBlockBreaker()) {
				filterPanel.addChildren((fx, fy, fW, fH) -> {
					var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
						openPropertySelectionForFilters();
					}).bounds(fx + 68, fy + 46, 16, 16);
					bld.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("block_properties").withStyle(style -> style.withColor(0x55AAFF)));
					bld.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("block_properties_desc").withStyle(style -> style.withColor(0xAAAAAA)));
					var buton = bld.build();
					buton.setTextures(new ResourceLocation(MODID, "textures/item/wrench.png"));
					return buton;
				});
				
				// Baktığı bloğu filtreye ekleyen buton
				filterPanel.addChildren((fx, fy, fW, fH) -> {
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
	
	protected void openBlockTagPanel(FilterSlotWidget slotWidget, Either<ItemStack, FluidStack> toConvert) {
		toConvert.ifLeft(itemStack -> {
			if (itemStack.getItem() instanceof BlockItem blockItem) {
				openBlockTagPanelForBlock(slotWidget, blockItem.getBlock());
			}
		});
		
		toConvert.ifRight(fluidStack -> {
			openFluidTagPanelOnly(slotWidget, fluidStack);
		});
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
					(mx, my, btn) -> setBlockTagFilter(tag, slotWidget),
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
					(mx, my, btn) -> setFluidTagFilter(tag, slotWidget),
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
		slotWidget.getFilterSlot().set(ItemStack.EMPTY);
		menu.getFilterEntries().set(slotIndex, tagEntry);
		
		Channel.sendToServer(new BlockTagFilterToServer(menu.containerId, slotIndex, tag));
		
		closeTagPanel();
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
		for (int i = 0; i < menu.getFilterEntries().size(); i++) {
			FilterEntry entry = menu.getFilterEntries().get(i);
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
			BlockStateEntry bse = (BlockStateEntry) menu.getFilterEntries().get(slotIndex);
			BlockState state = bse.getFilterState();
			Block block = state.getBlock();
			
			final int headerIndex = componentIndex++;
			innerList.addChild((x, y, width, height) -> {
				var comp = new ScrollablePanelComponent(
					x, y + headerIndex * 18, width, 16, innerList,
					(mx, my, btn) -> {}, headerIndex, font,
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
						(mx, my, btn) -> toggleProperty(finalSlotIndex, propName),
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
					(mx, my, btn) -> openPropertyValuePanel(finalSlotIdx),
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
		FilterEntry entry = menu.getFilterEntries().get(slotIndex);
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
		FilterEntry entry = menu.getFilterEntries().get(slotIndex);
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
					(mx, my, btn) -> cyclePropertyValue(slotIndex, propName, innerList),
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
		FilterEntry entry = menu.getFilterEntries().get(slotIndex);
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
		for (int i = 0; i < menu.getFilterEntries().size(); i++) {
			FilterEntry entry = menu.getFilterEntries().get(i);
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
		
		if (!machineState.hasProperty(BlockStateProperties.FACING)) return;
		
		BlockPos targetPos = machinePos.relative(machineState.getValue(BlockStateProperties.FACING));
		BlockState targetState = minecraft.level.getBlockState(targetPos);
		
		if (targetState.isAir()) return;
		
		int emptySlotIndex = -1;
		for (int i = 0; i < menu.getFilterEntries().size(); i++) {
			FilterEntry entry = menu.getFilterEntries().get(i);
			if (entry.isEmpty()) {
				emptySlotIndex = i;
				break;
			}
		}
		
		if (emptySlotIndex == -1) return;
		
		BlockStateEntry blockStateEntry = BlockStateEntry.matchBlockOnly(targetState);
		blockStateEntry.setIgnoreNBT(menu.isIgnoresNbt());
		blockStateEntry.setWhitelist(menu.isWhitelist());
		menu.getFilterEntries().set(emptySlotIndex, blockStateEntry);
		menu.getFAKE_FILTER_SLOTS().get(emptySlotIndex).setFilter(blockStateEntry);
		
		Channel.sendToServer(new BlockStateFilterToServer(menu.containerId, emptySlotIndex, targetState));
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
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyAndFluidBar(gui);
	}
}

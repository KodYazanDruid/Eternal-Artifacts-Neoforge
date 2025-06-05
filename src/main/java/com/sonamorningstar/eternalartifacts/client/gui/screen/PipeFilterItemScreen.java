package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanelComponent;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.container.PipeFilterItemMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.network.*;
import com.sonamorningstar.eternalartifacts.util.FluidRendererHelper;
import com.sonamorningstar.eternalartifacts.util.ItemRendererHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PipeFilterItemScreen extends AbstractModContainerScreen<PipeFilterItemMenu> {
	private final int type;
	@Getter
	@Nullable
	private SimpleDraggablePanel tagList;
	@Nullable
	private Either<Item, Fluid> toConvert = null;
	@Nullable
	private Slot convertingSlot;
	public PipeFilterItemScreen(PipeFilterItemMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		renderEffects = false;
		this.type = menu.getAttType();
		setGuiTint(type == 0 ? 0xffea630c : 0xff4d9fe5);
	}
	
	public void setupTagPanel() {
		if (toConvert != null && convertingSlot instanceof FakeSlot fakeSlot) {
			if (tagList != null) removeWidget(tagList);
			
			Component desc = toConvert.map(Item::getDescription, fluid -> fluid.getFluidType().getDescription());
			tagList = new SimpleDraggablePanel(
				Component.translatable("gui.eternalartifacts.pipe_filter.tags_for", desc),
				leftPos - 10, topPos - 10, 162, 96, SimpleDraggablePanel.Bounds.full(this));
			tagList.addClosingButton();
			tagList.setColor(getGuiTint());
			tagList.addOnCloseListener(panel -> {
				toConvert = null;
				convertingSlot = null;
				removeWidget(tagList);
			});
			var innerList = new ScrollablePanel<ScrollablePanelComponent>(tagList.getX() + 4, tagList.getY() + 17,
				146, 75, 10);
			toConvert.ifLeft(item -> {
				var tags = BuiltInRegistries.ITEM.getTagNames().filter(key -> item.builtInRegistryHolder().is(key)).toList();
				for (int i = 0; i < tags.size(); i++) {
					int finalI = i;
					TagKey<Item> tag = tags.get(i);
					String tagLocation = tags.get(i).location().toString();
					innerList.addChild((x, y, width, height) -> {
							var comp = new ScrollablePanelComponent(x, y + finalI * 18, width, 16, innerList,
								(mx, my, button) -> setItemTagFilter(tag, fakeSlot.getSlotIndex()), finalI, font, Component.literal(tagLocation),
								0xff2C2F33, 0xff3C8DBC, 0xff68C8FA);
							comp.setRenderIcon(false);
							return comp;
						}
					);
				}
			});
			toConvert.ifRight(fluid -> {
				var tags = BuiltInRegistries.FLUID.getTagNames().filter(key -> fluid.builtInRegistryHolder().is(key)).toList();
				for (int i = 0; i < tags.size(); i++) {
					int finalI = i;
					TagKey<Fluid> tag = tags.get(i);
					String tagLocation = tags.get(i).location().toString();
					innerList.addChild((x, y, width, height) -> {
							var comp = new ScrollablePanelComponent(x, y + finalI * 18, width, 16, innerList,
								(mx, my, button) -> setFluidTagFilter(tag, fakeSlot.getSlotIndex()), finalI, font, Component.literal(tagLocation),
								0xff2C2F33, 0xff3C8DBC, 0xff68C8FA);
							comp.setRenderIcon(false);
							return comp;
						}
					);
				}
			});
			innerList.reCalcInnerHeight();
			tagList.addChildren((x, y, width, height) -> innerList);
			addRenderableWidget(tagList);
		} else if (tagList != null) {
			removeWidget(tagList);
			tagList = null;
		}
	}
	
	private void setItemTagFilter(TagKey<Item> tag, int index) {
		if (convertingSlot instanceof FakeSlot fakeSlot) {
			fakeSlot.set(ItemStack.EMPTY);
			menu.getFilterEntries().set(index, new ItemTagEntry(tag));
			Channel.sendToServer(new ItemTagFilterToServer(menu.containerId, index, tag));
		}
	}
	private void setFluidTagFilter(TagKey<Fluid> tag, int index) {
		if (convertingSlot instanceof FakeSlot fakeSlot) {
			fakeSlot.set(ItemStack.EMPTY);
			menu.getFilterEntries().set(index, new FluidTagEntry(tag));
			Channel.sendToServer(new FluidTagFilterToServer(menu.containerId, index, tag));
		}
	}
	
	private ItemStack getListIcon() {
		return menu.isWhitelist() ? Items.PAPER.getDefaultInstance() :
			ModItems.CARBON_PAPER.toStack();
	}
	private ItemStack getNbtToleranceIcon() {
		return menu.isIgnoresNbt() ? ModItems.GREEN_APPLE.toStack() :
			Items.APPLE.getDefaultInstance();
	}
	
	private List<Component> getListTooltips() {
		return menu.isWhitelist() ?
			List.of(
				ModConstants.GUI.withSuffixTranslatable("whitelist").withStyle(style -> style.withColor(0x00ff00)),
				ModConstants.GUI.withSuffixTranslatable("pipe_filter_blacklist_swap").withStyle(style -> style.withColor(0xff0000))
			) :
			List.of(
				ModConstants.GUI.withSuffixTranslatable("blacklist").withStyle(style -> style.withColor(0xff0000)),
				ModConstants.GUI.withSuffixTranslatable("pipe_filter_whitelist_swap").withStyle(style -> style.withColor(0x00ff00))
			);
	}
	private List<Component> getNbtToleranceTooltips() {
		return menu.isIgnoresNbt() ?
			List.of(
				ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_nbt").withStyle(style -> style.withColor(0x00ff00)),
				ModConstants.GUI.withSuffixTranslatable("pipe_filter_ignore_nbt_swap").withStyle(style -> style.withColor(0xff0000))
			) :
			List.of(
				ModConstants.GUI.withSuffixTranslatable("pipe_filter_nbt_tolerant").withStyle(style -> style.withColor(0xff0000)),
				ModConstants.GUI.withSuffixTranslatable("pipe_filter_nbt_tolerant_swap").withStyle(style -> style.withColor(0x00ff00))
			);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float delta) {
		super.render(gui, mx, my, delta);
		ItemRendererHelper.renderFakeItemTransparent(gui, getListIcon(),
			leftPos + 116, topPos + 35, 255);
		ItemRendererHelper.renderFakeItemTransparent(gui, getNbtToleranceIcon(),
			leftPos + 134, topPos + 35, 255);
		renderTooltip(gui, mx, my);
		if (isCursorInBounds(leftPos + 116, topPos + 35, 16, 16, mx, my)) {
			gui.renderTooltip(font, getListTooltips(), Optional.empty(), mx, my);
		}
		if (isCursorInBounds(leftPos + 134, topPos + 35, 16, 16, mx, my)) {
			gui.renderTooltip(font, getNbtToleranceTooltips(), Optional.empty(), mx, my);
		}
	}
	
	protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
		if (menu.getCarried().isEmpty() && this.hoveredSlot instanceof FilterFakeSlot fakeSlot) {
			FilterEntry entry = menu.getFilterEntries().get(fakeSlot.getSlotIndex());
			long tick = Minecraft.getInstance().clientTickCount;
			if (entry instanceof ItemTagEntry tagEntry) {
				Ingredient ingredient = Ingredient.of(tagEntry.getTag());
				ItemStack[] values = ingredient.getItems();
				if (values.length == 0) {
					super.renderTooltip(guiGraphics, x, y);
					return;
				}
				ItemStack itemStack = values[(int) ((tick / 20) % ingredient.getItems().length)].copy();
				itemStack.setHoverName(Component.literal(tagEntry.getTag().location().toString()));
				guiGraphics.renderTooltip(font, getTooltipFromContainerItem(itemStack), itemStack.getTooltipImage(), itemStack, x, y);
			} else if (entry instanceof FluidStackEntry fluidStackEntry) {
				FluidStack fluidStack = fluidStackEntry.getFilterStack();
				if (!fluidStack.isEmpty())
					guiGraphics.renderTooltip(font, PipeFilterScreen.getTooltipFromContainerFluid(fluidStack,
						minecraft.options.advancedItemTooltips), Optional.empty(), x, y);
			} else if (entry instanceof FluidTagEntry fluidTagEntry) {
				FluidIngredient ingredient = FluidIngredient.of(fluidTagEntry.getTag(), 1000);
				FluidStack[] values = ingredient.getFluidStacks();
				if (values.length == 0) {
					super.renderTooltip(guiGraphics, x, y);
					return;
				}
				FluidStack fluidStack = values[(int) ((tick / 20) % values.length)].copy();
				CompoundTag tag = fluidStack.getOrCreateTag();
				tag.putString("EtarFluidStackName", fluidTagEntry.getTag().location().toString());
				guiGraphics.renderTooltip(font, PipeFilterScreen.getTooltipFromContainerFluid(fluidStack, minecraft.options.advancedItemTooltips), Optional.empty(), x, y);
			}  else super.renderTooltip(guiGraphics, x, y);
			return;
		}
		super.renderTooltip(guiGraphics, x, y);
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (isCursorInBounds(leftPos + 116, topPos + 35, 16, 16, mx, my)) {
			menu.setWhitelist(!menu.isWhitelist());
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
			return true;
		} else if (isCursorInBounds(leftPos + 134, topPos + 35, 16, 16, mx, my)) {
			menu.setIgnoresNbt(!menu.isIgnoresNbt());
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
			return true;
		}
		return super.mouseClicked(mx, my, button);
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		pGuiGraphics.drawString(font, getTitle(), titleLabelX, titleLabelY, -1, false);
		pGuiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, -1, false);
	}
	
	@Override
	protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
		super.slotClicked(slot, slotId, mouseButton, type);
		if (slot instanceof FakeSlot fakeSlot && !fakeSlot.isDisplayOnly()) {
			ItemStack carried = menu.getCarried();
			ItemStack stack = slot.getItem();
			toConvert = null;
			convertingSlot = null;
			setupTagPanel();
			int index = fakeSlot.getSlotIndex();
			NonNullList<FilterEntry> filters = menu.getFilterEntries();
			FilterEntry entry = filters.get(index);
			if (carried.isEmpty()) {
				if (!stack.isEmpty()) {
					if (mouseButton == 0) {
						fakeSlot.set(ItemStack.EMPTY);
						filters.set(index, ItemStackEntry.EMPTY);
						updateItem(menu.containerId, index, ItemStack.EMPTY);
					} else if (mouseButton == 1) {
						toConvert = Either.left(stack.getItem());
						convertingSlot = slot;
						setupTagPanel();
					}
				} else {
					fakeSlot.set(ItemStack.EMPTY);
					filters.set(index, ItemStackEntry.EMPTY);
					updateItem(menu.containerId, index, ItemStack.EMPTY);
					updateFluid(menu.containerId, index, FluidStack.EMPTY);
				}
			} else {
				if (entry instanceof ItemStackEntry itemEntry && hasCapAndMatches(itemEntry, carried)) {
					FluidStack filtered = itemEntry.getFilterStack().getCapability(Capabilities.FluidHandler.ITEM).getFluidInTank(0).copyWithAmount(1000);
					filters.set(index, new FluidStackEntry(filtered, menu.isIgnoresNbt()));
					updateFluid(menu.containerId, index, filtered);
				} else {
					ItemStack carriedCopy = carried.copyWithCount(1);
					fakeSlot.set(carriedCopy);
					filters.set(index, new ItemStackEntry(carriedCopy, menu.isIgnoresNbt()));
					updateItem(menu.containerId, index, carriedCopy);
				}
			}
		}
	}
	
	private boolean hasCapAndMatches(ItemStackEntry entry, ItemStack carried) {
		IFluidHandlerItem hasCap = entry.getFilterStack().getCapability(Capabilities.FluidHandler.ITEM);
		IFluidHandlerItem carriedCap = carried.getCapability(Capabilities.FluidHandler.ITEM);
		if (hasCap != null && carriedCap != null) {
			return hasCap.getFluidInTank(0).is(carriedCap.getFluidInTank(0).getFluid());
		}
		return false;
	}
	
	@Override
	protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, int x, int y, @Nullable String countString) {
		if (slot instanceof FakeSlot fakeSlot && !fakeSlot.isDisplayOnly()) {
			FilterEntry entry = menu.getFilterEntries().get(fakeSlot.getSlotIndex());
			if (entry instanceof ItemTagEntry itemTagEntry) {
				if (!ItemRendererHelper.renderItemCarousel(guiGraphics, Ingredient.of(itemTagEntry.getTag()).getItems(), x, y,
					0x8054FFA3, 1.0F)) {
					super.renderSlotContents(guiGraphics, itemstack, slot, x, y, countString);
				}
			} else if (entry instanceof FluidStackEntry fluidStackEntry) {
				if (!FluidRendererHelper.renderFluidStack(guiGraphics, fluidStackEntry.getFilterStack(),
					x, y, 16, 16)) {
					super.renderSlotContents(guiGraphics, itemstack, slot, x, y, countString);
				}
			} else if (entry instanceof FluidTagEntry fluidTagEntry) {
				if (!FluidRendererHelper.renderFluidStackCarousel(guiGraphics, FluidIngredient.of(fluidTagEntry.getTag(), 1000).getFluidStacks(),
					x, y, 16, 16)) {
					super.renderSlotContents(guiGraphics, itemstack, slot, x, y, countString);
				}
			} else {
				if (convertingSlot == slot) {
					guiGraphics.fill(x, y, x + 16, y + 16, 0x80FF0000);
				}
				super.renderSlotContents(guiGraphics, itemstack, slot, x, y, countString);
			}
		} else {
			super.renderSlotContents(guiGraphics, itemstack, slot, x, y, countString);
		}
	}
	
	private void updateItem(int menuId, int slotIndex, ItemStack stack) {
		Channel.sendToServer(new UpdateFakeSlotToServer(menuId, slotIndex, stack));
	}
	private void updateFluid(int menuId, int slotIndex, FluidStack stack) {
		Channel.sendToServer(new FluidStackFilterToServer(menuId, slotIndex, stack));
	}
}

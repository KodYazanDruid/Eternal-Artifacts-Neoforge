package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.render.FluidRendererHelper;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.FluidStackFilterToServer;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FilterSlotWidget extends SlotWidget {
	@Getter
	private final FilterFakeSlot filterSlot;
	private final Supplier<FilterEntry> filterEntrySupplier;
	@Nullable
	private Consumer<FilterEntry> onFilterChanged;
	@Nullable
	private BiConsumer<FilterSlotWidget, Either<ItemStack, FluidStack>> onRightClick;
	@Setter
	private boolean highlighted = false;
	
	public FilterSlotWidget(FilterFakeSlot slot, Supplier<FilterEntry> filterEntrySupplier) {
		super(slot);
		this.filterSlot = slot;
		this.filterEntrySupplier = filterEntrySupplier;
	}
	
	public FilterSlotWidget setOnFilterChanged(Consumer<FilterEntry> onFilterChanged) {
		this.onFilterChanged = onFilterChanged;
		return this;
	}
	
	public FilterSlotWidget setOnRightClick(BiConsumer<FilterSlotWidget, Either<ItemStack, FluidStack>> onRightClick) {
		this.onRightClick = onRightClick;
		return this;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button) {
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.screen instanceof AbstractContainerScreen<?> containerScreen)) {
			return;
		}
		AbstractContainerMenu menu = containerScreen.getMenu();
		
		ItemStack carried = menu.getCarried();
		int slotIndex = filterSlot.getSlotIndex();
		int containerId = menu.containerId;
		
		Optional<FluidStack> fluidContained = FluidUtil.getFluidContained(carried);
		boolean hasPlaceableFluid = fluidContained.isPresent() &&
			!fluidContained.get().isEmpty() &&
			!fluidContained.get().getFluid().defaultFluidState().createLegacyBlock().isEmpty();
		boolean isBlockItem = carried.getItem() instanceof BlockItem;
		
		if (!carried.isEmpty() && !isBlockItem && !hasPlaceableFluid) {
			return;
		}
		
		if (button == 1) {
			handleRightClick(carried);
			return;
		}
		
		if (!carried.isEmpty()) {
			FluidStack fluidInItem = fluidContained.orElse(FluidStack.EMPTY);
			if (isBlockItem && !fluidInItem.isEmpty()) {
				FilterEntry currentEntry = filterEntrySupplier.get();
				if (currentEntry instanceof ItemStackEntry ise && !ise.isEmpty()) {
					FluidStack existingFluid = FluidUtil.getFluidContained(ise.getFilterStack()).orElse(FluidStack.EMPTY);
					if (!existingFluid.isEmpty() && existingFluid.getFluid().isSame(fluidInItem.getFluid())) {
						FluidStackEntry newEntry = new FluidStackEntry(fluidInItem.copy(), true);
						filterSlot.setFilter(newEntry);
						filterSlot.set(ItemStack.EMPTY);
						Channel.sendToServer(new FluidStackFilterToServer(containerId, slotIndex, fluidInItem.copy()));
						notifyFilterChanged(newEntry);
						return;
					}
				}
				ItemStack filterStack = carried.copyWithCount(1);
				ItemStackEntry newEntry = new ItemStackEntry(filterStack, true);
				filterSlot.setFilter(newEntry);
				filterSlot.set(filterStack);
				Channel.sendToServer(new UpdateFakeSlotToServer(containerId, slotIndex, filterStack));
				notifyFilterChanged(newEntry);
			} else if (!fluidInItem.isEmpty()) {
				FluidStackEntry newEntry = new FluidStackEntry(fluidInItem.copy(), true);
				filterSlot.setFilter(newEntry);
				filterSlot.set(ItemStack.EMPTY);
				Channel.sendToServer(new FluidStackFilterToServer(containerId, slotIndex, fluidInItem.copy()));
				notifyFilterChanged(newEntry);
			} else {
				ItemStack filterStack = carried.copyWithCount(1);
				ItemStackEntry newEntry = new ItemStackEntry(filterStack, true);
				filterSlot.setFilter(newEntry);
				filterSlot.set(filterStack);
				Channel.sendToServer(new UpdateFakeSlotToServer(containerId, slotIndex, filterStack));
				notifyFilterChanged(newEntry);
			}
		} else {
			filterSlot.setFilter(ItemFilterEntry.Empty.create(true));
			filterSlot.set(ItemStack.EMPTY);
			Channel.sendToServer(new UpdateFakeSlotToServer(containerId, slotIndex, ItemStack.EMPTY));
			notifyFilterChanged(ItemFilterEntry.Empty.create(true));
		}
	}
	
	private void handleRightClick(ItemStack carried) {
		if (onRightClick == null) return;
		
		if (!carried.isEmpty()) {
			FluidStack fluidInItem = FluidUtil.getFluidContained(carried).orElse(FluidStack.EMPTY);
			if (!fluidInItem.isEmpty()) {
				onRightClick.accept(this, Either.right(fluidInItem.copy()));
			} else {
				onRightClick.accept(this, Either.left(carried.copy()));
			}
		} else {
			FilterEntry entry = filterEntrySupplier.get();
			if (entry instanceof ItemStackEntry ise && !ise.isEmpty()) {
				onRightClick.accept(this, Either.left(ise.getFilterStack().copy()));
			} else if (entry instanceof FluidStackEntry fse && !fse.isEmpty()) {
				onRightClick.accept(this, Either.right(fse.getFilterStack().copy()));
			}
		}
	}
	
	private void notifyFilterChanged(FilterEntry entry) {
		if (onFilterChanged != null) {
			onFilterChanged.accept(entry);
		}
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mx, int my, float partialTick) {
		if (filterSlot.isActive()) {
			gui.pose().pushPose();
			gui.blitSprite(new ResourceLocation("container/slot"), getX() - 1, getY() - 1, 0, 18, 18);
			renderFilterSlot(gui, mx, my);
			gui.pose().popPose();
		}
	}
	
	protected void renderFilterSlot(GuiGraphics gui, int mouseX, int mouseY) {
		FilterEntry entry = filterEntrySupplier.get();
		int x = getX();
		int y = getY();
		
		if (highlighted) {
			gui.fill(x, y, x + 16, y + 16, 0x80FF0000);
		}
		
		if (entry instanceof ItemStackEntry itemStackEntry) {
			ItemStack stack = itemStackEntry.getFilterStack();
			if (!stack.isEmpty()) {
				renderSlotContents(gui, stack, filterSlot, x, y, null);
			}
		} else if (entry instanceof ItemTagEntry itemTagEntry) {
			ItemStack[] items = Ingredient.of(itemTagEntry.getTag()).getItems();
			if (!ItemRendererHelper.renderItemCarousel(gui, items, x, y, 0x8054FFA3, 1.0F)) {
				renderSlotContents(gui, filterSlot.getItem(), filterSlot, x, y, null);
			}
		} else if (entry instanceof FluidStackEntry fluidStackEntry) {
			FluidStack fluidStack = fluidStackEntry.getFilterStack();
			if (!fluidStack.isEmpty()) {
				FluidRendererHelper.renderFluidStack(gui, fluidStack, x, y, 16, 16);
			}
		} else if (entry instanceof FluidTagEntry fluidTagEntry) {
			FluidStack[] fluidStacks = FluidIngredient.of(fluidTagEntry.getTag(), 1000).getFluidStacks();
			FluidRendererHelper.renderFluidStackCarousel(gui, fluidStacks, x, y, 16, 16);
		} else {
			ItemStack stack = filterSlot.getItem();
			if (!stack.isEmpty()) {
				renderSlotContents(gui, stack, filterSlot, x, y, null);
			}
		}
		
		renderSlotHighlight(gui, filterSlot, mouseX, mouseY, 0);
	}
	
	@Override
	public void renderTooltip(GuiGraphics gui, int mouseX, int mouseY, int tooltipZ) {
		if (!filterSlot.isActive() || !isMouseOver(mouseX, mouseY)) return;
		
		FilterEntry entry = filterEntrySupplier.get();
		Minecraft mc = Minecraft.getInstance();
		long tick = Minecraft.getInstance().clientTickCount;
		
		gui.pose().pushPose();
		gui.pose().translate(0, 0, tooltipZ);
		
		if (entry instanceof ItemStackEntry itemStackEntry) {
			ItemStack stack = itemStackEntry.getFilterStack();
			if (!stack.isEmpty()) {
				gui.renderTooltip(mc.font, stack, mouseX, mouseY);
			}
		} else if (entry instanceof ItemTagEntry itemTagEntry) {
			ItemStack[] items = Ingredient.of(itemTagEntry.getTag()).getItems();
			if (items.length > 0) {
				ItemStack stack = items[(int) ((tick / 20) % items.length)];
				stack.setHoverName(Component.literal(itemTagEntry.getTag().location().toString()));
				gui.renderTooltip(mc.font, stack, mouseX, mouseY);
			}
		} else if (entry instanceof FluidStackEntry fluidStackEntry) {
			FluidStack stack = fluidStackEntry.getFilterStack();
			if (!stack.isEmpty()) {
				gui.renderTooltip(mc.font, StringUtils.getTooltipFromContainerFluid(stack, mc.level, mc.options.advancedItemTooltips),
					Optional.empty(), mouseX, mouseY);
			}
		} else if (entry instanceof FluidTagEntry fluidTagEntry) {
			FluidStack[] stacks = FluidIngredient.of(fluidTagEntry.getTag(), 1000).getFluidStacks();
			if (stacks.length > 0) {
				FluidStack stack = stacks[(int) ((tick / 20) % stacks.length)];
				CompoundTag tag = stack.getOrCreateTag();
				tag.putString("EtarFluidStackName", fluidTagEntry.getTag().location().toString());
				gui.renderTooltip(mc.font, StringUtils.getTooltipFromContainerFluid(stack, mc.level, mc.options.advancedItemTooltips),
					Optional.empty(),mouseX, mouseY);
			}
		} else {
			super.renderTooltip(gui, mouseX, mouseY, tooltipZ);
		}
		
		gui.pose().popPose();
	}
}

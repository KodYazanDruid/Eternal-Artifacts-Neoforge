package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.render.FluidRendererHelper;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import com.sonamorningstar.eternalartifacts.container.slot.FilterFakeSlot;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import com.sonamorningstar.eternalartifacts.network.*;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FilterSlotWidget extends SlotWidget {
	@Getter
	private final FilterFakeSlot filterSlot;
	@Setter
	@Getter
	private FilterEntry filterEntry;
	@Nullable
	private Consumer<FilterEntry> onFilterChanged;
	@Nullable
	private BiConsumer<FilterSlotWidget, Either<ItemStack, FluidStack>> onRightClick;
	@Setter
	private boolean highlighted = false;
	
	@Setter
	private boolean isDraggingOnRV = false;
	private final int dragDropHighlightColor = 0x80FFFF00;
	
	private static final int ITEM_STACK_HIGHLIGHT = 0x8054FFA3;  // Yeşil
	private static final int ITEM_TAG_HIGHLIGHT = 0xC040CC80;    // Koyu yeşil
	private static final int BLOCK_STATE_HIGHLIGHT = 0x80FFA354; // Turuncu
	private static final int BLOCK_TAG_HIGHLIGHT = 0xC0CC8040;   // Koyu turuncu
	
	public FilterSlotWidget(FilterFakeSlot slot, FilterEntry filterEntry) {
		super(slot);
		this.filterSlot = slot;
		this.filterEntry = filterEntry;
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
		
		if (button == 1) {
			handleRightClick(carried);
			return;
		}
		
		if (!carried.isEmpty()) {
			if (filterEntry instanceof ItemFilterEntry) {
				ItemStack filterStack = carried.copyWithCount(1);
				ItemStackEntry newEntry = new ItemStackEntry(filterStack, true);
				filterSlot.setFilter(newEntry);
				filterSlot.set(filterStack);
				Channel.sendToServer(new UpdateFakeSlotToServer(containerId, slotIndex, filterStack));
				notifyFilterChanged(newEntry);
			} else if (filterEntry instanceof BlockFilterEntry && carried.getItem() instanceof BlockItem blockItem) {
				BlockState blockState = blockItem.getBlock().defaultBlockState();
				BlockStateEntry newEntry = BlockStateEntry.matchBlockOnly(blockState);
				filterSlot.setFilter(newEntry);
				filterSlot.set(carried.copyWithCount(1));
				Channel.sendToServer(new BlockStateFilterToServer(containerId, slotIndex, blockState));
				notifyFilterChanged(newEntry);
			} else if (filterEntry instanceof FluidFilterEntry) {
				FluidStack fluidInItem = FluidUtil.getFluidContained(carried).orElse(FluidStack.EMPTY);
				if (!fluidInItem.isEmpty()) {
					FluidStackEntry newEntry = new FluidStackEntry(fluidInItem.copy(), true);
					filterSlot.setFilter(newEntry);
					filterSlot.set(ItemStack.EMPTY);
					Channel.sendToServer(new FluidStackFilterToServer(containerId, slotIndex, fluidInItem.copy()));
					notifyFilterChanged(newEntry);
				}
			}
		} else {
			if (filterEntry instanceof ItemFilterEntry) {
				filterSlot.setFilter(ItemStackEntry.EMPTY);
				Channel.sendToServer(new UpdateFakeSlotToServer(containerId, slotIndex, ItemStack.EMPTY));
				notifyFilterChanged(ItemStackEntry.EMPTY);
			} else if (filterEntry instanceof BlockFilterEntry) {
				filterSlot.setFilter(BlockStateEntry.EMPTY);
				Channel.sendToServer(new BlockStateFilterToServer(containerId, slotIndex, null));
				notifyFilterChanged(BlockStateEntry.EMPTY);
			} else if (filterEntry instanceof FluidFilterEntry) {
				filterSlot.setFilter(FluidStackEntry.EMPTY);
				Channel.sendToServer(new FluidStackFilterToServer(containerId, slotIndex, FluidStack.EMPTY));
				notifyFilterChanged(FluidStackEntry.EMPTY);
			}
			filterSlot.set(ItemStack.EMPTY);
		}
	}

	private void handleRightClick(ItemStack carried) {
		if (onRightClick == null) return;
		if (!carried.isEmpty()) {
			if (filterEntry instanceof ItemFilterEntry) {
				onRightClick.accept(this, Either.left(carried.copy()));
			} else if (filterEntry instanceof BlockFilterEntry && carried.getItem() instanceof BlockItem blockItem) {
				onRightClick.accept(this, Either.left(new ItemStack(blockItem)));
			} else if (filterEntry instanceof FluidFilterEntry) {
				onRightClick.accept(this, Either.right(FluidUtil.getFluidContained(carried).orElse(FluidStack.EMPTY).copy()));
			}
		} else {
			if (filterEntry instanceof ItemStackEntry itemEntry && !itemEntry.isEmpty()) {
				onRightClick.accept(this, Either.left(itemEntry.getFilterStack().copy()));
			} else if (filterEntry instanceof BlockStateEntry blockEntry && !blockEntry.isEmpty() && blockEntry.getFilterState() != null) {
				Item item = blockEntry.getFilterState().getBlock().asItem();
				if (item != Items.AIR) onRightClick.accept(this, Either.left(new ItemStack(item)));
			} else if (filterEntry instanceof FluidStackEntry fluidEntry && !fluidEntry.isEmpty()) {
				onRightClick.accept(this, Either.right(fluidEntry.getFilterStack().copy()));
			}
			filterSlot.set(ItemStack.EMPTY);
		}
	}
	
	private void notifyFilterChanged(FilterEntry entry) {
		if (onFilterChanged != null) {
			filterEntry = entry;
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
		int x = getX();
		int y = getY();
		
		if (highlighted) {
			gui.fill(x, y, x + 16, y + 16, 0x80FF0000);
		}
		
		if (filterEntry instanceof ItemStackEntry itemStackEntry) {
			ItemStack stack = itemStackEntry.getFilterStack();
			if (!stack.isEmpty()) {
				gui.fill(x, y, x + 16, y + 16, ITEM_STACK_HIGHLIGHT);
				renderSlotContents(gui, stack, filterSlot, x, y, null);
			}
		} else if (filterEntry instanceof ItemTagEntry itemTagEntry) {
			ItemStack[] items = Ingredient.of(itemTagEntry.getTag()).getItems();
			if (!ItemRendererHelper.renderItemCarousel(gui, items, x, y, ITEM_TAG_HIGHLIGHT, 1.0F)) {
				renderSlotContents(gui, filterSlot.getItem(), filterSlot, x, y, null);
			}
		} else if (filterEntry instanceof FluidStackEntry fluidStackEntry) {
			FluidStack fluidStack = fluidStackEntry.getFilterStack();
			if (!fluidStack.isEmpty()) {
				FluidRendererHelper.renderFluidStack(gui, fluidStack, x, y, 16, 16);
			}
		} else if (filterEntry instanceof FluidTagEntry fluidTagEntry) {
			FluidStack[] fluidStacks = FluidIngredient.of(fluidTagEntry.getTag(), 1000).getFluidStacks();
			FluidRendererHelper.renderFluidStackCarousel(gui, fluidStacks, x, y, 16, 16);
		} else if (filterEntry instanceof BlockStateEntry blockStateEntry) {
			BlockState state = blockStateEntry.getFilterState();
			if (state != null && !state.isAir()) {
				gui.fill(x, y, x + 16, y + 16, BLOCK_STATE_HIGHLIGHT);
				ItemStack blockStack = new ItemStack(state.getBlock().asItem());
				if (!blockStack.isEmpty()) {
					renderSlotContents(gui, blockStack, filterSlot, x, y, null);
				} else {
					gui.blit(new ResourceLocation("textures/item/barrier.png"),
						x, y, 0, 0, 16, 16, 16, 16);
				}
			}
		} else if (filterEntry instanceof BlockTagEntry blockTagEntry) {
			Block[] blocks = BuiltInRegistries.BLOCK.stream()
				.filter(block -> block.defaultBlockState().is(blockTagEntry.getTag()))
				.toArray(Block[]::new);
			if (blocks.length > 0) {
				gui.fill(x, y, x + 16, y + 16, BLOCK_TAG_HIGHLIGHT);
				ItemStack[] items = new ItemStack[blocks.length];
				for (int i = 0; i < blocks.length; i++) {
					items[i] = new ItemStack(blocks[i].asItem());
				}
				ItemRendererHelper.renderItemCarousel(gui, items, x, y, 0, 1.0F);
			}
		} else {
			ItemStack stack = filterSlot.getItem();
			if (!stack.isEmpty()) {
				renderSlotContents(gui, stack, filterSlot, x, y, null);
			}
		}
		
		if (isDraggingOnRV) {
			gui.fill(RenderType.guiOverlay(), x, y, x + 16, y + 16, dragDropHighlightColor);
			isDraggingOnRV = false;
		}
		
		renderSlotHighlight(gui, filterSlot, mouseX, mouseY, 0);
	}
	
	@Override
	public void renderTooltip(GuiGraphics gui, int mouseX, int mouseY, int tooltipZ) {
		if (!filterSlot.isActive() || !isMouseOver(mouseX, mouseY)) return;
		
		Minecraft mc = Minecraft.getInstance();
		long tick = Minecraft.getInstance().clientTickCount;
		
		gui.pose().pushPose();
		gui.pose().translate(0, 0, tooltipZ);
		
		if (filterEntry instanceof ItemStackEntry itemStackEntry) {
			ItemStack stack = itemStackEntry.getFilterStack();
			if (!stack.isEmpty()) {
				gui.renderTooltip(mc.font, stack, mouseX, mouseY);
			}
		} else if (filterEntry instanceof ItemTagEntry itemTagEntry) {
			ItemStack[] items = Ingredient.of(itemTagEntry.getTag()).getItems();
			if (items.length > 0) {
				ItemStack stack = items[(int) ((tick / 20) % items.length)];
				stack.setHoverName(Component.literal(itemTagEntry.getTag().location().toString()));
				gui.renderTooltip(mc.font, stack, mouseX, mouseY);
			}
		} else if (filterEntry instanceof FluidStackEntry fluidStackEntry) {
			FluidStack stack = fluidStackEntry.getFilterStack();
			if (!stack.isEmpty()) {
				gui.renderTooltip(mc.font, StringUtils.getTooltipFromContainerFluid(stack, mc.level, mc.options.advancedItemTooltips),
					Optional.empty(), mouseX, mouseY);
			}
		} else if (filterEntry instanceof FluidTagEntry fluidTagEntry) {
			FluidStack[] stacks = FluidIngredient.of(fluidTagEntry.getTag(), 1000).getFluidStacks();
			if (stacks.length > 0) {
				FluidStack stack = stacks[(int) ((tick / 20) % stacks.length)];
				CompoundTag tag = stack.getOrCreateTag();
				tag.putString("EtarFluidStackName", fluidTagEntry.getTag().location().toString());
				gui.renderTooltip(mc.font, StringUtils.getTooltipFromContainerFluid(stack, mc.level, mc.options.advancedItemTooltips),
					Optional.empty(),mouseX, mouseY);
			}
		} else if (filterEntry instanceof BlockStateEntry blockStateEntry) {
			BlockState state = blockStateEntry.getFilterState();
			if (state != null && !state.isAir()) {
				ItemStack blockStack = new ItemStack(state.getBlock().asItem());
				if (!blockStack.isEmpty()) {
					gui.renderTooltip(mc.font, blockStack, mouseX, mouseY);
				} else {
					gui.renderTooltip(mc.font, StringUtils.getTooltipForBlockState(null, state, mc.level, mc.options.advancedItemTooltips),
						Optional.empty(), mouseX, mouseY);
				}
			}
		} else if (filterEntry instanceof BlockTagEntry blockTagEntry) {
			Block[] blocks = BuiltInRegistries.BLOCK.stream()
				.filter(block -> block.defaultBlockState().is(blockTagEntry.getTag()))
				.toArray(Block[]::new);
			if (blocks.length > 0) {
				Block block = blocks[(int) ((tick / 20) % blocks.length)];
				ItemStack stack = new ItemStack(block.asItem());
				stack.setHoverName(Component.literal(blockTagEntry.getTag().location().toString()));
				gui.renderTooltip(mc.font, stack, mouseX, mouseY);
			}
		} else {
			super.renderTooltip(gui, mouseX, mouseY, tooltipZ);
		}
		
		gui.pose().popPose();
	}
}

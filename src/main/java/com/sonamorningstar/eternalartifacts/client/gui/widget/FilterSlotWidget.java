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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
	@Setter
	private boolean isBlockFilter = false;
	
	@Setter
	private boolean isDraggingOnRV = false;
	private final int dragDropHighlightColor = 0x80FFFF00;
	
	private static final int ITEM_STACK_HIGHLIGHT = 0x8054FFA3;  // Yeşil
	private static final int ITEM_TAG_HIGHLIGHT = 0xC040CC80;    // Koyu yeşil
	private static final int BLOCK_STATE_HIGHLIGHT = 0x80FFA354; // Turuncu
	private static final int BLOCK_TAG_HIGHLIGHT = 0xC0CC8040;   // Koyu turuncu
	
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
		
		if (!carried.isEmpty()) {
			if (!isBlockItem && !hasPlaceableFluid) return;
		}
		
		if (button == 1) {
			handleRightClick(carried);
			return;
		}
		
		if (!carried.isEmpty()) {
			FluidStack fluidInItem = fluidContained.orElse(FluidStack.EMPTY);
			
			if (isBlockFilter) {
				if (isBlockItem) {
					BlockItem blockItem = (BlockItem) carried.getItem();
					BlockState blockState = blockItem.getBlock().defaultBlockState();
					
					if (!fluidInItem.isEmpty()) {
						FilterEntry currentEntry = filterEntrySupplier.get();
						if (currentEntry instanceof BlockStateEntry bse && !bse.isEmpty() && bse.getFilterState() != null) {
							if (bse.getFilterState().getBlock() == blockState.getBlock()) {
								FluidStackEntry newEntry = new FluidStackEntry(fluidInItem.copy(), true);
								filterSlot.setFilter(newEntry);
								filterSlot.set(ItemStack.EMPTY);
								Channel.sendToServer(new FluidStackFilterToServer(containerId, slotIndex, fluidInItem.copy()));
								notifyFilterChanged(newEntry);
								return;
							}
						}
					}
					
					BlockStateEntry newEntry = BlockStateEntry.matchBlockOnly(blockState);
					filterSlot.setFilter(newEntry);
					filterSlot.set(ItemStack.EMPTY);
					Channel.sendToServer(new BlockStateFilterToServer(containerId, slotIndex, blockState));
					notifyFilterChanged(newEntry);
				} else if (!fluidInItem.isEmpty()) {
					FluidStackEntry newEntry = new FluidStackEntry(fluidInItem.copy(), true);
					filterSlot.setFilter(newEntry);
					filterSlot.set(ItemStack.EMPTY);
					Channel.sendToServer(new FluidStackFilterToServer(containerId, slotIndex, fluidInItem.copy()));
					notifyFilterChanged(newEntry);
				}
			} else {
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
			}
		} else {
			if (isBlockFilter) {
				filterSlot.setFilter(BlockFilterEntry.Empty.create(true));
				Channel.sendToServer(new BlockStateFilterToServer(containerId, slotIndex, null));
				notifyFilterChanged(BlockFilterEntry.Empty.create(true));
			} else {
				filterSlot.setFilter(ItemFilterEntry.Empty.create(true));
				Channel.sendToServer(new UpdateFakeSlotToServer(containerId, slotIndex, ItemStack.EMPTY));
				notifyFilterChanged(ItemFilterEntry.Empty.create(true));
			}
			filterSlot.set(ItemStack.EMPTY);
		}
	}
	
	private void handleRightClick(ItemStack carried) {
		FilterEntry entry = filterEntrySupplier.get();
		if (!carried.isEmpty()) {
			if (carried.getItem() instanceof BlockItem blockItem && onRightClick != null) {
				if (entry instanceof ItemStackEntry ise && !ise.isEmpty()) {
					FluidStack fluidInFilter = FluidUtil.getFluidContained(ise.getFilterStack()).orElse(FluidStack.EMPTY);
					if (!fluidInFilter.isEmpty()) {
						onRightClick.accept(this, Either.right(fluidInFilter.copy()));
						return;
					}
				} else if (entry instanceof BlockStateEntry bse && !bse.isEmpty()) {
					BlockState state = bse.getFilterState();
					if (state != null && !state.isAir()) {
						ItemStack carriedBlockStack = new ItemStack(bse.getFilterState().getBlock().asItem());
						FluidStack fluidInFilter = FluidUtil.getFluidContained(carriedBlockStack).orElse(FluidStack.EMPTY);
						FluidStack fluidInCarried = FluidUtil.getFluidContained(carried).orElse(FluidStack.EMPTY);
						if (!fluidInFilter.isEmpty() && !fluidInCarried.isEmpty() && fluidInFilter.isFluidEqual(fluidInCarried)) {
							onRightClick.accept(this, Either.right(fluidInFilter.copy()));
							return;
						}
					}
				} else {
					ItemStack blockStack = new ItemStack(blockItem);
					if (!blockStack.isEmpty()) {
						onRightClick.accept(this, Either.left(blockStack));
						return;
					}
				}
			}
			FluidStack fluidInItem = FluidUtil.getFluidContained(carried).orElse(FluidStack.EMPTY);
			if (onRightClick != null) {
				if (!fluidInItem.isEmpty()) {
					onRightClick.accept(this, Either.right(fluidInItem.copy()));
				} else {
					onRightClick.accept(this, Either.left(carried.copy()));
				}
			}
		} else {
			if (entry instanceof ItemStackEntry ise && !ise.isEmpty() && onRightClick != null) {
				onRightClick.accept(this, Either.left(ise.getFilterStack().copy()));
			} else if (entry instanceof BlockStateEntry bse && !bse.isEmpty() && onRightClick != null) {
				BlockState state = bse.getFilterState();
				if (state != null && !state.isAir()) {
					ItemStack blockStack = new ItemStack(state.getBlock().asItem());
					if (!blockStack.isEmpty()) {
						onRightClick.accept(this, Either.left(blockStack));
					}
				}
			}else if (entry instanceof FluidStackEntry fse && !fse.isEmpty() && onRightClick != null) {
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
				gui.fill(x, y, x + 16, y + 16, ITEM_STACK_HIGHLIGHT);
				renderSlotContents(gui, stack, filterSlot, x, y, null);
			}
		} else if (entry instanceof ItemTagEntry itemTagEntry) {
			ItemStack[] items = Ingredient.of(itemTagEntry.getTag()).getItems();
			if (!ItemRendererHelper.renderItemCarousel(gui, items, x, y, ITEM_TAG_HIGHLIGHT, 1.0F)) {
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
		} else if (entry instanceof BlockStateEntry blockStateEntry) {
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
		} else if (entry instanceof BlockTagEntry blockTagEntry) {
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
		} else if (entry instanceof BlockStateEntry blockStateEntry) {
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
		} else if (entry instanceof BlockTagEntry blockTagEntry) {
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

package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityPredicateEntry.EntityPredicate;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

/**
 * Helper class for creating entity filter panels.
 * Can be used in any screen that needs entity filtering functionality.
 */
public class EntityFilterPanel {
	
	@Getter
	private final SimpleDraggablePanel panel;
	@Getter
	private final ScrollablePanel<ScrollablePanelComponent> predicateList;
	private final Supplier<EntityPredicateEntry> filterSupplier;
	private final Predicate<EntityPredicate> filterValidator;
	private final Font font;
	@Setter
	@Nullable
	private Consumer<EntityPredicateEntry> onFilterChanged;
	private int guiTint = 0xffffffff;
	
	public EntityFilterPanel(int x, int y, int screenWidth, int screenHeight,
							 Supplier<EntityPredicateEntry> filterSupplier, Predicate<EntityPredicate> filterValidator, Font font) {
		this.filterSupplier = filterSupplier;
		this.filterValidator = filterValidator;
		this.font = font;
		
		panel = new SimpleDraggablePanel(
			Component.translatable("gui.eternalartifacts.entity_filter"),
			x, y, 160, 140,
			SimpleDraggablePanel.Bounds.of(0, 0, screenWidth, screenHeight)
		);
		panel.visible = false;
		panel.active = false;
		panel.addClosingButton();
		
		predicateList = new ScrollablePanel<>(panel.getX() + 4, panel.getY() + 17, 144, 100, 10);
		
		panel.addChildren((fx, fy, fW, fH) -> {
			var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
				EntityPredicateEntry filter = filterSupplier.get();
				filter.setWhitelist(!filter.isWhitelist());
				notifyFilterChanged();
				b.setTextures(getListIcon());
				updatePredicateColors();
			}).bounds(fx + 8, fy + 122, 16, 16);
			bld.addTooltipHover(() -> filterSupplier.get().isWhitelist() ?
				ModConstants.GUI.withSuffixTranslatable("whitelist").withStyle(style -> style.withColor(0x55FF55)) :
				ModConstants.GUI.withSuffixTranslatable("blacklist").withStyle(style -> style.withColor(0xFF5555)));
			var button = bld.build();
			button.setTextures(getListIcon());
			return button;
		});
		
		panel.addChildren((fx, fy, fW, fH) -> {
			var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
				EntityPredicateEntry filter = filterSupplier.get();
				EntityPredicateEntry.PredicateMode currentMode = filter.getMode();
				EntityPredicateEntry.PredicateMode newMode = currentMode == EntityPredicateEntry.PredicateMode.ANY ?
					EntityPredicateEntry.PredicateMode.ALL : EntityPredicateEntry.PredicateMode.ANY;
				filter.setMode(newMode);
				notifyFilterChanged();
				b.setTextures(getModeIcon());
			}).bounds(fx + 26, fy + 122, 16, 16);
			bld.addTooltipHover(() -> filterSupplier.get().getMode() == EntityPredicateEntry.PredicateMode.ANY ?
				ModConstants.GUI.withSuffixTranslatable("mode_any").withStyle(style -> style.withColor(0x55AAFF)) :
				ModConstants.GUI.withSuffixTranslatable("mode_all").withStyle(style -> style.withColor(0xFFAA55)));
			var button = bld.build();
			button.setTextures(getModeIcon());
			return button;
		});
		
		panel.addChildren((fx, fy, fW, fH) -> {
			var bld = SpriteButton.builderNoTexture(Component.empty(), (b, i) -> {
				filterSupplier.get().clearPredicates();
				notifyFilterChanged();
				updatePredicateColors();
			}).bounds(fx + fW - 26, fy + 122, 16, 16);
			bld.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("clear_all").withStyle(style -> style.withColor(0xFF5555)));
			var button = bld.build();
			button.setTextures(new ResourceLocation(MODID, "textures/gui/sprites/sided_buttons/deny.png"));
			return button;
		});
		
		buildPredicateList();
		
		panel.addChildren((fx, fy, fW, fH) -> {
			predicateList.setX(fx + 4);
			predicateList.setY(fy + 17);
			return predicateList;
		});
	}
	
	public EntityFilterPanel withColor(int color) {
		this.guiTint = color;
		panel.setColor(color);
		return this;
	}
	
	public EntityFilterPanel withOnFilterChanged(Consumer<EntityPredicateEntry> callback) {
		this.onFilterChanged = callback;
		return this;
	}
	
	public void toggle() {
		panel.toggle();
	}
	
	public void show() {
		panel.visible = true;
		panel.active = true;
	}
	
	public void hide() {
		panel.visible = false;
		panel.active = false;
	}
	
	public SpriteButton createFilterButton(int x, int y) {
		return SpriteButton.builder(Component.empty(), (b, i) -> toggle(),
				new ResourceLocation(MODID, "textures/item/machine_item_filter.png"))
			.bounds(x, y, 16, 16).build();
	}
	
	private void buildPredicateList() {
		predicateList.clearChildren();
		
		EntityPredicate[] predicates = EntityPredicate.values();
		int idx = 0;
		for (EntityPredicate predicate : predicates) {
			if (!filterValidator.test(predicate)) continue;
			idx++;
			int finalI = idx - 1;
			predicateList.addChild((x, y, width, height) -> {
				boolean isActive = filterSupplier.get().hasPredicate(predicate);
				int baseColor = isActive ? 0xff2E7D32 : 0xff2C2F33;
				int hoverColor = isActive ? 0xff43A047 : 0xff3C8DBC;
				int focusColor = isActive ? 0xff66BB6A : 0xff68C8FA;
				
				int barPadding = predicateList.scrollbarVisible() ? -predicateList.scrollbarWidth() : 0;
				var comp = new ScrollablePanelComponent(
					x, y + finalI * 18, width + barPadding, 16, predicateList,
					(mx, my, btn) -> togglePredicate(predicate),
					finalI, font, predicate.getDisplayName(),
					baseColor, hoverColor, focusColor
				);
				comp.setRenderIcon(false);
				return comp;
			});
		}
		predicateList.reCalcInnerHeight();
	}
	
	public void updatePredicateColors() {
		EntityPredicate[] predicates = EntityPredicate.values();
		var children = predicateList.getChildren();
		for (int i = 0; i < children.size() && i < predicates.length; i++) {
			var comp = children.get(i);
			boolean isActive = filterSupplier.get().hasPredicate(predicates[i]);
			int baseColor = isActive ? 0xff2E7D32 : 0xff2C2F33;
			int hoverColor = isActive ? 0xff43A047 : 0xff3C8DBC;
			int focusColor = isActive ? 0xff66BB6A : 0xff68C8FA;
			comp.setColors(baseColor, hoverColor, focusColor);
		}
	}
	
	private void togglePredicate(EntityPredicate predicate) {
		filterSupplier.get().togglePredicate(predicate);
		notifyFilterChanged();
		updatePredicateColors();
	}
	
	private void notifyFilterChanged() {
		if (onFilterChanged != null) {
			onFilterChanged.accept(filterSupplier.get());
		}
	}
	
	private ResourceLocation getListIcon() {
		return filterSupplier.get().isWhitelist() ?
			new ResourceLocation("textures/item/paper.png") :
			new ResourceLocation(MODID, "textures/item/carbon_paper.png");
	}
	
	private ResourceLocation getModeIcon() {
		return filterSupplier.get().getMode() == EntityPredicateEntry.PredicateMode.ANY ?
			new ResourceLocation(MODID, "textures/item/encumbator.png") :
			new ResourceLocation(MODID, "textures/item/holy_dagger.png");
	}
}

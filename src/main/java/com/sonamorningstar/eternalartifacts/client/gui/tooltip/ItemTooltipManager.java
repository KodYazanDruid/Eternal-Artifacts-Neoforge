package com.sonamorningstar.eternalartifacts.client.gui.tooltip;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTooltipManager {
	public static final Map<Item, Component> charmDescriptions = new HashMap<>();
	public static final Map<Item, Component> versatileDescriptions = new HashMap<>();
	private static boolean initialized = false;
	
	public static void bootstrap() {
		if (initialized) {
			EternalArtifacts.LOGGER.warn("ItemTooltipManager attempted to initialize more than once. This is not allowed.");
			return;
		} else initialized = true;
		
		registerCharmTooltip(ModItems.FINAL_CUT.get(), (int) (Config.FINAL_CUT_EXECUTE_THRESHOLD.get() * 100));
		registerCharmTooltip(ModItems.HOLY_DAGGER.get(), 50, ModEffects.DIVINE_PROTECTION.get().getDisplayName(), 30);
		registerCharmTooltip(ModItems.MEDKIT.get(), MobEffects.REGENERATION.getDisplayName());
		registerCharmTooltip(ModItems.FROG_LEGS.get(), 2, 3, 50);
		registerCharmTooltip(ModItems.MAGIC_FEATHER.get(), ModEffects.FLIGHT.get().getDisplayName());
		registerCharmTooltip(ModItems.ENCUMBATOR.get());
		registerCharmTooltip(ModItems.HEART_NECKLACE.get(), MobEffects.REGENERATION.getDisplayName(), 10, 30);
		registerCharmTooltip(ModItems.SAGES_TALISMAN.get(), 20);
		registerCharmTooltip(ModItems.MAGIC_QUIVER.get());
		registerCharmTooltip(ModItems.MAGIC_BANE.get(),(int) (Config.MAGIC_BANE_DAMAGE_CONVERT_MULTIPLIER.get() * 100));
		registerCharmTooltip(ModItems.EMERALD_SIGNET.get(), 35);
		registerCharmTooltip(ModItems.MAGNET.get(), 5);
		registerCharmTooltip(ModItems.SKYBOUND_TREADS.get());
		registerCharmTooltip(ModItems.GALE_SASH.get());
		registerCharmTooltip(ModItems.RAINCOAT.get());
		registerCharmTooltip(ModItems.ODDLY_SHAPED_OPAL.get(), 50);
	}
	
	public static void setReload() {
		initialized = false;
		charmDescriptions.clear();
		bootstrap();
	}
	
	private static void registerCharmTooltip(Item item) {
		registerCharmTooltip(item, TranslatableContents.NO_ARGS);
	}
	private static void registerCharmTooltip(Item item, Object... args) {
		registerCharmTooltip_Internal(item,
			Component.empty()
				.append(CommonComponents.SPACE)
				.append(Component.translatable(ModConstants.TOOLTIP.withSuffix(BuiltInRegistries.ITEM.getKey(item).getPath()), args))
				.withColor(0x89CFF0)
		);
	}
	
	private static void registerCharmTooltip_Internal(Item item, Component tooltip) {
		charmDescriptions.put(item, tooltip);
	}
	
	public static void applyTooltips(ItemStack stack, List<Component> tooltips) {
		if (charmDescriptions.containsKey(stack.getItem())) {
			tooltips.add(charmDescriptions.get(stack.getItem()));
		}
		if (versatileDescriptions.containsKey(stack.getItem())) {
			tooltips.add(versatileDescriptions.get(stack.getItem()));
		}
	}
}

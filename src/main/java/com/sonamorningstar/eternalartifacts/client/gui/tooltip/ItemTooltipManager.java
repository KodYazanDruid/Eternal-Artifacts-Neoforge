package com.sonamorningstar.eternalartifacts.client.gui.tooltip;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTooltipManager {
	public static final Map<Item, Component> ITEM_DESCRIPTIONS = new HashMap<>();
	public static final Map<TagKey<Item>, Component> TAG_DESCRIPTIONS = new HashMap<>();
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
		registerCharmTooltip(ModItems.MOONGLASS_PENDANT.get(), (int) (Config.MOONGLASS_PENDANT_HEAL_MULTIPLIER.get() * 100));
		registerCharmTooltip(ModItems.EYE_OF_DESTRUCTION.get(), Config.EYES_OF_DESTRUCTION_CRIT_BONUS.get());
		registerCharmTooltip(ModItems.SANGUINE_AMULET.get(), Config.SANGUINE_AMULET_MAX_HEALTH.get(), Config.SANGUINE_AMULET_MAX_SOULS.get());
		
		registerCharmTooltip(ModTags.Items.FLINT_TOOLS, Config.FLINT_TOOLS_FIRE_CHANCE.getAsDouble() * 100, Config.FLINT_TOOLS_FIRE_DURATION.getAsInt());
		registerCharmTooltip(ModTags.Items.BONE_TOOLS, Config.BONE_TOOLS_REPAIR_PERCENTAGE.getAsDouble() * 100, NeoForgeMod.MILK_TYPE.get().getDescription());
	}
	
	public static void setReload() {
		initialized = false;
		ITEM_DESCRIPTIONS.clear();
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
				.withColor(0x75abfe)
		);
	}
	private static void registerCharmTooltip_Internal(Item item, Component tooltip) {
		ITEM_DESCRIPTIONS.put(item, tooltip);
	}
	private static void registerCharmTooltip(TagKey<Item> tag) {
		registerCharmTooltip(tag, TranslatableContents.NO_ARGS);
	}
	private static void registerCharmTooltip(TagKey<Item> tag, Object... args) {
		ResourceLocation rl = tag.location();
		String suffix = rl.getNamespace() + "." + rl.getPath();
		registerCharmTooltip_Internal(tag,
			Component.empty()
				.append(CommonComponents.SPACE)
				.append(Component.translatable(ModConstants.TOOLTIP.withSuffix(suffix), args))
				.withColor(0x75abfe)
		);
	}
	private static void registerCharmTooltip_Internal(TagKey<Item> tag, Component tooltip) {
		TAG_DESCRIPTIONS.put(tag, tooltip);
	}
	
	public static void applyTooltips(ItemStack stack, List<Component> tooltips) {
		if (ITEM_DESCRIPTIONS.containsKey(stack.getItem())) {
			tooltips.add(ITEM_DESCRIPTIONS.get(stack.getItem()));
		}
		if (TAG_DESCRIPTIONS.keySet().stream().anyMatch(stack::is)) {
			TAG_DESCRIPTIONS.keySet().stream()
				.filter(stack::is)
				.forEach(tag -> tooltips.add(TAG_DESCRIPTIONS.get(tag)));
		}
		if (versatileDescriptions.containsKey(stack.getItem())) {
			tooltips.add(versatileDescriptions.get(stack.getItem()));
		}
	}
}

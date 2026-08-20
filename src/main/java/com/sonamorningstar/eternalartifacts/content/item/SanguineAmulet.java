package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SanguineAmulet extends Item {
	public static final String SOUL_KEY = "StoredSouls";
	public static final Map<EntityType<?>, Integer> SOUL_VALUES = new HashMap<>();
	
	public SanguineAmulet(Properties properties) {
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		if (stack.hasTag() && stack.getTag().contains(SOUL_KEY)) {
			int souls = stack.hasTag() ? stack.getTag().getInt(SanguineAmulet.SOUL_KEY) : 0;
			int maxSouls = Config.SANGUINE_AMULET_MAX_SOULS.getAsInt();
			float fillPercent = (float) souls / maxSouls;
			float health = (float) (fillPercent * Config.SANGUINE_AMULET_MAX_HEALTH.getAsDouble());
			tooltipComponents.add(ModConstants.TOOLTIP.withSuffixTranslatable("sanguine_amulet.health", health));
			tooltipComponents.add(ModConstants.TOOLTIP.withSuffixTranslatable("sanguine_amulet.stored_souls", souls, maxSouls));
		} else {
			tooltipComponents.add(ModConstants.TOOLTIP.withSuffixTranslatable("sanguine_amulet.health", 0));
			tooltipComponents.add(ModConstants.TOOLTIP.withSuffixTranslatable("sanguine_amulet.stored_souls", 0, Config.SANGUINE_AMULET_MAX_SOULS.getAsInt()));
		}
	}
	
	public static int getSoulValue(EntityType<?> entityType) {
		return SOUL_VALUES.getOrDefault(entityType, 1);
	}
}

package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.item.ColoredShulkerShellItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class ShulkerShellColoringModifier extends LootModifier {
	public static final Supplier<Codec<ShulkerShellColoringModifier>> CODEC = Suppliers.memoize(()-> RecordCodecBuilder.create(instance -> codecStart(instance)
		.apply(instance, ShulkerShellColoringModifier::new)));
	public ShulkerShellColoringModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}
	
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
		if (entity instanceof Shulker shulker){
			DyeColor color = shulker.getColor();
			return generatedLoot.stream().filter(s -> s.is(Items.SHULKER_SHELL))
				.map(shell -> ColoredShulkerShellItem.getItemByColor(color))
				.collect(ObjectArrayList::new, (list, item) -> list.add(item.getDefaultInstance()), ObjectArrayList::addAll);
		}
		return generatedLoot;
	}
	
	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}

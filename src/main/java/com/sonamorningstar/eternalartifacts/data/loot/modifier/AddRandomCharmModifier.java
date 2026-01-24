package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.function.Supplier;

public class AddRandomCharmModifier extends LootModifier {
	public static final Supplier<Codec<AddRandomCharmModifier>> CODEC = Suppliers.memoize(()-> RecordCodecBuilder.create(instance -> codecStart(instance)
		.and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
		.apply(instance, AddRandomCharmModifier::new)));
	
	public static List<Item> CHARM_ITEMS;
	private final float chance;
	
	public AddRandomCharmModifier(LootItemCondition[] conditionsIn, float chance) {
		super(conditionsIn);
		this.chance = chance;
	}
	
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		if (CHARM_ITEMS == null) return generatedLoot;
		
		RandomSource random = context.getRandom();
		if (random.nextFloat() < chance) {
			ItemStack stack = CHARM_ITEMS.get(random.nextInt(CHARM_ITEMS.size())).getDefaultInstance();
			EnchantmentHelper.enchantItem(random, stack, 1 + random.nextInt(7), false);
			generatedLoot.add(stack);
		}
		return generatedLoot;
	}
	
	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}

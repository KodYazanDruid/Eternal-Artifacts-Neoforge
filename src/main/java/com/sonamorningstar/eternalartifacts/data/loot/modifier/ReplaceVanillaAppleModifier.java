package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class ReplaceVanillaAppleModifier extends LootModifier {
	public static final Supplier<Codec<ReplaceVanillaAppleModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(instance ->
		codecStart(instance).apply(instance, ReplaceVanillaAppleModifier::new)));
	
	public ReplaceVanillaAppleModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.stream().filter(stack -> stack.is(Items.APPLE)).forEach(stack -> {
			RandomSource random =  context.getRandom();
			replaceApple(random, stack, generatedLoot);
		});
		return generatedLoot;
	}
	
	private void replaceApple(RandomSource randomSource, ItemStack stack, ObjectArrayList<ItemStack> generatedLoot) {
		float chance = randomSource.nextFloat();
		stack.shrink(1);
		if (chance < 1 / 3F) {
			generatedLoot.add(Items.APPLE.getDefaultInstance());
		} else if(chance < 2 / 3F) {
			generatedLoot.add(ModItems.GREEN_APPLE.toStack());
		} else {
			generatedLoot.add(ModItems.YELLOW_APPLE.toStack());
		}
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}

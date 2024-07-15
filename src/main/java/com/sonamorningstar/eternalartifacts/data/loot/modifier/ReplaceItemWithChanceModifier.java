package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class ReplaceItemWithChanceModifier extends LootModifier {
    public static final Supplier<Codec<ReplaceItemWithChanceModifier>> CODEC = Suppliers.memoize(()-> RecordCodecBuilder.create(instance -> codecStart(instance)
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
            .and(NumberProviders.CODEC.fieldOf("numberProvider").forGetter(m -> m.numberProvider))
            .and(PrimitiveCodec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
            .apply(instance, ReplaceItemWithChanceModifier::new)));

    private final Item item;
    private final NumberProvider numberProvider;
    private final float chance;

    public ReplaceItemWithChanceModifier(LootItemCondition[] conditionsIn, Item item, NumberProvider numberProvider, float chance) {
        super(conditionsIn);
        this.item = item;
        this.numberProvider = numberProvider;
        this.chance = chance;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for(LootItemCondition condition : conditions) if(!condition.test(context)) return generatedLoot;
        ItemStack loot = new ItemStack(item, numberProvider.getInt(context));
        RandomSource random = context.getRandom();
        return chance >= random.nextFloat() ? ObjectArrayList.of(loot) : generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}

package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class ReplaceItemModifier extends LootModifier {

    public static final Supplier<Codec<ReplaceItemModifier>> CODEC = Suppliers.memoize(()-> RecordCodecBuilder.create(instance -> codecStart(instance)
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
            .and(NumberProviders.CODEC.fieldOf("numberProvider").forGetter(m -> m.numberProvider))
            .apply(instance, ReplaceItemModifier::new)));

    private final Item item;
    private final NumberProvider numberProvider;

    public ReplaceItemModifier(LootItemCondition[] conditionsIn, Item item, NumberProvider numberProvider) {
        super(conditionsIn);
        this.item = item;
        this.numberProvider = numberProvider;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for(LootItemCondition condition : conditions) if(!condition.test(context)) return generatedLoot;
        ItemStack loot = new ItemStack(item, numberProvider.getInt(context));
        return ObjectArrayList.of(loot);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}

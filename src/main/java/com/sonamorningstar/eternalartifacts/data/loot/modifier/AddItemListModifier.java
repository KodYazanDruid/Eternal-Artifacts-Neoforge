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
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.function.Supplier;

public class AddItemListModifier extends LootModifier {

    public static final Supplier<Codec<AddItemListModifier>> CODEC = Suppliers.memoize(()-> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .and(BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("item").forGetter(m -> m.itemList))
                    .apply(instance, AddItemListModifier::new)));


    private final List<Item> itemList;

    public AddItemListModifier(LootItemCondition[] conditionsIn, List<Item> item) {
        super(conditionsIn);
        this.itemList = item;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for(LootItemCondition condition : conditions)
            if(!condition.test(context)) return generatedLoot;

        itemList.forEach(i -> generatedLoot.add(i.getDefaultInstance()));

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}

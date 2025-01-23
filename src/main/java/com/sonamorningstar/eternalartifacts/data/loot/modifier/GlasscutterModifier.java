package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class GlasscutterModifier extends LootModifier {

    public static final Supplier<Codec<GlasscutterModifier>> CODEC = Suppliers.memoize(()-> RecordCodecBuilder.create(instance -> codecStart(instance)
            .apply(instance, GlasscutterModifier::new)));

    public GlasscutterModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState minedBlockState = context.getParam(LootContextParams.BLOCK_STATE);
        ItemStack stack = minedBlockState.getBlock().asItem().getDefaultInstance();
        if (!generatedLoot.contains(stack)) return ObjectArrayList.of(stack);
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {return CODEC.get();}
}

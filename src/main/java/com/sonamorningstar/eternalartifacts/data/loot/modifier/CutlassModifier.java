package com.sonamorningstar.eternalartifacts.data.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.Map;
import java.util.function.Supplier;

public class CutlassModifier extends LootModifier {
    public static final Supplier<Codec<CutlassModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(instance ->
            codecStart(instance).apply(instance, CutlassModifier::new)));

    public static final Map<EntityType<?>, Item> ENTITY_HEAD_MAP = Map.of(
            EntityType.ZOMBIE, Items.ZOMBIE_HEAD,
            EntityType.SKELETON, Items.SKELETON_SKULL,
            EntityType.CREEPER, Items.CREEPER_HEAD,
            EntityType.WITHER_SKELETON, Items.WITHER_SKELETON_SKULL,
            EntityType.PIGLIN, Items.PIGLIN_HEAD,
            EntityType.ENDER_DRAGON, Items.DRAGON_HEAD
    );

    public CutlassModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Entity target = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        Entity killer = context.getParamOrNull(LootContextParams.DIRECT_KILLER_ENTITY);
        if (!(killer instanceof LivingEntity living)) return generatedLoot;
        else if(!(living.getMainHandItem().is(ModTags.Items.TOOLS_CUTLASS))) return generatedLoot;

        EntityType<?> entityType = target != null ? target.getType() : null;
        ItemStack playerSkull = Items.PLAYER_HEAD.getDefaultInstance();
        ItemStack entityHead = ItemStack.EMPTY;
        if (target instanceof Player player) {
            GameProfile gameprofile = player.getGameProfile();
            playerSkull.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameprofile));
        } else if (target instanceof LivingEntity) {
            if (ENTITY_HEAD_MAP.containsKey(entityType)) {
                entityHead = ENTITY_HEAD_MAP.get(entityType).getDefaultInstance();
            }
        }

        if (context.getRandom().nextDouble() <= Config.CUTLASS_BEHEAD_CHANCE.get()) {
            if (entityType == EntityType.PLAYER) generatedLoot.add(playerSkull);
            else if (!entityHead.isEmpty()) generatedLoot.add(entityHead);
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {return CODEC.get();}
}

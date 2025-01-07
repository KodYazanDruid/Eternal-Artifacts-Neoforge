package com.sonamorningstar.eternalartifacts.content.item;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.capabilities.handler.INutritionHandler;
import com.sonamorningstar.eternalartifacts.core.ModCapabilities;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class FeedingCanister extends Item {
    private final FoodProperties.Builder foodPropertiesBuilder = new FoodProperties.Builder();

    public FeedingCanister(Properties props) {
        super(props);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        INutritionHandler nutrition = stack.getCapability(ModCapabilities.NutritionStorage.ITEM);
        if (nutrition != null && entity instanceof Player player) {
            FoodData foodData = player.getFoodData();
            int missingNut = 20 - foodData.getFoodLevel();
            float missingSat = 20F - foodData.getSaturationLevel();
            if (missingNut <= 0 || missingSat <= 0) return stack;
            float limitedSat = Math.min(missingSat, missingNut);
            int drainedNut = nutrition.drainNutrition(missingNut, false);
            float drainedSat = nutrition.drainSaturation(limitedSat, false);
            eat(level, player, foodData, drainedNut, drainedSat / (drainedNut * 2), stack);
        }
        return stack;
    }

    private void eat(Level level, Player player, FoodData data, int nutrition, float satMod, ItemStack food) {
        BlockPos pos = player.blockPosition();

        data.eat(nutrition, satMod);
        player.awardStat(Stats.ITEM_USED.get(food.getItem()));
        level.playSound(
            null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS,
            0.5F, level.random.nextFloat() * 0.1F + 0.9F
        );
        if (player instanceof ServerPlayer serverPlayer) CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, food);

        level.playSound(
                null,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                player.getEatingSound(food),
                SoundSource.NEUTRAL,
                1.0F,
                1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
        );
        addEatEffect(food, level, player);
        level.gameEvent(null, GameEvent.EAT, pos);
    }
    private void addEatEffect(ItemStack food, Level level, Player player) {
        Item item = food.getItem();
        FoodProperties foodProps = food.getFoodProperties(player);
        if (item.isEdible() && foodProps != null) {
            for(Pair<MobEffectInstance, Float> pair : foodProps.getEffects()) {
                if (!level.isClientSide && pair.getFirst() != null && level.random.nextFloat() < pair.getSecond()) {
                    player.addEffect(new MobEffectInstance(pair.getFirst()));
                }
            }
        }
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        INutritionHandler nutrition = stack.getCapability(ModCapabilities.NutritionStorage.ITEM);
        if (nutrition != null) return foodPropertiesBuilder.build();
        return super.getFoodProperties(stack, entity);
    }

    @Override
    public boolean isEdible() {
        FoodProperties foodProperties = foodPropertiesBuilder.build();
        return foodProperties.canAlwaysEat() || foodProperties.getNutrition() > 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        INutritionHandler nutrition = stack.getCapability(ModCapabilities.NutritionStorage.ITEM);
        if (nutrition != null) {
            tooltipComponents.add(ModConstants.TOOLTIP.withSuffixTranslatable("nutrition").append(": ").append(nutrition.getNutritionAmount() + " / " + nutrition.getMaxNutritionAmount()).withStyle(ChatFormatting.YELLOW));
            tooltipComponents.add(ModConstants.TOOLTIP.withSuffixTranslatable("saturation").append(": ").append(String.format("%.1f", nutrition.getSaturationAmount())+ " / " + nutrition.getMaxSaturationAmount()).withStyle(ChatFormatting.YELLOW));
        }
    }
}

package com.sonamorningstar.eternalartifacts.content.item.base;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.event.custom.SpellCastEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * A spell tome that fires repeatedly while the player holds right click.
 * Each tick interval, it casts the spell once. Cooldown is applied on release.
 */
public class ChanneledSpellTomeItem<S extends Spell> extends SpellTomeItem<S> {
    private final int fireInterval;

    /**
     * @param spellHolder  The spell this tome casts.
     * @param fireInterval Ticks between each shot while held.
     * @param props        Item properties.
     */
    public ChanneledSpellTomeItem(DeferredHolder<Spell, S> spellHolder, int fireInterval, Properties props) {
        super(spellHolder, props);
        this.fireInterval = fireInterval;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack tome = player.getItemInHand(hand);
        if (!player.getCooldowns().isOnCooldown(tome.getItem())) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(tome);
        }
        return InteractionResultHolder.fail(tome);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        int ticksUsing = getUseDuration(stack) - remainingUseDuration;
        if (ticksUsing > 0 && ticksUsing % fireInterval == 0) {
            if (livingEntity instanceof Player player) {
                S spell = getSpell();
                float amplifiedDamage = spell.getAmplifiedDamage(player);
                InteractionHand hand = player.getUsedItemHand();
                SpellCastEvent event = new SpellCastEvent(player, level, stack, amplifiedDamage, spell);
                if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
                    amplifiedDamage = event.getAmplifiedDamage();
                    castSpell(event.getSpell(), event.getTome(), level, player, hand, player.getRandom(), amplifiedDamage);
                }
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeChargeLeft) {
        if (livingEntity instanceof Player player) {
            int ticksUsing = getUseDuration(stack) - timeChargeLeft;
            if (ticksUsing > 0) {
                S spell = getSpell();
                player.getCooldowns().addCooldown(stack.getItem(), spell.getDecreasedCooldown(player));
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }
}

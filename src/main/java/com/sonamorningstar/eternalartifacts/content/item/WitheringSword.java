package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.core.ModSpells;
import com.sonamorningstar.eternalartifacts.core.ModTiers;
import com.sonamorningstar.eternalartifacts.event.custom.SpellCastEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

public class WitheringSword extends SwordItem {
    public WitheringSword() {
        super(ModTiers.WITHER, 4, -2.4F, new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Spell spell = ModSpells.WITHER_SKULL.get();
        if (!Spell.checkCooldown(player, this)) {
            float amplifiedDamage = spell.getAmplifiedDamage(player);
            SpellCastEvent event = NeoForge.EVENT_BUS.post(new SpellCastEvent(player, level, stack, amplifiedDamage, spell));
            if (!event.isCanceled()) {
                if (event.getSpell().cast(event.getTome(), player, hand, level, player.getRandom(), event.getAmplifiedDamage())) {
                    player.getCooldowns().addCooldown(this, spell.getDecreasedCooldown(player));
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return InteractionResultHolder.fail(stack);
    }
    
}

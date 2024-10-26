package com.sonamorningstar.eternalartifacts.content.item.base;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SpellTomeItem<S extends Spell> extends Item {
    private final DeferredHolder<Spell, S> spellHolder;
    public SpellTomeItem(DeferredHolder<Spell, S> spellHolder, Properties props) {
        super(props.stacksTo(1));
        this.spellHolder = spellHolder;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return spellHolder.get().rarity;
    }

    public Spell getSpell() {
        return spellHolder.get();
    }

    /**
     *
     * If you want to change spell casting logic (giving tome an energy/xp cost etc.) override
     * {@link #castSpell(Level, LivingEntity, InteractionHand)} instead. <br><br>
     *
     * Overriding this method is fine but make sure to call {@code super} for this method so spell will cast.
     */
    @Deprecated
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack tome = player.getItemInHand(hand);
        if (!player.getCooldowns().isOnCooldown(tome.getItem())){
            if (castSpell(level, player, hand)) return InteractionResultHolder.sidedSuccess(tome, level.isClientSide);
            else return InteractionResultHolder.pass(tome);
        } else return InteractionResultHolder.fail(tome);
    }

    /**
     * Casts a spell using the spell tome, with the specified level, caster, and hand used.
     *
     * @param level  The level where the spell is cast.
     * @param caster The entity that is casting the spell.
     * @param hand   The hand (main or offhand) used to cast the spell.
     * @return {@code true} if the spell was successfully cast, {@code false} otherwise.
     */
    protected boolean castSpell(Level level, LivingEntity caster, InteractionHand hand) {
        return spellHolder.get().cast(caster.getItemInHand(hand), level, caster);
    }
}

package com.sonamorningstar.eternalartifacts.content.item.base;

import com.sonamorningstar.eternalartifacts.client.render.BEWLRProps;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Consumer;

public class AnimatedSpellTomeItem<S extends Spell> extends SpellTomeItem<S> {
    public AnimatedSpellTomeItem(DeferredHolder<Spell, S> spellHolder, Properties props) {
        super(spellHolder, props);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(BEWLRProps.INSTANCE);
    }
}

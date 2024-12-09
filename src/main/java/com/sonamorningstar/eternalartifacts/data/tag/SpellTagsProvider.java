package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import com.sonamorningstar.eternalartifacts.core.ModSpells;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class SpellTagsProvider extends IntrinsicHolderTagsProvider<Spell> {

    public SpellTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookup
    ) {
        this(output, lookup, EternalArtifacts.MODID, null);
    }

    public SpellTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookup,
            ExistingFileHelper existingFileHelper
    ) {
        this(output, lookup, EternalArtifacts.MODID, existingFileHelper);
    }

    public SpellTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookup,
            CompletableFuture<TagsProvider.TagLookup<Spell>> parent
    ) {
        this(output, lookup, parent, EternalArtifacts.MODID, null);
    }

    public SpellTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookup,
            String modid,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, ModRegistries.Keys.SPELL, lookup, spell -> spell.getKey().get(), modid, existingFileHelper);
    }

    public SpellTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookup,
            CompletableFuture<TagsProvider.TagLookup<Spell>> parent,
            String modid,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, ModRegistries.Keys.SPELL, lookup, parent, spell -> spell.getKey().get(), modid, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Spells.FIRE).add(
                ModSpells.FIREBALL.get()
        );
        tag(ModTags.Spells.AIR).add(
                ModSpells.TORNADO.get(),
                ModSpells.SHULKER_BULLETS.get()
        );
        tag(ModTags.Spells.NATURE).add(
                ModSpells.SHULKER_BULLETS.get()
        );


    }
}

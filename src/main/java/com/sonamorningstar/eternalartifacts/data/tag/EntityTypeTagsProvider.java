package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class EntityTypeTagsProvider extends net.minecraft.data.tags.EntityTypeTagsProvider {
    public EntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookup, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(EntityTypeTags.FROG_FOOD).add(ModEntities.PINKY.get());
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(ModEntities.DUCK.get());
        tag(ModTags.Entities.CUTLASS_SPAWN_EGG_BLACKLISTED).addTag(Tags.EntityTypes.BOSSES);
        
        tag(ModTags.Entities.MORPH_BLACKLISTED).addTag(Tags.EntityTypes.BOSSES);
        tag(ModTags.Entities.MORPH_BLACKLISTED).add(
            EntityType.CREEPER,
            EntityType.BLAZE
        );
    }
}

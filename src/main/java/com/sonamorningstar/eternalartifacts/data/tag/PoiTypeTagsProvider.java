package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModVillagers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.PoiTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class PoiTypeTagsProvider extends net.minecraft.data.tags.PoiTypeTagsProvider {
	public PoiTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, MODID, existingFileHelper);
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(ModVillagers.MECHANIC_POI.getKey());
	}
}

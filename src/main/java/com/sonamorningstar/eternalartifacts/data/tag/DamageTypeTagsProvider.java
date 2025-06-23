package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class DamageTypeTagsProvider extends net.minecraft.data.tags.DamageTypeTagsProvider {
	public DamageTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pLookupProvider, MODID, existingFileHelper);
	}
	
	@Override
	protected void addTags(HolderLookup.Provider prov) {
		tag(DamageTypeTags.BYPASSES_RESISTANCE).addOptional(ModDamageTypes.EXECUTE.get().location());
		tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(ModDamageTypes.EXECUTE.get().location());
		
	}
}

package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.core.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.Tags;
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
		tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(ModDamageTypes.MAGIC_BYPASS_IFRAME.get().location());
		tag(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(ModDamageTypes.MAGIC_BYPASS_IFRAME.get().location());
		tag(DamageTypeTags.WITCH_RESISTANT_TO).addOptional(ModDamageTypes.MAGIC_BYPASS_IFRAME.get().location());
		tag(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).addOptional(ModDamageTypes.MAGIC_BYPASS_IFRAME.get().location());
		tag(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH).addOptional(ModDamageTypes.MAGIC_BYPASS_IFRAME.get().location());
		
		tag(Tags.DamageTypes.IS_MAGIC).addOptional(ModDamageTypes.MAGIC_BYPASS_IFRAME.get().location());
		
	}
}

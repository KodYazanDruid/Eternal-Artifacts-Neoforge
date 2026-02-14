package com.sonamorningstar.eternalartifacts.data.tag;

import com.sonamorningstar.eternalartifacts.content.block.base.MultiblockBlock;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockEntityTagsProvider extends IntrinsicHolderTagsProvider<BlockEntityType<?>> {
	public BlockEntityTagsProvider(PackOutput output,
								   CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.BLOCK_ENTITY_TYPE, lookup, type -> type.builtInRegistryHolder().key(), MODID, existingFileHelper);
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider) {
		for (Map.Entry<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>> entry : BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet()) {
			BlockEntityType<?> type = entry.getValue();
			boolean hasAny = type.getValidBlocks().stream().anyMatch(block -> block instanceof MultiblockBlock);
			if (hasAny) {
				tag(ModTags.BlockEntityTypes.LIFTER_BLACKLISTED).add(entry.getKey());
			}
		}
	}
}

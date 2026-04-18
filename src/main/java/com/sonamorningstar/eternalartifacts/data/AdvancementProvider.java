package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends net.neoforged.neoforge.common.data.AdvancementProvider {
	
	public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
		super(output, registries, existingFileHelper, List.of(new AdvancementGenerator()));
	}
	
	private static class AdvancementGenerator implements net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator {
		
		@Override
		public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
			/*Advancement.Builder.advancement()
				.display(ModItems.LIGHTNING_IN_A_BOTTLE, ModConstants.ADVANCEMENT.withSuffixTranslatable("shock_dog"),
					ModConstants.ADVANCEMENT.withSuffixTranslatable("shock_dog.desc"), null, AdvancementType.TASK,
					true, true, true)
				.addCriterion("shock_dog",
					PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(
						Optional.of(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(Damage))))
						Optional.of(EntityPredicate.Builder.entity().of(EntityType.WOLF).)));*/
		}
	}
}

package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.content.item.HammerItem;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class HammeringCategory extends BasicEmiRecipe {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "fake_recipe/hammering");
	public static final EmiRecipeCategory HAMMERING_CATEGORY = new EmiRecipeCategory(ID, EmiIngredient.of(ModTags.Items.TOOLS_HAMMER));
	private final Either<Block, TagKey<Block>> mined;
	private final Map<Item, Pair<Float, Float>> results;
	
	public HammeringCategory(Either<Block, TagKey<Block>> mined, Map<Item, Pair<Float, Float>> results, ResourceLocation id) {
		super(HAMMERING_CATEGORY, id, 72, 18);
		this.mined = mined;
		this.results = results;
	}
	
	public static void fillRecipes(EmiRegistry registry) {
		HammerItem.tagDropRates.forEach((tag, pair) -> {
			ResourceLocation id = HammerItem.getTableForTag(tag);
			registry.addRecipe(new HammeringCategory(Either.right(tag), Map.of(pair.getFirst(), pair.getSecond()),
				new ResourceLocation(id.getNamespace(), "/"+id.getPath())));
		});
		HammerItem.blockDropRates.forEach((block, pair) -> {
			ResourceLocation id = HammerItem.getTableForBlock(block);
			registry.addRecipe(new HammeringCategory(Either.left(block), Map.of(pair.getFirst(), pair.getSecond()),
				new ResourceLocation(id.getNamespace(), "/"+id.getPath())));
		});
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addSlot(getInputs().get(0), 0, 0);
		widgets.addFillingArrow(24, 0, 10000);
		for (int i = 0; i < getOutputs().size(); i++) {
			EmiStack output = getOutputs().get(i);
			widgets.addSlot(output, 54 + i * 18, 0).recipeContext(this);
		}
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		EmiIngredient ingredient = mined.map(block -> EmiIngredient.of(Ingredient.of(block)), EmiIngredient::of);
		return List.of(ingredient);
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return results.entrySet()
			.stream()
			.map(e -> EmiStack.of(e.getKey(), e.getValue().getSecond().longValue()))
			.toList();
	}
	
	@Override
	public boolean supportsRecipeTree() {
		return true;
	}
}

package com.sonamorningstar.eternalartifacts.container.slot;

import com.sonamorningstar.eternalartifacts.api.filter.FilterEntry;
import com.sonamorningstar.eternalartifacts.api.filter.FluidStackEntry;
import com.sonamorningstar.eternalartifacts.api.filter.FluidTagEntry;
import com.sonamorningstar.eternalartifacts.api.filter.ItemTagEntry;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

public class FilterFakeSlot extends FakeSlot {
	@Getter @Setter
	private FilterEntry filter;
	
	public FilterFakeSlot(Container container, FilterEntry filter, int index, int x, int y, boolean displayOnly) {
		super(container, index, x, y, displayOnly);
		this.filter = filter;
	}
	
	public Ingredient getIngredient() {
		return filter instanceof ItemTagEntry ite ? Ingredient.of(ite.getTag()) : Ingredient.EMPTY;
	}
	
	public FluidStack getFluid() {
		return filter instanceof FluidStackEntry fse ? fse.getFilterStack() : FluidStack.EMPTY;
	}
	
	public FluidIngredient getFluidIngredient() {
		return filter instanceof FluidTagEntry fte ? FluidIngredient.of(fte.getTag(), 1000) : FluidIngredient.EMPTY;
	}
}

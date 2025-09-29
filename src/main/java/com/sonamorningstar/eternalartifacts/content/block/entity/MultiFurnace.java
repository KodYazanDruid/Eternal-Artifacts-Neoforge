package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiFurnace<M extends AbstractMachineMenu> extends SidedTransferMachine<M> {
	public MultiFurnace(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, M> quadF) {
		super(type, pos, blockState, quadF);
	}
	
	public short recipeTypeId = 0;
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putShort("RecipeType", recipeTypeId);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		recipeTypeId = tag.getShort("RecipeType");
	}
	
	@Override
	public void saveContents(CompoundTag additionalTag) {
		super.saveContents(additionalTag);
		additionalTag.putShort("RecipeType", recipeTypeId);
	}
	
	@Override
	public void loadContents(CompoundTag additionalTag) {
		super.loadContents(additionalTag);
		recipeTypeId = additionalTag.getShort("RecipeType");
	}
	
	public RecipeType<? extends Recipe<? extends Container>> getSelectedRecipeType() {
		return switch (recipeTypeId) {
			case 1 -> RecipeType.BLASTING;
			case 2 -> RecipeType.SMOKING;
			case 3 -> RecipeType.CAMPFIRE_COOKING;
			default -> RecipeType.SMELTING;
		};
	}
	
	public void setRecipeTypeId(short id) {
		recipeTypeId = id;
		findRecipe();
		sendUpdate();
	}
	
	@Override
	protected void findRecipe() {
		recipeType = getSelectedRecipeType();
		super.findRecipe();
	}
	
	
}

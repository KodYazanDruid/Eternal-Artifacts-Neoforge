package com.sonamorningstar.eternalartifacts.content.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;

public abstract class ModLayeredCauldronBlack extends AbstractCauldronBlock {
	public ModLayeredCauldronBlack(CauldronInteraction.InteractionMap map, Properties props) {
		super(props, map);
		registerDefaultState(this.stateDefinition.any().setValue(getLevelProperty(), getMinLevel()));
	}
	
	public abstract IntegerProperty getLevelProperty();
	public abstract int getMinLevel();
	public abstract int getMaxLevel();
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(getLevelProperty());
	}
	
	@Override
	public boolean isFull(BlockState state) {
		return state.getValue(getLevelProperty()) == getMaxLevel();
	}
	
	@Override
	protected double getContentHeight(BlockState pState) {
		return (6.0 + (double) pState.getValue(getLevelProperty()) * getMaxLevel()) / 16.0;
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		return Items.CAULDRON.getDefaultInstance();
	}
}

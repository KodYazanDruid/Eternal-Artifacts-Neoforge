package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.container.MachineWorkbenchMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;

public class MachineWorkbench extends Machine<MachineWorkbenchMenu> {
	public MachineWorkbench(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.MACHINE_WORKBENCH.get(), pos, blockState, MachineWorkbenchMenu::new);
		setInventory(() -> createBasicInventory(1,
			(slot, stack) -> stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof BaseMachineBlock<?>,
			slot -> 1
		));
	}
	
	
	
}

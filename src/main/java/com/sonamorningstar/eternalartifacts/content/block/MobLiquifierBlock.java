package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.MobLiquifierMachineBlockEntity;

public class MobLiquifierBlock extends MachineFourWayBlock<MobLiquifierMachineBlockEntity> {
    public MobLiquifierBlock(Properties pProperties) {
        super(pProperties, MobLiquifierMachineBlockEntity::new);
    }
}

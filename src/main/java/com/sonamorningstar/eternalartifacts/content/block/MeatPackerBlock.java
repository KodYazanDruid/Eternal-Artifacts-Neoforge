package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.MeatPackerMachineBlockEntity;

public class MeatPackerBlock extends MachineFourWayBlock<MeatPackerMachineBlockEntity> {
    public MeatPackerBlock(Properties pProperties) {
        super(pProperties, MeatPackerMachineBlockEntity::new);
    }
}

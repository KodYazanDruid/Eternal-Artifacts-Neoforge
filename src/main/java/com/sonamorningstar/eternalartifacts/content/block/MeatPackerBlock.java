package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.MeatPackerMachineBlockEntity;

public class MeatPackerBlock extends BaseMachineBlock<MeatPackerMachineBlockEntity>{
    public MeatPackerBlock(Properties pProperties) {
        super(pProperties, MeatPackerMachineBlockEntity::new);
    }
}

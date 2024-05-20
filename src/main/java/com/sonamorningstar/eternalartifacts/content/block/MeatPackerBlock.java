package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.MeatPackerBlockEntity;

public class MeatPackerBlock extends BaseMachineBlock<MeatPackerBlockEntity>{
    public MeatPackerBlock(Properties pProperties) {
        super(pProperties, MeatPackerBlockEntity::new);
    }
}

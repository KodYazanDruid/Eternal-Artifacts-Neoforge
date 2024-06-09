package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.MobLiquifierBlockEntity;

public class MobLiquifierBlock extends BaseMachineBlock<MobLiquifierBlockEntity>{
    public MobLiquifierBlock(Properties pProperties) {
        super(pProperties, MobLiquifierBlockEntity::new);
    }
}

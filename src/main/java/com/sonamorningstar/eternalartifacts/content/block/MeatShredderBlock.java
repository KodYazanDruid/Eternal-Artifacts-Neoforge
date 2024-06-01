package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.MeatShredderBlockEntity;

public class MeatShredderBlock extends BaseMachineBlock<MeatShredderBlockEntity> {
    public MeatShredderBlock(Properties pProperties) {
        super(pProperties, MeatShredderBlockEntity::new);
    }

}

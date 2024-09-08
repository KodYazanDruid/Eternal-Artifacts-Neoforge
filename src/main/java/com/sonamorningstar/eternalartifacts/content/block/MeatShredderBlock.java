package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.MeatShredderBlockEntity;

public class MeatShredderBlock extends MachineFourWayBlock<MeatShredderBlockEntity> {
    public MeatShredderBlock(Properties pProperties) {
        super(pProperties, MeatShredderBlockEntity::new);
    }

}

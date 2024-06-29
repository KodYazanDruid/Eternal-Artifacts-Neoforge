package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.MeatShredderMachineBlockEntity;

public class MeatShredderBlock extends MachineFourWayBlock<MeatShredderMachineBlockEntity> {
    public MeatShredderBlock(Properties pProperties) {
        super(pProperties, MeatShredderMachineBlockEntity::new);
    }

}

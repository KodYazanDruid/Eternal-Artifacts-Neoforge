package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.BioFurnaceEntity;

public class BioFurnaceBlock extends BaseMachineBlock<BioFurnaceEntity> {
    public BioFurnaceBlock(Properties pProperties) {
        super(pProperties, BioFurnaceEntity::new);
    }

}

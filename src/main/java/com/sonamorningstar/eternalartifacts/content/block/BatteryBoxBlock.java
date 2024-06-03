package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.BatteryBoxBlockEntity;

public class BatteryBoxBlock extends BaseMachineBlock<BatteryBoxBlockEntity> {

    public BatteryBoxBlock(Properties pProperties) {
        super(pProperties, BatteryBoxBlockEntity::new);
    }
}

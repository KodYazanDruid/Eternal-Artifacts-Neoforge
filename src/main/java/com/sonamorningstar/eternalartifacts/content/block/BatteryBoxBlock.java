package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.BatteryBoxBlockEntity;

public class BatteryBoxBlock extends MachineFourWayBlock<BatteryBoxBlockEntity> {

    public BatteryBoxBlock(Properties pProperties) {
        super(pProperties, BatteryBoxBlockEntity::new);
    }
}

package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.BatteryBox;

public class BatteryBoxBlock extends MachineFourWayBlock<BatteryBox> {

    public BatteryBoxBlock(Properties pProperties) {
        super(pProperties, BatteryBox::new);
    }
}

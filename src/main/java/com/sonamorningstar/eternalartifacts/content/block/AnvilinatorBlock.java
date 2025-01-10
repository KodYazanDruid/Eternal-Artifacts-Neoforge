package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.AnvilinatorBlockEntity;

public class AnvilinatorBlock extends MachineFourWayBlock<AnvilinatorBlockEntity> {
    public AnvilinatorBlock(Properties pProperties) {
        super(pProperties, AnvilinatorBlockEntity::new);
    }
}

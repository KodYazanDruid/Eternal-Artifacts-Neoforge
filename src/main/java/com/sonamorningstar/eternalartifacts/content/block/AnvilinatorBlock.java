package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.Anvilinator;

public class AnvilinatorBlock extends MachineFourWayBlock<Anvilinator> {
    public AnvilinatorBlock(Properties pProperties) {
        super(pProperties, Anvilinator::new);
    }
}

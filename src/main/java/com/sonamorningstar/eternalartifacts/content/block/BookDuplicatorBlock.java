package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.BookDuplicatorMachineBlockEntity;

public class BookDuplicatorBlock extends MachineFourWayBlock<BookDuplicatorMachineBlockEntity> {
    public BookDuplicatorBlock(Properties p_49795_) {
        super(p_49795_, BookDuplicatorMachineBlockEntity::new);
    }


}

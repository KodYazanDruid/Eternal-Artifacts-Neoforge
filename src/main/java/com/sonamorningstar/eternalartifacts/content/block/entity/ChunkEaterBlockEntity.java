package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

//add slot for pickaxe
public class ChunkEaterBlockEntity extends AbstractMultiblockBlockEntity {
    public long currentPos;
    public ChunkEaterBlockEntity(BlockPos pos, BlockState state) {
        super(ModMultiblocks.CHUNK_EATER.getBlockEntity(), pos, state, ModMultiblocks.CHUNK_EATER.getMultiblock());
        setEnergy(() -> createBasicEnergy(20000, 1000, true, false));
        for (int i = 1; i < 10 ; i++) {
            outputSlots.add(i);
        }
        setInventory(() -> createBasicInventory(10, false));
        setEnergyPerTick(100);
    }

    @Override
    public void tickMaster(Level lvl, BlockPos pos, BlockState st) {
        getFakePlayer();
        setupFakePlayer(st, ((ServerLevel) lvl));
        progress(() -> {
            
        });
    }
}

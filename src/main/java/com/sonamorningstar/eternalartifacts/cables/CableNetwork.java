package com.sonamorningstar.eternalartifacts.cables;

import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class CableNetwork {
    private final Map<BlockPos, Set<ConnectionContext>> cableGraph = new HashMap<>();
    private final Level level;

    public CableNetwork(Level level) {
        this.level = level;
    }

    public void addCable(BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        if (!(block instanceof CableBlock cable)) return;
        cableGraph.putIfAbsent(pos, new HashSet<>());
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            if (CableBlock.connectsTo(neighborPos, level, direction)) {
                cableGraph.putIfAbsent(neighborPos, new HashSet<>());
                connect(pos, neighborPos);
            }
        }
    }

    public void connect(BlockPos cablePos, BlockPos neighborPos) {
        Block block1 = level.getBlockState(cablePos).getBlock();
        Block block2 = level.getBlockState(neighborPos).getBlock();
        if (cableGraph.get(cablePos) == null || cableGraph.get(neighborPos) == null) return;

        ConnectorType type = block2 instanceof CableBlock ? ConnectorType.CABLE : ConnectorType.OTHER;
        if (block1 instanceof CableBlock) {
            cableGraph.get(cablePos).add(new ConnectionContext(neighborPos, type));
            cableGraph.get(neighborPos).add(new ConnectionContext(cablePos, ConnectorType.CABLE));
        }
    }

    public void printNetwork() {
        for (Map.Entry<BlockPos, Set<ConnectionContext>> entry : cableGraph.entrySet()) {
            System.out.println("Block at " + entry.getKey().toShortString() + " connected to " + entry.getValue());
        }
    }

    public record ConnectionContext(BlockPos pos, ConnectorType type) {
        @Override
        public String toString() {
            return "ConnectionContext{" +
                    "pos=" + pos.toShortString() +
                    ", type=" + type +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ConnectionContext that)) return false;
            return Objects.equals(pos, that.pos) && type == that.type;
        }
    }

    public enum ConnectorType{
        CABLE,
        OTHER
    }
}

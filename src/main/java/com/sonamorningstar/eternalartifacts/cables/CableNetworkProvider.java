package com.sonamorningstar.eternalartifacts.cables;

import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableNetworkProvider {
    public static IEnergyStorage getCapability(Level lvl, BlockPos pos, BlockState state, Direction requested) {
        if (!(state.getBlock() instanceof CableBlock cable) || requested == null) return null;
        //CableNetwork network = new CableNetwork(lvl);
        /*for (Direction direction : cable.getConnections(pos, lvl)) {
            BlockPos relative = pos.relative(direction);
            BlockState relativeState = lvl.getBlockState(relative);
            if (relativeState.getBlock() instanceof CableBlock) network.addCable(relative);
        }*/

        /*for (Direction direction : cable.getConnections(pos, lvl)) {
            BlockPos relative = pos.relative(direction);
            if (direction.equals(requested.getOpposite())) continue;
            IEnergyStorage cap = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, relative, direction);
            if (cap != null) {
                return cap;
            }
        }*/
        return null;
    }

    private static void getNodes(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

    }
}

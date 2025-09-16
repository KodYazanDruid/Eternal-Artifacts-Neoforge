package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.Cable;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UncoveredCableBlock extends CableBlock{
    private static final VoxelShape SHAPE = BlockHelper.generateByArea(4, 4, 4, 6, 6, 6);
    private static final VoxelShape SHAPE_NORTH = BlockHelper.generateByArea(4, 4, 6, 6, 6, 0);
    private static final VoxelShape SHAPE_SOUTH = BlockHelper.generateByArea(4, 4, 6, 6, 6, 10);
    private static final VoxelShape SHAPE_EAST = BlockHelper.generateByArea(6, 4, 4, 10, 6, 6);
    private static final VoxelShape SHAPE_WEST = BlockHelper.generateByArea(6, 4, 4, 0, 6, 6);
    private static final VoxelShape SHAPE_UP = BlockHelper.generateByArea(4, 6, 4, 6, 10, 6);
    private static final VoxelShape SHAPE_DOWN = BlockHelper.generateByArea(4, 6, 4, 6, 0, 6);

    public UncoveredCableBlock(CableTier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape joinedShape = SHAPE;
        boolean isNorth = state.getValue(NORTH);
        boolean isEast = state.getValue(EAST);
        boolean isSouth = state.getValue(SOUTH);
        boolean isWest = state.getValue(WEST);
        boolean isUp = state.getValue(UP);
        boolean isDown = state.getValue(DOWN);

        if (isNorth) joinedShape = Shapes.or(joinedShape, SHAPE_NORTH);
        if (isEast) joinedShape = Shapes.or(joinedShape, SHAPE_EAST);
        if (isSouth) joinedShape = Shapes.or(joinedShape, SHAPE_SOUTH);
        if (isWest) joinedShape = Shapes.or(joinedShape, SHAPE_WEST);
        if (isUp) joinedShape = Shapes.or(joinedShape, SHAPE_UP);
        if (isDown) joinedShape = Shapes.or(joinedShape, SHAPE_DOWN);

        return joinedShape;
    }
    
    /*@Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living && !living.isInvulnerableTo(living.damageSources().lightningBolt())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Cable cable) {
                int damageAmount = cable.getTier().getDamageCost();
                int totalExtractable = cable.extractEnergyFromSources(damageAmount, true);
                
                float damage = Math.max(1.0F, totalExtractable / 10.0F);
                
                if (totalExtractable > 0) {
                    if (living.hurt(living.damageSources().lightningBolt(), damage)) {
                        cable.extractEnergyFromSources(totalExtractable, false);
                        if (level instanceof ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                living.getX(), living.getY() + 1.0, living.getZ(),
                                20, 0.0, 0.0, 0.0, 0.5);
                        }
                    }
                }
            }
        }
    }*/
    
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living && !living.isInvulnerableTo(living.damageSources().lightningBolt())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Cable cable) {
                int damageAmount = cable.getTier().getDamageCost();
                
                int totalExtractable = cable.extractEnergyFromReachableSources(pos, damageAmount, true);
                
                float damage = Math.max(1.0F, totalExtractable / 10.0F);
                
                if (totalExtractable > 0) {
                    if (living.hurt(living.damageSources().lightningBolt(), damage)) {
                        cable.extractEnergyFromReachableSources(pos, totalExtractable, false);
                        if (level instanceof ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                living.getX(), living.getY() + 1.0, living.getZ(),
                                20, 0.0, 0.0, 0.0, 0.5);
                        }
                    }
                }
            }
        }
    }
}

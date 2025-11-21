package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import com.sonamorningstar.eternalartifacts.content.block.entity.BluePlasticCauldronBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.TickableServer;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BluePlasticCauldronBlock extends LayeredCauldronBlock implements EntityBlock {
    public BluePlasticCauldronBlock(Properties props) {
        super(Biome.Precipitation.NONE, ModCauldronInteraction.BLUE_PLASTIC, props);
    }

    @Override
    public MapCodec<LayeredCauldronBlock> codec() { return simpleCodec(BluePlasticCauldronBlock::new);}

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof BluePlasticCauldronBlockEntity cauldronBlockEntity) {
            int layerLevel = state.getValue(LEVEL);
            int cooldown = cauldronBlockEntity.cooldown;
            if (cooldown <= 0) {
                if(layerLevel == 1) {
                    popResource(level, pos, Blocks.CAULDRON.defaultBlockState());
                    cauldronBlockEntity.resetCooldown();
                    cauldronBlockEntity.sendUpdate();
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }else if (layerLevel <= 3) {
                    popResource(level, pos, ModBlocks.BLUE_PLASTIC_CAULDRON.get().defaultBlockState().setValue(LEVEL, layerLevel-1));
                    cauldronBlockEntity.resetCooldown();
                    cauldronBlockEntity.sendUpdate();
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    private void popResource(Level level, BlockPos pos, BlockState newState) {
        level.setBlockAndUpdate(pos, newState);
        level.playSound(null, pos, SoundEvents.ANCIENT_DEBRIS_STEP, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        popResourceFromFace(level, pos, Direction.UP, ModItems.PLASTIC_SHEET.toStack());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof BluePlasticCauldronBlockEntity cauldronBlockEntity) {
            int cooldown = cauldronBlockEntity.cooldown;
            double padding = (random.nextDouble()*10/16D) + 3/16D;
            double d0 = (double)pos.getX() + padding;
            double d1 = (double)pos.getY() + 1;
            double d2 = (double)pos.getZ() + padding;
            if (cooldown > 0) {
                if (random.nextDouble() < 0.1) level.playLocalSound(d0, d1, d2, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                level.addParticle(
                        new DustParticleOptions(new Vector3f(30/255f, 144/255f, 255/255f), 1.0F),
                        d0, d1, d2, 0, 0, 0
                );
            } else {
                level.addParticle(
                        new DustParticleOptions(new Vector3f(0/255f, 255/255f, 127/255f), 1.0F),
                        d0, d1, d2, 0, 0, 0
                );
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return Items.CAULDRON.getDefaultInstance();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new BluePlasticCauldronBlockEntity(pos, state);}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TickableServer entity) {
                    entity.tickServer(lvl, pos, st);
                }
            };
        } else return null;
    }
}

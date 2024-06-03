package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.Lazy;

public class OreBerryBlock extends BushBlock {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape AGE_0_SHAPE = BlockHelper.generateByArea(4, 4, 4, 6, 0, 6);
    private static final VoxelShape AGE_1_SHAPE = BlockHelper.generateByArea(10, 10, 10, 3, 0, 3);

    private final BerryMaterial material;
    public OreBerryBlock(Properties pProperties, BerryMaterial material) {
        super(pProperties);
        this.material = material;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AGE)) {
            case 0 -> AGE_0_SHAPE;
            case 1 -> AGE_1_SHAPE;
            default -> BlockHelper.generateByArea(16, 16, 16, 0, 0, 0);
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if(age < 3 && level.getRawBrightness(pos, 0) < 10 && net.neoforged.neoforge.common.CommonHooks.onCropsGrowPre(level, pos, state, random.nextInt(5) == 0)) {
            BlockState newState = state.setValue(AGE, age + 1);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
            net.neoforged.neoforge.common.CommonHooks.onCropsGrowPost(level, pos, state);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);
        if(age == MAX_AGE) {
            ItemStack harvested = material.getHarvestStack(player.getRandom());
            popResourceFromFace(level, pos, hit.getDirection(), harvested);
            level.playSound(null, pos, SoundEvents.CAT_AMBIENT, SoundSource.BLOCKS, 1, 0.8F + level.random.nextFloat() * 0.4F);
            BlockState newState = state.setValue(AGE, 2);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }else return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) { return level.getLightEmission(pos) < 10;}

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !level.getBlockState(pos.below()).isEmpty();
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return simpleCodec(props -> new OreBerryBlock(props, material));
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return (adjacentBlockState.is(this) && state.getValue(AGE).equals(adjacentBlockState.getValue(AGE))) || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_309057_, BlockGetter p_308936_, BlockPos p_308956_, CollisionContext p_309006_) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
        return true;
    }

    public enum BerryMaterial{
        COPPER(Lazy.of(ModItems.COPPER_NUGGET), UniformInt.of(1, 3)),
        IRON(Lazy.of(()->Items.IRON_NUGGET), ConstantInt.of(1)),
        GOLD(Lazy.of(()-> Items.GOLD_NUGGET), ConstantInt.of(1));

        final Lazy<Item> harvest;
        final IntProvider yield;

        BerryMaterial(Lazy<Item> harvest, IntProvider yield) {
            this.harvest = harvest;
            this.yield = yield;
        }

        public Item getHarvest() { return harvest.get(); }

        public ItemStack getHarvestStack(RandomSource random) { return new ItemStack(getHarvest(), getYield(random)); }

        public int getYield(RandomSource random) { return this.yield.sample(random); }
    }
}

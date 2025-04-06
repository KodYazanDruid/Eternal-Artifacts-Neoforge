package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.morph.PlayerMorphUtil;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends DiggerItem {
    public WrenchItem(Tier tier, Properties props) {super(2F, -2F, tier, ModTags.Blocks.MINEABLE_WITH_WRENCH, props); }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if(state.is(ModTags.Blocks.MINEABLE_WITH_WRENCH)) level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1, 1);
        return super.mineBlock(stack, level, state, pos, living);
    }

    //Testing and debugging stuff.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        var map = PlayerMorphUtil.MORPH_MAP;
        
        if (!level.isClientSide) {
            map.forEach((sp, et) -> System.out.println(sp + " " + et));
        }
        
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        
        if (level instanceof ServerLevel && be instanceof MachineBlockEntity<?> machine) {
            var recipes = RecipeCache.getCachedRecipes(machine);
            for (Recipe<? extends Container> recipe : recipes) {
                System.out.println(recipe);
                if (recipe != null){
                    System.out.println(recipe.getType());
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        System.out.println(ingredient);
                    }
                }
                System.out.println("*-*-*-*-*-*-*-*");
            }
        }
        
        return super.useOn(ctx);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }
}

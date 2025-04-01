package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.base.NoResultItemRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleEntityContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.EntityIngredient;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.IFriendlyByteBufExtension;
import net.neoforged.neoforge.fluids.FluidStack;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class MobLiquifierRecipe extends NoResultItemRecipe<SimpleEntityContainer> {
    private final EntityIngredient entity;
    private final NonNullList<FluidStack> resultFluidList;

    @Override
    public boolean matches(SimpleEntityContainer con, Level level) {
        for(int i = 0; i < con.getContainerSize(); i++) {
            if (con.getEntityType(i) != null && entity.test(con.getEntityType(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.MOB_LIQUIFYING.getSerializer();}
    @Override
    public RecipeType<?> getType() {return ModRecipes.MOB_LIQUIFYING.getType();}

    public static class Serializer implements RecipeSerializer<MobLiquifierRecipe> {
        private static final Codec<MobLiquifierRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityIngredient.CODEC_NONEMPTY.fieldOf("entity_ingredient").forGetter(MobLiquifierRecipe::getEntity),
                NonNullList.codecOf(FluidStack.CODEC).fieldOf("result_fluid_list").forGetter(MobLiquifierRecipe::getResultFluidList)
        ).apply(inst, MobLiquifierRecipe::new));

        @Override
        public Codec<MobLiquifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public MobLiquifierRecipe fromNetwork(FriendlyByteBuf buff) {
            EntityIngredient entity = EntityIngredient.fromNetwork(buff);
            NonNullList<FluidStack> resultFluidList = buff.readCollection(NonNullList::createWithCapacity, IFriendlyByteBufExtension::readFluidStack);
            return new MobLiquifierRecipe(entity, resultFluidList);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, MobLiquifierRecipe recipe) {
            recipe.entity.toNetwork(buff);
            buff.writeCollection(recipe.resultFluidList, IFriendlyByteBufExtension::writeFluidStack);
        }
    }
}

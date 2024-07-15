package com.sonamorningstar.eternalartifacts.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.EntityIngredient;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.IFriendlyByteBufExtension;
import net.neoforged.neoforge.fluids.FluidStack;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class MobLiquifierRecipe implements Recipe<Container> {
    /*@Getter
    private final EntityType<?> entity;*/
    @Getter
    private final EntityIngredient entity;
    @Getter
    private final NonNullList<FluidStack> resultFluidList;

    public boolean matches(EntityType<?> entity) {
        return this.entity.test(entity);
    }

    /** @deprecated use {@link #matches(EntityType)} */
    @Deprecated
    @Override
    public boolean matches(Container con, Level level) {
        return false;
    }
    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {return ItemStack.EMPTY; }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {return false;}
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {return ItemStack.EMPTY;}
    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipes.MOB_LIQUIFIER_SERIALIZER.get();}
    @Override
    public RecipeType<?> getType() {return ModRecipes.MOB_LIQUIFIER_TYPE.get();}

    public static class Serializer implements RecipeSerializer<MobLiquifierRecipe> {
        private static final Codec<MobLiquifierRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                //BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(MobLiquifierRecipe::getEntity),
                EntityIngredient.MOD_CODEC.fieldOf("entity_ingredient").forGetter(MobLiquifierRecipe::getEntity),
                NonNullList.codecOf(FluidStack.CODEC).fieldOf("result_fluid_list").forGetter(MobLiquifierRecipe::getResultFluidList)
        ).apply(inst, MobLiquifierRecipe::new));

        @Override
        public Codec<MobLiquifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public MobLiquifierRecipe fromNetwork(FriendlyByteBuf buff) {
            //EntityType<?> entityType = buff.readById(BuiltInRegistries.ENTITY_TYPE::byId);
            EntityIngredient entity = EntityIngredient.fromNetwork(buff);
            NonNullList<FluidStack> resultFluidList = buff.readCollection(NonNullList::createWithCapacity, IFriendlyByteBufExtension::readFluidStack);
            return new MobLiquifierRecipe(entity, resultFluidList);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, MobLiquifierRecipe recipe) {
            //buff.writeById(BuiltInRegistries.ENTITY_TYPE::getId, recipe.entity);
            recipe.entity.toNetwork(buff);
            buff.writeCollection(recipe.resultFluidList, IFriendlyByteBufExtension::writeFluidStack);
        }
    }
}

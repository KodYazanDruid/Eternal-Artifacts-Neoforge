package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.compat.emi.categories.*;
import com.sonamorningstar.eternalartifacts.compat.emi.recipes.BlueprintRecipeHandler;
import com.sonamorningstar.eternalartifacts.compat.emi.recipes.EmiShapedRetexturedRecipe;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@SuppressWarnings("unused")
@EmiEntrypoint
public class EmiPlugin implements dev.emi.emi.api.EmiPlugin {
    
    @Override
    public void register(EmiRegistry registry) {
        registry.addGenericDragDropHandler(new EADragDropHandler());
        registry.addRecipeHandler(ModMenuTypes.BLUEPRINT.get(), new BlueprintRecipeHandler());
        registry.addGenericExclusionArea(new EAExclusionHandler());

        registry.addCategory(MeatPackerCategory.MEAT_PACKER_CATEGORY);
        registry.addCategory(MeatShredderCategory.MEAT_SHREDDER_CATEGORY);
        registry.addCategory(MobLiquifierCategory.MOB_LIQUIFIER_CATEGORY);
        registry.addCategory(MaceratingCategory.MACERATING_CATEGORY);
        registry.addCategory(AlloySmelterCategory.ALLOYING_CATEGORY);
        registry.addCategory(CompressingCategory.COMPRESSOR_CATEGORY);
        registry.addCategory(FluidInfuserCategory.FLUID_INFUSER_CATEGORY);
        registry.addCategory(SqueezingCategory.SQUEEZING_CATEGORY);
        registry.addCategory(MelterCategory.MELTER_CATEGORY);
        registry.addCategory(SolidifierCategory.SOLIDIFIER_CATEGORY);
        registry.addCategory(HammeringCategory.HAMMERING_CATEGORY);

        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModMachines.ADVANCED_CRAFTER.getItem()));
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModItems.PORTABLE_CRAFTER));
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModItems.BLUEPRINT));
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, EmiStack.of(ModMachines.INDUCTION_FURNACE.getItem()));
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, EmiStack.of(ModMachines.ELECTRIC_FURNACE.getItem()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMOKING, EmiStack.of(ModMachines.INDUCTION_FURNACE.getItem()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMOKING, EmiStack.of(ModMachines.ELECTRIC_FURNACE.getItem()));
        registry.addWorkstation(VanillaEmiRecipeCategories.CAMPFIRE_COOKING, EmiStack.of(ModMachines.INDUCTION_FURNACE.getItem()));
        registry.addWorkstation(VanillaEmiRecipeCategories.CAMPFIRE_COOKING, EmiStack.of(ModMachines.ELECTRIC_FURNACE.getItem()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(ModMachines.INDUCTION_FURNACE.getItem()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(ModMachines.ELECTRIC_FURNACE.getItem()));
        registry.addWorkstation(MeatPackerCategory.MEAT_PACKER_CATEGORY, EmiStack.of(ModMachines.MEAT_PACKER.getItem()));
        registry.addWorkstation(MeatShredderCategory.MEAT_SHREDDER_CATEGORY, EmiStack.of(ModMachines.MEAT_SHREDDER.getItem()));
        registry.addWorkstation(MobLiquifierCategory.MOB_LIQUIFIER_CATEGORY, EmiStack.of(ModMachines.MOB_LIQUIFIER.getItem()));
        registry.addWorkstation(MaceratingCategory.MACERATING_CATEGORY, EmiStack.of(ModMachines.INDUSTRIAL_MACERATOR.getItem()));
        registry.addWorkstation(AlloySmelterCategory.ALLOYING_CATEGORY, EmiStack.of(ModMachines.ALLOY_SMELTER.getItem()));
        registry.addWorkstation(CompressingCategory.COMPRESSOR_CATEGORY, EmiStack.of(ModMachines.COMPRESSOR.getItem()));
        registry.addWorkstation(FluidInfuserCategory.FLUID_INFUSER_CATEGORY, EmiStack.of(ModMachines.FLUID_INFUSER.getItem()));
        registry.addWorkstation(SqueezingCategory.SQUEEZING_CATEGORY, EmiStack.of(ModMachines.MATERIAL_SQUEEZER.getItem()));
        registry.addWorkstation(MelterCategory.MELTER_CATEGORY, EmiStack.of(ModMachines.MELTING_CRUCIBLE.getItem()));
        registry.addWorkstation(SolidifierCategory.SOLIDIFIER_CATEGORY, EmiStack.of(ModMachines.SOLIDIFIER.getItem()));
        registry.addWorkstation(HammeringCategory.HAMMERING_CATEGORY, EmiIngredient.of(ModTags.Items.TOOLS_HAMMER));

        registry.addRecipe(new MeatPackerCategory());
        HammeringCategory.fillRecipes(registry);

        MeatShredderCategory.fillRecipes(registry);
        MobLiquifierCategory.fillRecipes(registry);
        MaceratingCategory.fillRecipes(registry);
        AlloySmelterCategory.fillRecipes(registry);
        CompressingCategory.fillRecipes(registry);
        FluidInfuserCategory.fillRecipes(registry);
        SqueezingCategory.fillRecipes(registry);
        MelterCategory.fillRecipes(registry);
        SolidifierCategory.fillRecipes(registry);

        List<Item> items = BuiltInRegistries.ITEM.getTag(ModTags.Items.GARDENING_POT_SUITABLE)
                .map(holders -> holders.stream().map(Holder::value).toList()).orElseGet(ArrayList::new);
        for(Item texture : items){
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(texture);
            ItemStack stack = new ItemStack(ModItems.GARDENING_POT.get());
            CompoundTag tag = stack.getOrCreateTag();
            RetexturedHelper.setTexture(tag, id.toString());
            stack.setTag(tag);
            registry.addRecipe(new EmiShapedRetexturedRecipe(new ResourceLocation(MODID, ("/shaped_retextured_recipe/gardening_pot_"+id).replace(":", "_")), texture, stack));
        }
    }
    
}

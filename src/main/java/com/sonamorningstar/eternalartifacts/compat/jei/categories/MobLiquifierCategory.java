package com.sonamorningstar.eternalartifacts.compat.jei.categories;

import com.sonamorningstar.eternalartifacts.compat.jei.SimpleBackgroundDrawable;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MobLiquifierCategory implements IRecipeCategory<MobLiquifierRecipe> {
    public static final RecipeType<MobLiquifierRecipe> recipeType = new RecipeType<>(ModRecipes.MOB_LIQUIFYING.getKey(), MobLiquifierRecipe.class);
    private final IDrawable icon;
    private final Component title;
    private static final Map<MobLiquifierRecipe, List<LivingEntity>> entities = new HashMap<>();

    public MobLiquifierCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModMachines.MOB_LIQUIFIER.getItem()));
        this.title = Component.translatable(ModMachines.MOB_LIQUIFIER.getBlockTranslationKey());
    }

    @Override
    public RecipeType<MobLiquifierRecipe> getRecipeType() {return recipeType;}

    @Override
    public IDrawable getBackground() {
        SimpleBackgroundDrawable background = new SimpleBackgroundDrawable(134, 54);
        background.addSmallFluidSlot(56, 17);
        background.addSmallFluidSlot(74, 17);
        background.addSmallFluidSlot(92, 17);
        background.addSmallFluidSlot(110, 17);
        //background.setArrow(Pair.of(65, 19));
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, MobLiquifierRecipe recipe, IFocusGroup focus) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        for (int i = 0; i < recipe.getResultFluidList().size(); i++) {
            FluidStack output = recipe.getResultFluidList().get(i);
            layout.addSlot(RecipeIngredientRole.OUTPUT, 57 + 18 * i, 18)
                    .addFluidStack(output.getFluid(), output.getAmount())
                    .addTooltipCallback((slotView, components) -> components.add(Component.literal(output.getAmount() + " MB")));
		}
    }

    @Override
    public void draw(MobLiquifierRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        /*ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        long tick = Minecraft.getInstance().clientTickCount;
        int entityCount = recipe.getEntity().getEntityTypes().length;
        int index = entityCount > 1 ? (int) ((tick / 30) % entityCount) : 0;
        LivingEntity living;
        if (entities.containsKey(recipe)) living = entities.get(recipe).get(index);
        else {
            List<LivingEntity> livingEntities = new ArrayList<>();
            for (EntityType<?> entityType : recipe.getEntity().getEntityTypes()) {
                livingEntities.add(((LivingEntity) entityType.create(level)));
            }
            living = livingEntities.get(index);
            entities.put(recipe, livingEntities);
        }

        if (living != null){
            InventoryScreen.renderEntityInInventory(
                    guiGraphics, 23, 51, 25, new Vector3f(),
                    new Quaternionf().rotationXYZ(0.2F, 3F, (float) Math.PI), null,
                    living);
        }*/
    }
}

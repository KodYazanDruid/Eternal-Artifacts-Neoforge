package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.compat.emi.categories.base.EAEmiRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.EntityIngredient;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class MobLiquifierCategory extends EAEmiRecipe {
    private final EntityIngredient entityIngredient;
    private LivingEntity living;
    private static final ResourceLocation HEART = new ResourceLocation("textures/gui/sprites/hud/heart/half.png");
    private static final ResourceLocation HEART_CONTAINER = new ResourceLocation("textures/gui/sprites/hud/heart/container.png");
    private static final EmiTexture HEART_TEXTURE = new EmiTexture(HEART, 0, 0, 9, 9, 9, 9, 9 ,9);
    private static final EmiTexture HEART_CONTAINER_TEXTURE = new EmiTexture(HEART_CONTAINER, 0, 0, 9, 9, 9, 9, 9 ,9);
    public static final EmiRecipeCategory MOB_LIQUIFIER_CATEGORY = createCategory(ModRecipes.MOB_LIQUIFYING, ModMachines.MOB_LIQUIFIER);
    private static final Minecraft mc = Minecraft.getInstance();
    public MobLiquifierCategory(MobLiquifierRecipe recipe, ResourceLocation id) {
        super(MOB_LIQUIFIER_CATEGORY, id, 144, 50);
        recipe.getResultFluidList().forEach(fs -> outputs.add(EmiStack.of(fs.getFluid(), fs.getAmount())));
        this.entityIngredient = recipe.getEntity();
        EntityType<?>[] types = entityIngredient.getEntityTypes();
        if (mc.level != null) this.living = ((LivingEntity) types[0].create(mc.level));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        EntityType<?>[] types = entityIngredient.getEntityTypes();

        widgets.addDrawable(0, 24, 38, 38, (gui, mouseX, mouseY, delta) -> {
            if (types.length > 1) {
                EntityType<?> randomized = types[(int) ((mc.clientTickCount / 20) % (types.length))];
                if (living.getType() != randomized) living = (LivingEntity) randomized.create(mc.level);
            }
            InventoryScreen.renderEntityInInventory(
                    gui, 12, 24, 25, new Vector3f(),
                    new Quaternionf().rotationXYZ(0.2F, 3F, (float) Math.PI), null,
                    living);
            if (isInBounds(0, 0, 38, 38, mouseX, mouseY))
                gui.renderTooltip(mc.font, living.getName(), mouseX, mouseY);
        });
        widgets.addText(Component.literal("1x "), 34, 10, 0, false);
        widgets.addTexture(HEART_CONTAINER_TEXTURE, 46, 8);
        widgets.addTexture(HEART_TEXTURE, 46, 8);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 34, 17);
        for(int i = 0; i < outputs.size(); i++) {
            widgets.addTank(outputs.get(i), 64 + i * 20, 0, 18, 50, 16000);
        }
    }
    
    private boolean isInBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        Stream<ItemStack> spawnEggs = Arrays.stream(entityIngredient.getEntityTypes())
                .map(DeferredSpawnEggItem::byId).filter(Objects::nonNull).map(ItemStack::new);
        return List.of(EmiIngredient.of(Ingredient.of(spawnEggs)));
    }

    public static void fillRecipes(EmiRegistry registry) {
        for(MobLiquifierRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipes.MOB_LIQUIFYING.getType()).stream().map(RecipeHolder::value).toList()) {
            ResourceLocation id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
            String path;
            if (recipe.getEntity().values[0] instanceof EntityIngredient.TagValue tagValue) path = tagValue.tag().location().getPath();
            else path = BuiltInRegistries.ENTITY_TYPE.getKey(recipe.getEntity().getEntityTypes()[0]).getPath();
            registry.addRecipe(new MobLiquifierCategory(recipe, new ResourceLocation(id.getNamespace(), "/mob_liquifying/"+path)));
        }
    }
}

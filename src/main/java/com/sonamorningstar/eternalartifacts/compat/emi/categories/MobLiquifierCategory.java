package com.sonamorningstar.eternalartifacts.compat.emi.categories;

import com.sonamorningstar.eternalartifacts.content.recipe.MobLiquifierRecipe;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class MobLiquifierCategory extends BasicEmiRecipe {
    EntityType<?> entityType;
    LivingEntity living;
    private static final ResourceLocation HEART = new ResourceLocation("textures/gui/sprites/hud/heart/full.png");
    private static final ResourceLocation HEART_CONTAINER = new ResourceLocation("textures/gui/sprites/hud/heart/container.png");
    private static final EmiTexture HEART_TEXTURE = new EmiTexture(HEART, 0, 0, 9, 9, 9, 9, 9 ,9);
    private static final EmiTexture HEART_CONTAINER_TEXTURE = new EmiTexture(HEART_CONTAINER, 0, 0, 9, 9, 9, 9, 9 ,9);
    public static final EmiRecipeCategory MOB_LIQUIFIER_CATEGORY = new EmiRecipeCategory(new ResourceLocation(MODID, "mob_liquifying"), EmiStack.of(ModBlocks.MOB_LIQUIFIER));
    public MobLiquifierCategory(MobLiquifierRecipe recipe, ResourceLocation id) {
        super(MOB_LIQUIFIER_CATEGORY, id, 144, 50);
        recipe.getResultFluidList().forEach(fs -> outputs.add(EmiStack.of(fs.getFluid(), fs.getAmount())));
        this.entityType = recipe.getEntity();
        this.living = ((LivingEntity) recipe.getEntity().create(Minecraft.getInstance().level));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addDrawable(0, 24, 38, 38, (gui, mouseX, mouseY, delta) ->
                InventoryScreen.renderEntityInInventory(
                        gui, 12, 24, 25, new Vector3f(),
                        new Quaternionf().rotationXYZ(0.2F, 3F, (float) Math.PI), null,
                        living)
        );
        widgets.addText(Component.literal("1x "), 34, 10, 0, false);
        widgets.addTexture(HEART_CONTAINER_TEXTURE, 46, 8);
        widgets.addTexture(HEART_TEXTURE, 46, 8);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 34, 17);
        for(int i = 0; i < outputs.size(); i++) {
            widgets.addSlot(outputs.get(i), 64 + i * 20, 16);
        }
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        SpawnEggItem spawnEgg = DeferredSpawnEggItem.byId(entityType);
        return spawnEgg != null ? List.of(EmiIngredient.of(Ingredient.of(spawnEgg))) : super.getCatalysts();
    }
}

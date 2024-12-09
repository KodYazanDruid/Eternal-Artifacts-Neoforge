package com.sonamorningstar.eternalartifacts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.content.item.ColoredShulkerShellItem;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class ShulkerShellLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ResourceLocation SHULKER_LOCATION = new ResourceLocation("textures/entity/shulker/shulker.png");
    private final ShulkerModel<Shulker> model;
    private final M parent;
    public ShulkerShellLayer(LivingEntityRenderer<T, M> renderer, EntityRendererProvider.Context ctx) {
        super(renderer);
        this.parent = renderer.getModel();
        model = new ShulkerModel<>(ctx.bakeLayer(ModelLayers.SHULKER));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buff, int light, T living,
                       float limbSwing, float limbSwingAmount, float deltaTick, float ageInTicks, float yaw, float pitch) {

        ItemStack shell = PlayerCharmManager.findCharm(living, st -> st.is(ModTags.Items.SHULKER_SHELL));
        if (!shell.isEmpty()){
            ModelPart shulkerLid = model.getLid();
            ModelPart parentHead = parent.getHead();
            parent.copyPropertiesTo((EntityModel<T>) model);
            pose.pushPose();
            float babyOff = living.isBaby() ? 1.27F : 0.0F;
            float crouchOff = living.isCrouching() ? 0.05F : 0.0F;
            pose.scale(0.64F, 0.64F, 0.64F);
            shulkerLid.copyFrom(parentHead);
            shulkerLid.y += (babyOff + crouchOff) * 16;
            shulkerLid.render(pose, buff.getBuffer(model.renderType(getTextureLocation(living))), light,
                    LivingEntityRenderer.getOverlayCoords(living, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            if (shell.hasFoil()) renderGlint(pose, buff, light, shulkerLid);
            pose.popPose();
        }

    }

    private void renderGlint(PoseStack pose, MultiBufferSource buff, int light, ModelPart part) {
        part.render(pose, buff.getBuffer(RenderType.armorEntityGlint()), light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        ItemStack shell = PlayerCharmManager.findCharm(entity, st -> st.is(ModTags.Items.SHULKER_SHELL));
        DyeColor color = null;
        if (shell.getItem() instanceof ColoredShulkerShellItem cssi) color = cssi.getColor();
        return color != null ? getByColor(color) : SHULKER_LOCATION;
    }

    private ResourceLocation getByColor(DyeColor color) {
        return new ResourceLocation("textures/entity/shulker/shulker_" + color.getName() + ".png");
    }
}
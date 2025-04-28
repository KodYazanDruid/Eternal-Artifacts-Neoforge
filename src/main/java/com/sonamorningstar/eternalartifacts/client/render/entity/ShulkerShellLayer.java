package com.sonamorningstar.eternalartifacts.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.content.item.ColoredShulkerShellItem;
import com.sonamorningstar.eternalartifacts.core.ModTags;
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
    private static final ResourceLocation SHULKER_LOCATION = new ResourceLocation("textures/entity/shulker/shulker.png");
    private final ShulkerModel<Shulker> model;
    private final LivingEntityRenderer<T, M> renderer;
    public ShulkerShellLayer(LivingEntityRenderer<T, M> renderer, EntityRendererProvider.Context ctx) {
        super(renderer);
        this.renderer = renderer;
        model = new ShulkerModel<>(ctx.bakeLayer(ModelLayers.SHULKER));
    }
    
    @Override
    public void render(PoseStack pose, MultiBufferSource buff, int light, T living,
                       float limbSwing, float limbSwingAmount, float deltaTick, float ageInTicks, float yaw, float pitch) {

        ItemStack shell = CharmManager.findCharm(living, st -> st.is(ModTags.Items.SHULKER_SHELL));
        if (!shell.isEmpty()){
            pose.pushPose();
            M parent = renderer.getModel();
            ModelPart shulkerLid = model.getLid();
            ModelPart parentHead = parent.getHead();
            shulkerLid.setInitialPose(parentHead.getInitialPose());
            parent.copyPropertiesTo((EntityModel<T>) model);
            shulkerLid.copyFrom(parentHead);
            shulkerLid.xScale = parentHead.xScale * 0.64F;
            shulkerLid.yScale = parentHead.yScale * 0.64F;
            shulkerLid.zScale = parentHead.zScale * 0.64F;
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
        ItemStack shell = CharmManager.findCharm(entity, st -> st.is(ModTags.Items.SHULKER_SHELL));
        DyeColor color = null;
        if (shell.getItem() instanceof ColoredShulkerShellItem) color = ColoredShulkerShellItem.getColor(shell);
        return color != null ? getByColor(color) : SHULKER_LOCATION;
    }

    private ResourceLocation getByColor(DyeColor color) {
        return new ResourceLocation("textures/entity/shulker/shulker_" + color.getName() + ".png");
    }
}
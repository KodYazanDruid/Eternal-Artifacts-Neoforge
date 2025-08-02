package com.sonamorningstar.eternalartifacts.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.resources.model.SpellTomeModel;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SpellTomeRenderer {

    private final SpellTomeModel model;

    public SpellTomeRenderer(EntityRendererProvider.Context ctx) {
        this.model = new SpellTomeModel(ctx.bakeLayer(ModModelLayers.SPELL_TOME_LAYER));
    }

    public void render(ItemStack tome, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int light, int overlay) {
        poseStack.pushPose();
        model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityCutout(getTexture())), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private ResourceLocation getTexture() {
        return new ResourceLocation(MODID, "textures/item/fireball_tome.png");
    }
}

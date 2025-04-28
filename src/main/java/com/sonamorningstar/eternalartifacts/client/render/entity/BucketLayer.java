package com.sonamorningstar.eternalartifacts.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.client.resources.model.BucketHeadModel;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BucketLayer<L extends LivingEntity, M extends HumanoidModel<L>> extends RenderLayer<L, M> {
    private static final ResourceLocation BUCKET_LOCATION = new ResourceLocation(MODID, "textures/models/bucket.png");
    private final RenderLayerParent<L, M> renderer;
    private final BucketHeadModel<L> model;
    public BucketLayer(RenderLayerParent<L, M> renderer, EntityRendererProvider.Context ctx) {
        super(renderer);
        this.renderer = renderer;
        model = new BucketHeadModel<>(ctx.bakeLayer(ModModelLayers.BUCKET_LAYER));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buff, int light, L living,
                       float limbSwing, float limbSwingAmount, float deltaTick, float ageInTicks, float yaw, float pitch) {

        ItemStack bucketStack = CharmManager.findCharm(living, st -> st.is(Items.BUCKET));
        if (!bucketStack.isEmpty()){
            pose.pushPose();
            M parent = renderer.getModel();
            ModelPart bucket = model.getRoot();
            ModelPart parentHead = parent.getHead();
            bucket.setInitialPose(parentHead.getInitialPose());
            parent.copyPropertiesTo(model);
            bucket.copyFrom(parentHead);
            bucket.render(pose, buff.getBuffer(model.renderType(getTextureLocation(living))), light,
                    LivingEntityRenderer.getOverlayCoords(living, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            if (bucketStack.hasFoil()) renderGlint(pose, buff, light, bucket);
            pose.popPose();
        }
    }

    private void renderGlint(PoseStack pose, MultiBufferSource buff, int light, ModelPart part) {
        part.render(pose, buff.getBuffer(RenderType.armorEntityGlint()), light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(L entity) {
        return BUCKET_LOCATION;
    }
}

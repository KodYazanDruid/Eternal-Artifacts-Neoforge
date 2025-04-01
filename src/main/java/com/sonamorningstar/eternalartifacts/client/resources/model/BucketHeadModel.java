package com.sonamorningstar.eternalartifacts.client.resources.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

@Getter
public class BucketHeadModel<L extends LivingEntity> extends EntityModel<L> {
    private final ModelPart root;
    public BucketHeadModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("bucket_down",
            CubeListBuilder.create().texOffs(0, 18)
                .addBox(-4, 13.3F, -1,8, 3, 8),
            PartPose.rotation(-22.5F, 0, 0));
        part.addOrReplaceChild("bucket_main",
            CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5, 5.3F, -2, 10, 8, 10),
            PartPose.rotation(-22.5F, 0, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(L living, float limbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer consumer,
                               int light, int overlay, float r, float g, float b, float a) {
        root.render(pose, consumer, light, overlay, r, g, b, a);
    }
}

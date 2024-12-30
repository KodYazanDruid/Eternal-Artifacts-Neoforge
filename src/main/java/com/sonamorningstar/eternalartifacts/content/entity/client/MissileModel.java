package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class MissileModel extends Model {
    private final ModelPart root;
    public MissileModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("missile", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, 0.0F, -6.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
                , PartPose.ZERO
        );

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer consumer, int light, int overlay, float r, float g, float b, float a) {
        root.render(pose, consumer, light, overlay, r, g, b, a);
    }
}

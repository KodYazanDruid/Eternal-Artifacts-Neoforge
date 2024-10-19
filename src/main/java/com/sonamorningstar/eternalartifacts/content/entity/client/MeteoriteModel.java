package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class MeteoriteModel extends Model {
    private final ModelPart root;
    private final ModelPart meteorite;
    public MeteoriteModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.root = root;
        this.meteorite = root.getChild("meteorite");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("meteorite", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F, new CubeDeformation(0.0F))
                , PartPose.offset(0, 16.0F, 0)
        );

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer,
                               int light, int overlay, float r, float g, float b, float a) {
        root.render(poseStack, consumer, light, overlay, r, g, b, a);
    }

    public void setupAnim(float ageInTicks) {
        float f3 = ageInTicks / 60;
        meteorite.xRot = (float) (Math.PI * f3);
        meteorite.yRot = (float) (Math.PI * f3);
        meteorite.zRot = (float) (Math.PI * f3);
    }
}

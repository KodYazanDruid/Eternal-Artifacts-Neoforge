package com.sonamorningstar.eternalartifacts.client.resources.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class SpellTomeModel extends Model {
    private final ModelPart root;
    private final ModelPart left_lid;

    public SpellTomeModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
        this.left_lid = root.getChild("left_lid");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("middle_part", CubeListBuilder.create().addBox(0, 0, 0, 8, 16, 1), PartPose.ZERO);
        root.addOrReplaceChild("left_lid", CubeListBuilder.create().addBox(0, 0, 1, 1, 16, 15), PartPose.ZERO);
        root.addOrReplaceChild("right_lid", CubeListBuilder.create().addBox(7, 0, 1, 1, 16, 15), PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, float r, float g, float b, float a) {
        this.root.render(poseStack, consumer, light, overlay, r, g, b, a);
    }


}

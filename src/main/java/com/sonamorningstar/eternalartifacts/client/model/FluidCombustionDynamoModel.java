package com.sonamorningstar.eternalartifacts.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidCombustionDynamoBlockEntity;
import lombok.Getter;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class FluidCombustionDynamoModel extends Model {
    @Getter
    private final ModelPart root;
    private final ModelPart back;
    private final ModelPart front;
    private final ModelPart coil;

    public FluidCombustionDynamoModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
        this.back = root.getChild("back");
        this.front = root.getChild("front");
        this.coil = root.getChild("coil");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("back",
                CubeListBuilder.create().texOffs(0, 20)
                        .addBox(0.0F, 0.0F, 0.0F, 16.0F, 4.0F, 16.0F),
                PartPose.ZERO);
        partdefinition.addOrReplaceChild("front",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 16.0F, 4.0F, 16.0F),
                PartPose.offset(0.0F, 4.0F, 0.0F));
        partdefinition.addOrReplaceChild("coil",
                CubeListBuilder.create().texOffs(0, 40)
                        .addBox(0.0F, 0.0F, 0.0F, 6.0F, 12.0F, 6.0F),
                PartPose.offset(5.0F, 4.0F, 5.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, consumer, light, overlay, red, green, blue, alpha);
    }

    public void setupAnim(FluidCombustionDynamoBlockEntity dynamo, float tick) {
        front.y = dynamo.getAnimationLerp(tick);
    }
}

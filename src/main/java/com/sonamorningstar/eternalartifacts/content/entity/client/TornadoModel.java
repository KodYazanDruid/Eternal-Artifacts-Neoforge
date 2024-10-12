package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.Tornado;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class TornadoModel extends Model {
    private final ModelPart root;
    private final ModelPart bottomCube;
    private final ModelPart middleCube;
    private final ModelPart topCube;

    public TornadoModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.root = root;
        this.bottomCube = root.getChild("bottom_cube");
        this.middleCube = root.getChild("middle_cube");
        this.topCube = root.getChild("top_cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("bottom_cube", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -3.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                ,PartPose.offset(0, 3.0F, 0)
        );

        part.addOrReplaceChild("middle_cube", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-7.0F, -5.0F, -7.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
                ,PartPose.offset(0, 11.0F, 0)
        );

        part.addOrReplaceChild("top_cube", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-10F, -8.0F, -10.0F, 20.0F, 16.0F, 20.0F, new CubeDeformation(0.0F))
                ,PartPose.offset(0, 24.0F, 0)
        );

        return LayerDefinition.create(mesh, 16, 16);
    }

    public void setupAnim(Tornado tornado, float ageInTicks) {
        float f3 = ageInTicks / 60;
        topCube.xRot = 0.3F;
        middleCube.xRot = -0.3F;
        bottomCube.xRot = 0.3F;
        topCube.yRot = (float) (Math.PI * f3 * 6);
        middleCube.yRot = (float) (Math.PI * f3 * -8);
        bottomCube.yRot = (float) (Math.PI * f3 * 10);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay,
                               float r, float g, float b, float a) {
        root.render(poseStack, consumer, light, overlay, r, g, b, a);
    }
}

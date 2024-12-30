package com.sonamorningstar.eternalartifacts.client.resources.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class PortableBatteryModel<L extends LivingEntity> extends HumanoidModel<L> {
    private final ModelPart battery;

    public PortableBatteryModel(ModelPart root) {
        super(root);
        this.battery = root.getChild("battery");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(1.0F), 0.0F);
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild(
                "battery",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 12.0F, 22.0F, 12.0F, CubeDeformation.NONE),
                PartPose.ZERO
                //PartPose.offset(5.0F, 12.0F, 10.0F)
        );
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    protected Iterable<ModelPart> headParts() {return ImmutableList.of();}
    @Override
    protected Iterable<ModelPart> bodyParts() {return ImmutableList.of(battery);}
}

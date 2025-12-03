package com.sonamorningstar.eternalartifacts.client.resources.model;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

@Getter
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
                        .addBox(-3.0F, 1.0F, 2.0F, 6.0F, 11.0F, 6.0F, CubeDeformation.NONE),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 32, 32);
    }
    
    @Override
    protected Iterable<ModelPart> headParts() {return ImmutableList.of();}
    @Override
    protected Iterable<ModelPart> bodyParts() {return ImmutableList.of(battery);}
}

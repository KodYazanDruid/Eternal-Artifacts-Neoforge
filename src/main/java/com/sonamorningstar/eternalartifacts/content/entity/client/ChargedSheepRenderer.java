package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.sonamorningstar.eternalartifacts.client.renderer.entity.ChargedSheepFurLayer;
import com.sonamorningstar.eternalartifacts.client.renderer.entity.SheepPowerLayer;
import com.sonamorningstar.eternalartifacts.content.entity.ChargedSheepEntity;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChargedSheepRenderer extends MobRenderer<ChargedSheepEntity, SheepModel<ChargedSheepEntity>>{
    private static final ResourceLocation SHEEP_LOCATION = new ResourceLocation("textures/entity/sheep/sheep.png");

    public ChargedSheepRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SheepModel<>(ctx.bakeLayer(ModelLayers.SHEEP)), 0.7F);
        this.addLayer(new ChargedSheepFurLayer(this, ctx.getModelSet()));
        this.addLayer(new SheepPowerLayer(this, ctx.getModelSet()));
    }

    public static LayerDefinition createFurSwirlLayer(CubeDeformation deform) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, deform.extend(0.6F)),
                PartPose.offset(0.0F, 6.0F, -8.0F)
        );
        part.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, deform.extend(1.75F)),
                PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, deform.extend(0.5F));
        part.addOrReplaceChild("right_hind_leg", cubelistbuilder, PartPose.offset(-3.0F, 12.0F, 7.0F));
        part.addOrReplaceChild("left_hind_leg", cubelistbuilder, PartPose.offset(3.0F, 12.0F, 7.0F));
        part.addOrReplaceChild("right_front_leg", cubelistbuilder, PartPose.offset(-3.0F, 12.0F, -5.0F));
        part.addOrReplaceChild("left_front_leg", cubelistbuilder, PartPose.offset(3.0F, 12.0F, -5.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ResourceLocation getTextureLocation(ChargedSheepEntity pEntity) {
        return SHEEP_LOCATION;
    }

}

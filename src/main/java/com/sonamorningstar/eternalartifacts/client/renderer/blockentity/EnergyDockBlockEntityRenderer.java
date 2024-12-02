package com.sonamorningstar.eternalartifacts.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.content.block.EnergyDockBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.EnergyDockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.properties.DockPart;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class EnergyDockBlockEntityRenderer implements BlockEntityRenderer<EnergyDockBlockEntity> {
    public static final Material TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "block/energy_dock"));
    private final ModelPart main;

    public EnergyDockBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.main = ctx.bakeLayer(ModModelLayers.ENERGY_DOCK_LAYER);
    }
    @Override
    public void render(EnergyDockBlockEntity dock, float partTick, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
        if (dock.getBlockState().getValue(EnergyDockBlock.DOCK_PART) != DockPart.CENTER) return;
        main.render(pose, TEXTURE.buffer(buff, RenderType::entityCutout), light, overlay);
    }

    @Override
    public AABB getRenderBoundingBox(EnergyDockBlockEntity dock) {
        return new AABB(dock.getBlockPos()).inflate(1, 0, 1);
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("main",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-16, 0, -16, 48, 4, 48)
        , PartPose.ZERO);
        return LayerDefinition.create(mesh, 256, 256);
    }
}

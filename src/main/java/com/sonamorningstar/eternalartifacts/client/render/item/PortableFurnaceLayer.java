package com.sonamorningstar.eternalartifacts.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.content.item.PortableFurnaceItem;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class PortableFurnaceLayer<L extends LivingEntity, H extends HumanoidModel<L>> extends RenderLayer<L, H> {
	public PortableFurnaceLayer(RenderLayerParent<L, H> pRenderer) {
		super(pRenderer);
	}
	
	@Override
	public void render(PoseStack pose, MultiBufferSource buff, int light, L living, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		ItemStack furnace = CharmManager.findCharm(living, ModItems.PORTABLE_FURNACE.get());
		if (furnace.isEmpty()) furnace = living.getItemBySlot(EquipmentSlot.CHEST);
		if (!furnace.isEmpty() && furnace.getItem() instanceof PortableFurnaceItem furnaceItem) {
			H parent = getParentModel();
			BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
			pose.pushPose();
			pose.translate(0.5F, 0.5F, 0.5F);
			pose.mulPose(Axis.XP.rotationDegrees(180));
			pose.scale(parent.body.xScale, parent.body.yScale, parent.body.zScale);
			pose.scale(0.5F, 0.5F, 0.5F);
			pose.rotateAround(Axis.XP.rotationDegrees(parent.body.xRot), 0, 1, 0);
			pose.translate(-1.5F, 0.0F, -0.25F);
			BlockState furnState = Blocks.FURNACE.defaultBlockState();
			renderer.renderSingleBlock(furnState, pose, buff, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
			pose.popPose();
		}
	}
}

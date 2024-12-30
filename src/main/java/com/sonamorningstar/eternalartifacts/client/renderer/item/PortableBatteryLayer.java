package com.sonamorningstar.eternalartifacts.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.client.resources.model.PortableBatteryModel;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class PortableBatteryLayer<L extends LivingEntity, H extends EntityModel<L>> extends RenderLayer<L, H> {
    private final PortableBatteryModel<L> battery;
    public PortableBatteryLayer(RenderLayerParent<L, H> renderer, EntityModelSet modelSet) {
        super(renderer);
        battery = new PortableBatteryModel<>(modelSet.bakeLayer(ModModelLayers.PORTABLE_BATTERY_LAYER));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buff, int light, L living,
                       float limbSwing, float limbSwingAmount, float delta,
                       float ageinTicks, float yaw, float pitch) {
        ItemStack battery = getBattery(living);
        if (!battery.isEmpty()) {
            pose.pushPose();
            this.battery.setupAnim(living, limbSwing, limbSwingAmount, ageinTicks, yaw, pitch);
            VertexConsumer consumer = buff.getBuffer(this.battery.renderType(getTexture(battery)));
            this.battery.renderToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            pose.popPose();
        }

    }

    private ResourceLocation getTexture(ItemStack battery) {
        return new ResourceLocation(MODID, "textures/models/portable_battery.png");
    }

    private ItemStack getBattery(L living) {
        ItemStack charm = PlayerCharmManager.findCharm(living, ModItems.PORTABLE_BATTERY.get());
        return !charm.isEmpty() ? charm : living.getItemBySlot(EquipmentSlot.CHEST);
    }
}

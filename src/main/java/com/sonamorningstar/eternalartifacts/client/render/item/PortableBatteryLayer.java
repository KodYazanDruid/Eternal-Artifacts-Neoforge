package com.sonamorningstar.eternalartifacts.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.client.resources.model.PortableBatteryModel;
import com.sonamorningstar.eternalartifacts.content.item.PortableBatteryItem;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModModelLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class PortableBatteryLayer<L extends LivingEntity, H extends HumanoidModel<L>> extends RenderLayer<L, H> {
    private final PortableBatteryModel<L> battery;
    private final LivingEntityRenderer<L, H> renderer;
    public PortableBatteryLayer(LivingEntityRenderer<L, H> renderer, EntityRendererProvider.Context ctx) {
        super(renderer);
        this.renderer = renderer;
        battery = new PortableBatteryModel<>(ctx.bakeLayer(ModModelLayers.PORTABLE_BATTERY_LAYER));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buff, int light, L living,
                       float limbSwing, float limbSwingAmount, float delta,
                       float ageinTicks, float yaw, float pitch) {
        ItemStack batteryStack = getBattery(living);
        if (batteryStack.isEmpty()) batteryStack = living.getItemBySlot(EquipmentSlot.CHEST);
        if (!batteryStack.isEmpty() && batteryStack.getItem() instanceof PortableBatteryItem) {
            pose.pushPose();
            H parent = renderer.getModel();
            ModelPart batteryPart = battery.getBattery();
            ModelPart body = parent.body;
            batteryPart.setInitialPose(body.getInitialPose());
            parent.copyPropertiesTo(battery);
            batteryPart.copyFrom(body);
            this.battery.setupAnim(living, limbSwing, limbSwingAmount, ageinTicks, yaw, pitch);
            VertexConsumer consumer = buff.getBuffer(this.battery.renderType(getTexture(batteryStack)));
            this.battery.renderToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            pose.popPose();
        }

    }

    private ResourceLocation getTexture(ItemStack battery) {
        return new ResourceLocation(MODID, "textures/models/portable_battery.png");
    }

    private ItemStack getBattery(L living) {
        return CharmManager.findCharm(living, ModItems.PORTABLE_BATTERY.get());
    }
}

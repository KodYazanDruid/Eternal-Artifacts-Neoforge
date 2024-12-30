package com.sonamorningstar.eternalartifacts.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.KnapsackScreen;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.ShootSkullsToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static final Direction[] DIRS = ArrayUtils.add(Direction.values(), null);

    @SubscribeEvent
    public static void renderLevelStage(final RenderLevelStageEvent event) {
        PoseStack pose = event.getPoseStack();
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getItemRenderer().getModel(ModItems.HOLY_DAGGER.toStack(), null, null, 0);
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        LivingEntity living = Minecraft.getInstance().player;

        /**
         * Source: https://github.com/TeamTwilight/twilightforest/blob/1.20.x/src/main/java/twilightforest/client/renderer/entity/ShieldLayer.java#L25
         * @link{com.sonamorningstar.eternalartifacts.client.renderer.entity.HolyDaggerLayer} for entity layer rendering.
         * This is for first person rendering.
         */
        if(event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_PARTICLES) && living.hasEffect(ModEffects.DIVINE_PROTECTION.get())) {
            float age = living.tickCount + event.getPartialTick();
            float rotateAngleY = age / -50.0F;
            float rotateAngleX = Mth.sin(age / 5.0F) / 4.0F;
            float rotateAngleZ = Mth.cos(age / 5.0F) / 4.0F;

            int count = 8;
            for(int c = 0; c < count; c++){
                pose.pushPose();
                pose.mulPose(Axis.ZP.rotationDegrees(rotateAngleZ * (180F / (float) Math.PI)));
                pose.mulPose(Axis.YP.rotationDegrees(rotateAngleY * (180F / (float) Math.PI) + (c * (360F / count))));
                pose.mulPose(Axis.XP.rotationDegrees(rotateAngleX * (180F / (float) Math.PI)));
                pose.translate(-0.5, -0.65, -0.5);
                pose.translate(0F, 0F, -1.5F);
                for (Direction dir : DIRS) {
                    minecraft.getItemRenderer().renderQuadList(
                            pose,
                            buffer.getBuffer(Sheets.translucentCullBlockSheet()),
                            model.getQuads(null, dir, living.getRandom(), ModelData.EMPTY, Sheets.translucentCullBlockSheet()),
                            ItemStack.EMPTY,
                            0xF000F0,
                            OverlayTexture.NO_OVERLAY
                    );
                }
                pose.popPose();
            }
        }
    }

    @SubscribeEvent
    public static void renderTooltipEvent(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        List<Either<FormattedText, TooltipComponent>> tooltips = event.getTooltipElements();

        List<CharmType> types = CharmType.getTypesOfItem(stack.getItem());
        if (!types.isEmpty()) {
            MutableComponent charm = ModConstants.TOOLTIP.withSuffixTranslatable("charm").withStyle(ChatFormatting.DARK_GREEN);
            MutableComponent combined = charm.append(": ");
            for (int i = 0; i < types.size(); i++) {
                CharmType type = types.get(i);
                MutableComponent name = type.getDisplayName().withStyle(ChatFormatting.GREEN);
                combined.append(name);
                if (i < types.size() - 1) combined.append(", ").withStyle(ChatFormatting.DARK_GREEN);
            }
            tooltips.add(tooltips.size() - 1, Either.left(combined));
        }

    }

    @SubscribeEvent
    public static void mouseScrollEvent(ScreenEvent.MouseScrolled.Pre event) {
        Screen screen = event.getScreen();
        if(screen instanceof KnapsackScreen ks) {
            Slot slot = ks.getSlotUnderMouse();
            if(slot != null && slot.getItem().is(ModItems.KNAPSACK)) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void leftClickEvent(PlayerInteractEvent.LeftClickEmpty event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        if (stack.is(ModItems.WITHERING_SWORD) && !player.getCooldowns().isOnCooldown(stack.getItem())) Channel.sendToServer(new ShootSkullsToServer(stack, event.getHand()));
    }

    @SubscribeEvent
    public static void mouseClickedPre(ScreenEvent.MouseButtonPressed.Pre event) {
        Screen screen = event.getScreen();
        if(screen instanceof KnapsackScreen ks) {
            Slot slot = ks.getSlotUnderMouse();
            if(slot != null && slot.getItem().is(ModItems.KNAPSACK)) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void screenOpenEvent(ScreenEvent.Opening event) {
        Screen screen = event.getScreen();
        Screen oldScreen = event.getCurrentScreen();
        TabHandler instance = TabHandler.INSTANCE;
        if (instance == null &&
                screen instanceof InventoryScreen inventoryScreen && oldScreen == null &&
                Config.CHARMS_ENABLED.getAsBoolean()) {
            TabHandler.onTabsConstruct(inventoryScreen);
        }
       if (instance != null) {
           if (!instance.requested) instance.currentTab = null;
           else instance.requested = false;
       }
    }

    static int delay = 2;
    @SubscribeEvent
    public static void clientTickEvent(TickEvent.ClientTickEvent event) {
        if(event.phase.equals(TickEvent.Phase.END)) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen == null && TabHandler.INSTANCE != null) {
                delay--;
                if (delay <= 0) {
                    TabHandler.onTabsFinalize();
                    delay = 2;
                }
            }
        }
    }

    @SubscribeEvent
    public static void screenRenderEvent(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof AbstractContainerScreen<?> acs &&
                !(acs instanceof CreativeModeInventoryScreen) &&
                Config.CHARMS_ENABLED.getAsBoolean()) {
            int left = acs.getGuiLeft();
            int top = acs.getGuiTop();
            TabHandler instance = TabHandler.INSTANCE;
            if (instance != null) instance.renderTabs(event.getGuiGraphics(), left, top);
        }
    }

}

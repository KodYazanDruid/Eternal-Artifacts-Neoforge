package com.sonamorningstar.eternalartifacts.event.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.api.charm.CharmAttributes;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.KnapsackScreen;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingDasher;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingJumper;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.movement.ConsumeDashTokenToServer;
import com.sonamorningstar.eternalartifacts.network.movement.ConsumeJumpTokenToServer;
import com.sonamorningstar.eternalartifacts.network.ShootSkullsToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

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
        
        if (ItemStack.shouldShowInTooltip(stack.getHideFlags(), ItemStack.TooltipPart.MODIFIERS)) {
            if (stack.is(ModTags.Items.CHARMS)) {
                for (CharmType type : types) {
                    Set<CharmAttributes> allAttributes = CharmStorage.itemAttributes;
                    for (CharmAttributes attr : allAttributes) {
                        if (attr.isStackCorrect(stack)) {
                            Multimap<Attribute, AttributeModifier> modifierMap = attr.getModifiers();
                            if (!modifierMap.isEmpty() && attr.getTypes().contains(type)) {
                                addModifierTooltip(tooltips, type, modifierMap);
                            }
                        }
                    }
                }
            }
            if (stack.hasTag()) {
                ListTag charmAttrNBT = stack.getTag().getList(CharmAttributes.ATTR_KEY, 10);
				Multimap<CharmType, Multimap<Attribute, AttributeModifier>> typeAttrMap = HashMultimap.create();
				for (int i = 0; i < charmAttrNBT.size(); i++) {
					CompoundTag compoundtag = charmAttrNBT.getCompound(i);
					Optional<Attribute> optional = BuiltInRegistries.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundtag.getString("AttributeName")));
					String typeName = "";
					CharmType nbtType = null;
					if (compoundtag.contains("Slot")) typeName = compoundtag.getString("Slot");
					try {
						nbtType = CharmType.valueOf(typeName.toUpperCase());
					} catch (IllegalArgumentException e) {
						//Do nothing
					}
					if (optional.isPresent() && nbtType != null) {
						AttributeModifier attributemodifier = AttributeModifier.load(compoundtag);
						if (attributemodifier != null && attributemodifier.getId().getLeastSignificantBits() != 0L
							&& attributemodifier.getId().getMostSignificantBits() != 0L) {
							Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
							multimap.put(optional.get(), attributemodifier);
							typeAttrMap.put(nbtType, multimap);
						}
					}
				}
                typeAttrMap.asMap().forEach((type, modifierMaps) -> {
                    Multimap<Attribute, AttributeModifier> mergedMap = HashMultimap.create();
                    modifierMaps.forEach(mergedMap::putAll);
                    if (!mergedMap.isEmpty()) addModifierTooltip(tooltips, type, mergedMap);
                });
			}
        }
        
        if (!types.isEmpty() || (stack.hasTag() && stack.getTag().contains(CharmType.CHARM_KEY))) {
            MutableComponent charm = ModConstants.TOOLTIP.withSuffixTranslatable("charm").withStyle(ChatFormatting.DARK_GREEN);
            MutableComponent combined = charm.append(": ");
            MutableComponent charmTypes = Component.empty();
            for (int i = 0; i < types.size(); i++) {
                CharmType type = types.get(i);
                MutableComponent name = type.getDisplayName().withStyle(ChatFormatting.GREEN);
                charmTypes.append(name);
                if (i < types.size() - 1) charmTypes.append(", ").withStyle(ChatFormatting.DARK_GREEN);
            }
            if (stack.hasTag()) {
                ListTag nbtType = stack.getTag().getList(CharmType.CHARM_KEY, 10);
                for (Tag tag : nbtType) {
                    CompoundTag compound = (CompoundTag) tag;
                    String typeName = compound.getString("CharmType");
                    try {
                        CharmType type = CharmType.valueOf(typeName.toUpperCase());
                        MutableComponent name = type.getDisplayName().withStyle(ChatFormatting.GREEN);
                        if (charmTypes.getSiblings().contains(name)) continue;
                        if (!charmTypes.getString().isEmpty()) charmTypes.append(", ").append(name);
                        else charmTypes.append(name);
                    } catch (IllegalArgumentException e){
                        //Do nothing
                    }
                }
            }
            combined.append(charmTypes);
            tooltips.add(tooltips.size() - 1, Either.left(combined));
        }
    }
    
    private static void addModifierTooltip(List<Either<FormattedText, TooltipComponent>> tooltips, CharmType type, Multimap<Attribute, AttributeModifier> modifierMap) {
        MutableComponent attributeText = ModConstants.CHARM_SLOT_MODIFIER.withSuffixTranslatable(type.getLowerCaseName())
            .withStyle(ChatFormatting.GRAY);
        tooltips.add(tooltips.size() - 1, Either.left(CommonComponents.EMPTY));
        tooltips.add(tooltips.size() - 1, Either.left(attributeText));
        for (Map.Entry<Attribute, AttributeModifier> entry : modifierMap.entries()) {
            Attribute attribute = entry.getKey();
            AttributeModifier modifier = entry.getValue();
            double formattedAmount = getFormattedAmount(modifier, attribute);
            MutableComponent modifierText;
            if (modifier.getAmount() < 0) {
                formattedAmount *= -1;
                modifierText = Component.translatable("attribute.modifier.take." + modifier.getOperation().toValue(),
                    ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(formattedAmount),
                    Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.RED);
            } else {
                modifierText = Component.translatable("attribute.modifier.plus." + modifier.getOperation().toValue(),
                    ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(formattedAmount),
                    Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.BLUE);
            }
            if (formattedAmount != 0) tooltips.add(tooltips.size() - 1, Either.left(modifierText));
        }
    }

    private static double getFormattedAmount(AttributeModifier modifier, Attribute attribute) {
        double amount = modifier.getAmount();
        double formattedAmount;
        if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE
                || modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
            formattedAmount = amount * 100.0;
        } else if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
            formattedAmount = amount * 10.0;
        } else {
            formattedAmount = amount;
        }
        return formattedAmount;
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
    
    @SubscribeEvent
    public static void keyInputEvent(InputEvent.Key event) {
        int key = event.getKey();
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        CompoundTag tag = player.getPersistentData();
        if (key == mc.options.keyJump.getKey().getValue() && !player.onGround()) {
            ItemStack charm = PlayerCharmManager.findCharm(player, ModItems.SKYBOUND_TREADS.get());
            int jumps = tag.getInt(ILivingJumper.KEY);
            if (!charm.isEmpty() && player instanceof ILivingJumper jumper && jumps > 0 && player.noJumpDelay == 0) {
                jumper.jumpGround();
                Channel.sendToServer(new ConsumeJumpTokenToServer());
                tag.putInt(ILivingJumper.KEY, jumps - 1);
                player.noJumpDelay = 10;
            }
        }
        if (key == mc.options.keySprint.getKey().getValue() && !player.onGround()) {
            ItemStack charm = PlayerCharmManager.findCharm(player, ModItems.GALE_SASH.get());
            int dashes = tag.getInt(ILivingDasher.KEY);
            if (!charm.isEmpty() && player instanceof ILivingDasher dasher && dashes > 0 && dasher.dashCooldown() == 0) {
                dasher.dashAir(player);
                Channel.sendToServer(new ConsumeDashTokenToServer());
                tag.putInt(ILivingDasher.KEY, dashes - 1);
                dasher.setDashCooldown(10);
            }
        }
        
    }
}

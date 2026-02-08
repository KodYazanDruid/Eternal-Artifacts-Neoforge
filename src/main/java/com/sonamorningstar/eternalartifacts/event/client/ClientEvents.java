package com.sonamorningstar.eternalartifacts.event.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.api.charm.CharmAttributes;
import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.api.client.ClientFilterTooltip;
import com.sonamorningstar.eternalartifacts.api.client.ClientFiltersClampedTooltip;
import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.AbstractPipeFilterScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.KnapsackScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.GenericSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.tooltip.ItemTooltipManager;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.TooltipRenderable;
import com.sonamorningstar.eternalartifacts.client.render.FluidRendererHelper;
import com.sonamorningstar.eternalartifacts.client.render.ModRenderTypes;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Filterable;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.content.item.PipeAttachmentItem;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.event.custom.RenderEtarSlotEvent;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingDasher;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.ILivingJumper;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.movement.ConsumeDashTokenToServer;
import com.sonamorningstar.eternalartifacts.network.movement.ConsumeJumpTokenToServer;
import com.sonamorningstar.eternalartifacts.network.ShootSkullsToServer;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import org.joml.Matrix4f;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.GlStateBackup;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static final Direction[] DIRS = ArrayUtils.add(Direction.values(), null);
    @SubscribeEvent
    public static void renderLevelStage(final RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();PoseStack pose = event.getPoseStack();
        BakedModel model = mc.getItemRenderer().getModel(ModItems.HOLY_DAGGER.toStack(), null, null, 0);
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
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
                    mc.getItemRenderer().renderQuadList(
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
        
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)) {
            Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
            pose.pushPose();
            pose.translate(-camPos.x, -camPos.y, -camPos.z);
            
            if (mc.level != null && mc.player != null) {
                int viewDist = mc.options.renderDistance().get();
                BlockPos playerPos = mc.player.blockPosition();
                int chunkX = playerPos.getX() >> 4;
                int chunkZ = playerPos.getZ() >> 4;
                
                for (int cx = chunkX - viewDist; cx <= chunkX + viewDist; cx++) {
                    for (int cz = chunkZ - viewDist; cz <= chunkZ + viewDist; cz++) {
                        var chunk = mc.level.getChunkSource().getChunkNow(cx, cz);
                        if (chunk != null) {
                            for (var be : chunk.getBlockEntities().values()) {
                                if (be instanceof WorkingAreaProvider wap && wap.shouldRenderArea()) {
                                    renderWorkingArea(pose, buffer, wap, be.getBlockPos());
                                }
                            }
                        }
                    }
                }
            }
            
            buffer.endBatch(ModRenderTypes.AREA_FACE);
            buffer.endBatch(ModRenderTypes.AREA_OUTLINE);
            
            pose.popPose();
        }
    }
    
    // Area rendering constants
    private static final float OUTLINE_R = 0.3f;
    private static final float OUTLINE_G = 0.6f;
    private static final float OUTLINE_B = 1.0f;
    private static final float OUTLINE_A = 1.0f;
    
    private static final float FACE_R = 0.2f;
    private static final float FACE_G = 0.5f;
    private static final float FACE_B = 0.9f;
    private static final float FACE_A = 0.15f;
    
    private static void renderWorkingArea(PoseStack pose, MultiBufferSource.BufferSource buffer, WorkingAreaProvider wap, BlockPos bePos) {
        AABB box = wap.getWorkingArea(bePos);
        
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;
        
        // Render translucent faces
        renderAreaFaces(pose, buffer, minX, minY, minZ, maxX, maxY, maxZ);
        
        // Render thick outline
        renderAreaOutline(pose, buffer, minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    private static void renderAreaFaces(PoseStack pose, MultiBufferSource buff,
                             float minX, float minY, float minZ,
                             float maxX, float maxY, float maxZ) {
        VertexConsumer consumer = buff.getBuffer(ModRenderTypes.AREA_FACE);
        Matrix4f matrix = pose.last().pose();
        
        int color = ((int)(FACE_A * 255) << 24) | ((int)(FACE_R * 255) << 16) | ((int)(FACE_G * 255) << 8) | (int)(FACE_B * 255);
        
        // Bottom face (Y-)
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();
        
        // Top face (Y+)
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();
        
        // North face (Z-)
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();
        
        // South face (Z+)
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();
        
        // West face (X-)
        consumer.vertex(matrix, minX, minY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, minX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, minX, minY, minZ).color(color).endVertex();
        
        // East face (X+)
        consumer.vertex(matrix, maxX, minY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, minZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, maxY, maxZ).color(color).endVertex();
        consumer.vertex(matrix, maxX, minY, maxZ).color(color).endVertex();
    }
    
    private static void renderAreaOutline(PoseStack pose, MultiBufferSource buff,
                               float minX, float minY, float minZ,
                               float maxX, float maxY, float maxZ) {
        VertexConsumer consumer = buff.getBuffer(ModRenderTypes.AREA_OUTLINE);
        Matrix4f matrix = pose.last().pose();
        
        // Bottom edges
        areaLine(consumer, matrix, minX, minY, minZ, maxX, minY, minZ);
        areaLine(consumer, matrix, maxX, minY, minZ, maxX, minY, maxZ);
        areaLine(consumer, matrix, maxX, minY, maxZ, minX, minY, maxZ);
        areaLine(consumer, matrix, minX, minY, maxZ, minX, minY, minZ);
        
        // Top edges
        areaLine(consumer, matrix, minX, maxY, minZ, maxX, maxY, minZ);
        areaLine(consumer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ);
        areaLine(consumer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ);
        areaLine(consumer, matrix, minX, maxY, maxZ, minX, maxY, minZ);
        
        // Vertical edges
        areaLine(consumer, matrix, minX, minY, minZ, minX, maxY, minZ);
        areaLine(consumer, matrix, maxX, minY, minZ, maxX, maxY, minZ);
        areaLine(consumer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ);
        areaLine(consumer, matrix, minX, minY, maxZ, minX, maxY, maxZ);
    }
    
    private static void areaLine(VertexConsumer consumer, Matrix4f matrix,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) len = 1;
        float nx = dx / len;
        float ny = dy / len;
        float nz = dz / len;
        
        consumer.vertex(matrix, x1, y1, z1)
                .color(OUTLINE_R, OUTLINE_G, OUTLINE_B, OUTLINE_A)
                .normal(nx, ny, nz)
                .endVertex();
        consumer.vertex(matrix, x2, y2, z2)
                .color(OUTLINE_R, OUTLINE_G, OUTLINE_B, OUTLINE_A)
                .normal(nx, ny, nz)
                .endVertex();
    }
    
    @SubscribeEvent
    public static void charmDescriptions(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> toolTips = event.getToolTip();
        ItemTooltipManager.applyTooltips(stack, toolTips);
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void pipeFilterTooltip(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        List<Either<FormattedText, TooltipComponent>> tooltips = event.getTooltipElements();
        
        if (stack.getItem() instanceof PipeAttachmentItem) {
            CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
            CompoundTag filterData = tag.getCompound("FilterData");
            NonNullList<FilterEntry> entries = NonNullList.withSize(9, ItemFilterEntry.Empty.create(true));
            ListTag itemEntries = filterData.getList("ItemFilters", 10);
            for (int i = 0; i < itemEntries.size(); i++) {
                CompoundTag entryTag = itemEntries.getCompound(i);
                entries.set(i, ItemFilterEntry.fromNBT(entryTag));
            }
            ListTag fluidEntries = filterData.getList("FluidFilters", 10);
            for (int i = 0; i < fluidEntries.size(); i++) {
                FilterEntry entry = entries.get(i);
                CompoundTag entryTag = fluidEntries.getCompound(i);
                if (!(entry instanceof ItemTagEntry || (entry instanceof ItemStackEntry itemEntry && !itemEntry.getFilterStack().isEmpty())))
                    entries.set(i, FluidFilterEntry.fromNBT(entryTag));
            }
            if (!entries.isEmpty() && entries.stream().anyMatch(entry -> !entry.isEmpty())){
                Minecraft mc = Minecraft.getInstance();
                tooltips.add(Either.left(
                    Component.translatable(ModConstants.TOOLTIP.withSuffix("press_key_for_detailed_information"),
                    mc.options.keyShift.getTranslatedKeyMessage())));
                if (Screen.hasShiftDown()) {
                    for (FilterEntry entry : entries) {
                        if (entry.isEmpty()) continue;
                        tooltips.add(Either.right(new ClientFilterTooltip.FilterTooltip(entry)));
                    }
                } else {
                    tooltips.add(Either.right(new ClientFiltersClampedTooltip.FiltersClampedTooltip(entries.stream().filter(entry -> !entry.isEmpty()).toList())));
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void charmTooltip(RenderTooltipEvent.GatherComponents event) {
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
                        EternalArtifacts.LOGGER.error("Invalid CharmType: {}", typeName, e);
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
            
            if (CharmStorage.isBlacklistedWildcard(stack)) {
                MutableComponent blacklisted = ModConstants.TOOLTIP.withSuffixTranslatable("wildcard_blacklisted")
                    .withStyle(ChatFormatting.GRAY);
                tooltips.add(Either.left(blacklisted));
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
            tooltips.add(Either.left(combined));
        }
    }
    
    private static void addModifierTooltip(List<Either<FormattedText, TooltipComponent>> tooltips, CharmType type, Multimap<Attribute, AttributeModifier> modifierMap) {
        MutableComponent attributeText = ModConstants.CHARM_SLOT_MODIFIER.withSuffixTranslatable(type.getLowerCaseName())
            .withStyle(ChatFormatting.GRAY);
        tooltips.add(Either.left(CommonComponents.EMPTY));
        tooltips.add(Either.left(attributeText));
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
            if (formattedAmount != 0) tooltips.add(Either.left(modifierText));
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
        GuiGraphics gui = event.getGuiGraphics();
        if (screen instanceof AbstractContainerScreen<?> acs &&
                !(acs instanceof CreativeModeInventoryScreen) &&
                Config.CHARMS_ENABLED.getAsBoolean()) {
            int left = acs.getGuiLeft();
            int top = acs.getGuiTop();
            TabHandler instance = TabHandler.INSTANCE;
            if (instance != null) instance.renderTabs(event.getGuiGraphics(), left, top);
        }
        if (screen instanceof GenericSidedMachineScreen gsms) {
            var owner = gsms.getMachine();
            final int x = gsms.getGuiLeft();
            final int y = gsms.getGuiTop();
            if (owner instanceof DimensionalAnchor anchor) {
                GuiDrawer.drawFramedBackground(gui, x + 37, y + 18, 100, 50, 1,
                    0xff000000, 0xff404040, 0xffa0a0a0);
                gui.drawString(screen.getMinecraft().font,
                    Component.translatable(ModConstants.GUI.withSuffix("forceload.loaded_chunks_count"),
                        anchor.getForcedChunks().size()),
                    x + 40, y + 20, 0xfff0f0f0, false
                );
            }
            if (owner instanceof FluidPump pump) {
                GuiDrawer.drawFramedBackground(gui, x + 47, y + 18, 100, 50, 1,
                    0xff000000, 0xff404040, 0xffa0a0a0);
                int veinSize = pump.veinSize;
                if (veinSize > 0) {
                    gui.drawString(screen.getMinecraft().font,
                        Component.translatable(ModConstants.GUI.withSuffix("fluid_pump.vein_size"), veinSize),
                        x + 50, y + 20, 0xfff0f0f0, false
                    );
                }
            }
        }
    }
    
    public static ItemStack recipeViewDraggedStack = ItemStack.EMPTY;
    public static FluidStack recipeViewDraggedFluid = FluidStack.EMPTY;
    private static final GlStateBackup GL_STATE = new GlStateBackup();
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderScreenLowPrio(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        GuiGraphics gui = event.getGuiGraphics();
        int mx = event.getMouseX();
        int my = event.getMouseY();
        float deltaTick = event.getPartialTick();
        if (screen instanceof AbstractModContainerScreen<?> amcs) {
            int leftPos = amcs.getGuiLeft();
            int topPos = amcs.getGuiTop();
            if (amcs.getMenu() instanceof AbstractMachineMenu amm) {
                BlockEntity be = amm.getBlockEntity();
                /*if (be instanceof BatteryBox batteryBox) {
                    ModEnergyStorage energy = batteryBox.energy;
                    
                    int avgIn  = energy.getClientAverageReceivedEnergy();
                    int avgOut = energy.getClientAverageExtractedEnergy();
                    int avgNet = energy.getClientAverageNetEnergyChange();
                    
                    Font font = screen.getMinecraft().font;
                    
                    int x = leftPos + 120;
                    int y = topPos + 36;
                    gui.drawString(font, "+" + avgIn, x, y, ChatFormatting.GREEN.getColor(), false);
                    gui.drawString(font, "-" + avgOut, x, y + 10, ChatFormatting.RED.getColor(), false);

                    int netColor = avgNet > 0
                        ? ChatFormatting.GREEN.getColor()
                        : avgNet < 0
                        ? ChatFormatting.RED.getColor()
                        : ChatFormatting.YELLOW.getColor();
                    
                    gui.drawString(font, "~" + avgNet, x, y + 20, netColor, false);
                    
                    int avgNetPerSecond = avgNet * 20;
                    gui.drawString(font, avgNetPerSecond + " FE/s", x, y + 32, ChatFormatting.GRAY.getColor(), false);
                }*/
            }
            
            PoseStack pose = gui.pose();
            pose.pushPose();
            RenderSystem.backupGlState(GL_STATE);
            
            List<SimpleDraggablePanel> visiblePanels = new ArrayList<>();
            for (GuiEventListener child : amcs.upperLayerChildren) {
                if (child instanceof SimpleDraggablePanel panel && panel.visible) {
                    visiblePanels.add(panel);
                }
            }
            visiblePanels.sort(Comparator.comparingInt(SimpleDraggablePanel::getZIndex));
            
            int panelZ = AbstractModContainerScreen.BASE_PANEL_Z;
            RenderSystem.enableDepthTest();
            for (GuiEventListener upperLayerChild : amcs.upperLayerChildren) {
                if (upperLayerChild instanceof Renderable renderable) {
                    pose.translate(0, 0, panelZ);
                    renderable.render(gui, mx, my, deltaTick);
                    panelZ += AbstractModContainerScreen.PANEL_Z_INCREMENT;
                }
            }
            
            int tooltipZ = amcs.getMaxPanelZ() + AbstractModContainerScreen.TOOLTIP_Z_OFFSET;
            RenderSystem.disableDepthTest();
            pose.pushPose();
            pose.translate(0, 0, tooltipZ);
            for (SimpleDraggablePanel panel : visiblePanels) {
                panel.renderChildTooltips(gui, mx, my, tooltipZ);
            }
            pose.popPose();
            
            if (amcs instanceof AbstractMachineScreen<?> machineScreen) {
                machineScreen.renderMachineTooltips(gui, tooltipZ);
            }
            
            if (amcs instanceof AbstractPipeFilterScreen<?> pipeFilterScreen) {
                pipeFilterScreen.renderExtraTooltips(gui, mx, my, tooltipZ);
            }
            
            for (GuiEventListener child : amcs.children()) {
                if (child instanceof TooltipRenderable tooltipRenderable) {
                    tooltipRenderable.renderTooltip(gui, mx, my, tooltipZ);
                }
            }
            
            amcs.renderTooltip(gui, mx, my);
            
            renderCarriedItem(gui, amcs, mx, my, tooltipZ + 100);
            
            if (!recipeViewDraggedStack.isEmpty()) {
                renderFloatingItem(gui, recipeViewDraggedStack, amcs, mx, my, tooltipZ + 200);
                recipeViewDraggedStack = ItemStack.EMPTY;
            }
            if (!recipeViewDraggedFluid.isEmpty()) {
                renderFloatingFluid(gui, recipeViewDraggedFluid, amcs, mx, my, tooltipZ + 200);
                recipeViewDraggedFluid = FluidStack.EMPTY;
            }
            
            RenderSystem.restoreGlState(GL_STATE);
            pose.popPose();
        }
    }
    
    private static void renderCarriedItem(GuiGraphics gui, AbstractModContainerScreen<?> screen, int mx, int my, int zIndex) {
        ItemStack carried = screen.getMenu().getCarried();
        if (!carried.isEmpty()) {
            renderFloatingItem(gui, carried, screen, mx, my, zIndex);
        }
    }
    
    private static void renderFloatingItem(GuiGraphics gui, ItemStack floating, AbstractModContainerScreen<?> screen, int mx, int my, int zIndex) {
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, zIndex);
        RenderSystem.disableDepthTest();
        gui.renderItem(floating, mx - 8, my - 8);
        gui.renderItemDecorations(Minecraft.getInstance().font, floating, mx - 8, my - 8);
        RenderSystem.enableDepthTest();
        pose.popPose();
    }
    
    private static void renderFloatingFluid(GuiGraphics gui, FluidStack floating, AbstractModContainerScreen<?> screen, int mx, int my, int zIndex) {
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, zIndex);
        RenderSystem.disableDepthTest();
        FluidRendererHelper.renderFluidStack(gui, floating, mx - 8, my - 8, 16, 16);
        RenderSystem.enableDepthTest();
        pose.popPose();
    }
    
    @SubscribeEvent
    public static void keyInputEvent(InputEvent.Key event) {
        int key = event.getKey();
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        CompoundTag tag = player.getPersistentData();
        boolean isStill = player.getDeltaMovement().length() < 1.0E-4;
        if (key == mc.options.keyJump.getKey().getValue() && !player.onGround() && !player.isPassenger() && !isStill) {
            ItemStack charm = CharmManager.findCharm(player, ModItems.SKYBOUND_TREADS.get());
            int jumps = tag.getInt(ILivingJumper.KEY);
            if (!charm.isEmpty() && player instanceof ILivingJumper jumper && jumps > 0 && player.noJumpDelay == 0) {
                jumper.jumpGround();
                Channel.sendToServer(new ConsumeJumpTokenToServer());
                tag.putInt(ILivingJumper.KEY, jumps - 1);
                player.noJumpDelay = 10;
            }
        }
        if (key == mc.options.keySprint.getKey().getValue() && !player.onGround() && !player.isPassenger() && !isStill) {
            ItemStack charm = CharmManager.findCharm(player, ModItems.GALE_SASH.get());
            int dashes = tag.getInt(ILivingDasher.KEY);
            if (!charm.isEmpty() && player instanceof ILivingDasher dasher && dashes > 0 && dasher.dashCooldown() == 0) {
                dasher.dashAir(player);
                Channel.sendToServer(new ConsumeDashTokenToServer());
                tag.putInt(ILivingDasher.KEY, dashes - 1);
                dasher.setDashCooldown(10);
            }
        }
    }
    
    @SubscribeEvent
    public static void screenInitPost(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof GenericSidedMachineScreen gsms) {
            int x = gsms.getGuiLeft();
            int y = gsms.getGuiTop();
            int width = gsms.getXSize();
            int height = gsms.getYSize();
            if (gsms.getMachine() instanceof Filterable filterable) {
            
            }
        }
    }
    
    private static final ItemStack[] BOOKS = new ItemStack[] {
        Items.BOOK.getDefaultInstance(),
        Items.WRITABLE_BOOK.getDefaultInstance()
    };
    
    @SubscribeEvent
    public static void renderEtarSlotEvent(RenderEtarSlotEvent event) {
        AbstractModContainerScreen<?> screen = event.getScreen();
        AbstractModContainerMenu menu = screen.getMenu();
        Slot slot = event.getSlot();
        GuiGraphics gui = event.getGui();
        if (menu instanceof AbstractMachineMenu amm) {
            BlockEntity be = amm.getBlockEntity();
            var inv = amm.getBeInventory();
            if (be instanceof BookDuplicator && inv != null && inv.getStackInSlot(1).isEmpty() && slot.index == 37) {
                ItemRendererHelper.renderItemCarousel(gui, BOOKS, event.getX() + 1, event.getY() + 1, 96);
            }
            if (be instanceof Disenchanter && inv != null && inv.getStackInSlot(1).isEmpty() && slot.index == 37) {
                ItemRendererHelper.renderFakeItemTransparent(gui, Items.BOOK.getDefaultInstance(),
                    event.getX() + 1, event.getY() + 1, 96 * 255);
            }
            if (be instanceof Harvester && inv != null && inv.getStackInSlot(13).isEmpty() && slot.index == 49) {
                ItemRendererHelper.renderItemCarousel(gui, Ingredient.of(Harvester.hoe_tillables).getItems(),
                    event.getX() + 1, event.getY() + 1, 96);
            }
            if (be instanceof Smithinator && inv != null) {
                if (slot.index == 36 && inv.getStackInSlot(0).isEmpty()) {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(new ResourceLocation("item/empty_slot_smithing_template_netherite_upgrade"));
                    gui.blit(event.getX() + 1, event.getY() + 1, 0, 16, 16,
                        sprite, 1, 1, 1, 1);
                }
                if (slot.index == 38 && inv.getStackInSlot(2).isEmpty()) {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(new ResourceLocation("item/empty_slot_ingot"));
                    gui.blit(event.getX() + 1, event.getY() + 1, 0, 16, 16,
                        sprite, 1, 1, 1, 1);
                }
            }
        }
    }
}

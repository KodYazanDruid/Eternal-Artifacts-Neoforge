package com.sonamorningstar.eternalartifacts.event.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
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
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.KnapsackScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.GenericSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.tooltip.ItemTooltipManager;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SlotWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.client.render.ModRenderTypes;
import com.sonamorningstar.eternalartifacts.container.BookDuplicatorMenu;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
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
import net.minecraft.world.Container;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
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
            ListTag itemEntries = filterData.getList("ItemFilters", 10);for (int i = 0; i < itemEntries.size(); i++) {
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
            if (!entries.isEmpty() && entries.stream().noneMatch(FilterEntry::isEmpty)){
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
            if (owner instanceof DimensionalAnchor anchor) {
                int x = gsms.getGuiLeft();
                int y = gsms.getGuiTop();
                GuiDrawer.drawFramedBackground(gui, x + 37, y + 18, 100, 50, 1, 0xff000000, 0xff404040, 0xffa0a0a0);
                gui.drawString(screen.getMinecraft().font,
                    Component.translatable(ModConstants.GUI.withSuffix("forceload.loaded_chunks_count"),
                        anchor.getForcedChunks().size()),
                    x + 40, y + 20, 0xfff0f0f0, false
                );
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderScreenLowPrio(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        GuiGraphics gui = event.getGuiGraphics();
        int mx = event.getMouseX();
        int my = event.getMouseY();
        float deltaTick = event.getPartialTick();
        if (screen instanceof AbstractModContainerScreen<?> amcs) {
            PoseStack pose = gui.pose();
            pose.pushPose();
            pose.translate(0.0D, 0.0D, 30.0F);
            for (GuiEventListener upperLayerChild : amcs.upperLayerChildren) {
                if (upperLayerChild instanceof Renderable renderable) {
                    renderable.render(gui, mx, my, deltaTick);
                }
            }
            pose.popPose();
        }
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
            if (gsms.getMachine() instanceof BlockBreaker breaker || gsms.getMachine() instanceof BlockPlacer placer) {
                SimpleDraggablePanel filterPanel = new SimpleDraggablePanel(Component.empty(),
                    x + 23, y + 8, 129, 70,
                    SimpleDraggablePanel.Bounds.of(0, 0, gsms.width, gsms.height));
                filterPanel.visible = false;
                filterPanel.active = false;
                
                event.addListener(SpriteButton.builder(Component.empty(), (b, i) -> {
                    filterPanel.visible = true;
                    filterPanel.active = true;
                }, new ResourceLocation(MODID, "textures/gui/sprites/blank_ender.png")).bounds(x + width - 21, y + 3, 18, 18).build());
                filterPanel.addClosingButton();
                
                SlotWidget slotWidget = new SlotWidget(new Slot(new SimpleContainer(Items.APPLE.getDefaultInstance()), 0, 0, 0), Component.empty());
                
                filterPanel.addChildren((fx, fy, fW, fH) -> {
                    slotWidget.setPosition(fx + 10, fy + 10);
                    return slotWidget;
                });
                
                gsms.addUpperLayerChild(filterPanel);
            }
        }
    }
    
    @SubscribeEvent
    public static void renderEtarSlotEvent(RenderEtarSlotEvent event) {
        AbstractModContainerScreen<?> screen = event.getScreen();
        AbstractModContainerMenu menu = screen.getMenu();
        Slot slot = event.getSlot();
        GuiGraphics gui = event.getGui();
        if (menu instanceof BookDuplicatorMenu bd) {
            var inv = bd.getBeInventory();
            if (slot.index == 38 && inv != null && inv.getStackInSlot(2).isEmpty()) {
                ItemRendererHelper.renderFakeItemTransparent(gui, Items.BOOK.getDefaultInstance(), event.getX() + 1, event.getY() + 1, 96);
            }
        }
        if (menu instanceof GenericMachineMenu gms) {
            BlockEntity be = gms.getBlockEntity();
            var inv = gms.getBeInventory();
            if (be instanceof Disenchanter && inv != null &&
                    inv.getStackInSlot(1).isEmpty() && slot.index == 37) {
                ItemRendererHelper.renderFakeItemTransparent(gui, Items.BOOK.getDefaultInstance(), event.getX() + 1, event.getY() + 1, 96);
            }
            if (be instanceof Smithinator && inv != null) {
                if (slot.index == 36 && inv.getStackInSlot(0).isEmpty()) {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(new ResourceLocation("item/empty_slot_smithing_template_netherite_upgrade"));
                    gui.blit(event.getX() + 1, event.getY() + 1, 0, 16, 16, sprite,
                        1.0F, 1.0F, 1.0F, 1.0F);
                }
                if (slot.index == 38 && inv.getStackInSlot(2).isEmpty()) {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(new ResourceLocation("item/empty_slot_ingot"));
                    gui.blit(event.getX() + 1, event.getY() + 1, 0, 16, 16, sprite,
                        1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }
}

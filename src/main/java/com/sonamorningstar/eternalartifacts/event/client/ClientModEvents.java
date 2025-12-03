package com.sonamorningstar.eternalartifacts.event.client;

import com.sonamorningstar.eternalartifacts.api.client.ClientFilterTooltip;
import com.sonamorningstar.eternalartifacts.api.client.ClientFiltersClampedTooltip;
import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.*;
import com.sonamorningstar.eternalartifacts.client.ColorUtils;
import com.sonamorningstar.eternalartifacts.client.RetexturedColor;
import com.sonamorningstar.eternalartifacts.client.gui.overlay.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.base.MultiBlockRenderer;
import com.sonamorningstar.eternalartifacts.client.render.entity.*;
import com.sonamorningstar.eternalartifacts.client.render.item.PortableBatteryLayer;
import com.sonamorningstar.eternalartifacts.client.render.item.PortableFurnaceLayer;
import com.sonamorningstar.eternalartifacts.client.resources.model.*;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.*;
import com.sonamorningstar.eternalartifacts.client.shader.SpellShaders;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.BatteryBox;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.client.*;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.CutlassModifier;
import com.sonamorningstar.eternalartifacts.event.custom.CreateConfigWidgetEvent;
import com.sonamorningstar.eternalartifacts.event.custom.RegisterTabHoldersEvent;
import com.sonamorningstar.eternalartifacts.event.custom.RegisterUnrenderableOverridesEvent;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.MachineConfigurationToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import io.netty.buffer.Unpooled;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    private static final ResourceLocation allow = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/allow.png");
    private static final ResourceLocation deny = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/deny.png");
    private static final ResourceLocation input = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/input.png");
    private static final ResourceLocation output = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/output.png");
    private static final ResourceLocation auto_input = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_input.png");
    private static final ResourceLocation auto_output = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_output.png");
    private static final ResourceLocation auto_input_enabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_input_enabled.png");
    private static final ResourceLocation auto_output_enabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/auto_output_enabled.png");
    private static final ResourceLocation item_transfer = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/item_transfer.png");
    private static final ResourceLocation fluid_transfer = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/fluid_transfer.png");
    private static final ResourceLocation energy_transfer = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/energy_transfer.png");
    private static final ResourceLocation item_transfer_disabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/item_transfer_disabled.png");
    private static final ResourceLocation fluid_transfer_disabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/fluid_transfer_disabled.png");
    private static final ResourceLocation energy_transfer_disabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/energy_transfer_disabled.png");
    private static final ResourceLocation redstone_active = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/redstone_active.png");
    private static final ResourceLocation redstone_passive = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/redstone_passive.png");
    private static final ResourceLocation redstone_ignored = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/redstone_ignored.png");
    private static final ResourceLocation batbox_full = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/batbox_full.png");
    private static final ResourceLocation batbox_empty = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/batbox_empty.png");
    private static final ResourceLocation batbox_percentage_below = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/batbox_percentage_below.png");
    private static final ResourceLocation batbox_percentage_above = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/batbox_percentage_above.png");
    private static final ResourceLocation batbox_percentage_exact = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/batbox_percentage_exact.png");
    private static final ResourceLocation block_mode_enabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/block_mode_enabled.png");
    private static final ResourceLocation fluid_mode_enabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/fluid_mode_enabled.png");
    private static final ResourceLocation continuous_mode_enabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/continuous_mode_enabled.png");
    private static final ResourceLocation block_mode_disabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/block_mode_disabled.png");
    private static final ResourceLocation fluid_mode_disabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/fluid_mode_disabled.png");
    private static final ResourceLocation continuous_mode_disabled = new ResourceLocation(MODID,"textures/gui/sprites/sided_buttons/continuous_mode_disabled.png");
    
    @SubscribeEvent
    public static void registerConfigWidgets(CreateConfigWidgetEvent event) {
        event.register(SideConfig.class, (config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            AbstractModContainerScreen<?> screen = ctx.screen;
            
            if (!(screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            List<SimpleDraggablePanel.WidgetPosition> sideButtons = new ArrayList<>();
            
            for (int i = 0; i < 6; i++) {
				
				Direction dir = Direction.from3DDataValue(i);
                String sideName = switch (i)  {
                    case 0 -> "up";
                    case 1 -> "left";
                    case 2 -> "front";
                    case 3 -> "right";
                    case 4 -> "down";
                    case 5 -> "back";
                    default -> "unknown";
                };
                
                Function<Direction, SideConfig.TransferType> type = d -> config.getSides().get(d);
                SpriteButton sideButton = SpriteButton.builder(Component.empty(), (button, key) -> {
                    if (Screen.hasShiftDown()) {
                        if (key == 0) config.getSides().put(dir, SideConfig.TransferType.NONE);
                        else if (key == 1) config.getSides().put(dir, SideConfig.TransferType.DEFAULT);
                    } else {
                        if (key == 0) config.cycleNext(dir);
                        else if (key == 1) config.cyclePrev(dir);
                    }
                    button.setTextures(getTextureForTransferType(type.apply(dir)));
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    config.writeToServer(buf);
                    Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
                }, getTextureForTransferType(config.getSides().get(dir)))
                    .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable(sideName)
                        .append(": ").append(ModConstants.GUI.withSuffixTranslatable(type.apply(dir).toString().toLowerCase())))
                    .size(9, 9).build();
                
                /*panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(4, 4, 29, 29));
                panel.addChildren((fx, fy, fw, fh) -> {
                    switch (finalI) {
                        case 0 -> sideButton.setPosition(fx + 14, fy + 4);
                        case 1 -> sideButton.setPosition(fx + 4, fy + 14);
                        case 2 -> sideButton.setPosition(fx + 14, fy + 14);
                        case 3 -> sideButton.setPosition(fx + 24, fy + 14);
                        case 4 -> sideButton.setPosition(fx + 14, fy + 24);
                        case 5 -> sideButton.setPosition(fx + 24, fy + 24);
                    }
                    return sideButton;
                });*/
                // Relative pozisyonlar
                int relX = switch (i) {
                    case 0, 2, 4 -> 10; // up, front, down - ortada
                    case 1 -> 0;        // left
                    case 3, 5 -> 20;    // right, back
                    default -> 0;
                };
                
                int relY = switch (i) {
                    case 0 -> 0;        // up
                    case 1, 2, 3 -> 10; // left, front, right - ortada
                    case 4, 5 -> 20;    // down, back
                    default -> 0;
                };
                
                sideButtons.add(new SimpleDraggablePanel.WidgetPosition(sideButton, relX, relY));
            }
            panel.addWidgetGroup(sideButtons, 1);
        });
        
        event.register(AutoTransferConfig.class, (config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            MachineConfiguration configs = mbe.getConfiguration();
            
            BooleanSupplier isInput = config::isInput;
            SpriteButton autoInput = SpriteButton.builder(Component.empty(), (button, key) -> {
                boolean newValue = !config.isInput();
                config.setInput(newValue);
                button.setTextures(newValue ? auto_input_enabled : auto_input);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                config.writeToServer(buf);
                Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
            }, isInput.getAsBoolean() ? auto_input_enabled : auto_input).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("auto_input")
                    .append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isInput() ? "enabled" : "disabled"))).build();
            
            BooleanSupplier isOutput = config::isOutput;
            SpriteButton autoOutput = SpriteButton.builder(Component.empty(), (button, key) -> {
                boolean newValue = !config.isOutput();
                config.setOutput(newValue);
                button.setTextures(newValue ? auto_output_enabled : auto_output);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                config.writeToServer(buf);
                Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
            }, isOutput.getAsBoolean() ? auto_output_enabled : auto_output).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("auto_output")
                    .append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isOutput() ? "enabled" : "disabled"))).build();
            
            /*panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(34, 4, 9, 19));
            panel.addChildren((fx, fy, fw, fh) -> {
                autoInput.setPosition(fx + 34, fy + 4);
                return autoInput;
            });
            panel.addChildren((fx, fy, fw, fh) -> {
                autoOutput.setPosition(fx + 34, fy + 14);
                return autoOutput;
            });*/
            panel.addWidgetGroup(List.of(
                new SimpleDraggablePanel.WidgetPosition(autoInput, 30, 0),
                new SimpleDraggablePanel.WidgetPosition(autoOutput, 30, 10)
            ), 1);
        });
        
        event.register(ReverseToggleConfig.class, "block_mode", ((config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            BooleanSupplier isDisabled = config::isDisabled;
            SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
                boolean newValue = !config.isDisabled();
                config.setDisabled(newValue);
                button.setTextures(newValue ? block_mode_disabled : block_mode_enabled);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                config.writeToServer(buf);
                Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
            }, isDisabled.getAsBoolean() ? block_mode_disabled : block_mode_enabled).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("block_mode")
                    .append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isDisabled() ? "disabled" : "enabled"))).build();
            
            panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(64, 4, 9, 9));
            panel.addChildren((fx, fy, fw, fh) -> {
                btn.setPosition(fx + 64, fy + 4);
                return btn;
            });
        }));
        event.register(ReverseToggleConfig.class, "fluid_mode", ((config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            BooleanSupplier isDisabled = config::isDisabled;
            SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
                    boolean newValue = !config.isDisabled();
                    config.setDisabled(newValue);
                    button.setTextures(newValue ? fluid_mode_disabled : fluid_mode_enabled);
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    config.writeToServer(buf);
                    Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
                }, isDisabled.getAsBoolean() ? fluid_mode_disabled : fluid_mode_enabled).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("fluid_mode")
                    .append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isDisabled() ? "disabled" : "enabled"))).build();
            
            panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(64, 14, 9, 9));
            panel.addChildren((fx, fy, fw, fh) -> {
                btn.setPosition(fx + 64, fy + 14);
                return btn;
            });
        }));
        
        event.register(ReverseToggleConfig.class, "item_transfer", (config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            BooleanSupplier isDisabled = config::isDisabled;
            SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
                boolean newValue = !config.isDisabled();
                config.setDisabled(newValue);
                button.setTextures(newValue ? item_transfer_disabled : item_transfer);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                config.writeToServer(buf);
                Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
            }, isDisabled.getAsBoolean() ? item_transfer_disabled : item_transfer).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("item_transportation")
                    .append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isDisabled() ? "disabled" : "enabled"))).build();
            
            panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(44, 4, 9, 9));
            panel.addChildren((fx, fy, fw, fh) -> {
                btn.setPosition(fx + 44, fy + 4);
                return btn;
            });
        });
        event.register(ReverseToggleConfig.class, "fluid_transfer", (config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            BooleanSupplier isDisabled = config::isDisabled;
            SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
                boolean newValue = !config.isDisabled();
                config.setDisabled(newValue);
                button.setTextures(newValue ? fluid_transfer_disabled : fluid_transfer);
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                config.writeToServer(buf);
                Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
            }, isDisabled.getAsBoolean() ? fluid_transfer_disabled : fluid_transfer).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("fluid_transportation")
                    .append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isDisabled() ? "disabled" : "enabled"))).build();
            
            panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(44, 14, 9, 9));
            panel.addChildren((fx, fy, fw, fh) -> {
                int yOffset = mbe.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, mbe.getBlockPos(), null) == null ? 4 : 14;
                btn.setPosition(fx + 44, fy + yOffset);
                return btn;
            });
        });
        event.register(ReverseToggleConfig.class, "energy_transfer", (config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            BooleanSupplier isDisabled = config::isDisabled;
            SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
                    boolean newValue = !config.isDisabled();
                    config.setDisabled(newValue);
                    button.setTextures(newValue ? energy_transfer_disabled : energy_transfer);
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    config.writeToServer(buf);
                    Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
                }, isDisabled.getAsBoolean() ? energy_transfer_disabled : energy_transfer).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("energy_transportation")
                    .append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isDisabled() ? "disabled" : "enabled"))).build();
            
            panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(44, 24, 9, 9));
            panel.addChildren((fx, fy, fw, fh) -> {
                boolean hasInv = mbe.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, mbe.getBlockPos(), null) == null;
                boolean hasTank = mbe.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, mbe.getBlockPos(), null) == null;
                int yOffset = hasInv && hasTank ? 24 : hasInv || hasTank ? 14 : 4;
                btn.setPosition(fx + 44, fy + yOffset);
                return btn;
            });
        });
        
        event.register(RedstoneConfig.class, (config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            Supplier<RedstoneConfig.Mode> mode = config::getMode;
            SpriteButton redstoneButton = SpriteButton.builder(Component.empty(), (button, key) -> {
                config.cycleMode();
                button.setTextures(getTextureForRedstoneType(mode.get()));
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                config.writeToServer(buf);
                Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
            }, getTextureForRedstoneType(config.getMode())).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("redstone")
                    .append(": ").append(getComponentForRedstone(mode.get()))).build();
            
            panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(54, 4, 9, 9));
            panel.addChildren((fx, fy, fw, fh) -> {
                int xOffset = (mbe instanceof AbstractDynamo<?>) ? 4 : 54;
                redstoneButton.setPosition(fx + xOffset, fy + 4);
                return redstoneButton;
            });
        });
        
        event.register(BatteryBoxExportConfig.class, (config, ctx) -> {
            SimpleDraggablePanel panel = ctx.panel;
            if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
            if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
            
            Supplier<BatteryBoxExportConfig.ExportMode> mode = config::getExportMode;
            Supplier<Float> percGetter = config::getPercentage;
            SpriteButton exportButton = SpriteButton.builder(Component.empty(), (button, key) -> {
                config.cycleNextMode();
                button.setTextures(getTextureForBatBox(mode.get()));
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                config.writeToServer(buf);
                Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
            }, getTextureForBatBox(mode.get())).size(9, 9)
                .addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("battery_box_export")
                .append(": ").append(getComponentForBatBox(mode.get(), percGetter.get()))).build();
            
            ExtendedSlider slider = new ExtendedSlider(0, 0, panel.getWidth() - 8, 10, Component.empty(), Component.empty(),
                0.0d, 100.0d, percGetter.get(), 0.1d, 0, false) {
                @Override
                protected void applyValue() {
                    super.applyValue();
                    config.setPercentage(((float) getValue()));
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    config.writeToServer(buf);
                    Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
                }
            };
            
            panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(34, 24, 9, 9));
            panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(4, 34, panel.getWidth() - 8, 10));
            panel.addChildren((fx, fy, fw, fh) -> {
                exportButton.setPosition(fx + 34, fy + 24);
                return exportButton;
            });
            panel.addChildren((fx, fy, fw, fh) -> {
                slider.setPosition(fx + 4, fy + 34);
                return slider;
            });
        });
    }
    
    private static ResourceLocation getTextureForTransferType(SideConfig.TransferType transferType) {
        if(transferType == SideConfig.TransferType.NONE) return deny;
        if(transferType == SideConfig.TransferType.PULL) return input;
        if(transferType == SideConfig.TransferType.PUSH) return output;
        return allow;
    }
    
    private static ResourceLocation getTextureForRedstoneType(RedstoneConfig.Mode redstoneType) {
        if(redstoneType == RedstoneConfig.Mode.IGNORE) return redstone_ignored;
        if(redstoneType == RedstoneConfig.Mode.HIGH) return redstone_active;
        if(redstoneType == RedstoneConfig.Mode.LOW) return redstone_passive;
        return redstone_ignored;
    }
    
    private static MutableComponent getComponentForRedstone(RedstoneConfig.Mode type) {
        if (type == RedstoneConfig.Mode.LOW) return ModConstants.GUI.withSuffixTranslatable("redstone_passive");
        if (type == RedstoneConfig.Mode.HIGH) return ModConstants.GUI.withSuffixTranslatable("redstone_active");
        return ModConstants.GUI.withSuffixTranslatable("redstone_default");
    }
    
    private static ResourceLocation getTextureForBatBox(BatteryBoxExportConfig.ExportMode mode) {
        if (mode == BatteryBoxExportConfig.ExportMode.EMPTY) return batbox_empty;
        if (mode == BatteryBoxExportConfig.ExportMode.PERCENTAGE_BELOW) return batbox_percentage_below;
        if (mode == BatteryBoxExportConfig.ExportMode.PERCENTAGE_ABOVE) return batbox_percentage_above;
        if (mode == BatteryBoxExportConfig.ExportMode.PERCENTAGE_EXACT) return batbox_percentage_exact;
        return batbox_full;
    }
    
    private static MutableComponent getComponentForBatBox(BatteryBoxExportConfig.ExportMode mode, float perc) {
        if (mode == BatteryBoxExportConfig.ExportMode.EMPTY) return ModConstants.GUI.withSuffixTranslatable("battery_box_export_empty");
        if (mode == BatteryBoxExportConfig.ExportMode.PERCENTAGE_BELOW)
            return Component.translatable(ModConstants.GUI.withSuffix("battery_box_export_percentage_below"), perc);
        if (mode == BatteryBoxExportConfig.ExportMode.PERCENTAGE_ABOVE)
            return Component.translatable(ModConstants.GUI.withSuffix("battery_box_export_percentage_above"), perc);
        if (mode == BatteryBoxExportConfig.ExportMode.PERCENTAGE_EXACT)
            return Component.translatable(ModConstants.GUI.withSuffix("battery_box_export_percentage_exact"), perc);
        return ModConstants.GUI.withSuffixTranslatable("battery_box_export_full");
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.ANVILINATOR.get(), AnvilinatorScreen::new);
        event.register(ModMenuTypes.BIOFURNACE.get(), BioFurnaceScreen::new);
        event.register(ModMenuTypes.BOOK_DUPLICATOR.get(), BookDuplicatorScreen::new);
        event.register(ModMenuTypes.BATTERY_BOX.get(), BatteryBoxScreen::new);
        event.register(ModMenuTypes.DYNAMO_MENU.get(), FluidDynamoScreen::new);
        event.register(ModMenuTypes.KNAPSACK.get(), KnapsackScreen::new);
        event.register(ModMenuTypes.NOUS_TANK.get(), NousTankScreen::new);
        event.register(ModMenuTypes.TANK_KNAPSACK.get(), TankKnapsackScreen::new);
        event.register(ModMenuTypes.TANK_KNAPSACK_ITEM.get(), TankKnapsackScreen::new);
        event.register(ModMenuTypes.TESSERACT.get(), TesseractScreen::new);
        event.register(ModMenuTypes.ITEM_DYNAMO.get(), ItemDynamoScreen::new);
        event.register(ModMenuTypes.PICTURE_SCREEN.get(), PictureScreenScreen::new);
        event.register(ModMenuTypes.DEEP_ITEM_STORAGE_UNIT.get(), DeepItemStorageScreen::new);
        event.register(ModMenuTypes.DEEP_FLUID_STORAGE_UNIT.get(), DeepFluidStorageUnitScreen::new);
    }

    @SubscribeEvent
    public static void registerTabHolder(RegisterTabHoldersEvent event) {
        event.register(ModItems.KNAPSACK.get(), ModInventoryTabs.KNAPSACK.get());
        event.register(ModItems.ENDER_KNAPSACK.get(), ModInventoryTabs.ENDER_KNAPSACK.get());
        event.register(ModItems.TANK_KNAPSACK.get(), ModInventoryTabs.TANK_KNAPSACK.get());
        event.register(ModItems.PORTABLE_CRAFTER.get(), ModInventoryTabs.CRAFTER.get());
        event.register(Items.COD, ModInventoryTabs.FISH_TAB.get());
        event.register(ModItems.PORTABLE_BATTERY.get(), ModInventoryTabs.PORTABLE_BATTERY.get());
        event.register(ModItems.PORTABLE_FURNACE.get(), ModInventoryTabs.PORTABLE_FURNACE.get());
    }

    @SubscribeEvent
    public static void registerUnrenderableOverrides(RegisterUnrenderableOverridesEvent event) {
        event.register(EquipmentSlot.HEAD, ModTags.Items.SHULKER_SHELL);
        event.register(EquipmentSlot.HEAD, Items.BUCKET);
        event.register(EquipmentSlot.HEAD, Items.PLAYER_HEAD);
        event.register(EquipmentSlot.HEAD, ModItems.DEATH_CAP.get());
        CutlassModifier.ENTITY_HEAD_MAP.values().forEach(item -> {
            if(item != Items.DRAGON_HEAD) event.register(EquipmentSlot.HEAD, item);
        });
    }
    
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        ModKeyMappings.KEY_MAPPINGS.forEach(event::register);
    }
    
    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        SpellShaders.registerShaders(event);
    }

    @SubscribeEvent
    public static void registerColorHandlerItem(RegisterColorHandlersEvent.Item event) {
        event.register((stack, ti) -> {
            BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
            return BlockColors.createDefault().getColor(blockstate, null, null, ti);
        }, ModBlocks.FOUR_LEAF_CLOVER.asItem());
        event.register(new RetexturedColor(), ModItems.GARDENING_POT.get());
        event.register(new RetexturedColor(), ModItems.FANCY_CHEST.get());
        ModFluids.FLUIDS.getEntries().forEach(holder -> {
            if (ModFluids.FLUIDS.isGeneric(holder) && holder.getBucketItem() != null) event.register((stack, ti) -> ti == 1 ? holder.getTintColor() : 0xFFFFFFFF, holder.getBucketItem());
        });
        event.register(ColorUtils::getColorFromNBT, ModItems.LIGHTSABER);
    }

    @SubscribeEvent
    public static void registerColorHandlerBlock(RegisterColorHandlersEvent.Block event) {
        event.register(new RetexturedColor(), ModBlocks.GARDENING_POT.get());
        event.register(new RetexturedColor(), ModBlocks.FANCY_CHEST.get());
        event.register((state, level, pos, ti) ->
            level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos)
                    : GrassColor.getDefaultColor()
        , ModBlocks.FOUR_LEAF_CLOVER.get());
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(new ResourceLocation(MODID, "colored_block"), ColoredBlockModel.LOADER);
        event.register(new ResourceLocation(MODID, "retextured"), RetexturedModel.LOADER);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "compass_overlay"), new CompassOverlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "recovery_compass_overlay"), new RecoveryCompassOverlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "clock_overlay"), new ClockOverlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "map_overlay"), new MapOverlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "hammering_recipe_overlay"), new HammeringRecipeOverlay());
    }
    
    @SubscribeEvent
    public static void registerSkullModelLayers(EntityRenderersEvent.CreateSkullModels event) {
        EntityModelSet modelSet = event.getEntityModelSet();
        event.registerSkullModel(ModSkullType.DROWNED,
            new TwoLayerSkullModel(
                ModSkullType.DROWNED,
                modelSet.bakeLayer(ModModelLayers.DROWNED_HEAD),
                modelSet.bakeLayer(ModModelLayers.DROWNED_HEAD_OVERLAY)
            )
        );
        event.registerSkullModel(ModSkullType.HUSK, new SkullModel(modelSet.bakeLayer(ModModelLayers.HUSK_HEAD)));
        event.registerSkullModel(ModSkullType.STRAY,
            new TwoLayerSkullModel(
                ModSkullType.STRAY,
                modelSet.bakeLayer(ModModelLayers.STRAY_SKULL),
                modelSet.bakeLayer(ModModelLayers.STRAY_SKULL_OVERLAY)
            )
        );
        event.registerSkullModel(ModSkullType.BLAZE, new SkullModel(modelSet.bakeLayer(ModModelLayers.BLAZE_HEAD)));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.DEMON_EYE_LAYER, DemonEyeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.DUCK_LAYER, DuckModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TORNADO_LAYER, TornadoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.METEORITE_LAYER, MeteoriteModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SPELL_TOME_LAYER, SpellTomeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CHARGED_SHEEP_SWIRL, () -> ChargedSheepRenderer.createFurSwirlLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(ModModelLayers.MISSILE_LAYER, MissileModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.PORTABLE_BATTERY_LAYER, PortableBatteryModel::createLayer);
        event.registerLayerDefinition(ModModelLayers.BUCKET_LAYER, BucketHeadModel::createLayer);

        //event.registerLayerDefinition(ModModelLayers.FANCY_CHEST_LAYER, FancyChestRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.JAR_LAYER, JarRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.DYNAMO_LAYER, DynamoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.NOUS_TANK_LAYER, NousTankRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.OIL_REFINERY_LAYER, OilRefineryRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ENERGY_DOCK_LAYER, EnergyDockBlockEntityRenderer::createSingleBodyLayer);
        
        event.registerLayerDefinition(ModModelLayers.DROWNED_HEAD, () -> TwoLayerSkullModel.createBaseLayer(64, 64));
        event.registerLayerDefinition(ModModelLayers.DROWNED_HEAD_OVERLAY, () -> TwoLayerSkullModel.createOverlayLayer(64, 64));
        event.registerLayerDefinition(ModModelLayers.HUSK_HEAD, () -> TwoLayerSkullModel.createBaseLayer(64, 64));
        event.registerLayerDefinition(ModModelLayers.STRAY_SKULL, () -> TwoLayerSkullModel.createBaseLayer(64, 32));
        event.registerLayerDefinition(ModModelLayers.STRAY_SKULL_OVERLAY, () -> TwoLayerSkullModel.createOverlayLayer(64, 32));
        event.registerLayerDefinition(ModModelLayers.BLAZE_HEAD, () -> TwoLayerSkullModel.createBaseLayer(64, 32));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FANCY_CHEST.get(), FancyChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.JAR.get(), JarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NOUS_TANK.get(), NousTankRenderer::new);
        event.registerBlockEntityRenderer(ModMachines.OIL_REFINERY.getBlockEntity(), OilRefineryRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ENERGY_DOCK.get(), EnergyDockBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TESSERACT.get(), ctx -> new TesseractRenderer());
        event.registerBlockEntityRenderer(ModBlockEntities.SKULL.get(), ModSkullBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MACHINE_WORKBENCH.get(), MachineWorkbenchRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PICTURE_SCREEN.get(), PictureScreenRenderer::new);
        
        event.registerBlockEntityRenderer(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), DynamoRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOLID_COMBUSTION_DYNAMO.get(), DynamoRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ALCHEMICAL_DYNAMO.get(), DynamoRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CULINARY_DYNAMO.get(), DynamoRenderer::new);

        event.registerBlockEntityRenderer(ModMachines.MOB_LIQUIFIER.getBlockEntity(), ctx -> new AreaRenderer<>());
        event.registerBlockEntityRenderer(ModBlockEntities.DEEP_ITEM_STORAGE_UNIT.get(), ctx -> new DSUItemRenderer());
        event.registerBlockEntityRenderer(ModBlockEntities.DEEP_FLUID_STORAGE_UNIT.get(), ctx -> new DSUFluidRenderer());

        event.registerBlockEntityRenderer(ModMultiblocks.PUMPJACK.getBlockEntity(), MultiBlockRenderer::new);
        event.registerBlockEntityRenderer(ModMultiblocks.GENERATOR.getBlockEntity(), MultiBlockRenderer::new);
        
        event.registerEntityRenderer(ModEntities.DEMON_EYE.get(), DemonEyeRenderer::new);
        event.registerEntityRenderer(ModEntities.PINKY.get(), PinkyRenderer::new);
        event.registerEntityRenderer(ModEntities.CHARGED_SHEEP.get(), ChargedSheepRenderer::new);
        event.registerEntityRenderer(ModEntities.DUCK.get(), DuckRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGICAL_BOOK.get(), MagicalBookRenderer::new);
        event.registerEntityRenderer(ModEntities.PRIMED_BLOCK.get(), TntRenderer::new);
        event.registerEntityRenderer(ModEntities.TORNADO.get(), TornadoRenderer::new);
        event.registerEntityRenderer(ModEntities.METEORITE.get(), MeteoriteRenderer::new);
        event.registerEntityRenderer(ModEntities.MISSILE.get(), MissileRenderer::new);
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE.get(), SpellProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.AMETHYST_ARROW.get(), AmethystArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.HONEY_SLIME.get(), HoneySlimeRenderer::new);
        event.registerEntityRenderer(ModEntities.PRISMARINE_ARROW.get(), PrismarineArrowRenderer::new);
    }

    @SubscribeEvent
    public static void attachLayers(EntityRenderersEvent.AddLayers event) {
        EntityRendererProvider.Context ctx = event.getContext();
        for (EntityType<?> type : event.getEntityTypes()) {
            EntityRenderer<?> renderer = event.getRenderer(type);
            if (renderer instanceof LivingEntityRenderer<?,?> ler) attachLayers(ler, ctx);
        }
        event.getSkins().forEach(renderer ->{
            LivingEntityRenderer<Player, EntityModel<Player>> skin = event.getSkin(renderer);
            attachLayers(Objects.requireNonNull(skin), ctx);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity, M extends EntityModel<T>> void attachLayers(LivingEntityRenderer<T, M> renderer, EntityRendererProvider.Context ctx) {
        if (renderer instanceof HumanoidMobRenderer<?, ?> hmr) {
            renderer.addLayer(new HolyDaggerLayer<>(renderer));
            renderer.addLayer(new ProtectiveAuraLayer<>(renderer));
            renderer.addLayer((RenderLayer<T, M>) new ShulkerShellLayer<>(hmr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new BucketLayer<>(hmr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new PortableBatteryLayer<>(hmr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new PortableFurnaceLayer<>(hmr));
        }
        if (renderer instanceof PlayerRenderer pr) {
            renderer.addLayer(new HolyDaggerLayer<>(renderer));
            renderer.addLayer(new ProtectiveAuraLayer<>(renderer));
            renderer.addLayer((RenderLayer<T, M>) new ShulkerShellLayer<>(pr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new BucketLayer<>(pr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new PortableBatteryLayer<>(pr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new PortableFurnaceLayer<>(pr));
        }
    }
    
    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ClientFilterTooltip.FilterTooltip.class, ClientFilterTooltip::new);
        event.register(ClientFiltersClampedTooltip.FiltersClampedTooltip.class, ClientFiltersClampedTooltip::new);
    }

}
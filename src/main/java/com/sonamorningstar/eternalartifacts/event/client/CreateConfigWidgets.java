package com.sonamorningstar.eternalartifacts.event.client;

import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SpriteButton;
import com.sonamorningstar.eternalartifacts.client.gui.widget.records.ButtonDrawContent;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.event.custom.CreateConfigWidgetEvent;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.MachineConfigurationToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreateConfigWidgets {
	private static final ResourceLocation CONFIG_SPRITES = new ResourceLocation(MODID,"textures/gui/sprites/config_sprites.png");
	
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
						button.setSprites(getTextureForTransferType(type.apply(dir)));
						FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
						config.writeToServer(buf);
						Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
					})
					.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable(sideName)
						.append(": ").append(ModConstants.GUI.withSuffixTranslatable(type.apply(dir).toString().toLowerCase(Locale.ROOT))))
					.size(9, 9).build();
				sideButton.setSprites(getTextureForTransferType(type.apply(dir)));
				
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
			
			ButtonDrawContent autoInputEnabledCtx = new ButtonDrawContent(9, 9);
			autoInputEnabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, 45, 0, 9, 9);
			ButtonDrawContent autoInputCtx = new ButtonDrawContent(9, 9);
			autoInputCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, 36, 0, 9, 9);
			
			BooleanSupplier isInput = config::isInput;
			SpriteButton autoInput = SpriteButton.builder(Component.empty(), (button, key) -> {
					boolean newValue = !config.isInput();
					config.setInput(newValue);
					button.setSprites(newValue ? autoInputEnabledCtx : autoInputCtx);
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					config.writeToServer(buf);
					Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
				}).size(9, 9)
				.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("auto_input")
					.append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isInput() ? "enabled" : "disabled"))).build();
			autoInput.setSprites(isInput.getAsBoolean() ? autoInputEnabledCtx : autoInputCtx);
			
			ButtonDrawContent autoOutputEnabledCtx = new ButtonDrawContent(9, 9);
			autoOutputEnabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, 63, 0, 9, 9);
			ButtonDrawContent autoOutputCtx = new ButtonDrawContent(9, 9);
			autoOutputCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, 54, 0, 9, 9);
			
			BooleanSupplier isOutput = config::isOutput;
			SpriteButton autoOutput = SpriteButton.builder(Component.empty(), (button, key) -> {
					boolean newValue = !config.isOutput();
					config.setOutput(newValue);
					button.setSprites(newValue ? autoOutputEnabledCtx : autoOutputCtx);
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					config.writeToServer(buf);
					Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
				}).size(9, 9)
				.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("auto_output")
					.append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isOutput() ? "enabled" : "disabled"))).build();
     
			autoOutput.setSprites(isOutput.getAsBoolean() ? autoOutputEnabledCtx : autoOutputCtx);
			
			panel.addWidgetGroup(List.of(
				new SimpleDraggablePanel.WidgetPosition(autoInput, 30, 0),
				new SimpleDraggablePanel.WidgetPosition(autoOutput, 30, 10)
			), 1);
		});
		
		addReverseToggleConfigWidget(event, "item_transfer",
			0, 9,
			0, 18,
			44, 4);
		event.register(ReverseToggleConfig.class, "fluid_transfer", (config, ctx) -> {
			SimpleDraggablePanel panel = ctx.panel;
			if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
			if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
			
			ButtonDrawContent disabledCtx = new ButtonDrawContent(9, 9);
			disabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, 9, 18, 9, 9);
			ButtonDrawContent enabledCtx = new ButtonDrawContent(9, 9);
			enabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, 9, 9, 9, 9);
			
			BooleanSupplier isDisabled = config::isDisabled;
			SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
					boolean newValue = !config.isDisabled();
					config.setDisabled(newValue);
					button.setSprites(newValue ? disabledCtx : enabledCtx);
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					config.writeToServer(buf);
					Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
				}).size(9, 9)
				.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("fluid_transportation")
					.append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isDisabled() ? "disabled" : "enabled"))).build();
			
			btn.setSprites(isDisabled.getAsBoolean() ? disabledCtx : enabledCtx);
			
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
			
			ButtonDrawContent disabledCtx = new ButtonDrawContent(9, 9);
			disabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, 18, 18, 9, 9);
			ButtonDrawContent enabledCtx = new ButtonDrawContent(9, 9);
			enabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, 18, 9, 9, 9);
			
			BooleanSupplier isDisabled = config::isDisabled;
			SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
					boolean newValue = !config.isDisabled();
					config.setDisabled(newValue);
					button.setSprites(newValue ? disabledCtx : enabledCtx);
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					config.writeToServer(buf);
					Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
				}).size(9, 9)
				.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("energy_transportation")
					.append(": ").append(ModConstants.GUI.withSuffixTranslatable(config.isDisabled() ? "disabled" : "enabled"))).build();
			
			btn.setSprites(isDisabled.getAsBoolean() ? disabledCtx : enabledCtx);
			
			panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(44, 24, 9, 9));
			panel.addChildren((fx, fy, fw, fh) -> {
				boolean hasInv = mbe.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, mbe.getBlockPos(), null) == null;
				boolean hasTank = mbe.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, mbe.getBlockPos(), null) == null;
				int yOffset = hasInv && hasTank ? 24 : hasInv || hasTank ? 14 : 4;
				btn.setPosition(fx + 44, fy + yOffset);
				return btn;
			});
		});
		
		addReverseToggleConfigWidget(event, "block_mode",
			27, 9,
			27, 18,
			64, 4);
		addReverseToggleConfigWidget(event, "fluid_mode",
			36, 9,
			36, 18,
			64, 14);
		addReverseToggleConfigWidget(event, "heat",
			45, 9,
			45, 18,
			54, 14);
		addReverseToggleConfigWidget(event, "harvester_use_tool",
			54, 9,
			54, 18,
			54, 34);

		addToggleConfigWidget(event, "render_area",
			0, 27,
			0, 36,
			54, 14);
		addToggleConfigWidget(event, "bottler_mode",
			9, 27,
			9, 36,
			54, 14);
		addToggleConfigWidget(event, "harvester_output_mode",
			18, 27,
			18, 36,
			54, 24);
		addToggleConfigWidget(event, "always_mine",
			63, 9,
			63, 18,
			64, 24);
		
		event.register(RedstoneConfig.class, (config, ctx) -> {
			SimpleDraggablePanel panel = ctx.panel;
			if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
			if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
			
			Supplier<RedstoneConfig.Mode> mode = config::getMode;
			SpriteButton redstoneButton = SpriteButton.builder(Component.empty(), (button, key) -> {
					config.cycleMode();
					button.setSprites(getTextureForRedstoneType(mode.get()));
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					config.writeToServer(buf);
					Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
				}).size(9, 9)
				.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("redstone")
					.append(": ").append(getComponentForRedstone(mode.get()))).build();
			
			redstoneButton.setSprites(getTextureForRedstoneType(mode.get()));
			
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
					button.setSprites(getTextureForBatBox(mode.get()));
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					config.writeToServer(buf);
					Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
				}).size(9, 9)
				.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable("battery_box_export")
					.append(": ").append(getComponentForBatBox(mode.get(), percGetter.get()))).build();
			
			exportButton.setSprites(getTextureForBatBox(mode.get()));
			
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
	
	private static void addToggleConfigWidget(CreateConfigWidgetEvent event, String subType,
											  int spriteUEnabled, int spriteVEnabled,
											  int spriteUDisabled, int spriteVDisabled,
											  int x, int y) {
		event.register(ToggleConfig.class, subType, (config, ctx) -> {
			SimpleDraggablePanel panel = ctx.panel;
			if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
			if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
			
			ButtonDrawContent enabledCtx = new ButtonDrawContent(9, 9);
			enabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, spriteUEnabled, spriteVEnabled, 9, 9);
			ButtonDrawContent disabledCtx = new ButtonDrawContent(9, 9);
			disabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, spriteUDisabled, spriteVDisabled, 9, 9);
			
			BooleanSupplier isEnabled = config::isEnabled;
			SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
					boolean newValue = !config.isEnabled();
					config.setEnabled(newValue);
					button.setSprites(newValue ? enabledCtx : disabledCtx);
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					config.writeToServer(buf);
					Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
				}).size(9, 9)
				.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable(subType +
					(config.isEnabled() ? "_enabled" : "_disabled"))).build();
			
			btn.setSprites(isEnabled.getAsBoolean() ? enabledCtx : disabledCtx);
			
			panel.addChildren((fx, fy, fw, fh) -> {
				btn.setPosition(fx + x, fy + y);
				return btn;
			});
		});
	}
	private static void addReverseToggleConfigWidget(CreateConfigWidgetEvent event, String subType,
											  int spriteUEnabled, int spriteVEnabled,
											  int spriteUDisabled, int spriteVDisabled,
											  int x, int y) {
		event.register(ReverseToggleConfig.class, subType, (config, ctx) ->  {
			SimpleDraggablePanel panel = ctx.panel;
			if (!(ctx.screen.getMenu() instanceof AbstractMachineMenu amm)) return;
			if (!(amm.getBlockEntity() instanceof ModBlockEntity mbe)) return;
			
			ButtonDrawContent disabledCtx = new ButtonDrawContent(9, 9);
			disabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, spriteUDisabled, spriteVDisabled, 9, 9);
			ButtonDrawContent enabledCtx = new ButtonDrawContent(9, 9);
			enabledCtx.addBlitSprite(CONFIG_SPRITES, 180, 180, spriteUEnabled, spriteVEnabled, 9, 9);
			
			BooleanSupplier isDisabled = config::isDisabled;
			SpriteButton btn = SpriteButton.builder(Component.empty(), (button, key) -> {
					boolean newValue = !config.isDisabled();
					config.setDisabled(newValue);
					button.setSprites(newValue ? disabledCtx : enabledCtx);
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					config.writeToServer(buf);
					Channel.sendToServer(new MachineConfigurationToServer(mbe.getBlockPos(), config.getLocation(), buf));
				}).size(9, 9)
				.addTooltipHover(() -> ModConstants.GUI.withSuffixTranslatable(subType +
					(config.isDisabled() ? "_disabled" : "_enabled"))).build();
			
			btn.setSprites(isDisabled.getAsBoolean() ? disabledCtx : enabledCtx);
			
			panel.getOccupiedAreas().add(new SimpleDraggablePanel.Bounds(64, 14, 9, 9));
			panel.addChildren((fx, fy, fw, fh) -> {
				btn.setPosition(fx + x, fy + y);
				return btn;
			});
		});
	}
	
	private static ButtonDrawContent getTextureForTransferType(SideConfig.TransferType transferType) {
		ButtonDrawContent ctx = new ButtonDrawContent(9, 9);
		int u = 0;
		int v = 0;
		if (transferType == null) return ctx;
		switch (transferType) {
			case NONE -> u = 9;
			case PULL -> u = 18;
			case PUSH -> u = 27;
		}
		ctx.addBlitSprite(CONFIG_SPRITES, 180, 180, u, v, 9, 9);
		return ctx;
	}
	
	private static ButtonDrawContent getTextureForRedstoneType(RedstoneConfig.Mode redstoneType) {
		ButtonDrawContent ctx = new ButtonDrawContent(9, 9);
		int u = 0;
		int v = 0;
		switch (redstoneType) {
			case HIGH -> u = 81;
			case LOW -> u = 90;
			case IGNORE -> u = 72;
		}
		ctx.addBlitSprite(CONFIG_SPRITES, 180, 180, u, v, 9, 9);
		return ctx;
	}
	
	private static MutableComponent getComponentForRedstone(RedstoneConfig.Mode type) {
		if (type == RedstoneConfig.Mode.LOW) return ModConstants.GUI.withSuffixTranslatable("redstone_passive");
		if (type == RedstoneConfig.Mode.HIGH) return ModConstants.GUI.withSuffixTranslatable("redstone_active");
		return ModConstants.GUI.withSuffixTranslatable("redstone_default");
	}
	
	private static ButtonDrawContent getTextureForBatBox(BatteryBoxExportConfig.ExportMode mode) {
		ButtonDrawContent ctx = new ButtonDrawContent(9, 9);
		int u = 0;
		int v = 0;
		switch (mode) {
			case EMPTY -> u = 99;
			case PERCENTAGE_BELOW -> u = 126;
			case PERCENTAGE_ABOVE -> u = 117;
			case PERCENTAGE_EXACT -> u = 135;
			case FULL -> u = 108;
		}
		ctx.addBlitSprite(CONFIG_SPRITES, 180, 180, u, v, 9, 9);
		return ctx;
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
}

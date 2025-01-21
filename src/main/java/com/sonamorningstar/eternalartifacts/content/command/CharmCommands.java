package com.sonamorningstar.eternalartifacts.content.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.charm.CycleWildcardToClient;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class CharmCommands {
	
	public static void registerCommand(RegisterCommandsEvent event) {
		var dispatcher = event.getDispatcher();
		var buildContext = event.getBuildContext();
		dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(MODID)
			.requires(source -> source.hasPermission(2))
			.then(LiteralArgumentBuilder.<CommandSourceStack>literal("charm")
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("list")
					.then(Commands.argument("target", EntityArgument.entity())
						.executes(context -> {
							var source = context.getSource();
							printCharmList(source, EntityArgument.getEntity(context, "target"));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("clear")
					.then(Commands.argument("target", EntityArgument.entity())
						.executes(context -> {
							var source = context.getSource();
							clearCharms(source, EntityArgument.getEntity(context, "target"));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
					.then(Commands.argument("target", EntityArgument.entity())
						.then(Commands.argument("index", IntegerArgumentType.integer(0, 12))
							.then(Commands.argument("stack", ItemArgument.item(buildContext))
								.executes(context -> {
									var source = context.getSource();
									var entity = EntityArgument.getEntity(context, "target");
									var index = IntegerArgumentType.getInteger(context, "index");
									var stack = ItemArgument.getItem(context, "stack").createItemStack(1, false);
									setCharm(source, entity, index, stack);
									return Command.SINGLE_SUCCESS;
								})
							)
						)
					)
				)
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("remove")
					.then(Commands.argument("target", EntityArgument.entity())
						.then(Commands.argument("index", IntegerArgumentType.integer(0, 12))
							.executes(context -> {
								var source = context.getSource();
								var entity = EntityArgument.getEntity(context, "target");
								var index = IntegerArgumentType.getInteger(context, "index");
								removeCharm(source, entity, index);
								return Command.SINGLE_SUCCESS;
							})
						)
					)
				)
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("wildcard")
					.then(Commands.argument("target", EntityArgument.entity())
						.then(Commands.literal("activate")
							.executes(context -> {
								var source = context.getSource();
								var entity = EntityArgument.getEntity(context, "target");
								setWildcardCharm(source, entity, true);
								return Command.SINGLE_SUCCESS;
							})
						)
						.then(Commands.literal("deactivate")
							.executes(context -> {
								var source = context.getSource();
								var entity = EntityArgument.getEntity(context, "target");
								setWildcardCharm(source, entity, false);
								return Command.SINGLE_SUCCESS;
							})
						)
					)
				)
			)
		);
	}
	
	private static void printCharmList(CommandSourceStack source, Entity entity) {
		if (entity instanceof LivingEntity living) {
			MutableComponent charms = Component.empty();
			charms.append(Component.translatable(ModConstants.COMMAND.withSuffix("charm.list"), living.getDisplayName()).withStyle(ChatFormatting.GOLD));
			var charmStorage = CharmStorage.get(living);
			for (int i = 0; i < charmStorage.getSlots(); i++) {
				var stack = charmStorage.getStackInSlot(i);
				if (!stack.isEmpty()) {
					CharmType type = null;
					if(i != 12) type = CharmStorage.slotTypes.get(i);
					MutableComponent charmName = type != null ? type.getDisplayName() : CharmType.getWildcardDisplayName();
					charmName.withStyle(ChatFormatting.AQUA);
					charms.append(Component.literal("\n" + i + ": ").withStyle(ChatFormatting.GRAY)
						.append(charmName).append(" ").append(stack.getDisplayName()));
				}
			}
			source.sendSuccess(() -> charms, false);
		}
	}
	
	private static void clearCharms(CommandSourceStack source, Entity entity) {
		if (entity instanceof LivingEntity living) {
			var charms = CharmStorage.get(living);
			for (int i = 0; i < charms.getSlots(); i++) {
				charms.setStackInSlot(i, ItemStack.EMPTY);
			}
			CharmStorage.syncForAndTracking(living);
			source.sendSuccess(() -> Component.translatable(ModConstants.COMMAND.withSuffix("charm.cleared"), living.getDisplayName()), true);
		}
	}
	
	private static void setCharm(CommandSourceStack source, Entity entity, int index, ItemStack stack) {
		if (entity instanceof LivingEntity living) {
			var charms = CharmStorage.get(living);
			charms.setStackInSlot(index, stack);
			CharmStorage.syncForAndTracking(living);
			source.sendSuccess(() -> Component.translatable(ModConstants.COMMAND.withSuffix("charm.given"), stack.getDisplayName(), living.getDisplayName()), true);
		}
	}
	
	private static void removeCharm(CommandSourceStack source, Entity entity, int index) {
		if (entity instanceof LivingEntity living) {
			var charms = CharmStorage.get(living);
			charms.setStackInSlot(index, ItemStack.EMPTY);
			CharmStorage.syncForAndTracking(living);
			source.sendSuccess(() -> Component.translatable(ModConstants.COMMAND.withSuffix("charm.removed"), index, living.getDisplayName()), true);
		}
	}
	
	private static void setWildcardCharm(CommandSourceStack source, Entity entity, boolean active) {
		if (entity instanceof LivingEntity living) {
			var charms = CharmStorage.get(living);
			charms.setWildcardNbt(active);
			Channel.sendToSelfAndTracking(new CycleWildcardToClient(living.getId(), active), living);
			source.sendSuccess(() -> Component.translatable(ModConstants.COMMAND.withSuffix("charm.wildcard." + (active ? "activated" : "deactivated")), living.getDisplayName()), true);
		}
	}
}

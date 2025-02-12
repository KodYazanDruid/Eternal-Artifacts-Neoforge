package com.sonamorningstar.eternalartifacts.content.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.Network;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Set;
import java.util.UUID;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class TesseractCommands {
	public static void registerCommand(RegisterCommandsEvent event) {
		var dispatcher = event.getDispatcher();
		var buildContext = event.getBuildContext();
		dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(MODID)
			.requires(source -> source.hasPermission(2))
			.then(LiteralArgumentBuilder.<CommandSourceStack>literal("tesseract")
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("list")
					.executes(context -> {
						var source = context.getSource();
						listNetworks(source);
						return Command.SINGLE_SUCCESS;
					})
				)
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("delete")
					.then(Commands.argument("uuid", UuidArgument.uuid())
						.executes(context -> {
							var source = context.getSource();
							var uuid = UuidArgument.getUuid(context, "uuid");
							removeNetworks(source, uuid);
							return Command.SINGLE_SUCCESS;
						})
					)
				)
			)
		);
	}
	
	private static void listNetworks(CommandSourceStack source) {
		ServerLevel level = source.getLevel();
		MutableComponent networks = Component.empty();
		networks.append(Component.translatable(ModConstants.COMMAND.withSuffix("network.list")).withStyle(ChatFormatting.GOLD));
		Set<Network<?>> networkList = TesseractNetworks.get(level).getNetworks();
		for (Network<?> network : networkList) {
			MutableComponent networkComp = Component.literal("\n")
				.append(network.getName()).withStyle(ChatFormatting.YELLOW).append(" ")
				.append(ComponentUtils.copyOnClickText(network.getUuid().toString())).withStyle(ChatFormatting.GRAY).append(" ");
			networks.append(networkComp);
		}
		source.sendSuccess(() -> networks, false);
	}
	
	private static void removeNetworks(CommandSourceStack source, UUID uuid) {
		ServerLevel level = source.getLevel();
		if (TesseractNetworks.get(level).removeNetwork(uuid)) {
			source.sendSuccess(() -> Component.translatable(ModConstants.COMMAND.withSuffix("network.removed"), uuid), true);
		} else {
			source.sendFailure(Component.translatable(ModConstants.COMMAND.withSuffix("network.not_found"), uuid));
		}
	}
}

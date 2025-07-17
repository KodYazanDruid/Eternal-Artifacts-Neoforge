package com.sonamorningstar.eternalartifacts.content.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sonamorningstar.eternalartifacts.api.forceload.ForceLoadManager;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ChunkLoader;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Set;
import java.util.stream.Collectors;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ForceLoadCommands {
	public static void registerCommand(RegisterCommandsEvent event) {
		var dispatcher = event.getDispatcher();
		var buildContext = event.getBuildContext();
		dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(MODID)
			.requires(source -> source.hasPermission(2))
			.then(LiteralArgumentBuilder.<CommandSourceStack>literal("forceload")
				.then(LiteralArgumentBuilder.<CommandSourceStack>literal("list")
					.executes(context -> {
						var source = context.getSource();
						listForcedChunks(source);
						return Command.SINGLE_SUCCESS;
					})
				)
			)
		);
	}
	
	private static void listForcedChunks(CommandSourceStack source) {
		Set<ForceLoadManager.ForcedChunkPos> forcedChunkList = ForceLoadManager.ALL_LOADERS.stream().map(ChunkLoader::getForcedChunks)
			.flatMap(Set::stream).collect(Collectors.toSet());
		MutableComponent forcedChunks = Component.empty();
		forcedChunks.append(
			Component.translatable(ModConstants.GUI.withSuffix("forceload.loaded_chunks_count"), forcedChunkList.size())
				.withStyle(net.minecraft.ChatFormatting.GOLD));
		forcedChunks.append("\n");
		forcedChunks.append(ModConstants.COMMAND.withSuffixTranslatable("forceload.list").withStyle(net.minecraft.ChatFormatting.GOLD));
		if (forcedChunkList.isEmpty()) {
			forcedChunks.append(Component.translatable(ModConstants.COMMAND.withSuffix("forceload.list.empty"))
				.withStyle(net.minecraft.ChatFormatting.RED));
		} else {
			for (ForceLoadManager.ForcedChunkPos forcedChunk : forcedChunkList) {
				MutableComponent chunkComp = Component.literal("\n")
					.append(Component.translatable(ModConstants.COMMAND.withSuffix("forceload.chunk"),
							Component.literal(forcedChunk.toString()).withStyle(net.minecraft.ChatFormatting.GRAY))
						.withStyle(net.minecraft.ChatFormatting.YELLOW));
				forcedChunks.append(chunkComp);
			}
		}
		source.sendSuccess(() -> forcedChunks, false);
	}
}

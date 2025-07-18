package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.command.CharmCommands;
import com.sonamorningstar.eternalartifacts.content.command.ForceLoadCommands;
import com.sonamorningstar.eternalartifacts.content.command.TesseractCommands;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class ModCommands {
	
	public static void addListener() {
		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, CharmCommands::registerCommand);
		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, TesseractCommands::registerCommand);
		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, ForceLoadCommands::registerCommand);
	}
}

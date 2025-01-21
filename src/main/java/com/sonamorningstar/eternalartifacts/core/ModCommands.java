package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.command.CharmCommands;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class ModCommands {
	
	public static void addListener() {
		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, CharmCommands::registerCommand);
	}
}

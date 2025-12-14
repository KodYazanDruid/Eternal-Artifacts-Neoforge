package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class MachineConfigurationAddEvent extends Event implements ICancellableEvent {
	private Config config;
}

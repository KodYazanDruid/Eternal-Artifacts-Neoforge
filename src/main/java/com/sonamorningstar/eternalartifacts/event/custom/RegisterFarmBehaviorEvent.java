package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import com.sonamorningstar.eternalartifacts.api.farm.FarmBehaviorRegistry;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class RegisterFarmBehaviorEvent extends Event implements IModBusEvent {
	public void register(FarmBehavior behavior) {
		FarmBehaviorRegistry.register(behavior);
	}
}

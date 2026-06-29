package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@UtilityClass
public class FabricClientTickEventsBridge {
	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(client ->
			net.frozenblock.lib.event.api.events.ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick(client)
		);

		ClientTickEvents.END_CLIENT_TICK.register(client ->
			net.frozenblock.lib.event.api.events.ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick(client)
		);

		ClientTickEvents.START_LEVEL_TICK.register(level ->
			net.frozenblock.lib.event.api.events.ClientTickEvents.START_LEVEL_TICK.invoker().onStartTick(level)
		);

		ClientTickEvents.END_LEVEL_TICK.register(level ->
			net.frozenblock.lib.event.api.events.ClientTickEvents.END_LEVEL_TICK.invoker().onEndTick(level)
		);
	}
}

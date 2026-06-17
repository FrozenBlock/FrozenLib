package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.frozenblock.lib.event.api.events.FrozenLibServerTickEvents;

@UtilityClass
public class FabricServerTickEventsBridge {
	public static void init() {
		ServerTickEvents.START_SERVER_TICK.register(server ->
			FrozenLibServerTickEvents.START_SERVER_TICK.invoker().onStartTick(server)
		);

		ServerTickEvents.END_SERVER_TICK.register(server ->
			FrozenLibServerTickEvents.END_SERVER_TICK.invoker().onEndTick(server)
		);

		ServerTickEvents.START_LEVEL_TICK.register(level ->
			FrozenLibServerTickEvents.START_LEVEL_TICK.invoker().onStartTick(level)
		);

		ServerTickEvents.END_LEVEL_TICK.register(level ->
			FrozenLibServerTickEvents.END_LEVEL_TICK.invoker().onEndTick(level)
		);
	}
}

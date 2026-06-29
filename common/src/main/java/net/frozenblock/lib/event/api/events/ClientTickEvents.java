package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

@ClientOnly
public class ClientTickEvents {

	public static final Event<StartTick> START_CLIENT_TICK = FrozenEvents.createEnvironmentEvent(StartTick.class, callbacks -> client -> {
		for (StartTick callback : callbacks) {
			callback.onStartTick(client);
		}
	});

	public static final Event<EndTick> END_CLIENT_TICK = FrozenEvents.createEnvironmentEvent(EndTick.class, callbacks -> client -> {
		for (EndTick callback : callbacks) {
			callback.onEndTick(client);
		}
	});

	public static final Event<StartLevelTick> START_LEVEL_TICK = FrozenEvents.createEnvironmentEvent(StartLevelTick.class, callbacks -> level -> {
		for (StartLevelTick callback : callbacks) {
			callback.onStartTick(level);
		}
	});

	public static final Event<EndLevelTick> END_LEVEL_TICK = FrozenEvents.createEnvironmentEvent(EndLevelTick.class, callbacks -> level -> {
		for (EndLevelTick callback : callbacks) {
			callback.onEndTick(level);
		}
	});

	@ClientOnly
	@FunctionalInterface
	public interface StartTick {
		void onStartTick(Minecraft client);
	}

	@ClientOnly
	@FunctionalInterface
	public interface EndTick {
		void onEndTick(Minecraft client);
	}

	@ClientOnly
	@FunctionalInterface
	public interface StartLevelTick {
		void onStartTick(ClientLevel level);
	}

	@ClientOnly
	@FunctionalInterface
	public interface EndLevelTick {
		void onEndTick(ClientLevel level);
	}
}

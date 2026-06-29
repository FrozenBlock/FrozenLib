package net.frozenblock.lib.event.api.events;

import java.util.function.Consumer;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public class ConfigurationConnectionEvents {

	public static final Event<ServerConfigure> SERVER_CONFIGURE = FrozenEvents.createEnvironmentEvent(
		ServerConfigure.class,
		callbacks -> (handler, server, taskAdder) -> {
			for (ServerConfigure callback : callbacks) {
				callback.onServerConfigure(handler, server, taskAdder);
			}
		}
	);

	@FunctionalInterface
	public interface ServerConfigure {
		void onServerConfigure(
			ServerConfigurationPacketListenerImpl handler,
			MinecraftServer server,
			Consumer<ConfigurationTask> taskAdder
		);
	}
}

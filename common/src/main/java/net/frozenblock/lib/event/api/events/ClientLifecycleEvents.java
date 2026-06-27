package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;

@ClientOnly
public class ClientLifecycleEvents {

	public static final Event<ClientStarted> CLIENT_STARTED = FrozenEvents.createEnvironmentEvent(ClientStarted.class, callbacks -> client -> {
		for (ClientStarted callback : callbacks) {
			callback.onClientStarted(client);
		}
	});

	// TODO NEOFORGE
	public static final Event<ClientStopping> CLIENT_STOPPING = FrozenEvents.createEnvironmentEvent(ClientStopping.class, callbacks -> client -> {
		for (ClientStopping callback : callbacks) {
			callback.onClientStopping(client);
		}
	});

	@ClientOnly
	@FunctionalInterface
	public interface ClientStarted {
		void onClientStarted(Minecraft client);
	}

	@ClientOnly
	@FunctionalInterface
	public interface ClientStopping {
		void onClientStopping(Minecraft client);
	}
}

package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.Nullable;

@ClientOnly
public class ClientConnectionEvents {

	public static final Event<Disconnect> DISCONNECT = FrozenEvents.createEnvironmentEvent(Disconnect.class, callbacks -> (handler, client) -> {
		for (Disconnect callback : callbacks) {
			callback.onDisconnect(handler, client);
		}
	});

	@ClientOnly
	@FunctionalInterface
	public interface Disconnect {
		void onDisconnect(@Nullable ClientPacketListener handler, Minecraft client);
	}
}

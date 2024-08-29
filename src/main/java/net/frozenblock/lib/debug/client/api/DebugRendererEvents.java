package net.frozenblock.lib.debug.client.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class DebugRendererEvents {

	public static final Event<DebugRenderersCreated> DEBUG_RENDERERS_CREATED = FrozenEvents.createEnvironmentEvent(
		DebugRenderersCreated.class,
		callbacks -> (client) -> {
			for (var callback : callbacks) {
				callback.onDebugRenderersCreated(client);
			}
		});

	@FunctionalInterface
	public interface DebugRenderersCreated extends ClientEventEntrypoint {
		void onDebugRenderersCreated(Minecraft client);
	}
}

package net.frozenblock.lib.config.api.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;

@FunctionalInterface
public interface ConfigSaveEvent extends CommonEventEntrypoint {

	Event<ConfigSaveEvent> EVENT = FrozenEvents.createEnvironmentEvent(ConfigSaveEvent.class, callbacks -> config -> {
		for (var callback : callbacks) {
			callback.onSave(config);
		}
	});

	void onSave(Config<?> config) throws Exception;

	@Environment(EnvType.CLIENT)
	interface Client extends ClientEventEntrypoint {

		Event<Client> EVENT = FrozenEvents.createEnvironmentEvent(Client.class, callbacks -> config -> {
			for (var callback : callbacks) {
				callback.onSave(config);
			}
		});

		void onSave(Config<?> config) throws Exception;
	}
}

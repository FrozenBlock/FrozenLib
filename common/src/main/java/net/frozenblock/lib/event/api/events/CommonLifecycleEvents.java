package net.frozenblock.lib.event.api.events;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.RegistryAccess;

@UtilityClass
public final class CommonLifecycleEvents {
	public static final Event<TagsLoaded> TAGS_LOADED = EventRegistry.createEnvironmentEvent(TagsLoaded.class, callbacks -> (registries, client) -> {
		for (TagsLoaded callback : callbacks) {
			callback.onTagsLoaded(registries, client);
		}
	});

	@FunctionalInterface
	public interface TagsLoaded {
		void onTagsLoaded(RegistryAccess registries, boolean client);
	}
}

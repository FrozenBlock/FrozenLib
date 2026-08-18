package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.CommonLifecycleEvents;

@UtilityClass
public class FabricCommonLifecycleEventsBridge {
	public static void init() {
		net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents.TAGS_LOADED.register((registries, client) ->
			CommonLifecycleEvents.TAGS_LOADED.invoker().onTagsLoaded(registries, client)
		);
	}
}

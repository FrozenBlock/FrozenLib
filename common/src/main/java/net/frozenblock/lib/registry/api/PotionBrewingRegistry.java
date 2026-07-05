package net.frozenblock.lib.registry.api;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.world.item.alchemy.PotionBrewing;

public class PotionBrewingRegistry {

	public static final Event<Build> BUILD = FrozenEvents.createEnvironmentEvent(Build.class, callbacks -> builder -> {
		for (Build callback : callbacks) {
			callback.build(builder);
		}
	});

	@FunctionalInterface
	public interface Build {
		void build(PotionBrewing.Builder builder);
	}
}

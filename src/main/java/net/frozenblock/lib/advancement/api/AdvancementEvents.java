package net.frozenblock.lib.advancement.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;

public final class AdvancementEvents {
	private AdvancementEvents() {}

	public static final Event<AdvancementInit> INIT = FrozenEvents.createEnvironmentEvent(AdvancementInit.class, callbacks -> context -> {
		for (AdvancementInit callback : callbacks) {
			callback.onInit(context);
		}
	});

	@FunctionalInterface
	public interface AdvancementInit {
		void onInit(AdvancementContext advancement);
	}
}

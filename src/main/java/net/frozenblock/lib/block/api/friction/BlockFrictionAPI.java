package net.frozenblock.lib.block.api.friction;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;

public class BlockFrictionAPI {

	public static final Event<FrictionModification> MODIFICATIONS = FrozenEvents.createEnvironmentEvent(FrictionModification.class, callbacks -> context -> {
		for (FrictionModification modification : callbacks) {
			modification.modifyFriction(context);
		}
	});

	@FunctionalInterface
	public interface FrictionModification extends CommonEventEntrypoint {
		void modifyFriction(FrictionContext context);
	}
}

package net.frozenblock.lib.events.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoints.CommonEventEntrypoint;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.Nullable;

public final class RegistryFreezeEvents {

	private RegistryFreezeEvents() {
		throw new UnsupportedOperationException("RegistryFreeze events supports only static declarations.");
	}

	/**
	 * An event indicating the start of a {@link Registry}'s freeze.
	 * <p>
	 * The registry will not be frozen when this is invoked.
	 */
	public static final Event<StartRegistryFreeze> START_REGISTRY_FREEZE = FrozenEvents.createEnvironmentEvent(StartRegistryFreeze.class,
			callbacks -> (registry, allRegistries) -> {
				for (var callback : callbacks) {
					callback.onStartRegistryFreeze(registry, allRegistries);
				}
			});

	/**
	 * An event indicating the end of a {@link Registry}'s freeze.
	 * <p>
	 * The registry will be frozen when this is invoked.
	 */
	public static final Event<EndRegistryFreeze> END_REGISTRY_FREEZE = FrozenEvents.createEnvironmentEvent(EndRegistryFreeze.class,
			callbacks -> (registry, allRegistries) -> {
				for (var callback : callbacks) {
					callback.onEndRegistryFreeze(registry, allRegistries);
				}
			});

	@FunctionalInterface
	public interface StartRegistryFreeze extends CommonEventEntrypoint {
		/**
		 * @param allRegistries	This indicates whether the Registry is being frozen from {@link Registry#freezeBuiltins()} or not.
		 */
		void onStartRegistryFreeze(@Nullable Registry<?> registry, boolean allRegistries);
	}

	@FunctionalInterface
	public interface EndRegistryFreeze extends CommonEventEntrypoint {
		/**
		 * @param allRegistries	This indicates whether the Registry is being frozen from {@link Registry#freezeBuiltins()} or not.
		 */
		void onEndRegistryFreeze(@Nullable Registry<?> registry, boolean allRegistries);
	}
}

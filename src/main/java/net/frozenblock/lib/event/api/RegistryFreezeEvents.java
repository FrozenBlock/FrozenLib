/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.event.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;

public final class RegistryFreezeEvents {

	private RegistryFreezeEvents() {
		throw new UnsupportedOperationException("RegistryFreeze events contains only static declarations.");
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
		 * @param allRegistries	This indicates whether the Registry is being frozen from {@link BuiltInRegistries#freeze()} or not.
		 */
		void onStartRegistryFreeze(@Nullable Registry<?> registry, boolean allRegistries);
	}

	@FunctionalInterface
	public interface EndRegistryFreeze extends CommonEventEntrypoint {
		/**
		 * @param allRegistries	This indicates whether the Registry is being frozen from {@link BuiltInRegistries#freeze()} or not.
		 */
		void onEndRegistryFreeze(@Nullable Registry<?> registry, boolean allRegistries);
	}
}

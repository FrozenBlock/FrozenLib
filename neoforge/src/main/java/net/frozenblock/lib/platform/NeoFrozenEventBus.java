/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.platform;

import net.frozenblock.lib.FrozenLibConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

/**
 * Lazily resolves FrozenLib's own mod event bus.
 *
 * <p>Custom FrozenLib events are created from static initializers, which can run at
 * arbitrary classloading time — not necessarily while FrozenLib's own mod constructor is
 * on the stack. {@code ModLoadingContext.get().getActiveContainer()} is only valid during
 * a mod's own construction, so it cannot be used here. Mod discovery (and thus FrozenLib's
 * {@code ModContainer}) is available well before any mod's classes that would touch a
 * FrozenLib event are loaded, so resolving the container by id via {@link ModList} is safe.
 */
final class NeoFrozenEventBus {
	private static IEventBus bus;

	private NeoFrozenEventBus() {
	}

	static synchronized IEventBus get() {
		if (bus == null) {
			bus = ModList.get().getModContainerById(FrozenLibConstants.MOD_ID)
				.orElseThrow(() -> new IllegalStateException("FrozenLib mod container not found"))
				.getEventBus();
		}
		return bus;
	}
}

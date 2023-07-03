/*
 * Copyright 2023 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.core.registry.api.event;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents information about a registry entry.
 * <p>
 * Underlying implementations may be mutable; do not store this object in your own fields directly.
 * <p>
 * Modified to work on Fabric
 *
 * @param <V> the entry type used by the relevant {@link Registry}
 */
public interface RegistryEntryContext<V> {
	/**
	 * {@return the relevant registry for this entry}
	 */
	Registry<V> registry();

	/**
	 * {@return the entry's object}
	 */
	V value();

	/**
	 * {@return the entry's namespaced identifier}
	 */
	ResourceLocation resourceLocation();

	/**
	 * {@return the entry's raw int identifier}
	 */
	int rawId();

	/**
	 * Safely registers a new entry in the registry of this context.
	 * <p>
	 * Registration may be delayed when called from {@link RegistryMonitor#forAll(RegistryEvents.EntryAdded)}.
	 *
	 * @param id    the identifier of the entry
	 * @param value the value to register
	 * @param <T>   the type of the value
	 * @return the registered value
	 */
	default <T extends V> T register(ResourceLocation id, T value) {
		return Registry.register(this.registry(), id, value);
	}
}

/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.frozenblock.core.registry.api.event;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

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
	 * {@return The relevant registry for this entry}
	 */
	Registry<V> registry();

	/**
	 * {@return The entry's object}
	 */
	V value();

	/**
	 * {@return The entry's namespaced identifier}
	 */
	Identifier identifier();

	/**
	 * {@return The entry's raw int identifier}
	 */
	int rawId();

	/**
	 * Safely registers a new entry in the registry of this context.
	 * <p>
	 * Registration may be delayed when called from {@link RegistryMonitor#forAll(RegistryEvents.EntryAdded)}.
	 *
	 * @param id The identifier of the entry
	 * @param value The value to register
	 * @param <T> The type of the value
	 * @return The registered value
	 */
	default <T extends V> T register(Identifier id, T value) {
		return Registry.register(this.registry(), id, value);
	}
}

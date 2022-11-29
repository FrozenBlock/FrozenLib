/*
 * Copyright 2022 QuiltMC
 * Copyright 2022 FrozenBlock
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

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface RegistryEventStorage<V> {
	/**
	 * {@return the entry added event}
	 */
	Event<RegistryEvents.EntryAdded<V>> frozenLib_quilt$getEntryAddedEvent();

	/**
	 * Casts a {@link Registry} to the duck interface.
	 */
	@SuppressWarnings("unchecked")
	static <W> RegistryEventStorage<W> as(MappedRegistry<W> registry) {
		return (RegistryEventStorage<W>) registry;
	}
}

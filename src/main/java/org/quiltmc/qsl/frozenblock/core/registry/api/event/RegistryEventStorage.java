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

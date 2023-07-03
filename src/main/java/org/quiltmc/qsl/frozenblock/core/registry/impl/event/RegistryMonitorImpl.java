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

package org.quiltmc.qsl.frozenblock.core.registry.impl.event;

import java.util.function.Predicate;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEventStorage;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEntryContext;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryMonitor;

/**
 * The default implementation of {@link RegistryMonitor}.
 * <p>
 * Modified to work on Fabric
 *
 * @param <V> the entry type of the monitored {@link Registry}
 */
@ApiStatus.Internal
public class RegistryMonitorImpl<V> implements RegistryMonitor<V> {
	private final Registry<V> registry;
	private @Nullable Predicate<RegistryEntryContext<V>> filter = null;

	public RegistryMonitorImpl(Registry<V> registry) {
		this.registry = registry;
	}

	@Override
	public RegistryMonitor<V> filter(Predicate<RegistryEntryContext<V>> filter) {
		this.filter = this.filter == null ? filter : this.filter.and(filter);
		return this;
	}

	@Override
	public void forAll(RegistryEvents.EntryAdded<V> callback) {
		if (!(this.registry instanceof WritableRegistry<V>)) {
			throw new UnsupportedOperationException("Registry " + this.registry + " is not supported!");
		}

		var delayed = new DelayedRegistry<>((MappedRegistry<V>) this.registry);
		var context = new MutableRegistryEntryContextImpl<>(delayed);

		this.registry.holders().forEach(entry -> {
			context.set(entry.key().location(), entry.value());

			if (this.testFilter(context)) {
				callback.onAdded(context);
			}
		});

		this.forUpcoming(callback);

		delayed.applyDelayed();
	}

	@Override
	public void forUpcoming(RegistryEvents.EntryAdded<V> callback) {
		RegistryEvents.getEntryAddEvent(this.registry).register(context -> {
			if (this.testFilter(context)) {
				callback.onAdded(context);
			}
		});
	}

	/**
	 * Tests the current filter on the specified entry context.
	 * <p>
	 * Accounts for the filter being {@code null} by treating it as always {@code true}.
	 */
	private boolean testFilter(RegistryEntryContext<V> context) {
		if (this.filter == null) {
			return true;
		}

		return this.filter.test(context);
	}
}

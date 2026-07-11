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

package net.frozenblock.lib.event.api.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.impl.EventType;

public final class EventRegistryImpl {

	public static <T> Event<T> createEnvironmentEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
		final FabricEvent<T> event = new FabricEvent<>(type, null, invokerFactory);
		autoRegister(event, type);
		return event;
	}

	public static <T> Event<T> createEnvironmentEvent(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
		final FabricEvent<T> event = new FabricEvent<>(type, emptyInvoker, invokerFactory);
		autoRegister(event, type);
		return event;
	}

	@SuppressWarnings("unchecked")
	private static <T> void autoRegister(Event<T> event, Class<? super T> type) {
		for (var eventType : EventType.VALUES) {
			if (!eventType.listener().isAssignableFrom(type)) continue;
			final List<?> entrypoints = FabricLoader.getInstance().getEntrypoints(eventType.entrypoint(), eventType.listener());

			for (Object entrypoint : entrypoints) {
				if (!type.isAssignableFrom(entrypoint.getClass())) continue;
				event.register((T) entrypoint);
			}
			break;
		}
	}

	/**
	 * An {@link Event} backed by a real Fabric {@code Event<T>}.
	 *
	 * <p>Fabric's array-backed {@code Event<T>} has no listener removal API, so this keeps its own
	 * listener list as the source of truth and rebuilds the underlying Fabric event from scratch
	 * whenever a listener is removed or all listeners are cleared.
	 */
	private static final class FabricEvent<T> implements Event<T> {
		private final Class<? super T> type;
		private final T emptyInvoker;
		private final Function<T[], T> invokerFactory;
		private final List<T> listeners = new ArrayList<>();
		private volatile net.fabricmc.fabric.api.event.Event<T> delegate;

		@SuppressWarnings("unchecked")
		private FabricEvent(Class<? super T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
			this.type = type;
			this.emptyInvoker = emptyInvoker;
			this.invokerFactory = invokerFactory;
			this.delegate = emptyInvoker != null
				? EventFactory.createArrayBacked((Class<T>) type, emptyInvoker, invokerFactory)
				: EventFactory.createArrayBacked(type, invokerFactory);
		}

		@Override
		public synchronized void register(T listener) {
			this.listeners.add(listener);
			this.delegate.register(listener);
		}

		@Override
		public synchronized void unregister(T listener) {
			this.listeners.remove(listener);
			this.rebuild();
		}

		@Override
		public synchronized boolean isRegistered(T listener) {
			return this.listeners.contains(listener);
		}

		@Override
		public synchronized void clearCallbacks() {
			this.listeners.clear();
			this.rebuild();
		}

		@SuppressWarnings("unchecked")
		private void rebuild() {
			final net.fabricmc.fabric.api.event.Event<T> rebuilt = this.emptyInvoker != null
				? EventFactory.createArrayBacked((Class<T>) this.type, this.emptyInvoker, this.invokerFactory)
				: EventFactory.createArrayBacked(this.type, this.invokerFactory);
			for (T listener : this.listeners) rebuilt.register(listener);
			this.delegate = rebuilt;
		}

		@Override
		public T invoker() {
			return this.delegate.invoker();
		}
	}
}

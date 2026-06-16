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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.event.api.FrozenEvent;
import net.frozenblock.lib.event.impl.EventType;
import net.frozenblock.lib.platform.service.EventHelper;

public class FabricEventHelper implements EventHelper {
	private final List<Event<?>> registeredEvents = new ArrayList<>();

	@Override
	public <T> FrozenEvent<T> createEnvironmentEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
		final Event<T> event = EventFactory.createArrayBacked(type, invokerFactory);
		this.autoRegister(event, type);
		return wrap(event);
	}

	@Override
	public <T> FrozenEvent<T> createEnvironmentEvent(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
		final Event<T> event = EventFactory.createArrayBacked(type, emptyInvoker, invokerFactory);
		this.autoRegister(event, type);
		return wrap(event);
	}

	private static <T> FrozenEvent<T> wrap(Event<T> event) {
		return new FrozenEvent<>() {
			@Override
			public void register(T listener) {
				event.register(listener);
			}

			@Override
			public T invoker() {
				return event.invoker();
			}
		};
	}

	@SuppressWarnings("unchecked")
	private <T> void autoRegister(Event<T> event, Class<? super T> type) {
		if (this.registeredEvents.contains(event)) return;
		this.registeredEvents.add(event);

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
}

/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.event.api;

import java.util.function.Function;
import net.frozenblock.lib.platform.FrozenInitPlatformUtils;

public class FrozenEvents {

	/**
	 * Creates an environment event with the specified event type and invoker factory.
	 *
	 * @param type The type of event to be created
	 * @param invokerFactory The function to create the invoker for the event
	 * @return A new Event of the specified type
	 */
	public static <T> Event<T> createEnvironmentEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
		return FrozenInitPlatformUtils.EVENT.createEnvironmentEvent(type, invokerFactory);
	}

	/**
	 * Creates an environment event with the specified event type, empty invoker, and invoker factory.
	 *
	 * @param type The type of event to be created
	 * @param emptyInvoker An empty invoker for the event
	 * @param invokerFactory The function to create the invoker for the event
	 * @return A new Event of the specified type
	 */
	public static <T> Event<T> createEnvironmentEvent(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
		return FrozenInitPlatformUtils.EVENT.createEnvironmentEvent(type, emptyInvoker, invokerFactory);
	}
}

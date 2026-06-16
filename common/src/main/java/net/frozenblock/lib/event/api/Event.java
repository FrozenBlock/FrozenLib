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

package net.frozenblock.lib.event.api;

/**
 * A cross-platform event, analogous to Fabric's {@code Event<T>}.
 *
 * <p>On Fabric this is backed directly by Fabric's array-backed event system.
 * On NeoForge this is backed by a real {@code IEventBus} dispatch.
 *
 * @param <T> the listener callback type
 */
public interface Event<T> {
	T invoker();
	void register(T callback);
	void unregister(T callback);
	boolean isRegistered(T callback);
	void clearCallbacks();
}

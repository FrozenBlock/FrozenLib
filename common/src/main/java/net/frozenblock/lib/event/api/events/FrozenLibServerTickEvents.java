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

package net.frozenblock.lib.event.api.events;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

@UtilityClass
public class FrozenLibServerTickEvents {
	/**
	 * Called at the start of the server tick.
	 *
	 * <p>When the dedicated server is "paused", this event is not invoked.
	 */
	public static final Event<StartTick> START_SERVER_TICK = FrozenEvents.createEnvironmentEvent(StartTick.class, callbacks -> server -> {
		for (StartTick event : callbacks) {
			event.onStartTick(server);
		}
	});

	/**
	 * Called at the end of the server tick.
	 *
	 * <p>When the dedicated server is "paused", this event is not invoked.
	 */
	public static final Event<EndTick> END_SERVER_TICK = FrozenEvents.createEnvironmentEvent(EndTick.class, callbacks -> server -> {
		for (EndTick event : callbacks) {
			event.onEndTick(server);
		}
	});

	/**
	 * Called at the start of a ServerLevel's tick.
	 *
	 * <p>When the dedicated server is "paused", this event is not invoked.
	 */
	public static final Event<StartLevelTick> START_LEVEL_TICK = FrozenEvents.createEnvironmentEvent(StartLevelTick.class, callbacks -> level -> {
		for (StartLevelTick callback : callbacks) {
			callback.onStartTick(level);
		}
	});

	/**
	 * Called at the end of a ServerLevel's tick.
	 *
	 * <p>End of level tick may be used to start async computations for the next tick.
	 *
	 * <p>When the dedicated server is "paused", this event is not invoked.
	 */
	public static final Event<EndLevelTick> END_LEVEL_TICK = FrozenEvents.createEnvironmentEvent(EndLevelTick.class, callbacks -> level -> {
		for (EndLevelTick callback : callbacks) {
			callback.onEndTick(level);
		}
	});

	@FunctionalInterface
	public interface StartTick {
		void onStartTick(MinecraftServer server);
	}

	@FunctionalInterface
	public interface EndTick {
		void onEndTick(MinecraftServer server);
	}

	@FunctionalInterface
	public interface StartLevelTick {
		void onStartTick(ServerLevel level);
	}

	@FunctionalInterface
	public interface EndLevelTick {
		void onEndTick(ServerLevel level);
	}
}

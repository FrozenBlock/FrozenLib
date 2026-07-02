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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

@Environment(EnvType.CLIENT)
public class ClientTickEvents {

	public static final Event<StartTick> START_CLIENT_TICK = FrozenEvents.createEnvironmentEvent(StartTick.class, callbacks -> client -> {
		for (StartTick callback : callbacks) {
			callback.onStartTick(client);
		}
	});

	public static final Event<EndTick> END_CLIENT_TICK = FrozenEvents.createEnvironmentEvent(EndTick.class, callbacks -> client -> {
		for (EndTick callback : callbacks) {
			callback.onEndTick(client);
		}
	});

	public static final Event<StartLevelTick> START_LEVEL_TICK = FrozenEvents.createEnvironmentEvent(StartLevelTick.class, callbacks -> level -> {
		for (StartLevelTick callback : callbacks) {
			callback.onStartTick(level);
		}
	});

	public static final Event<EndLevelTick> END_LEVEL_TICK = FrozenEvents.createEnvironmentEvent(EndLevelTick.class, callbacks -> level -> {
		for (EndLevelTick callback : callbacks) {
			callback.onEndTick(level);
		}
	});

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface StartTick {
		void onStartTick(Minecraft client);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface EndTick {
		void onEndTick(Minecraft client);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface StartLevelTick {
		void onStartTick(ClientLevel level);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface EndLevelTick {
		void onEndTick(ClientLevel level);
	}
}

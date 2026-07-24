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

package net.frozenblock.lib.event.api.events.client;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import java.util.function.BooleanSupplier;

@UtilityClass
@ClientOnly
public final class ClientTickEvents {
	/**
	 * Called when {@link Minecraft#tick()} starts.
	 */
	public static final Event<StartTick> START_CLIENT_TICK = EventRegistry.createEnvironmentEvent(StartTick.class, callbacks -> client -> {
		for (StartTick callback : callbacks) {
			callback.onStartTick(client);
		}
	});

	/**
	 * Called when {@link Minecraft#tick()} is finished.
	 */
	public static final Event<EndTick> END_CLIENT_TICK = EventRegistry.createEnvironmentEvent(EndTick.class, callbacks -> client -> {
		for (EndTick callback : callbacks) {
			callback.onEndTick(client);
		}
	});

	/**
	 * Called before a {@link ClientLevel} is ticked on {@code NeoForge}, or when {@link ClientLevel#tickEntities()} starts on {@code Fabric}.
	 */
	public static final Event<StartLevelTick> START_LEVEL_TICK = EventRegistry.createEnvironmentEvent(StartLevelTick.class, callbacks -> level -> {
		for (StartLevelTick callback : callbacks) {
			callback.onStartTick(level);
		}
	});

	/**
	 * Called after a {@link ClientLevel} is ticked on {@code NeoForge}, or when {@link ClientLevel#tick(BooleanSupplier)} ()} finishes on {@code Fabric}.
	 */
	public static final Event<EndLevelTick> END_LEVEL_TICK = EventRegistry.createEnvironmentEvent(EndLevelTick.class, callbacks -> level -> {
		for (EndLevelTick callback : callbacks) {
			callback.onEndTick(level);
		}
	});

	@FunctionalInterface
	public interface StartTick {
		void onStartTick(Minecraft client);
	}

	@FunctionalInterface
	public interface EndTick {
		void onEndTick(Minecraft client);
	}

	@FunctionalInterface
	public interface StartLevelTick {
		void onStartTick(ClientLevel level);
	}

	@FunctionalInterface
	public interface EndLevelTick {
		void onEndTick(ClientLevel level);
	}
}

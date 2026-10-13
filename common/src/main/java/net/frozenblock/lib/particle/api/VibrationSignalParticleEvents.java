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

package net.frozenblock.lib.particle.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

@UtilityClass
public final class VibrationSignalParticleEvents {
	/**
	 * The event that is triggered to evaluate whether a Vibration Signal Particle should be created.
	 */
	public static final Event<EvaluateVisibility> EVALUATE_VISIBILITY = EventRegistry.createEnvironmentEvent(EvaluateVisibility.class,
		callbacks -> (data, user) -> {
			for (EvaluateVisibility callback : callbacks) if (callback.isVisible(data, user)) return false;
			return true;
		}
	);

	@FunctionalInterface
	public interface EvaluateVisibility {
		boolean isVisible(VibrationSystem.Data data, VibrationSystem.User user);
	}
}

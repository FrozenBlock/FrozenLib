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

package net.frozenblock.lib.sound.api.type;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.event.api.events.EntityTrackingEvents;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.sound.impl.MovingSoundManager;

@UtilityClass
public final class MovingSoundTypes {
	private static final DeferredRegister<MovingSoundType<?>> REGISTER = DeferredRegister.create(
		FrozenLibRegistries.MOVING_SOUND_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<MovingSoundType<?>, MovingLoopingSoundType> LOOPING = REGISTER.register(
		"looping",
		() -> new MovingLoopingSoundType(FrozenLibConstants.id("looping"))
	);
	public static final DeferredHolder<MovingSoundType<?>, FadingDistanceLoopingMovingSoundType> LOOPING_FADING_DISTANCE = REGISTER.register(
		"looping_fading_distance",
		() -> new FadingDistanceLoopingMovingSoundType(FrozenLibConstants.id("looping_fading_distance"))
	);

	static {
		REGISTER.register();
	}

	public static void init() {
		EntityTrackingEvents.START_TRACKING.register(MovingSoundManager::syncWithPlayer);
	}
}

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

package net.frozenblock.lib.sound.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.sound.api.type.MovingSoundType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@UtilityClass
public final class MovingSoundManager {

	public static void tick(Entity entity) {
		for (MovingSoundType<?> type : FrozenLibRegistries.MOVING_SOUND_TYPE) type.tickSounds(entity);
	}

	public static void syncWithPlayer(Entity entity, ServerPlayer player) {
		for (MovingSoundType<?> type : FrozenLibRegistries.MOVING_SOUND_TYPE) type.syncSounds(entity, player);
	}
}

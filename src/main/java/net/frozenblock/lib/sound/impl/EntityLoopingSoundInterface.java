/*
 * Copyright (C) 2024-2025 FrozenBlock
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

import net.frozenblock.lib.sound.api.MovingLoopingSoundEntityManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface EntityLoopingSoundInterface {

    default MovingLoopingSoundEntityManager frozenLib$getSoundManager() {
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}

    default void frozenLib$addSound(
		ResourceLocation soundID,
		SoundSource category,
		float volume,
		float pitch,
		ResourceLocation restrictionId,
		boolean stopOnDeath
	) {
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}

}

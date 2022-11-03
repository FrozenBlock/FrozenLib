/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.impl;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;

public final class PlayerDamageSourcesWithSounds {

    public static final Map<DamageSource, SoundEvent> DAMAGE_SOURCES_AND_SOUNDS = new HashMap<>();

	public static void addDamageSound(DamageSource source, SoundEvent sound) {
		DAMAGE_SOURCES_AND_SOUNDS.put(source, sound);
	}

	public static SoundEvent getDamageSound(DamageSource source) {
		return DAMAGE_SOURCES_AND_SOUNDS.containsKey(source) ? DAMAGE_SOURCES_AND_SOUNDS.get(source) : SoundEvents.PLAYER_HURT;
	}

}

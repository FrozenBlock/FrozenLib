/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.api;

import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public abstract class RestrictedSoundInstance<T extends Entity> extends AbstractTickableSoundInstance {

	protected final T entity;
	protected final SoundPredicate.LoopPredicate<T> predicate;
	protected boolean firstTick = true;

	protected RestrictedSoundInstance(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource, T entity, SoundPredicate.LoopPredicate<T> predicate) {
		super(soundEvent, soundSource, randomSource);
		this.entity = entity;
		this.predicate = predicate;
		this.predicate.onStart(entity);
	}

	@Override
	public boolean canPlaySound() {
		return !this.entity.isSilent();
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

	@Override
	public void stop() {
		this.predicate.onStop(this.entity);
		super.stop();
	}

	public boolean test() {
		if (this.firstTick) {
			this.firstTick = false;
			Boolean firstTickBool = this.predicate.firstTickTest(this.entity);
			if (firstTickBool != null) return firstTickBool;
		}
		return this.predicate.test(this.entity);
	}
}

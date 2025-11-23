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

package net.frozenblock.lib.sound.client.api.sounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class RestrictedMovingSound<T extends Entity> extends RestrictedSoundInstance<T> {
	private final boolean stopOnDeath;

    public RestrictedMovingSound(
		T entity, SoundEvent sound, SoundSource source, float volume, float pitch, SoundPredicate.LoopPredicate<T> predicate, boolean stopOnDeath
	) {
        super(sound, source, SoundInstance.createUnseededRandom(), entity, predicate);
        this.looping = false;
        this.volume = volume;
        this.pitch = pitch;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
		this.stopOnDeath = stopOnDeath;

		this.predicate.onStart(this.entity);
    }

	@Override
    public void tick() {
        if (this.stopOnDeath && this.entity.isRemoved()) {
            this.stop();
			return;
        }

		if (!this.test()) {
			this.stop();
			return;
		}
		this.x = this.entity.getX();
		this.y = this.entity.getY();
		this.z = this.entity.getZ();
    }
}

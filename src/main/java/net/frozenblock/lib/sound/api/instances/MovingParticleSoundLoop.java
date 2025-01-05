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

package net.frozenblock.lib.sound.api.instances;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

@Environment(EnvType.CLIENT)
public class MovingParticleSoundLoop<T extends Particle> extends AbstractTickableSoundInstance {

	private final T particle;
	private final int fadeInTicks;
	private final float increaseVolumeBy;
	private int ticks;
	private final boolean stopOnDeath;

	public MovingParticleSoundLoop(T particle, SoundEvent sound, SoundSource category, float volume, float pitch, int fadeInTicks, boolean stopOnDeath) {
		super(sound, category, SoundInstance.createUnseededRandom());
		this.particle = particle;
		this.looping = true;
		this.delay = 0;
		this.pitch = pitch;

		this.x = particle.x;
		this.y = particle.y;
		this.z = particle.z;

		this.fadeInTicks = fadeInTicks;
		this.increaseVolumeBy = fadeInTicks != 0 ? (volume / fadeInTicks) : volume;
		this.volume = fadeInTicks != 0 ? 0 : volume;

		this.stopOnDeath = stopOnDeath;
	}

	@Override
	public boolean canPlaySound() {
		return this.particle.isAlive();
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

	@Override
	public void tick() {
		if (this.ticks < this.fadeInTicks) {
			this.volume += this.increaseVolumeBy;
			this.ticks += 1;
		}
		if (this.particle == null || (this.stopOnDeath && !this.particle.isAlive())) {
			this.stop();
		} else {
			this.x = this.particle.x;
			this.y = this.particle.y;
			this.z = this.particle.z;
		}
	}

}

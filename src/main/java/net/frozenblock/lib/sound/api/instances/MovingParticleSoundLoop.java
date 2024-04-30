/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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

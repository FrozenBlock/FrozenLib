/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.sound.api.instances;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.sound.api.RestrictedSoundInstance;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class RestrictedStartingSound<T extends Entity> extends RestrictedSoundInstance {

	public final T entity;
	public final SoundPredicate.LoopPredicate<T> predicate;
	public final SoundEvent loopingSound;
	public final SoundEvent startingSound;
	public boolean hasSwitched = false;
	public final AbstractSoundInstance nextSound;

	public RestrictedStartingSound(T entity, SoundEvent startingSound, SoundEvent loopingSound, SoundSource category, float volume, float pitch, SoundPredicate.LoopPredicate<T> predicate, AbstractSoundInstance nextSound) {
		super(startingSound, category, SoundInstance.createUnseededRandom());
		this.startingSound = startingSound;
		this.nextSound = nextSound;
		this.loopingSound = loopingSound;
		this.entity = entity;
		this.looping = false;
		this.delay = 0;
		this.volume = volume;
		this.pitch = pitch;

		this.x = (float) entity.getX();
		this.y = (float) entity.getY();
		this.z = (float) entity.getZ();
		this.predicate = predicate;
		this.predicate.onStart(this.entity);
	}

	public void startNextSound() {
		this.stop();
		this.hasSwitched = true;
		Minecraft.getInstance().getSoundManager().play(this.nextSound);
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
	public boolean isStopped() {
		if (this.hasSwitched) {
			return true;
		}
		return super.isStopped();
	}

	@Override
	public void stop() {
		this.predicate.onStop(this.entity);
		super.stop();
	}

	@Override
	public void tick() {
		if (!this.isStopped()) {
			if (this.entity.isRemoved()) {
				this.stop();
			} else {
				if (!this.predicate.test(this.entity)) {
					this.stop();
				} else {
					var soundManager = Minecraft.getInstance().getSoundManager();
					var soundEngine = soundManager.soundEngine;
					var channelHandle = soundEngine.instanceToChannel.get(this);
					if (channelHandle != null) {
						channelHandle.execute(source -> {
							if (!source.playing()) {
								this.startNextSound();
							}
						});
					}

					this.x = (float) this.entity.getX();
					this.y = (float) this.entity.getY();
					this.z = (float) this.entity.getZ();
				}
			}
		}
	}
}

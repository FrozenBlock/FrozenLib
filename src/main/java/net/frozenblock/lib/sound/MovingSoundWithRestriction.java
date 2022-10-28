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

package net.frozenblock.lib.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.sound.SoundPredicate.SoundPredicate;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class MovingSoundWithRestriction<T extends Entity> extends AbstractTickableSoundInstance {

    private final T entity;
    private final SoundPredicate.LoopPredicate<T> predicate;

    public MovingSoundWithRestriction(T entity, SoundEvent sound, SoundSource category, float volume, float pitch, SoundPredicate.LoopPredicate<T> predicate) {
        super(sound, category, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.looping = false;
        this.volume = volume;
        this.pitch = pitch;
        this.x = (float) entity.getX();
        this.y = (float) entity.getY();
        this.z = (float) entity.getZ();
        this.predicate = predicate;
    }

    public boolean canPlaySound() {
        return !this.entity.isSilent();
    }

    public boolean canStartSilent() {
        return true;
    }

	@Override
	public void stop() {
		this.predicate.onStop(this.entity);
		super.stop();
	}

    public void tick() {
        if (this.entity.isRemoved()) {
            this.stop();
        } else {
            if (!this.predicate.test(this.entity)) {
                this.stop();
            } else {
                this.x = (float) this.entity.getX();
                this.y = (float) this.entity.getY();
                this.z = (float) this.entity.getZ();
            }
        }
    }

}

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

package net.frozenblock.lib.sound.api.instances.distance_based;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.sound.api.RestrictedSoundInstance;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class RestrictedMovingFadingDistanceSwitchingSoundLoop<T extends Entity> extends RestrictedSoundInstance<T> {

    private final boolean isFarSound;
    private final double maxDist;
    private final double fadeDist;
    private final float maxVol;

    public RestrictedMovingFadingDistanceSwitchingSoundLoop(T entity, SoundEvent sound, SoundSource category, float volume, float pitch, SoundPredicate.LoopPredicate<T> predicate, boolean stopOnDeath, double fadeDist, double maxDist, float maxVol, boolean isFarSound) {
        super(sound, category, SoundInstance.createUnseededRandom(), entity, predicate);
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.pitch = pitch;

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.isFarSound = isFarSound;
        this.maxDist = maxDist;
        this.fadeDist = fadeDist;
        this.maxVol = maxVol;
    }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (this.entity.isRemoved()) {
            this.stop();
        } else {
            if (!this.test()) {
                this.stop();
            } else {
                this.x = (float) this.entity.getX();
                this.y = (float) this.entity.getY();
                this.z = (float) this.entity.getZ();
                if (client.player != null) {
                    float distance = client.player.distanceTo(this.entity);
                    if (distance < this.fadeDist) {
                        this.volume = !this.isFarSound ? this.maxVol : 0.001F;
                    } else if (distance > this.maxDist) {
                        this.volume = this.isFarSound ? this.maxVol : 0.001F;
                    } else {
                        //Gets lower as you move farther
                        float fadeProgress = (float) ((this.maxDist - distance) / (this.maxDist - this.fadeDist));
                        this.volume = this.isFarSound ? (1F - fadeProgress) * this.maxVol : fadeProgress * this.maxVol;
                    }
                }
            }
        }
    }

}

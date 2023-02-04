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

package net.frozenblock.lib.sound.api.instances.distance_based;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

@Environment(EnvType.CLIENT)
public class FadingDistanceSwitchingSound extends AbstractTickableSoundInstance {

    private final boolean isFarSound;
    private final double maxDist;
    private final double fadeDist;
    private final float maxVol;

    public FadingDistanceSwitchingSound(SoundEvent sound, SoundSource category, float volume, float pitch, double fadeDist, double maxDist, float maxVol, boolean isFarSound, double x, double y, double z) {
        super(sound, category, SoundInstance.createUnseededRandom());
        this.delay = 0;
        this.volume = volume;
        this.pitch = pitch;

        this.isFarSound = isFarSound;
        this.maxDist = maxDist;
        this.fadeDist = fadeDist;
        this.maxVol = maxVol;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            float distance = (float) Math.sqrt(client.player.distanceToSqr(this.x, this.y, this.z));
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

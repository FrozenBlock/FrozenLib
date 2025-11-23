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

package net.frozenblock.lib.sound.client.api.sounds.distance_based;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class FadingDistanceSwitchingSound extends AbstractTickableSoundInstance {
    private final boolean isFarSound;
    private final double maxDist;
    private final double fadeDist;
    private final float maxVol;

    public FadingDistanceSwitchingSound(
		SoundEvent sound, SoundSource source, float volume, float pitch, double fadeDist, double maxDist, float maxVol, boolean isFarSound, Vec3 pos
	) {
        super(sound, source, SoundInstance.createUnseededRandom());
        this.delay = 0;
        this.volume = volume;
        this.pitch = pitch;

        this.isFarSound = isFarSound;
        this.maxDist = maxDist;
        this.fadeDist = fadeDist;
        this.maxVol = maxVol;
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    @Override
    public void tick() {
		final Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

		final double distance = Math.sqrt(client.player.distanceToSqr(this.x, this.y, this.z));
		if (distance < this.fadeDist) {
			this.volume = !this.isFarSound ? this.maxVol : 0.001F;
		} else if (distance > this.maxDist) {
			this.volume = this.isFarSound ? this.maxVol : 0.001F;
		} else {
			//Gets lower as you move farther
			final float fadeProgress = (float) ((this.maxDist - distance) / (this.maxDist - this.fadeDist));
			this.volume = this.isFarSound ? (1F - fadeProgress) * this.maxVol : fadeProgress * this.maxVol;
		}
    }

}

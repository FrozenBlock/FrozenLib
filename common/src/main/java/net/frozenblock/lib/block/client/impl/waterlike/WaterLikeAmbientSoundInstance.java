/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.client.impl.waterlike;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.impl.waterlike.PlayerInWaterLikeInterface;import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.sounds.SoundSource;

@Environment(EnvType.CLIENT)
public class WaterLikeAmbientSoundInstance extends AbstractTickableSoundInstance {
	public static final int FADE_DURATION = UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance.FADE_DURATION;
	private final WaterLikeType type;
	private final LocalPlayer player;
	private int fade;

	public WaterLikeAmbientSoundInstance(WaterLikeType type, LocalPlayer localPlayer) {
		super(type.ambientSound().value(), SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
		this.type = type;
		this.player = localPlayer;
		this.looping = true;
		this.delay = 0;
		this.volume = 1F;
		this.relative = true;
	}

	@Override
	public void tick() {
		if (this.player.isRemoved() || this.fade < 0) {
			this.stop();
			return;
		}

		if (this.player.isUnderWater() && ((PlayerInWaterLikeInterface) this.player).frozenLib$wasPlayerInWaterLike(this.type)) {
			this.fade++;
		} else {
			this.fade -= 2;
		}

		this.fade = Math.min(this.fade, FADE_DURATION);
		this.volume = Math.max(0F, Math.min((float) this.fade / FADE_DURATION, 1F));
	}
}

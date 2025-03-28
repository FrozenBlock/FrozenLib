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

package net.frozenblock.lib.music.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.music.api.client.pitch.MusicPitchApi;
import net.frozenblock.lib.music.impl.client.SoundEngineInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MusicManager.class)
public class MusicManagerMixin {

	@Shadow
	private @Nullable SoundInstance currentMusic;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private float frozenLib$currentPitch = 1F;

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;getSituationalMusic()Lnet/minecraft/sounds/Music;",
			shift = At.Shift.AFTER
		)
	)
	public void frozenLib$tick(CallbackInfo info) {
		float updatedPitch = MusicPitchApi.getCurrentPitch();
		if (this.currentMusic != null && this.frozenLib$currentPitch != updatedPitch) {
			this.frozenLib$pitchShift(updatedPitch);
		}
	}

	@WrapOperation(
		method = "startPlaying",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"
		)
	)
	public void frozenLib$startPlayingAtCorrectPitch(SoundManager instance, SoundInstance soundInstance, Operation<Void> original) {
		original.call(instance, soundInstance);
		if (this.minecraft.getSoundManager().soundEngine instanceof SoundEngineInterface soundEngineInterface) {
			soundEngineInterface.frozenLib$setPitch(this.currentMusic, MusicPitchApi.getCurrentPitch());
		}
	}

	@Inject(method = "startPlaying", at = @At("TAIL"))
	public void frozenLib$setCurrentPitchAtSongStart(Music music, CallbackInfo info) {
		this.frozenLib$currentPitch = MusicPitchApi.getCurrentPitch();
	}

	@Unique
	private void frozenLib$pitchShift(float targetPitch) {
		if (this.currentMusic != null && this.frozenLib$currentPitch != targetPitch) {
			if (this.frozenLib$currentPitch < targetPitch) {
				this.frozenLib$currentPitch = this.frozenLib$currentPitch + Mth.clamp(this.frozenLib$currentPitch, 5.0E-4F, 0.005F);
				if (this.frozenLib$currentPitch > targetPitch) {
					this.frozenLib$currentPitch = targetPitch;
				}
			} else {
				this.frozenLib$currentPitch = 0.03F * targetPitch + 0.97F * this.frozenLib$currentPitch;
				if (Math.abs(this.frozenLib$currentPitch - targetPitch) < 1.0E-4F || this.frozenLib$currentPitch < targetPitch) {
					this.frozenLib$currentPitch = targetPitch;
				}
			}

			this.frozenLib$currentPitch = Mth.clamp(this.frozenLib$currentPitch, 0F, 5F);
			if (this.frozenLib$currentPitch > 1.0E-4F) {
				if (this.minecraft.getSoundManager().soundEngine instanceof SoundEngineInterface soundEngineInterface) {
					soundEngineInterface.frozenLib$setPitch(this.currentMusic, this.frozenLib$currentPitch);
				}
			}
		}
	}
}

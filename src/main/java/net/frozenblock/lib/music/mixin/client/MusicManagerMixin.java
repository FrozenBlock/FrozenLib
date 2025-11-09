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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.music.api.client.pitch.MusicPitchApi;
import net.frozenblock.lib.music.impl.client.SoundEngineInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
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

	@Inject(method = "tick", at = @At("HEAD"))
	public void frozenLib$tick(CallbackInfo info) {
		if (this.minecraft.level != null) MusicPitchApi.updateTargetMusicPitch(minecraft.level, this.minecraft.level.getBiome(this.minecraft.gameRenderer.getMainCamera().blockPosition()));
		final float targetPitch = MusicPitchApi.getCurrentPitch();
		if (this.currentMusic != null && this.frozenLib$currentPitch != targetPitch) this.frozenLib$updateTargetPitchAndShift(targetPitch);
	}

	@ModifyExpressionValue(
		method = "startPlaying",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forMusic(Lnet/minecraft/sounds/SoundEvent;)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
		)
	)
	public SimpleSoundInstance frozenLib$startPlayingAtCorrectPitch(SimpleSoundInstance original) {
		original.pitch = this.frozenLib$currentPitch;
		return original;
	}

	@Unique
	private void frozenLib$updateTargetPitchAndShift(float targetPitch) {
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

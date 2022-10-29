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

package net.frozenblock.lib.screenshake;

import java.util.ArrayList;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class ScreenShaker {

	public static final ArrayList<ScreenShake> SCREEN_SHAKES = new ArrayList<>();
	private static final ArrayList<ScreenShake> SHAKES_TO_REMOVE = new ArrayList<>();
	private static float intensity;

	public static void tick(Camera camera) {
		float highestIntensity = 0F;
		float totalIntensity = 0F;
		int amount = 0;
		for (ScreenShake shake : SCREEN_SHAKES) {
			amount += 1;
			float shakeIntensity = shake.getIntensity(camera.getPosition());
			totalIntensity += shakeIntensity;
			highestIntensity = Math.max(shakeIntensity, highestIntensity);
			shake.ticks += 1;
			if (shake.ticks > shake.duration) {
				SHAKES_TO_REMOVE.add(shake);
			}
		}
		if (amount > 0 && totalIntensity != 0 && highestIntensity != 0) {
			intensity = (highestIntensity + (totalIntensity / amount)) * 0.5F;
		} else {
			intensity = 0F;
		}
		SCREEN_SHAKES.removeAll(SHAKES_TO_REMOVE);
		SHAKES_TO_REMOVE.clear();
	}

	public static void cameraShake(RandomSource randomSource, Camera camera, int windowWidth, int windowHeight) {
		if (intensity != 0) {
			camera.setRotation(camera.getYRot() + (Mth.nextFloat(randomSource, -intensity, intensity) * ((float) windowWidth / (float) windowHeight)), camera.getXRot() + Mth.nextFloat(randomSource, -intensity, intensity));
		}
	}

	public static void addShake(float intensity, int duration, Vec3 pos, float maxDistance) {
		SCREEN_SHAKES.add(new ScreenShake(intensity, duration, pos, maxDistance));
	}

	public static void addShakeWithTimeFalloff(float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance) {
		SCREEN_SHAKES.add(new ScreenShakeTimeFalloff(intensity, duration, falloffStart, pos, maxDistance));
	}

	public static class ScreenShake {
		private final float intensity;
		public final int duration;
		public final Vec3 pos;
		public final float maxDistance;
		public int ticks;

		public ScreenShake(float intensity, int duration, Vec3 pos, float maxDistance) {
			this.intensity = intensity;
			this.duration = duration;
			this.pos = pos;
			this.maxDistance = maxDistance;
		}

		public float getIntensity(Vec3 playerPos) {
			return Math.max((float) (1F - (playerPos.distanceTo(this.pos) / this.maxDistance) * this.intensity), 0);
		}
	}

	public static class ScreenShakeTimeFalloff extends ScreenShake {
		private final int durationFalloffStart;

		public ScreenShakeTimeFalloff(float intensity, int duration, int durationFalloffStart, Vec3 pos, float maxDistance) {
			super(intensity, duration, pos, maxDistance);
			this.durationFalloffStart = durationFalloffStart;
		}

		public float getIntensity(Vec3 playerPos) {
			float distanceBasedIntensity = super.getIntensity(playerPos);
			if (distanceBasedIntensity > 0) {
				int currentDuration = Math.max(this.ticks - this.durationFalloffStart, 0);
				int maxDuration = this.duration - this.durationFalloffStart;
				return distanceBasedIntensity * (maxDuration - currentDuration);
			}
			return 0F;
		}
	}

}

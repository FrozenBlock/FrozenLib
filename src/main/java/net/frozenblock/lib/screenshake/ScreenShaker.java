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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class ScreenShaker {

	public static final ArrayList<ScreenShake> SCREEN_SHAKES = new ArrayList<>();
	private static final ArrayList<ScreenShake> SHAKES_TO_REMOVE = new ArrayList<>();

	private static float prevYRot;
	private static float yRot;
	private static float prevXRot;
	private static float xRot;
	private static float prevZRot;
	private static float zRot;

	public static void tick(Camera camera, RandomSource randomSource, int windowWidth, int windowHeight) {
		prevYRot = yRot;
		prevXRot = xRot;
		prevZRot = zRot;
		if (!Minecraft.getInstance().isMultiplayerServer() && Minecraft.getInstance().isPaused()) {
			yRot = 0F;
			xRot = 0F;
			zRot = 0F;
			return;
		}
		float highestIntensity = 0F;
		float totalIntensity = 0F;
		int amount = 0;
		for (ScreenShake shake : SCREEN_SHAKES) {
			float shakeIntensity = shake.getIntensity(camera.getPosition());
			if (shakeIntensity > 0) {
				totalIntensity += shakeIntensity;
				highestIntensity = Math.max(shakeIntensity, highestIntensity);
				amount += 1;
			}
			if (shake.ticks > shake.duration) {
				SHAKES_TO_REMOVE.add(shake);
			}
			shake.ticks += 1;
		}
		float intensity = (amount > 0 && totalIntensity != 0 && highestIntensity != 0) ? (highestIntensity + ((totalIntensity / amount) * 0.5F)) : 0F;
		SCREEN_SHAKES.removeAll(SHAKES_TO_REMOVE);
		SHAKES_TO_REMOVE.clear();
		yRot = Mth.nextFloat(randomSource, -intensity, intensity) * ((float) windowWidth / (float) windowHeight);
		xRot = Mth.nextFloat(randomSource, -intensity, intensity);
		zRot = Mth.nextFloat(randomSource, -intensity, intensity);
	}


	public static void shake(PoseStack poseStack, float partialTicks) {
		poseStack.mulPose(Vector3f.XP.rotationDegrees(prevXRot + partialTicks * xRot - prevXRot));
		poseStack.mulPose(Vector3f.YP.rotationDegrees(prevYRot + partialTicks * yRot - prevYRot));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(prevZRot + partialTicks * zRot - prevZRot));
	}

	@Deprecated
	public static float cameraZ(float partialTicks) {
		return Mth.lerp(partialTicks, prevZRot, zRot);
	}

	@Deprecated
	public static void cameraShake(Camera camera, float partialTicks) {
		camera.setRotation(camera.getYRot() + (Mth.lerp(partialTicks, prevYRot, yRot)), camera.getXRot() + (Mth.lerp(partialTicks, prevXRot, xRot)));
	}

	public static void addShake(float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance) {
		SCREEN_SHAKES.add(new ScreenShake(intensity, duration, falloffStart, pos, maxDistance));
	}

	public static void addShake(Entity entity, float intensity, int duration, int falloffStart, float maxDistance) {
		SCREEN_SHAKES.add(new EntityScreenShake(entity, intensity, duration, falloffStart, maxDistance));
	}

	public static class ScreenShake {
		private final float intensity;
		public final int duration;
		private final int durationFalloffStart;
		protected Vec3 pos;
		public final float maxDistance;
		public int ticks;

		public ScreenShake(float intensity, int duration, int durationFalloffStart, Vec3 pos, float maxDistance) {
			this.intensity = intensity;
			this.duration = duration;
			this.durationFalloffStart = durationFalloffStart;
			this.pos = pos;
			this.maxDistance = maxDistance;
		}

		public float getIntensity(Vec3 playerPos) {
			float distanceBasedIntensity = Math.max((float) (1F - (playerPos.distanceTo(this.pos) / this.maxDistance) * this.intensity), 0);
			if (distanceBasedIntensity > 0) {
				int timeFromFalloffStart = Math.max(this.ticks - this.durationFalloffStart, 0); //Starts counting up once it reaches falloff start
				int falloffTime = this.duration - this.durationFalloffStart; //The amount of time the intensity falls off for before reaching 0
				float lerpedFalloffTime = Mth.lerp(this.ticks / this.duration, 0, falloffTime);
				return (distanceBasedIntensity * (falloffTime - timeFromFalloffStart)) / lerpedFalloffTime;
			}
			return 0F;
		}
	}

	public static class EntityScreenShake extends ScreenShake {
		private final Entity entity;

		public EntityScreenShake(Entity entity, float intensity, int duration, int durationFalloffStart, float maxDistance) {
			super(intensity, duration, durationFalloffStart, entity.position(), maxDistance);
			this.entity = entity;
		}

		@Override
		public float getIntensity(Vec3 playerPos) {
			if (this.entity != null && !this.entity.isRemoved()) {
				this.pos = this.entity.position();
				return super.getIntensity(playerPos);
			}
			return 0F;
		}
	}

}

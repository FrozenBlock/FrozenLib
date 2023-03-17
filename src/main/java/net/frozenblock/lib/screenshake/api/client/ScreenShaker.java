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

package net.frozenblock.lib.screenshake.api.client;

import java.util.ArrayList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class ScreenShaker {

	public static final ArrayList<ClientScreenShake> SCREEN_SHAKES = new ArrayList<>();

	private static float prevYRot;
	private static float yRot;
	private static float prevXRot;
	private static float xRot;
	private static float prevZRot;
	private static float zRot;

	public static void tick(Camera camera, RandomSource randomSource, int windowWidth, int windowHeight) {
		SCREEN_SHAKES.removeIf(ClientScreenShake::shouldRemove);
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
		for (ClientScreenShake shake : SCREEN_SHAKES) {
			float shakeIntensity = shake.getIntensity(camera.getPosition());
			if (shakeIntensity > 0) {
				totalIntensity += shakeIntensity;
				highestIntensity = Math.max(shakeIntensity, highestIntensity);
				amount += 1;
			}
			shake.ticks += 1;
		}
		float intensity = (amount > 0 && totalIntensity != 0 && highestIntensity != 0) ? (highestIntensity + ((totalIntensity / amount) * 0.5F)) : 0F;
		yRot = Mth.nextFloat(randomSource, -intensity, intensity) * ((float) windowWidth / (float) windowHeight);
		xRot = Mth.nextFloat(randomSource, -intensity, intensity);
		zRot = Mth.nextFloat(randomSource, -intensity, intensity);
	}

	public static void shake(PoseStack poseStack, float partialTicks) {
		poseStack.mulPose(Axis.XP.rotationDegrees(prevXRot + partialTicks * xRot - prevXRot));
		poseStack.mulPose(Axis.YP.rotationDegrees(prevYRot + partialTicks * yRot - prevYRot));
		poseStack.mulPose(Axis.ZP.rotationDegrees(prevZRot + partialTicks * zRot - prevZRot));
	}

	@Deprecated
	public static float cameraZ(float partialTicks) {
		return Mth.lerp(partialTicks, prevZRot, zRot);
	}

	@Deprecated
	public static void cameraShake(Camera camera, float partialTicks) {
		camera.setRotation(camera.getYRot() + (Mth.lerp(partialTicks, prevYRot, yRot)), camera.getXRot() + (Mth.lerp(partialTicks, prevXRot, xRot)));
	}

	public static void addShake(float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance, int ticks) {
		SCREEN_SHAKES.add(new ClientScreenShake(intensity, duration, falloffStart, pos, maxDistance, ticks));
	}

	public static void addShake(Entity entity, float intensity, int duration, int falloffStart, float maxDistance, int ticks) {
		SCREEN_SHAKES.add(new ClientEntityScreenShake(entity, intensity, duration, falloffStart, maxDistance, ticks));
	}

	public static class ClientScreenShake {
		private final float intensity;
		public final int duration;
		private final int durationFalloffStart;
		protected Vec3 pos;
		public final float maxDistance;
		public int ticks;

		public ClientScreenShake(float intensity, int duration, int durationFalloffStart, Vec3 pos, float maxDistance, int ticks) {
			this.intensity = intensity;
			this.duration = duration;
			this.durationFalloffStart = durationFalloffStart;
			this.pos = pos;
			this.maxDistance = maxDistance;
			this.ticks = ticks;
		}

		public float getIntensity(Vec3 playerPos) {
			float distanceBasedIntensity = Math.max((float) (1F - (playerPos.distanceTo(this.pos) / this.maxDistance)), 0);
			if (distanceBasedIntensity > 0) {
				float timeFromFalloffStart = Math.max(this.ticks - this.durationFalloffStart, 0); //Starts counting up once it reaches falloff start
				float falloffTime = this.duration - this.durationFalloffStart; //The amount of time the intensity falls off for before reaching 0
				float lerpedTimeFromFalloffStart = Mth.lerp((float)this.ticks / this.duration, 0, timeFromFalloffStart);
				return (distanceBasedIntensity * ((falloffTime - lerpedTimeFromFalloffStart) / falloffTime)) * intensity;
			}
			return 0F;
		}

		public boolean shouldRemove() {
			return this.ticks > this.duration;
		}
	}

	public static class ClientEntityScreenShake extends ClientScreenShake {
		private final Entity entity;

		public ClientEntityScreenShake(Entity entity, float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
			super(intensity, duration, durationFalloffStart, entity.position(), maxDistance, ticks);
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

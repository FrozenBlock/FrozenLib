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

package net.frozenblock.lib.screenshake.api.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ScreenShaker {
	public static final ArrayList<ClientScreenShake> SCREEN_SHAKES = new ArrayList<>();

	private static float prevYRot;
	private static float yRot;
	private static float prevXRot;
	private static float xRot;
	private static float prevZRot;
	private static float zRot;

	@ApiStatus.Internal
	public static void tick(@NotNull ClientLevel level) {
		if (level.tickRateManager().runsNormally()) {
			Minecraft client = Minecraft.getInstance();
			prevYRot = yRot;
			prevXRot = xRot;
			prevZRot = zRot;
			if (!client.isMultiplayerServer() && client.isPaused()) {
				yRot = 0F;
				xRot = 0F;
				zRot = 0F;
				return;
			}
			Window window = client.getWindow();
			int windowWidth = window.getWidth();
			int windowHeight = window.getHeight();
			RandomSource randomSource = level.getRandom();

			SCREEN_SHAKES.removeIf(clientScreenShake -> clientScreenShake.shouldRemove(level));
			float highestIntensity = 0;
			float totalIntensity = 0;
			int amount = 0;
			for (ClientScreenShake screenShake : SCREEN_SHAKES) {
				screenShake.tick();
				float shakeIntensity = screenShake.getIntensity(client.gameRenderer.getMainCamera().getPosition());
				if (shakeIntensity > 0) {
					totalIntensity += shakeIntensity;
					highestIntensity = Math.max(shakeIntensity, highestIntensity);
					amount += 1;
				}
				screenShake.ticks += 1;
			}
			float intensity = (amount > 0 && totalIntensity != 0 && highestIntensity != 0) ? (highestIntensity + ((totalIntensity / amount) * 0.5F)) : 0F;
			yRot += (Mth.nextFloat(randomSource, -intensity, intensity) * ((float) windowWidth / (float) windowHeight) - yRot) * 0.65F;
			xRot += (Mth.nextFloat(randomSource, -intensity, intensity) - xRot) * 0.65F;
			zRot += (Mth.nextFloat(randomSource, -intensity, intensity) - zRot) * 0.65F;
		}
	}

	@ApiStatus.Internal
	public static void reset() {
		prevXRot = 0F;
		prevYRot = 0F;
		prevZRot = 0F;
		xRot = 0F;
		yRot = 0F;
		zRot = 0F;

		clear();
	}

	@ApiStatus.Internal
	public static void shake(@NotNull PoseStack poseStack, float partialTicks) {
		poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, prevYRot, yRot)));
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, prevXRot, xRot)));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, prevZRot, zRot)));
	}

	public static void addShake(ClientLevel level, float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance, int ticks) {
		SCREEN_SHAKES.add(new ClientScreenShake(level, intensity, duration, falloffStart, pos, maxDistance, ticks));
	}

	public static void addShake(Entity entity, float intensity, int duration, int falloffStart, float maxDistance, int ticks) {
		SCREEN_SHAKES.add(new ClientEntityScreenShake(entity, intensity, duration, falloffStart, maxDistance, ticks));
	}

	@ApiStatus.Internal
	public static void clear() {
		SCREEN_SHAKES.clear();
	}

	/**
	 * A screenshake instance.
	 */
	public static class ClientScreenShake {
		public final int duration;
		public final float maxDistance;
		private final float intensity;
		private final int durationFalloffStart;
		public Level level;
		public int ticks;
		protected Vec3 pos;

		public ClientScreenShake(Level level, float intensity, int duration, int durationFalloffStart, Vec3 pos, float maxDistance, int ticks) {
			this.level = level;
			this.intensity = intensity;
			this.duration = duration;
			this.durationFalloffStart = durationFalloffStart;
			this.pos = pos;
			this.maxDistance = maxDistance;
			this.ticks = ticks;
		}

		public float getIntensity(@NotNull Vec3 playerPos) {
			float distanceBasedIntensity = Math.max((float) (1F - (playerPos.distanceTo(this.pos) / this.maxDistance)), 0);
			if (distanceBasedIntensity > 0) {
				float timeFromFalloffStart = Math.max(this.ticks - this.durationFalloffStart, 0); //Starts counting up once it reaches falloff start
				float falloffTime = this.duration - this.durationFalloffStart; //The amount of time the intensity falls off for before reaching 0
				float lerpedTimeFromFalloffStart = Mth.lerp((float) this.ticks / this.duration, 0, timeFromFalloffStart);
				return (distanceBasedIntensity * ((falloffTime - lerpedTimeFromFalloffStart) / falloffTime)) * intensity;
			}
			return 0F;
		}

		public void tick() {
		}

		public boolean shouldRemove(Level level) {
			return this.ticks > this.duration || level != this.level;
		}
	}

	/**
	 * A screenshake instance that follows an entity.
	 */
	public static class ClientEntityScreenShake extends ClientScreenShake {
		private final Entity entity;

		public ClientEntityScreenShake(@NotNull Entity entity, float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
			super(entity.level(), intensity, duration, durationFalloffStart, entity.position(), maxDistance, ticks);
			this.entity = entity;
		}

		@Override
		public float getIntensity(@NotNull Vec3 playerPos) {
			if (this.entity != null && !this.entity.isRemoved()) {
				this.pos = this.entity.position();
				return super.getIntensity(playerPos);
			}
			return 0F;
		}

		public Entity getEntity() {
			return this.entity;
		}

		@Override
		public void tick() {
			super.tick();
			this.level = this.entity.level();
		}

		@Override
		public boolean shouldRemove(Level level) {
			return super.shouldRemove(level) || entity == null || entity.isRemoved();
		}
	}

}

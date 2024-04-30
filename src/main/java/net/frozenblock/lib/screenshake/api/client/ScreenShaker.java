/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.screenshake.api.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
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
			yRot = Mth.nextFloat(randomSource, -intensity, intensity) * ((float) windowWidth / (float) windowHeight);
			xRot = Mth.nextFloat(randomSource, -intensity, intensity);
			zRot = Mth.nextFloat(randomSource, -intensity, intensity);
		}
	}

	public static void shake(@NotNull PoseStack poseStack, float partialTicks) {
		poseStack.mulPose(Axis.XP.rotationDegrees(prevXRot + partialTicks * xRot - prevXRot));
		poseStack.mulPose(Axis.YP.rotationDegrees(prevYRot + partialTicks * yRot - prevYRot));
		poseStack.mulPose(Axis.ZP.rotationDegrees(prevZRot + partialTicks * zRot - prevZRot));
	}

	@Deprecated
	public static float cameraZ(float partialTicks) {
		return Mth.lerp(partialTicks, prevZRot, zRot);
	}

	@Deprecated
	public static void cameraShake(@NotNull Camera camera, float partialTicks) {
		camera.setRotation(camera.getYRot() + (Mth.lerp(partialTicks, prevYRot, yRot)), camera.getXRot() + (Mth.lerp(partialTicks, prevXRot, xRot)));
	}

	public static void addShake(ClientLevel level, float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance, int ticks) {
		SCREEN_SHAKES.add(new ClientScreenShake(level, intensity, duration, falloffStart, pos, maxDistance, ticks));
	}

	public static void addShake(Entity entity, float intensity, int duration, int falloffStart, float maxDistance, int ticks) {
		SCREEN_SHAKES.add(new ClientEntityScreenShake(entity, intensity, duration, falloffStart, maxDistance, ticks));
	}

	public static void clear() {
		SCREEN_SHAKES.clear();
	}

	public static class ClientScreenShake {
		public ClientLevel level;
		private final float intensity;
		public final int duration;
		private final int durationFalloffStart;
		protected Vec3 pos;
		public final float maxDistance;
		public int ticks;

		public ClientScreenShake(ClientLevel level, float intensity, int duration, int durationFalloffStart, Vec3 pos, float maxDistance, int ticks) {
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
				float lerpedTimeFromFalloffStart = Mth.lerp((float)this.ticks / this.duration, 0, timeFromFalloffStart);
				return (distanceBasedIntensity * ((falloffTime - lerpedTimeFromFalloffStart) / falloffTime)) * intensity;
			}
			return 0F;
		}

		public void tick() {

		}

		public boolean shouldRemove(ClientLevel level) {
			return this.ticks > this.duration || level != this.level;
		}
	}

	public static class ClientEntityScreenShake extends ClientScreenShake {
		private final Entity entity;

		public ClientEntityScreenShake(@NotNull Entity entity, float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
			super((ClientLevel) entity.level(), intensity, duration, durationFalloffStart, entity.position(), maxDistance, ticks);
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
			this.level = (ClientLevel) this.entity.level();
		}

		@Override
		public boolean shouldRemove(ClientLevel level) {
			return super.shouldRemove(level) || entity == null || entity.isRemoved();
		}
	}

}

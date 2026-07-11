/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.frozenblock.lib.screenshake.api.ScreenShake;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ClientScreenShaker {
	private static float prevYRot;
	private static float yRot;
	private static float prevXRot;
	private static float xRot;
	private static float prevZRot;
	private static float zRot;

	@ApiStatus.Internal
	public static void tick(Minecraft minecraft, ClientLevel level) {
		prevYRot = yRot;
		prevXRot = xRot;
		prevZRot = zRot;

		if (minecraft.isPaused()) return;
		if (!level.tickRateManager().runsNormally()) {
			scaleBackCurrentRotations(0.25F);
			return;
		}

		final Vec3 cameraPos = minecraft.gameRenderer.mainCamera().position();
		final long gameTime = level.getGameTime();
		float highestIntensity = 0;
		float totalIntensity = 0;
		int count = 0;

		for (ScreenShake screenShake : ScreenShakes.get(level).screenShakes()) {
			final float shakeIntensity = screenShake.calculateIntensityAt(cameraPos, gameTime);
			if (shakeIntensity <= 0) continue;
			totalIntensity += shakeIntensity;
			highestIntensity = Math.max(shakeIntensity, highestIntensity);
			count += 1;
		}

		for (Entity entity : level.entitiesForRendering()) {
			for (ScreenShake screenShake : ScreenShakes.get(entity).screenShakes()) {
				final float shakeIntensity = screenShake.calculateIntensityAt(cameraPos, gameTime, entity);
				if (shakeIntensity <= 0) continue;
				totalIntensity += shakeIntensity;
				highestIntensity = Math.max(shakeIntensity, highestIntensity);
				count += 1;
			}
		}

		final RandomSource random = level.getRandom();
		final Window window = minecraft.getWindow();
		final float intensity = (count > 0 && totalIntensity != 0 && highestIntensity != 0)
			? (highestIntensity + ((totalIntensity / count) * 0.5F))
			: 0F;
		yRot += (Mth.nextFloat(random, -intensity, intensity) * ((float) window.getWidth() / (float) window.getHeight()) - yRot) * 0.65F;
		xRot += (Mth.nextFloat(random, -intensity, intensity) - xRot) * 0.65F;
		zRot += (Mth.nextFloat(random, -intensity, intensity) - zRot) * 0.65F;
	}

	public static void reset() {
		prevXRot = 0F;
		prevYRot = 0F;
		prevZRot = 0F;
		xRot = 0F;
		yRot = 0F;
		zRot = 0F;
	}

	public static void scaleBackCurrentRotations(float scalePerTick) {
		xRot += (0F - xRot) * scalePerTick;
		yRot += (0F - yRot) * scalePerTick;
		zRot += (0F - zRot) * scalePerTick;
	}

	public static void apply(PoseStack poseStack, float partialTicks) {
		poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, prevYRot, yRot)));
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, prevXRot, xRot)));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, prevZRot, zRot)));
	}
}

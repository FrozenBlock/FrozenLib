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

package net.frozenblock.lib.spottingicon.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frozenblock.lib.spottingicon.api.SpottingIconManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3fc;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class SpottingIconHudElement implements HudElement {
	private static final float Y_OFFSET = 0.5F;
	private static final int ICON_SIZE = 16;
	private static final int ICON_HALF = ICON_SIZE / 2;

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) return;

		final float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
		final Camera camera = minecraft.gameRenderer.mainCamera();
		final Vec3 cameraPos = camera.position();
		final Vector3fc forwardAs3fc = camera.forwardVector();
		final Vec3 cameraForward = new Vec3(forwardAs3fc.x(), forwardAs3fc.y(), forwardAs3fc.z());

		for (Entity entity : minecraft.level.entitiesForRendering()) {
			final SpottingIconManager iconManager = entity.frozenLib$getSpottingIconManager();
			final SpottingIconManager.SpottingIcon icon = iconManager.icon;
			if (icon == null || !iconManager.clientHasIconResource) continue;

			final Vec3 eyePos = entity.getEyePosition(partialTicks);
			final double dist = Math.sqrt(cameraPos.distanceToSqr(eyePos));
			if (dist <= icon.startFadeDist()) continue;

			final float endDist = icon.endFadeDist() - icon.startFadeDist();
			final int alpha = (int) (Math.min(1F, (float) (dist - icon.startFadeDist()) / endDist) * 255F);

			final Vec3 iconWorldPos = new Vec3(eyePos.x, eyePos.y + entity.getBbHeight() + Y_OFFSET - entity.getEyeHeight(), eyePos.z);
			// check if in front of camera
			if (iconWorldPos.subtract(cameraPos).dot(cameraForward) <= 0D) continue;
			final Vec3 project = minecraft.gameRenderer.projectPointToScreen(iconWorldPos);

			if (project.z > 1D || project.x < -1D || project.x > 1D || project.y < -1D || project.y > 1D) continue;

			final float screenX = (float) ((project.x + 1D) / 2D * graphics.guiWidth());
			final float screenY = (float) ((1D - project.y) / 2D * graphics.guiHeight());
			graphics.pose().pushMatrix();
			graphics.pose().translate(screenX - ICON_HALF, screenY - ICON_HALF);
			graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				icon.texture(),
				0,
				0,
				0F, 0F,
				ICON_SIZE, ICON_SIZE,
				ICON_SIZE, ICON_SIZE,
				ARGB.color(alpha, 255, 255, 255)
			);
			graphics.pose().popMatrix();
		}
	}
}

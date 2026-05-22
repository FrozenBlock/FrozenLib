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

package net.frozenblock.lib.spotting_icons.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frozenblock.lib.spotting_icons.api.SpottingIconManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class SpottingIconHudElement implements HudElement {

	private static final int ICON_SIZE = 16;
	private static final int ICON_HALF = ICON_SIZE / 2;

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) return;

		float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
		var camera = minecraft.gameRenderer.mainCamera();
		Vec3 cameraPos = camera.position();
		float cameraYaw = camera.yRot() * Mth.DEG_TO_RAD;
		float cameraPitch = camera.xRot() * Mth.DEG_TO_RAD;
		Vec3 cameraForward = new Vec3(
			-Mth.sin(cameraYaw) * Mth.cos(cameraPitch),
			-Mth.sin(cameraPitch),
			Mth.cos(cameraYaw) * Mth.cos(cameraPitch)
		);

		for (Entity entity : minecraft.level.entitiesForRendering()) {
			SpottingIconManager iconManager = entity.frozenLib$getSpottingIconManager();
			SpottingIconManager.SpottingIcon icon = iconManager.icon;
			if (icon == null || !iconManager.clientHasIconResource) continue;

			Vec3 eyePos = entity.getEyePosition(partialTick);
			double dist = Math.sqrt(cameraPos.distanceToSqr(eyePos));
			if (dist <= icon.startFadeDist()) continue;

			float endDist = icon.endFadeDist() - icon.startFadeDist();
			int alpha = (int) (Math.min(1F, (float) (dist - icon.startFadeDist()) / endDist) * 255F);

			Vec3 iconWorldPos = new Vec3(eyePos.x, eyePos.y + entity.getBbHeight() + 1F - entity.getEyeHeight(), eyePos.z);
			// check if in front of camera
			if (iconWorldPos.subtract(cameraPos).dot(cameraForward) <= 0D) continue;
			Vec3 project = minecraft.gameRenderer.projectPointToScreen(iconWorldPos);

			if (project.z > 1.0 || project.x < -1.0 || project.x > 1.0 || project.y < -1.0 || project.y > 1.0) continue;

			int screenX = (int) ((project.x + 1.0) / 2.0 * graphics.guiWidth());
			int screenY = (int) ((1.0 - project.y) / 2.0 * graphics.guiHeight());

			graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				icon.texture(),
				screenX - ICON_HALF,
				screenY - ICON_HALF,
				0f, 0f,
				ICON_SIZE, ICON_SIZE,
				ICON_SIZE, ICON_SIZE,
				ARGB.color(alpha, 255, 255, 255)
			);
		}
	}
}

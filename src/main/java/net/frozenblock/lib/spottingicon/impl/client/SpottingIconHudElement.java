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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frozenblock.lib.spottingicon.api.SpottingIcon;
import net.frozenblock.lib.spottingicon.api.SpottingIcons;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3fc;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class SpottingIconHudElement implements HudElement {
	private static final float Y_OFFSET = 0.25F;
	private static final int ICON_SIZE = 16;
	private static final int ICON_HALF = ICON_SIZE / 2;
	private static final int ICON_SPACING = ICON_HALF / 4;

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) return;

		final float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
		final Camera camera = minecraft.gameRenderer.mainCamera();
		final Vec3 cameraPos = camera.position();
		final Vector3fc forwardAs3fc = camera.forwardVector();
		final Vec3 cameraForward = new Vec3(forwardAs3fc.x(), forwardAs3fc.y(), forwardAs3fc.z());

		final List<Entity> entities = new ArrayList<>();
		minecraft.level.entitiesForRendering().forEach(entities::add);
		entities.sort(Comparator.comparingDouble(e -> -e.position().distanceTo(cameraPos)));
		for (Entity entity : entities) {
			final SpottingIcons icons = entity.getAttachedOrElse(SpottingIcons.ATTACHMENT, SpottingIcons.EMPTY);
			if (icons.isEmpty()) continue;

			final BlockPos blockPos = entity.blockPosition();
			if (!(minecraft.level.isOutsideBuildHeight(blockPos.getY()) || minecraft.levelRenderer.isSectionCompiledAndVisible(blockPos))) continue;

			final Vec3 entityTopPosition = entity.getPosition(partialTicks).add(0D, entity.getBbHeight(), 0D);
			final double distance = Math.sqrt(cameraPos.distanceToSqr(entityTopPosition));

			float startPos = 0F;
			float lastScale = 0F;

			for (SpottingIcon icon : icons) {
				final float transparency = icon.attributes().calculateTransparency(distance);
				if (transparency <= 0F) continue;

				final float scale = icon.attributes().calculateScale(distance);
				if (scale <= 0F) continue;

				final Vec3 iconWorldPos = new Vec3(entityTopPosition.x, entityTopPosition.y + Y_OFFSET, entityTopPosition.z);
				// Check if icon is in front of camera
				if (iconWorldPos.subtract(cameraPos).dot(cameraForward) <= 0D) continue;

				final Vec3 project = minecraft.gameRenderer.projectPointToScreen(iconWorldPos);
				if (project.z > 1D || project.x < -1D || project.x > 1D || project.y < -1D || project.y > 1D) continue;

				final float screenX = (float) ((project.x + 1D) / 2D * graphics.guiWidth());
				final float screenY = (float) ((1D - project.y) / 2D * graphics.guiHeight());
				final float scaleDifference = scale - lastScale;
				startPos += scaleDifference * (ICON_HALF);
				startPos += ICON_SPACING;

				graphics.pose().pushMatrix();
				graphics.pose().translate(screenX, screenY - startPos);
				graphics.pose().pushMatrix();
				graphics.pose().scale(scale);
				graphics.pose().translate(-ICON_HALF, -ICON_HALF);
				graphics.blit(
					RenderPipelines.GUI_TEXTURED,
					icon.texture(),
					0, 0,
					0F, 0F,
					ICON_SIZE, ICON_SIZE,
					ICON_SIZE, ICON_SIZE,
					ARGB.colorFromFloat(transparency, 1F, 1F, 1F)
				);
				graphics.pose().popMatrix();
				graphics.pose().popMatrix();

				startPos += (scale * ICON_SIZE);
				lastScale = scale;
			}
		}
	}
}

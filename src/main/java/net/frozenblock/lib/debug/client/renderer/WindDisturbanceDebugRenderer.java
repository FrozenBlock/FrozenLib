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

package net.frozenblock.lib.debug.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class WindDisturbanceDebugRenderer implements DebugRenderer.SimpleDebugRenderer {

	public WindDisturbanceDebugRenderer() {
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, double cameraX, double cameraY, double cameraZ, DebugValueAccess debugValueAccess, Frustum frustum) {
		ClientWindManager.Debug.getWindDisturbances().forEach(
			windDisturbance -> {
				final VoxelShape shape = Shapes.create(windDisturbance.affectedArea);
				if (frustum.isVisible(shape.bounds())) {
					DebugRenderer.renderVoxelShape(
						poseStack,
						bufferSource.getBuffer(RenderType.lines()),
						Shapes.create(windDisturbance.affectedArea),
						-cameraX,
						-cameraY,
						-cameraZ,
						0.5F,
						1F,
						0.5F,
						0.35F,
						true
					);
				}
				renderFilledBox(
					poseStack,
					bufferSource,
					AABB.ofSize(windDisturbance.origin, 0.2D, 0.2D, 0.2D),
					cameraX, cameraY, cameraZ,
					frustum
				);
			}
		);

		WindDebugRenderer.renderWindNodesFromList(poseStack, bufferSource, cameraX, cameraY, cameraZ, ClientWindManager.Debug.getDebugDisturbanceNodes(), frustum);
	}

	private static void renderFilledBox(
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		@NotNull AABB box,
		double cameraX, double cameraY, double cameraZ,
		@NotNull Frustum frustum
	) {
		if (!frustum.isVisible(box)) return;
		Vec3 vec3 = new Vec3(-cameraX, -cameraY, -cameraZ);
		DebugRenderer.renderFilledBox(poseStack, bufferSource, box.move(vec3), 1F, 1F, 1F, 1F);
	}
}

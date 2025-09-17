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
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class WindDebugRenderer implements DebugRenderer.SimpleDebugRenderer {

	public WindDebugRenderer() {
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, double cameraX, double cameraY, double cameraZ, DebugValueAccess debugValueAccess, Frustum frustum) {
		renderWindNodesFromList(poseStack, bufferSource, cameraX, cameraY, cameraZ, ClientWindManager.Debug.getDebugNodes(), frustum);
	}

	protected static void renderWindNodesFromList(
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double cameraX,
		double cameraY,
		double cameraZ,
		@NotNull List<List<Pair<Vec3, Integer>>> windNodes,
		Frustum frustum
	) {
		windNodes.forEach(nodes -> renderWindNodes(poseStack, bufferSource, cameraX, cameraY, cameraZ, nodes, frustum));
	}

	protected static void renderWindNodes(
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double cameraX,
		double cameraY,
		double cameraZ,
		@NotNull List<Pair<Vec3, Integer>> windNodes,
		Frustum frustum
	) {
		final int size = windNodes.size();
		if (size <= 1) return;

		for (int i = 1; i < size; i++) {
			final Pair<Vec3, Integer> startNode = windNodes.get(i - 1);
			final Pair<Vec3, Integer> endNode = windNodes.get(i);

			final Vec3 startVec = startNode.getFirst();
			final Vec3 endVec = endNode.getFirst();
			if (!frustum.pointInFrustum(startVec.x, startVec.y, startVec.z) && !frustum.pointInFrustum(endVec.x, endVec.y, endVec.z)) continue;

			drawLine(poseStack, bufferSource, cameraX, cameraY, cameraZ, startVec, endVec, startNode.getSecond());
		}
	}

	private static void drawLine(
		@NotNull PoseStack poseStack,
		@NotNull MultiBufferSource bufferSource,
		double cameraX,
		double cameraY,
		double cameraZ,
		@NotNull Vec3 start,
		@NotNull Vec3 target,
		int color
	) {
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.debugLineStrip(24D));
		vertexConsumer.addVertex(poseStack.last(), (float)(start.x - cameraX), (float)(start.y - cameraY), (float)(start.z - cameraZ)).setColor(color);
		vertexConsumer.addVertex(poseStack.last(), (float)(target.x - cameraX), (float)(target.y - cameraY), (float)(target.z - cameraZ)).setColor(color);
	}
}

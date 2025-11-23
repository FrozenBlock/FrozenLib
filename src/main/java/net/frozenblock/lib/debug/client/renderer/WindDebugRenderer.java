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

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class WindDebugRenderer implements DebugRenderer.SimpleDebugRenderer {

	public WindDebugRenderer() {
	}

	@Override
	public void emitGizmos(
		double cameraX, double cameraY, double cameraZ,
		DebugValueAccess debugValueAccess,
		Frustum frustum,
		float unknown
	) {
		emitWindNodesFromList(ClientWindManager.Debug.getDebugNodes());
	}

	protected static void emitWindNodesFromList(List<List<Pair<Vec3, Integer>>> windNodes) {
		windNodes.forEach(WindDebugRenderer::emitWindNodes);
	}

	protected static void emitWindNodes(List<Pair<Vec3, Integer>> windNodes) {
		final int size = windNodes.size();
		if (size <= 1) return;

		final int finalIndex = size - 1;
		for (int i = 1; i < size; i++) {
			final Pair<Vec3, Integer> startNode = windNodes.get(i - 1);
			final Pair<Vec3, Integer> endNode = windNodes.get(i);

			final Vec3 startVec = startNode.getFirst();
			final Vec3 endVec = endNode.getFirst();
			final int color = startNode.getSecond();

			if (i == finalIndex) {
				Gizmos.arrow(startVec, endVec, color, 3F);
			} else {
				Gizmos.line(startVec, endVec, color, 3F);
			}
		}
	}
}

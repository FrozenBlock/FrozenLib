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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;

@Environment(EnvType.CLIENT)
public class WindDisturbanceDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
	private static final GizmoStyle WIND_DISTURBANCE_AREA_STYLE = GizmoStyle.stroke(ARGB.colorFromFloat(0.5F, 1F, 0.5F, 0.35F));
	private static final GizmoStyle WIND_DISTURBANCE_CORE_STYLE = GizmoStyle.stroke(ARGB.colorFromFloat(1F, 1F, 1F, 1F));

	public WindDisturbanceDebugRenderer() {
	}

	@Override
	public void emitGizmos(
		double cameraX, double cameraY, double cameraZ,
		DebugValueAccess debugValueAccess,
		Frustum frustum,
		float unknown
	) {
		ClientWindManager.Debug.getWindDisturbances().forEach(
			windDisturbance -> {
				Gizmos.cuboid(windDisturbance.affectedArea, WIND_DISTURBANCE_AREA_STYLE);
				Gizmos.cuboid(AABB.ofSize(windDisturbance.origin, 0.2D, 0.2D, 0.2D), WIND_DISTURBANCE_CORE_STYLE);
			}
		);

		WindDebugRenderer.emitWindNodesFromList(ClientWindManager.Debug.getDebugDisturbanceNodes());
	}
}

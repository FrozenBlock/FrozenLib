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

package net.frozenblock.lib.core.client.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class FrustumUtil {

	/**
	 * Returns if an area is visible in the client's frustum.
	 *
	 * @param aabb The area to check.
	 * @return if an area is visible in the client's frustum.
	 */
	public static boolean isVisible(AABB aabb) {
		final Frustum frustum = Minecraft.getInstance().levelRenderer.getCapturedFrustum();
		if (frustum != null) return frustum.isVisible(aabb);
		return true;
	}

	/**
	 * Returns if a position is visible in the client's frustum.
	 *
	 * @param pos The position to check.
	 * @param area The area around the position to check.
	 * @return if a position is visible in the client's frustum.
	 */
	public static boolean isVisible(Vec3 pos, double area) {
		return isVisible(AABB.ofSize(pos, area, area, area));
	}

}

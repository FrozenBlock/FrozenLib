/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.client.impl.waterlike;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@UtilityClass
@ClientOnly
public final class WaterLikeFogUtil {
	private static final Vector3f EMPTY_VEC = new Vector3f(0F, 0F, 0F);
	private static Vector3f prevFogColor = new Vector3f(0F, 0F, 0F);
	private static Vector3f fogColor = new Vector3f(0F, 0F, 0F);
	private static float prevFogStrength = 0F;
	private static float fogStrength = 0F;
	private static float prevFogDistance = 1F;
	private static float fogDistance = 1F;
	private static FogType prevFogType = FogType.NONE;
	private static FogType fogType = FogType.NONE;

	public static void reset(boolean resetFogType) {
		prevFogColor = fogColor = new Vector3f(0F, 0F, 0F);
		prevFogStrength = fogStrength = 0F;
		prevFogDistance = fogDistance = 1F;
		if (resetFogType) prevFogType = fogType = FogType.NONE;
	}

	public static Float getModifiedFogDistance(float partialTicks, Float original) {
		if (prevFogStrength == 0F && fogStrength == 0F) return original;
		return original * Mth.lerp(Mth.lerp(partialTicks, prevFogStrength, fogStrength), 1F, Mth.lerp(partialTicks, prevFogDistance, fogDistance));
	}

	public static int getModifiedFogColor(float partialTicks, int original) {
		if (prevFogStrength == 0F && fogStrength == 0F) return original;
		return ARGB.color(
			new Vec3(
				ARGB.vector3fFromRGB24(original).lerp(
					prevFogColor.lerp(fogColor, partialTicks, new Vector3f(0F, 0F, 0F)),
					Mth.lerp(partialTicks, prevFogStrength, fogStrength)
				)
			)
		);
	}

	public static void tick(Level level, BlockPos pos, FogType fogType, boolean newlyInWaterOverride) {
		prevFogStrength = fogStrength;
		prevFogColor = fogColor;
		prevFogDistance = fogDistance;
		prevFogType = WaterLikeFogUtil.fogType;
		WaterLikeFogUtil.fogType = fogType;

		if (level.getBlockState(pos).getBlock() instanceof WaterLikeBlock waterLikeBlock) {
			final Vector3f waterLikeColor = ARGB.vector3fFromRGB24(waterLikeBlock.waterLikeColor().rgba());
			if (fogColor.equals(EMPTY_VEC)) {
				prevFogColor = fogColor = waterLikeColor;
			} else {
				fogColor = fogColor.add(waterLikeColor.sub(fogColor).mul(0.05F));
			}

			if (newlyInWaterOverride || (WaterLikeFogUtil.fogType == FogType.WATER && prevFogType != FogType.WATER)) {
				prevFogStrength = fogStrength = 1F;
				prevFogDistance = fogDistance = waterLikeBlock.waterFogDistance();
			} else {
				fogStrength += (1F - fogStrength) * 0.05F;
				fogDistance += (waterLikeBlock.waterFogDistance() - fogDistance) * 0.05F;
			}
		} else {
			fogStrength += (0F - fogStrength) * 0.05F;
			fogDistance += (1F - fogDistance) * 0.05F;
		}

		if (prevFogStrength <= 0F && fogStrength <= 0F) reset(false);
	}
}

package net.frozenblock.lib.block.client.impl.waterlike;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
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
			final Vector3f waterLikeFogColor = ARGB.vector3fFromRGB24(waterLikeBlock.waterFogColor().rgba());
			if (fogColor.equals(EMPTY_VEC)) {
				prevFogColor = fogColor = waterLikeFogColor;
			} else {
				fogColor = fogColor.add(waterLikeFogColor.sub(fogColor).mul(0.05F));
			}

			if (newlyInWaterOverride || (WaterLikeFogUtil.fogType == FogType.WATER && prevFogType != FogType.WATER)) {
				prevFogStrength = fogStrength = 1F;
				prevFogDistance = fogDistance = waterLikeBlock.waterFogDistance();
			} else {
				fogStrength += (1F - fogStrength) * 0.05F;
				fogDistance += (waterLikeBlock.waterFogDistance() - fogStrength) * 0.05F;
			}
		} else {
			fogStrength += (0F - fogStrength) * 0.05F;
			fogDistance += (1F - fogDistance) * 0.05F;
		}

		if (prevFogStrength <= 0F && fogStrength <= 0F) reset(false);
	}

}

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

package net.frozenblock.lib.levelgen.feature.api;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.feature.api.feature.CircularLavaVegetationPatchFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.CircularLavaVegetationPatchLessBordersFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.CircularWaterloggedVegetationPatchFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.CircularWaterloggedVegetationPatchLessBordersFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.ColumnFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.ColumnWithDiskFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.ConfigSelectorFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.CurvingSpikeFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.LargeSpireFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.UnderwaterVegetationPatchFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.UnderwaterVegetationPatchWithEdgeDecorationFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.VegetationPatchWithEdgeDecorationFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.disk.BallFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.noise_path.NoisePathFeature;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;

public class FrozenLibFeatureTypes {

	public static void init() {
		var register = FrozenDeferredRegister.create(
			Registries.FEATURE_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("config_selector", () -> ConfigSelectorFeature.CODEC);
		register.register("noise_path", () -> NoisePathFeature.CODEC);
		register.register("ball", () -> BallFeature.CODEC);
		register.register("column", () -> ColumnFeature.CODEC);
		register.register("column_with_disk", () -> ColumnWithDiskFeature.CODEC);
		register.register("vegetation_patch_with_edge_decoration", () -> VegetationPatchWithEdgeDecorationFeature.CODEC);
		register.register("underwater_vegetation_patch", () -> UnderwaterVegetationPatchFeature.CODEC);
		register.register("underwater_vegetation_patch_with_edge_decoration", () -> UnderwaterVegetationPatchWithEdgeDecorationFeature.CODEC);
		register.register("circular_waterlogged_vegetation_patch", () -> CircularWaterloggedVegetationPatchFeature.CODEC);
		register.register("circular_waterlogged_vegetation_patch_less_borders", () -> CircularWaterloggedVegetationPatchLessBordersFeature.CODEC);
		register.register("circular_lava_vegetation_patch", () -> CircularLavaVegetationPatchFeature.CODEC);
		register.register("circular_lava_vegetation_patch_less_borders", () -> CircularLavaVegetationPatchLessBordersFeature.CODEC);
		register.register("large_spire", () -> LargeSpireFeature.CODEC);
		register.register("curving_spike", () -> CurvingSpikeFeature.CODEC);

		register.register();
	}
}

/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.feature;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.feature.features.CircularWaterloggedVegetationPatchFeature;
import net.frozenblock.lib.feature.features.ColumnWithDiskFeature;
import net.frozenblock.lib.feature.features.DownwardsPillarFeature;
import net.frozenblock.lib.feature.features.NoisePathFeature;
import net.frozenblock.lib.feature.features.NoisePathSwapUnderWaterFeature;
import net.frozenblock.lib.feature.features.NoisePathUnderWaterFeature;
import net.frozenblock.lib.feature.features.NoisePlantFeature;
import net.frozenblock.lib.feature.features.UpwardsPillarFeature;
import net.frozenblock.lib.feature.features.config.ColumnWithDiskFeatureConfig;
import net.frozenblock.lib.feature.features.config.PathFeatureConfig;
import net.frozenblock.lib.feature.features.config.PathSwapUnderWaterFeatureConfig;
import net.frozenblock.lib.feature.features.config.PillarFeatureConfig;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class FrozenFeatures {

	public static final NoisePathFeature NOISE_PATH_FEATURE = new NoisePathFeature(PathFeatureConfig.CODEC);
	public static final NoisePlantFeature NOISE_PLANT_FEATURE = new NoisePlantFeature(PathFeatureConfig.CODEC);
	public static final NoisePathSwapUnderWaterFeature NOISE_PATH_SWAP_UNDER_WATER_FEATURE = new NoisePathSwapUnderWaterFeature(PathSwapUnderWaterFeatureConfig.CODEC);
	public static final NoisePathUnderWaterFeature NOISE_PATH_UNDER_WATER_FEATURE = new NoisePathUnderWaterFeature(PathFeatureConfig.CODEC);
	public static final ColumnWithDiskFeature COLUMN_WITH_DISK_FEATURE = new ColumnWithDiskFeature(ColumnWithDiskFeatureConfig.CODEC);
	public static final UpwardsPillarFeature UPWARDS_PILLAR_FEATURE = new UpwardsPillarFeature(PillarFeatureConfig.CODEC);
	public static final DownwardsPillarFeature DOWNWARDS_PILLAR_FEATURE = new DownwardsPillarFeature(PillarFeatureConfig.CODEC);
	public static final CircularWaterloggedVegetationPatchFeature CIRCULAR_WATERLOGGED_VEGETATION_PATCH = new CircularWaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC);

	public static void init() {
		Registry.register(Registry.FEATURE, FrozenMain.id("noise_path_feature"), NOISE_PATH_FEATURE);
		Registry.register(Registry.FEATURE, FrozenMain.id("noise_plant_feature"), NOISE_PLANT_FEATURE);
		Registry.register(Registry.FEATURE, FrozenMain.id("noise_path_swap_under_water_feature"), NOISE_PATH_SWAP_UNDER_WATER_FEATURE);
		Registry.register(Registry.FEATURE, FrozenMain.id("noise_path_under_water_feature"), NOISE_PATH_UNDER_WATER_FEATURE);
		Registry.register(Registry.FEATURE, FrozenMain.id("column_with_disk_feature"), COLUMN_WITH_DISK_FEATURE);
		Registry.register(Registry.FEATURE, FrozenMain.id("upwards_pillar"), UPWARDS_PILLAR_FEATURE);
		Registry.register(Registry.FEATURE, FrozenMain.id("downwards_pillar"), DOWNWARDS_PILLAR_FEATURE);
		Registry.register(Registry.FEATURE, FrozenMain.id("circular_waterlogged_vegetation_patch"), CIRCULAR_WATERLOGGED_VEGETATION_PATCH);
	}

}

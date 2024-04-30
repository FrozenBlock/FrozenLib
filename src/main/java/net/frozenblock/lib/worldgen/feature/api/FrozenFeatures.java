/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api;

import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.worldgen.feature.api.features.CircularLavaVegetationPatchFeature;
import net.frozenblock.lib.worldgen.feature.api.features.CircularLavaVegetationPatchLessBordersFeature;
import net.frozenblock.lib.worldgen.feature.api.features.CircularWaterloggedVegetationPatchFeature;
import net.frozenblock.lib.worldgen.feature.api.features.CircularWaterloggedVegetationPatchLessBordersFeature;
import net.frozenblock.lib.worldgen.feature.api.features.ColumnWithDiskFeature;
import net.frozenblock.lib.worldgen.feature.api.features.ComboFeature;
import net.frozenblock.lib.worldgen.feature.api.features.CurvingTunnelFeature;
import net.frozenblock.lib.worldgen.feature.api.features.DownwardsColumnFeature;
import net.frozenblock.lib.worldgen.feature.api.features.FadingDiskCarpetFeature;
import net.frozenblock.lib.worldgen.feature.api.features.FadingDiskFeature;
import net.frozenblock.lib.worldgen.feature.api.features.FadingDiskTagExceptInBiomeFeature;
import net.frozenblock.lib.worldgen.feature.api.features.FadingDiskTagFeature;
import net.frozenblock.lib.worldgen.feature.api.features.FadingDiskTagScheduleTickFeature;
import net.frozenblock.lib.worldgen.feature.api.features.FadingDiskWithPileTagFeature;
import net.frozenblock.lib.worldgen.feature.api.features.NoisePathFeature;
import net.frozenblock.lib.worldgen.feature.api.features.NoisePathScheduleTickFeature;
import net.frozenblock.lib.worldgen.feature.api.features.NoisePathSwapUnderWaterFeature;
import net.frozenblock.lib.worldgen.feature.api.features.NoisePathSwapUnderWaterTagFeature;
import net.frozenblock.lib.worldgen.feature.api.features.NoisePathTagFeature;
import net.frozenblock.lib.worldgen.feature.api.features.NoisePathTagUnderWaterFeature;
import net.frozenblock.lib.worldgen.feature.api.features.NoisePathUnderWaterFeature;
import net.frozenblock.lib.worldgen.feature.api.features.NoisePlantFeature;
import net.frozenblock.lib.worldgen.feature.api.features.SimpleBlockScheduleTickFeature;
import net.frozenblock.lib.worldgen.feature.api.features.UpwardsColumnFeature;
import net.frozenblock.lib.worldgen.feature.api.features.config.ColumnFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.ColumnWithDiskFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.ComboFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.CurvingTunnelFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.FadingDiskCarpetFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.FadingDiskFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.FadingDiskTagBiomeFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.FadingDiskTagFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathSwapUnderWaterFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathSwapUnderWaterTagFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathTagFeatureConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class FrozenFeatures {

	public static final NoisePathFeature NOISE_PATH_FEATURE = new NoisePathFeature(PathFeatureConfig.CODEC);
	public static final NoisePathTagFeature NOISE_PATH_TAG_FEATURE = new NoisePathTagFeature(PathTagFeatureConfig.CODEC);
	public static final NoisePlantFeature NOISE_PLANT_FEATURE = new NoisePlantFeature(PathFeatureConfig.CODEC);
	public static final NoisePathSwapUnderWaterFeature NOISE_PATH_SWAP_UNDER_WATER_FEATURE = new NoisePathSwapUnderWaterFeature(PathSwapUnderWaterFeatureConfig.CODEC);
	public static final NoisePathSwapUnderWaterTagFeature NOISE_PATH_SWAP_UNDER_WATER_TAG_FEATURE = new NoisePathSwapUnderWaterTagFeature(PathSwapUnderWaterTagFeatureConfig.CODEC);
	public static final NoisePathUnderWaterFeature NOISE_PATH_UNDER_WATER_FEATURE = new NoisePathUnderWaterFeature(PathFeatureConfig.CODEC);
	public static final NoisePathTagUnderWaterFeature NOISE_PATH_TAG_UNDER_WATER_FEATURE = new NoisePathTagUnderWaterFeature(PathTagFeatureConfig.CODEC);
	public static final ColumnWithDiskFeature COLUMN_WITH_DISK_FEATURE = new ColumnWithDiskFeature(ColumnWithDiskFeatureConfig.CODEC);
	public static final UpwardsColumnFeature UPWARDS_COLUMN_FEATURE = new UpwardsColumnFeature(ColumnFeatureConfig.CODEC);
	public static final DownwardsColumnFeature DOWNWARDS_COLUMN_FEATURE = new DownwardsColumnFeature(ColumnFeatureConfig.CODEC);
	public static final CircularWaterloggedVegetationPatchFeature CIRCULAR_WATERLOGGED_VEGETATION_PATCH = new CircularWaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC);
	public static final CircularWaterloggedVegetationPatchLessBordersFeature CIRCULAR_WATERLOGGED_VEGETATION_PATCH_LESS_BORDERS = new CircularWaterloggedVegetationPatchLessBordersFeature(VegetationPatchConfiguration.CODEC);
	public static final FadingDiskTagFeature FADING_DISK_TAG_FEATURE = new FadingDiskTagFeature(FadingDiskTagFeatureConfig.CODEC);
	public static final FadingDiskTagExceptInBiomeFeature FADING_DISK_TAG_EXCEPT_IN_BIOME_FEATURE = new FadingDiskTagExceptInBiomeFeature(FadingDiskTagBiomeFeatureConfig.CODEC);
	public static final FadingDiskFeature FADING_DISK_FEATURE = new FadingDiskFeature(FadingDiskFeatureConfig.CODEC);
	public static final FadingDiskCarpetFeature FADING_DISK_CARPET_FEATURE = new FadingDiskCarpetFeature(FadingDiskCarpetFeatureConfig.CODEC);
	public static final FadingDiskWithPileTagFeature FADING_DISK_WITH_PILE_TAG_FEATURE = new FadingDiskWithPileTagFeature(FadingDiskTagFeatureConfig.CODEC);
	public static final CurvingTunnelFeature CURVING_TUNNEL_FEATURE = new CurvingTunnelFeature(CurvingTunnelFeatureConfig.CODEC);
	public static final CircularLavaVegetationPatchFeature CIRCULAR_LAVA_VEGETATION_PATCH = new CircularLavaVegetationPatchFeature(VegetationPatchConfiguration.CODEC);
	public static final CircularLavaVegetationPatchLessBordersFeature CIRCULAR_LAVA_VEGETATION_PATCH_LESS_BORDERS = new CircularLavaVegetationPatchLessBordersFeature(VegetationPatchConfiguration.CODEC);
	public static final SimpleBlockScheduleTickFeature SIMPLE_BLOCK_SCHEDULE_TICK_FEATURE = new SimpleBlockScheduleTickFeature(SimpleBlockConfiguration.CODEC);
	public static final FadingDiskTagScheduleTickFeature FADING_DISK_TAG_SCHEDULE_TICK_FEATURE = new FadingDiskTagScheduleTickFeature(FadingDiskTagFeatureConfig.CODEC);
	public static final NoisePathScheduleTickFeature NOISE_PATH_SCHEDULE_TICK_FEATURE = new NoisePathScheduleTickFeature(PathFeatureConfig.CODEC);
	public static final ComboFeature COMBO_FEATURE = new ComboFeature(ComboFeatureConfig.CODEC);

	public static void init() {
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("noise_path_feature"), NOISE_PATH_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("noise_path_tag_feature"), NOISE_PATH_TAG_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("noise_plant_feature"), NOISE_PLANT_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("noise_path_swap_under_water_feature"), NOISE_PATH_SWAP_UNDER_WATER_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("noise_path_swap_under_water_tag_feature"), NOISE_PATH_SWAP_UNDER_WATER_TAG_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("noise_path_under_water_feature"), NOISE_PATH_UNDER_WATER_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("noise_path_tag_under_water_feature"), NOISE_PATH_TAG_UNDER_WATER_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("column_with_disk_feature"), COLUMN_WITH_DISK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("upwards_column"), UPWARDS_COLUMN_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("downwards_column"), DOWNWARDS_COLUMN_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("circular_waterlogged_vegetation_patch"), CIRCULAR_WATERLOGGED_VEGETATION_PATCH);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("circular_waterlogged_vegetation_patch_less_borders"), CIRCULAR_WATERLOGGED_VEGETATION_PATCH_LESS_BORDERS);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("fading_disk_tag_feature"), FADING_DISK_TAG_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("fading_disk_tag_except_in_biome_feature"), FADING_DISK_TAG_EXCEPT_IN_BIOME_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("fading_disk_feature"), FADING_DISK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("fading_disk_carpet_feature"), FADING_DISK_CARPET_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("fading_disk_with_pile_tag_feature"), FADING_DISK_WITH_PILE_TAG_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("curving_tunnel_feature"), CURVING_TUNNEL_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("circular_lava_vegetation_patch"), CIRCULAR_LAVA_VEGETATION_PATCH);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("circular_lava_vegetation_patch_less_borders"), CIRCULAR_LAVA_VEGETATION_PATCH_LESS_BORDERS);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("simple_block_schedule_tick"), SIMPLE_BLOCK_SCHEDULE_TICK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("noise_path_schedule_tick_feature"), NOISE_PATH_SCHEDULE_TICK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("fading_disk_tag_schedule_tick_feature"), FADING_DISK_TAG_SCHEDULE_TICK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenSharedConstants.id("combo_feature"), COMBO_FEATURE);
	}

}

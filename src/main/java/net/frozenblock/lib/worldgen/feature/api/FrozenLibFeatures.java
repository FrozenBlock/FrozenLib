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

package net.frozenblock.lib.worldgen.feature.api;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.worldgen.feature.api.feature.CircularLavaVegetationPatchFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.CircularLavaVegetationPatchLessBordersFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.CircularWaterloggedVegetationPatchFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.CircularWaterloggedVegetationPatchLessBordersFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.ColumnWithDiskFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.ComboFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.DownwardsColumnFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.FadingDiskCarpetFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.FadingDiskExceptInBiomeFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.FadingDiskFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.FadingDiskScheduleTickFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.SimpleBlockScheduleTickFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.UpwardsColumnFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.config.ColumnFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.feature.config.ColumnWithDiskFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.feature.config.ComboFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.feature.config.FadingDiskCarpetFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.feature.config.FadingDiskFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.feature.config.FadingDiskWithBiomeFeatureConfig;
import net.frozenblock.lib.worldgen.feature.api.feature.noise_path.NoisePathFeature;
import net.frozenblock.lib.worldgen.feature.api.feature.noise_path.config.NoisePathFeatureConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class FrozenLibFeatures {
	public static final NoisePathFeature NOISE_PATH_FEATURE = new NoisePathFeature(NoisePathFeatureConfig.CODEC);
	public static final ColumnWithDiskFeature COLUMN_WITH_DISK_FEATURE = new ColumnWithDiskFeature(ColumnWithDiskFeatureConfig.CODEC);
	public static final UpwardsColumnFeature UPWARDS_COLUMN_FEATURE = new UpwardsColumnFeature(ColumnFeatureConfig.CODEC);
	public static final DownwardsColumnFeature DOWNWARDS_COLUMN_FEATURE = new DownwardsColumnFeature(ColumnFeatureConfig.CODEC);
	public static final CircularWaterloggedVegetationPatchFeature CIRCULAR_WATERLOGGED_VEGETATION_PATCH = new CircularWaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC);
	public static final CircularWaterloggedVegetationPatchLessBordersFeature CIRCULAR_WATERLOGGED_VEGETATION_PATCH_LESS_BORDERS = new CircularWaterloggedVegetationPatchLessBordersFeature(VegetationPatchConfiguration.CODEC);
	public static final FadingDiskExceptInBiomeFeature FADING_DISK_EXCEPT_IN_BIOME_FEATURE = new FadingDiskExceptInBiomeFeature(FadingDiskWithBiomeFeatureConfig.CODEC);
	public static final FadingDiskFeature FADING_DISK_FEATURE = new FadingDiskFeature(FadingDiskFeatureConfig.CODEC);
	public static final FadingDiskScheduleTickFeature FADING_DISK_SCHEDULE_TICK_FEATURE = new FadingDiskScheduleTickFeature(FadingDiskFeatureConfig.CODEC);
	public static final FadingDiskCarpetFeature FADING_DISK_CARPET_FEATURE = new FadingDiskCarpetFeature(FadingDiskCarpetFeatureConfig.CODEC);
	public static final CircularLavaVegetationPatchFeature CIRCULAR_LAVA_VEGETATION_PATCH = new CircularLavaVegetationPatchFeature(VegetationPatchConfiguration.CODEC);
	public static final CircularLavaVegetationPatchLessBordersFeature CIRCULAR_LAVA_VEGETATION_PATCH_LESS_BORDERS = new CircularLavaVegetationPatchLessBordersFeature(VegetationPatchConfiguration.CODEC);
	public static final SimpleBlockScheduleTickFeature SIMPLE_BLOCK_SCHEDULE_TICK_FEATURE = new SimpleBlockScheduleTickFeature(SimpleBlockConfiguration.CODEC);
	public static final ComboFeature COMBO_FEATURE = new ComboFeature(ComboFeatureConfig.CODEC);

	public static void init() {
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("noise_path_feature"), NOISE_PATH_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("column_with_disk_feature"), COLUMN_WITH_DISK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("upwards_column"), UPWARDS_COLUMN_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("downwards_column"), DOWNWARDS_COLUMN_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("circular_waterlogged_vegetation_patch"), CIRCULAR_WATERLOGGED_VEGETATION_PATCH);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("circular_waterlogged_vegetation_patch_less_borders"), CIRCULAR_WATERLOGGED_VEGETATION_PATCH_LESS_BORDERS);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("fading_disk_except_in_biome_feature"), FADING_DISK_EXCEPT_IN_BIOME_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("fading_disk_feature"), FADING_DISK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("fading_disk_schedule_tick_feature"), FADING_DISK_SCHEDULE_TICK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("fading_disk_carpet_feature"), FADING_DISK_CARPET_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("circular_lava_vegetation_patch"), CIRCULAR_LAVA_VEGETATION_PATCH);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("circular_lava_vegetation_patch_less_borders"), CIRCULAR_LAVA_VEGETATION_PATCH_LESS_BORDERS);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("simple_block_schedule_tick"), SIMPLE_BLOCK_SCHEDULE_TICK_FEATURE);
		Registry.register(BuiltInRegistries.FEATURE, FrozenLibConstants.id("combo_feature"), COMBO_FEATURE);
	}

}

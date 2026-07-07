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
import net.frozenblock.lib.levelgen.feature.api.feature.configurations.ColumnFeatureConfiguration;
import net.frozenblock.lib.levelgen.feature.api.feature.configurations.ColumnWithDiskFeatureConfiguration;
import net.frozenblock.lib.levelgen.feature.api.feature.configurations.ConfigSelectorFeatureConfiguration;
import net.frozenblock.lib.levelgen.feature.api.feature.configurations.CurvingSpikeConfiguration;
import net.frozenblock.lib.levelgen.feature.api.feature.configurations.LargeSpireConfiguration;
import net.frozenblock.lib.levelgen.feature.api.feature.disk.BallFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.disk.config.BallFeatureConfiguration;
import net.frozenblock.lib.levelgen.feature.api.feature.noise_path.NoisePathFeature;
import net.frozenblock.lib.levelgen.feature.api.feature.noise_path.config.NoisePathFeatureConfiguration;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class FrozenLibFeatures {
	private static final FrozenDeferredRegister<Feature<?>> REGISTER = FrozenDeferredRegister.create(
		Registries.FEATURE,
		FrozenLibConstants.MOD_ID
	);

	public static final FrozenHolder<Feature<?>, ConfigSelectorFeature> CONFIG_SELECTOR = REGISTER.register("config_selector",
		() -> new ConfigSelectorFeature(ConfigSelectorFeatureConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, NoisePathFeature> NOISE_PATH = REGISTER.register("noise_path",
		() -> new NoisePathFeature(NoisePathFeatureConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, BallFeature> BALL = REGISTER.register("ball",
		() -> new BallFeature(BallFeatureConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, ColumnFeature> COLUMN = REGISTER.register("column",
		() -> new ColumnFeature(ColumnFeatureConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, ColumnWithDiskFeature> COLUMN_WITH_DISK = REGISTER.register("column_with_disk",
		() -> new ColumnWithDiskFeature(ColumnWithDiskFeatureConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, VegetationPatchWithEdgeDecorationFeature> VEGETATION_PATCH_WITH_EDGE_DECORATION = REGISTER.register("vegetation_patch_with_edge_decoration",
		() -> new VegetationPatchWithEdgeDecorationFeature(VegetationPatchConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, UnderwaterVegetationPatchFeature> UNDERWATER_VEGETATION_PATCH = REGISTER.register("underwater_vegetation_patch",
		() -> new UnderwaterVegetationPatchFeature(VegetationPatchConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, UnderwaterVegetationPatchWithEdgeDecorationFeature> UNDERWATER_VEGETATION_PATCH_WITH_EDGE_DECORATION = REGISTER.register("underwater_vegetation_patch_with_edge_decoration",
		() -> new UnderwaterVegetationPatchWithEdgeDecorationFeature(VegetationPatchConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, CircularWaterloggedVegetationPatchFeature> CIRCULAR_WATERLOGGED_VEGETATION_PATCH = REGISTER.register("circular_waterlogged_vegetation_patch",
		() -> new CircularWaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, CircularWaterloggedVegetationPatchLessBordersFeature> CIRCULAR_WATERLOGGED_VEGETATION_PATCH_LESS_BORDERS = REGISTER.register("circular_waterlogged_vegetation_patch_less_borders",
		() -> new CircularWaterloggedVegetationPatchLessBordersFeature(VegetationPatchConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, CircularLavaVegetationPatchFeature> CIRCULAR_LAVA_VEGETATION_PATCH = REGISTER.register("circular_lava_vegetation_patch",
		() -> new CircularLavaVegetationPatchFeature(VegetationPatchConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, CircularLavaVegetationPatchLessBordersFeature> CIRCULAR_LAVA_VEGETATION_PATCH_LESS_BORDERS = REGISTER.register("circular_lava_vegetation_patch_less_borders",
		() -> new CircularLavaVegetationPatchLessBordersFeature(VegetationPatchConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, LargeSpireFeature> LARGE_SPIRE = REGISTER.register("large_spire",
		() -> new LargeSpireFeature(LargeSpireConfiguration.CODEC)
	);
	public static final FrozenHolder<Feature<?>, CurvingSpikeFeature> CURVING_SPIKE = REGISTER.register("curving_spike",
		() -> new CurvingSpikeFeature(CurvingSpikeConfiguration.CODEC)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}
}

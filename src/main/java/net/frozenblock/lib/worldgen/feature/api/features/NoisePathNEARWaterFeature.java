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

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.worldgen.feature.api.FrozenLibFeatureUtils;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NoisePathNEARWaterFeature extends NoisePathFeature {

	public NoisePathNEARWaterFeature(Codec<PathFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean placeBlock(@NotNull WorldGenLevel level, BlockState state, BlockPos pos) {
		if (!FrozenLibFeatureUtils.isWaterNearby(level, pos, 2)) return false;
		return super.placeBlock(level, state, pos);
	}

}

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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import org.jetbrains.annotations.NotNull;

public class SimpleBlockScheduleTickFeature extends Feature<SimpleBlockConfiguration> {

	public SimpleBlockScheduleTickFeature(Codec<SimpleBlockConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<SimpleBlockConfiguration> featurePlaceContext) {
		SimpleBlockConfiguration simpleBlockConfiguration = featurePlaceContext.config();
		WorldGenLevel worldGenLevel = featurePlaceContext.level();
		BlockPos blockPos = featurePlaceContext.origin();
		BlockState blockState = simpleBlockConfiguration.toPlace().getState(featurePlaceContext.random(), blockPos);
		if (blockState.canSurvive(worldGenLevel, blockPos)) {
			if (blockState.getBlock() instanceof DoublePlantBlock) {
				if (!worldGenLevel.isEmptyBlock(blockPos.above())) return false;
				DoublePlantBlock.placeAt(worldGenLevel, blockState, blockPos, Block.UPDATE_CLIENTS);
				worldGenLevel.scheduleTick(blockPos, blockState.getBlock(), 1);
			} else {
				worldGenLevel.setBlock(blockPos, blockState, Block.UPDATE_CLIENTS);
				worldGenLevel.scheduleTick(blockPos, blockState.getBlock(), 1);
			}

			return true;
		} else {
			return false;
		}
	}

}

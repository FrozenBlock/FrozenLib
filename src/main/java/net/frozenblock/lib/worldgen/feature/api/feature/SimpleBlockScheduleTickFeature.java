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

package net.frozenblock.lib.worldgen.feature.api.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SimpleBlockScheduleTickFeature extends Feature<SimpleBlockConfiguration> {

	public SimpleBlockScheduleTickFeature(Codec<SimpleBlockConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> featurePlaceContext) {
		final SimpleBlockConfiguration config = featurePlaceContext.config();
		final WorldGenLevel level = featurePlaceContext.level();
		final BlockPos pos = featurePlaceContext.origin();
		final BlockState state = config.toPlace().getState(featurePlaceContext.random(), pos);

		if (!state.canSurvive(level, pos)) return false;

		if (state.getBlock() instanceof DoublePlantBlock) {
			if (!level.isEmptyBlock(pos.above())) return false;
			DoublePlantBlock.placeAt(level, state, pos, Block.UPDATE_CLIENTS);
			level.scheduleTick(pos, state.getBlock(), 1);
		} else {
			level.setBlock(pos, state, Block.UPDATE_CLIENTS);
			level.scheduleTick(pos, state.getBlock(), 1);
		}

		return true;
	}

}

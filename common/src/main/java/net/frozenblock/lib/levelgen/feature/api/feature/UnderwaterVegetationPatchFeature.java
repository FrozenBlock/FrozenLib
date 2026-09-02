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

package net.frozenblock.lib.levelgen.feature.api.feature;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class UnderwaterVegetationPatchFeature extends VegetationPatchFeature {
	public static final MapCodec<UnderwaterVegetationPatchFeature> CODEC = makeCodec(UnderwaterVegetationPatchFeature::new);

	public UnderwaterVegetationPatchFeature(
		HolderSet<Block> replaceable,
		Holder<BlockStateProvider> groundState,
		Holder<PlacedFeature> vegetationFeature,
		CaveSurface surface,
		IntProvider depth,
		float extraBottomBlockChance,
		int verticalRange,
		float vegetationChance,
		IntProvider xzRadius,
		float extraEdgeColumnChance
	) {
		super(replaceable, groundState, vegetationFeature, surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius, extraEdgeColumnChance);
	}

	@Override
	public MapCodec<? extends VegetationPatchFeature> codec() {
		return CODEC;
	}

	@Override
	public Set<BlockPos> placeGroundPatch(WorldGenLevel level, RandomSource random, BlockPos origin, Predicate<BlockState> replaceable, int xRadius, int zRadius) {
		final BlockPos.MutableBlockPos airMutable = origin.mutable();
		final BlockPos.MutableBlockPos groundMutable = airMutable.mutable();
		final Direction inwards = this.surface.getDirection();
		final Direction outwards = inwards.getOpposite();
		final Set<BlockPos> surface = new HashSet<>();

		for (int x = -xRadius; x <= xRadius; x++) {
			boolean onEdgeX = x == -xRadius || x == xRadius;
			for (int z = -zRadius; z <= zRadius; z++) {
				boolean onEdgeZ = z == -zRadius || z == zRadius;
				boolean onAnyEdge = onEdgeX || onEdgeZ;
				boolean onBothEdges = onEdgeX && onEdgeZ;
				boolean onOneEdge = onAnyEdge && !onBothEdges;

				if (onBothEdges || !(!onOneEdge || this.extraEdgeColumnChance != 0F && !(random.nextFloat() > this.extraEdgeColumnChance))) continue;

				airMutable.setWithOffset(origin, x, 0, z);
				for (int verticalSteps = 0; level.isStateAtPosition(airMutable, this::isWaterAt) && verticalSteps < this.verticalRange; verticalSteps++) {
					airMutable.move(inwards);
				}

				for (int verticalSteps = 0; level.isStateAtPosition(airMutable, state -> !this.isWaterAt(state)) && verticalSteps < this.verticalRange; verticalSteps++) {
					airMutable.move(outwards);
				}

				groundMutable.setWithOffset(airMutable, this.surface.getDirection());
				final BlockState state = level.getBlockState(groundMutable);
				if (!this.isWaterAt(level.getBlockState(airMutable)) || !state.isFaceSturdy(level, groundMutable, this.surface.getDirection().getOpposite())) continue;

				final int depth = this.depth.sample(random) + (this.extraBottomBlockChance > 0F && random.nextFloat() < this.extraBottomBlockChance ? 1 : 0);
				final BlockPos groundPos = groundMutable.immutable();
				final boolean groundPlaced = this.placeGround(level, replaceable, random, groundMutable, depth);
				if (groundPlaced) surface.add(groundPos);
			}
		}

		return surface;
	}

	public boolean isWaterAt(BlockState state) {
		return state.is(Blocks.WATER);
	}
}

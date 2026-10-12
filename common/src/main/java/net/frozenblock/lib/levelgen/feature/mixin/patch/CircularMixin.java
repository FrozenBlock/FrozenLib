/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.feature.mixin.patch;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import java.util.Set;
import java.util.function.Predicate;
import net.frozenblock.lib.levelgen.feature.api.feature.patch.FlexibleVegetationPatchFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VegetationPatchFeature.class)
public class CircularMixin {

	@Inject(method = "placeGroundPatch", at = @At("HEAD"))
	public void frozenLib$onlyUseXRadiusForCircular(
		WorldGenLevel level, RandomSource random, BlockPos origin, Predicate<BlockState> replaceable, int xRadius, int zRadius, CallbackInfoReturnable<Set<BlockPos>> info,
		@Local(argsOnly = true, ordinal = 1) LocalIntRef mutableZRadius
	) {
		if (!this.frozenLib$isCircular()) return;
		mutableZRadius.set(xRadius);
	}

	@Definition(id = "isCorner", local = @Local(name = "isCorner", type = boolean.class))
	@Expression("isCorner == false")
	@Inject(
		method = "placeGroundPatch",
		at = @At(
			value = "MIXINEXTRAS:EXPRESSION",
			ordinal = 1
		)
	)
	public void frozenLib$modifyEdges(
		WorldGenLevel level,
		RandomSource random,
		BlockPos origin,
		Predicate<BlockState> replaceable,
		int xRadius,
		int zRadius,
		CallbackInfoReturnable<Set<BlockPos>> info,
		@Local(name = "dx") int dx,
		@Local(name = "dz") int dz,
		@Local(name = "isEdge") LocalBooleanRef isEdge,
		@Local(name = "isCorner") LocalBooleanRef isCorner, // Set true to skip placement
		@Local(name = "isEdgeButNotCorner") LocalBooleanRef isEdgeButNotCorner // Set false to count as valid edge for extraEdgeColumnChance
	) {
		if (!this.frozenLib$isCircular()) return;

		final BlockPos zero = BlockPos.ZERO;
		final BlockPos offsetPos = zero.offset(dx, 0, dz);
		final boolean outsideCircle = offsetPos.distManhattan(zero) > xRadius;
		if (outsideCircle) {
			final BlockPos.MutableBlockPos mutableOffsetPos = new BlockPos.MutableBlockPos();
			for (Direction horizontal : Direction.Plane.HORIZONTAL) {
				if (mutableOffsetPos.setWithOffset(offsetPos, horizontal).distManhattan(zero) <= xRadius) {
					isCorner.set(false);
					isEdge.set(true);
					isEdgeButNotCorner.set(true);
					return;
				}
			}

			isCorner.set(true);
			isEdge.set(true);
			isEdgeButNotCorner.set(false);
		} else {
			isCorner.set(false);
			isEdge.set(false);
			isEdgeButNotCorner.set(false);
		}
	}

	@Unique
	private boolean frozenLib$isCircular() {
		return VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible && flexible.flexibleComponents.circular();
	}
}

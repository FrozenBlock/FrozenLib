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

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.frozenblock.lib.levelgen.feature.api.feature.patch.FlexibleVegetationPatchFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VegetationPatchFeature.class)
public class PlaceOnEdgesMixin {

	@Shadow
	@Final
	protected CaveSurface surface;

	@Inject(method = "distributeVegetation", at = @At("HEAD"))
	public void frozenLib$distributeVegetationWithEdges(
		WorldGenLevel level, ChunkGenerator generator, RandomSource random, Set<BlockPos> surface, CallbackInfo info,
		@Local(argsOnly = true) LocalRef<Set<BlockPos>> mutableSurface
	) {
		if (!(VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible)
			|| !flexible.flexibleComponents.placeOnEdges()
		) return;

		final BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();
		final List<BlockPos> finalDecorationPoses = new ArrayList<>(mutableSurface.get());
		final Direction inwards = this.surface.getDirection();
		final Direction outwards = inwards.getOpposite();

		final BlockPredicate isOpen = flexible.flexibleComponents.isOpen().isPresent()
			? flexible.flexibleComponents.isOpen().get()
			: BlockPredicate.ONLY_IN_AIR_PREDICATE;

		for (BlockPos pos : mutableSurface.get()) {
			testPos.setWithOffset(pos, outwards);
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				testPos.move(direction);
				final BlockPos groundPos = testPos.relative(inwards);

				if (!finalDecorationPoses.contains(groundPos)) {
					if (isOpen.test(level, testPos) && level.getBlockState(groundPos).isFaceSturdy(level, groundPos, outwards)) {
						finalDecorationPoses.add(groundPos);
					}
				}

				testPos.move(direction.getOpposite());
			}
		}

		mutableSurface.set(new HashSet<>(finalDecorationPoses));
	}
}

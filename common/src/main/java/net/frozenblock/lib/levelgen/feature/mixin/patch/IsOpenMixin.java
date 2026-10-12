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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Predicate;
import net.frozenblock.lib.levelgen.feature.api.feature.patch.FlexibleVegetationPatchFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VegetationPatchFeature.class)
public class IsOpenMixin {

	@WrapOperation(
		method = "placeGroundPatch",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;isStateAtPosition(Lnet/minecraft/core/BlockPos;Ljava/util/function/Predicate;)Z",
			ordinal = 0
		)
	)
	public boolean frozenLib$moveInwardsIfOpen(WorldGenLevel instance, BlockPos pos, Predicate predicate, Operation<Boolean> original) {
		if (!(VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible)
			|| flexible.flexibleComponents.isOpen().isEmpty()
		) return original.call(instance, pos, predicate);

		return flexible.flexibleComponents.isOpen().orElseThrow().test(instance, pos);
	}

	@WrapOperation(
		method = "placeGroundPatch",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;isStateAtPosition(Lnet/minecraft/core/BlockPos;Ljava/util/function/Predicate;)Z",
			ordinal = 1
		)
	)
	public boolean frozenLib$moveOutwardsIfNotOpen(WorldGenLevel instance, BlockPos pos, Predicate predicate, Operation<Boolean> original) {
		if (!(VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible)
			|| flexible.flexibleComponents.isOpen().isEmpty()
		) return original.call(instance, pos, predicate);

		return !flexible.flexibleComponents.isOpen().orElseThrow().test(instance, pos);
	}

	@WrapOperation(
		method = "placeGroundPatch",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	public boolean frozenLib$placeGroundIfOpen(WorldGenLevel instance, BlockPos pos, Operation<Boolean> original) {
		if (!(VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible)
			|| flexible.flexibleComponents.isOpen().isEmpty()
		) return original.call(instance, pos);

		return flexible.flexibleComponents.isOpen().orElseThrow().test(instance, pos);
	}
}

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

package net.frozenblock.lib.block.mixin.friction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.api.friction.BlockFrictionAPI;
import net.frozenblock.lib.block.api.friction.FrictionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// IN COMMON MIXINS.JSON
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@WrapOperation(
		method = "travelInAir",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"
		)
	)
	private float frozenLib$applyFrictionApi(
		BlockState instance, LevelReader levelReader, BlockPos blockPos, Entity entity, Operation<Float> original,
		@Local(name = "posBelow") BlockPos posBelow
	) {
		final FrictionContext frictionContext = new FrictionContext(
			entity.level(),
			(LivingEntity) entity,
			entity.level().getBlockState(posBelow),
			original.call(instance, levelReader, blockPos, entity)
		);
		BlockFrictionAPI.MODIFICATIONS.invoker().modifyFriction(frictionContext);

		return frictionContext.friction;
	}
}

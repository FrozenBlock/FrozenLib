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

package net.frozenblock.lib.block.mixin.waterlike;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Predicate;

@Mixin(EntityFluidInteraction.class)
public class EntityFluidInteractionMixin {

	@Inject(method = "update(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Predicate;)V", at = @At("HEAD"))
	public void frozenLib$setupMesogleaFluidDetection(
		Entity entity, Predicate<FluidType> typePushPredicate, CallbackInfo info,
		@Share("frozenLib$closestPosDistance") LocalDoubleRef closestPosDistanceRef
	) {
		closestPosDistanceRef.set(Double.MAX_VALUE);
		entity.frozenLib$setWaterReplacementParticlesFromBlock(null);
		entity.frozenLib$clearInWaterLikes();
		entity.frozenLib$clearTouchingWaterLikes();
	}

	@WrapOperation(
		method = "update(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Predicate;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/BlockGetter;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
		)
	)
	public FluidState frozenLib$saveBlockState(
		BlockGetter instance, BlockPos pos, Operation<FluidState> original,
		@Share("frozenLib$blockState") LocalRef<BlockState> blockStateRef
	) {
		final BlockState state = instance.getBlockState(pos);
		blockStateRef.set(state);
		return original.call(instance, pos);
	}

	@ModifyExpressionValue(
		method = "update(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Predicate;)V",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Math;max(DD)D"
		)
	)
	public double frozenLib$setReplacementParticlesFromWaterLike(
		double original,
		Entity entity,
		@Local(name = "mutablePos") BlockPos.MutableBlockPos mutablePos,
		@Share("frozenLib$blockState") LocalRef<BlockState> blockStateRef,
		@Share("frozenLib$closestPosDistance") LocalDoubleRef closestPosDistanceRef
	) {
		if (!(blockStateRef.get().getBlock() instanceof WaterLikeBlock waterLikeBlock)) return original;

		entity.frozenLib$addTouchingWaterLike(waterLikeBlock.myWaterLikeType(entity.registryAccess()));
		final double distance = entity.distanceToSqr(Vec3.atCenterOf(mutablePos));
		if (distance >= closestPosDistanceRef.get()) return original;

		closestPosDistanceRef.set(distance);
		entity.frozenLib$setWaterReplacementParticlesFromBlock(waterLikeBlock);

		return original;
	}

	@Inject(
		method = "update(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Predicate;)V",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/EntityFluidInteraction$Tracker;eyesInside:Z",
			opcode = Opcodes.PUTFIELD
		)
	)
	public void frozenLib$updateEyeInWaterLike(
		Entity entity, Predicate<FluidType> typePushPredicate, CallbackInfo info,
		@Share("frozenLib$blockState") LocalRef<BlockState> blockStateRef
	) {
		if (blockStateRef.get().getBlock() instanceof WaterLikeBlock waterLikeBlock) {
			entity.frozenLib$addInWaterLike(waterLikeBlock.myWaterLikeType(entity.registryAccess()));
		}
	}
}

/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.gravity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.frozenblock.lib.gravity.impl.EntityGravityInterface;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements EntityGravityInterface {

	@Shadow
	protected abstract double getEffectiveGravity();

	@WrapOperation(
		method = "travelInAir",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V",
			ordinal = 1
		)
	)
	private void frozenLib$newGravity(LivingEntity instance, double x, double y, double z, Operation<Void> original, @Local(ordinal = 0) double originalGravity) {
		LivingEntity entity = LivingEntity.class.cast(this);
		Vec3 gravityVec = GravityAPI.calculateGravity(entity);;
		double gravity = this.getEffectiveGravity();

		double newX = x - gravityVec.x * gravity;
		double newY = y + gravity - gravity * gravityVec.y;
		double newZ = z - gravityVec.z * gravity;

		original.call(instance, newX, newY, newZ);
	}
}

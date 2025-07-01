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

package net.frozenblock.lib.gravity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.gravity.impl.EntityGravityInterface;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Boat.class)
public abstract class BoatMixin implements EntityGravityInterface {

	@WrapOperation(
		method = "floatBoat",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/Boat;setDeltaMovement(DDD)V",
			ordinal = 0
		)
	)
	private void frozenLib$useGravity(Boat instance, double x, double y, double z, Operation<Void> original) {
		Vec3 newVec = new Vec3(x, y + this.frozenLib$getGravity(), z).subtract(this.frozenLib$getEffectiveGravity());
		original.call(instance, newVec.x(), newVec.y(), newVec.z());
	}
}

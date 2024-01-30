/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.gravity.mixin;

import net.frozenblock.lib.gravity.impl.EntityGravityInterface;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Boat.class)
public abstract class BoatMixin implements EntityGravityInterface {

	@ModifyArgs(method = "floatBoat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setDeltaMovement(DDD)V", ordinal = 0))
	private void useGravity(Args args) {
		double x = args.get(0);
		double y = (double) args.get(1) + this.frozenLib$getGravity();
		double z = args.get(2);

		Vec3 newVec = new Vec3(x, y, z).subtract(this.frozenLib$getEffectiveGravity());
		args.set(0, newVec.x);
		args.set(1, newVec.y);
		args.set(2, newVec.z);
	}
}

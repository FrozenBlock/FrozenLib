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

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.frozenblock.lib.gravity.impl.EntityGravityInterface;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin implements EntityGravityInterface {

	@ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
	private void useGravity(Args args, @Local double gravity) {
		AbstractMinecart minecart = AbstractMinecart.class.cast(this);

		Vec3 newVec = new Vec3(args.get(0), args.get(1), args.get(2))
			.subtract(
				new Vec3(gravity, gravity, gravity)
					.multiply(this.frozenLib$getEffectiveGravity())
			);
		if (minecart.isInWater())
			newVec = newVec.scale(0.005);

		args.set(0, newVec.x);
		args.set(1, newVec.y);
		args.set(2, newVec.z);
	}
}

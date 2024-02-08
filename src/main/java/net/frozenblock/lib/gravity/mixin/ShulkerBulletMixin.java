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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.gravity.impl.EntityGravityInterface;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShulkerBullet.class)
public abstract class ShulkerBulletMixin implements EntityGravityInterface {

	@ModifyExpressionValue(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;isNoGravity()Z",
			ordinal = 0
		)
	)
	private boolean frozenLib$useGravity(boolean original) {
		if (!original) {
			ShulkerBullet bullet = ShulkerBullet.class.cast(this);
			bullet.setDeltaMovement(bullet.getDeltaMovement().subtract(this.frozenLib$getEffectiveGravity()));
		}
		return true;
	}
}

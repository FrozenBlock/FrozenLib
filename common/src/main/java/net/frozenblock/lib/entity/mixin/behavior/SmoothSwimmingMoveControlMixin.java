/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.entity.mixin.behavior;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.entity.api.behavior.SmootherSwimmingMoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SmoothSwimmingMoveControl.class, priority = 1500)
public class SmoothSwimmingMoveControlMixin {

	@ModifyExpressionValue(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/ai/control/SmoothSwimmingMoveControl;getTurningSpeedFactor(F)F"
		)
	)
	public float frozenLib$discardTurningSpeedFactorIfSmoother(float original) {
		if (SmoothSwimmingMoveControl.class.cast(this) instanceof SmootherSwimmingMoveControl<?>) return 1F;
		return original;
	}
}

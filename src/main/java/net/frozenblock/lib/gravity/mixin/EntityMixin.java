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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public float fallDistance;

	@Inject(method = "checkFallDamage", at = @At("TAIL"))
	private void frozenLib$checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo info) {
		this.fallDistance *= (float) GravityAPI.calculateGravity(Entity.class.cast(this));
	}

	@ModifyReturnValue(method = "getGravity", at = @At("RETURN"))
	public double frozenLib$modifyGravity(double original) {
		return original * GravityAPI.calculateGravity(Entity.class.cast(this));
	}
}

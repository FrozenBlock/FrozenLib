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

import net.frozenblock.lib.gravity.api.GravityAPI;
import net.frozenblock.lib.gravity.impl.EntityGravityInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements EntityGravityInterface {

	@Shadow
	public float fallDistance;

	@Inject(method = "checkFallDamage", at = @At("TAIL"))
	private void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
		Vec3 gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
		double gravityDistance = gravity.length();
		this.fallDistance *= (float) gravityDistance;
	}

	@Unique
	@Override
	public double frozenLib$getGravity() {
		return 0.04D;
	}

	@Unique
	@Override
	public Vec3 frozenLib$getEffectiveGravity() {
		Entity entity = Entity.class.cast(this);
		return GravityAPI.calculateGravity(entity).scale(this.frozenLib$getGravity());
	}
}

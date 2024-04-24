/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.gravity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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
public abstract class EntityMixin implements EntityGravityInterface {

	@Shadow
	public float fallDistance;

	@Inject(method = "checkFallDamage", at = @At("TAIL"))
	private void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
		Vec3 gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
		double gravityDistance = gravity.length();
		this.fallDistance *= (float) gravityDistance;
	}

	@WrapOperation(method = "applyGravity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
	public Vec3 frozenLib$applyGravity(Vec3 instance, double x, double y, double z, Operation<Vec3> original, @Local(ordinal = 0) double originalGravity) {
		Vec3 gravityVec = GravityAPI.calculateGravity(Entity.class.cast(this)).scale(originalGravity);
		Vec3 directional = new Vec3(x, y + originalGravity, z).subtract(gravityVec);

		return original.call(instance, directional.x, directional.y, directional.z);
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

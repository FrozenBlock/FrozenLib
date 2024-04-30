/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.gravity.mixin.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public class ParticleMixin {

	@Unique
	private static final double BASE_GRAVITY = 0.04;

	@Shadow
	protected double xd;

	@Shadow
	protected double yd;

	@Shadow
	protected double zd;

	@Shadow
	@Final
	protected ClientLevel level;

	@Shadow
	public double y;

	@Shadow
	protected float gravity;

	@Inject(method = "tick", at = @At("HEAD"))
	private void storeY(
		CallbackInfo ci,
		@Share("oldX") LocalDoubleRef oldX,
		@Share("oldY") LocalDoubleRef oldY,
		@Share("oldZ") LocalDoubleRef oldZ
	) {
		oldX.set(this.xd);
		oldY.set(this.yd);
		oldZ.set(this.zd);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;move(DDD)V", ordinal = 0))
	private void useGravity(
		CallbackInfo ci,
		@Share("oldX") LocalDoubleRef oldX,
		@Share("oldY") LocalDoubleRef oldY,
		@Share("oldZ") LocalDoubleRef oldZ
	) {
		Vec3 gravity = GravityAPI.calculateGravity(this.level, this.y).scale(this.gravity).scale(BASE_GRAVITY);
		this.xd = oldX.get() - gravity.x;
		this.yd = oldY.get() - gravity.y;
		this.zd = oldZ.get() - gravity.z;
	}
}

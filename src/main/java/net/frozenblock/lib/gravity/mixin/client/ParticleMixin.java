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

package net.frozenblock.lib.gravity.mixin.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
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
	protected double yd;

	@Shadow
	@Final
	protected ClientLevel level;

	@Shadow
	public double y;

	@Shadow
	protected float gravity;

	@Inject(method = "tick", at = @At("HEAD"))
	private void storeY(CallbackInfo ci, @Share("oldY") LocalDoubleRef oldY) {
		oldY.set(this.yd);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;move(DDD)V", ordinal = 0))
	private void useGravity(CallbackInfo ci, @Share("oldY") LocalDoubleRef oldY) {
		this.yd = oldY.get() - (BASE_GRAVITY * GravityAPI.calculateGravity(this.level, this.y) * this.gravity);
	}
}

/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.gameevent.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.gameevent.api.VibrationHider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VibrationSystem.Ticker.class)
public interface VibrationSystemTickerMixin {

	@WrapOperation(method = "method_51408", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
	private static int wilderWild$removeParticleIfCrab(ServerLevel usedLevel, ParticleOptions vibrationParticleOption, double x, double y, double z, int count, double xOffset, double yOffset, double zOffset, double speed, Operation<Integer> operation, VibrationSystem.Data data, VibrationSystem.User user, ServerLevel level, VibrationInfo vibrationInfo) {
		if (VibrationHider.HIDDEN_VIBRATIONS.containsKey(user.getClass()) && VibrationHider.HIDDEN_VIBRATIONS.get(user.getClass()).shouldHideVibration(data, user, level)) {
			return 0;
		}
		return operation.call(usedLevel, vibrationParticleOption, x, y, z, count, xOffset, yOffset, zOffset, speed);
	}

	@WrapOperation(method = "tryReloadVibrationParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
	private static int wilderWild$removeParticleFromReloadIfCrab(ServerLevel usedLevel, ParticleOptions vibrationParticleOption, double x, double y, double z, int count, double xOffset, double yOffset, double zOffset, double speed, Operation<Integer> operation, ServerLevel level, VibrationSystem.Data data, VibrationSystem.User user) {
		if (VibrationHider.HIDDEN_VIBRATIONS.containsKey(user.getClass()) && VibrationHider.HIDDEN_VIBRATIONS.get(user.getClass()).shouldHideVibration(data, user, level)) {
			return 1;
		}
		return operation.call(usedLevel, vibrationParticleOption, x, y, z, count, xOffset, yOffset, zOffset, speed);
	}

}

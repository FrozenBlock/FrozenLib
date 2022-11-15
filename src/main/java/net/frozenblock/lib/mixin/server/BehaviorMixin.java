/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.entity.behavior.impl.FrozenBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Behavior.class)
public class BehaviorMixin<E extends LivingEntity> implements FrozenBehavior {

	@Unique
	private int wilderWild$duration;

	@Inject(method = "tryStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/Behavior;start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;J)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void tryStart(ServerLevel level, E owner, long gameTime, CallbackInfoReturnable<Boolean> cir, int i) {
		this.wilderWild$duration = i;
	}

	@Unique
	@Override
	public int getDuration() {
		return this.wilderWild$duration;
	}
}

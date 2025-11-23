/*
 * Copyright (C) 2024-2025 FrozenBlock
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

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.entity.impl.behavior.FrozenLibBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Behavior.class)
public class BehaviorMixin<E extends LivingEntity> implements FrozenLibBehavior {

	@Unique
	private int frozenLib$duration;

	@Inject(
		method = "tryStart",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/ai/behavior/Behavior;start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;J)V",
			shift = At.Shift.BEFORE
		)
	)
	private void frozenLib$tryStart(
		ServerLevel level, E entity, long gameTime, CallbackInfoReturnable<Boolean> info,
		@Local int i
	) {
		this.frozenLib$duration = i;
	}

	@Unique
	@Override
	public int frozenLib$getDuration() {
		return this.frozenLib$duration;
	}
}

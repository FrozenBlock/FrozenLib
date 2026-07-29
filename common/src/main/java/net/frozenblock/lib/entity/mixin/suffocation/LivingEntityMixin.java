/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.entity.mixin.suffocation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.HashMap;
import java.util.Map;
import net.frozenblock.lib.entity.api.suffocation.SuffocationManager;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationStateInterface;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements SuffocationStateInterface {

	@Unique
	private final Map<Holder<SuffocationType>, Integer> frozenLib$suffocationSourceTimers = new HashMap<>();

	@Unique
	private boolean frozenLib$preventAirRefill;

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void frozenLib$computeAirRefillGate(CallbackInfo info) {
		this.frozenLib$preventAirRefill = SuffocationManager.preventsVanillaAirRefill(LivingEntity.class.cast(this));
	}

	@WrapOperation(
		method = "baseTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I"
		)
	)
	private int frozenLib$gateAirRefill(LivingEntity instance, int currentSupply, Operation<Integer> original) {
		return this.frozenLib$preventAirRefill ? currentSupply : original.call(instance, currentSupply);
	}

	@Inject(method = "baseTick", at = @At("TAIL"))
	private void frozenLib$tickSuffocation(CallbackInfo info) {
		SuffocationManager.serverTick(LivingEntity.class.cast(this));
	}

	@Unique
	@Override
	public void frozenLib$applySuffocationSource(Holder<SuffocationType> type, int ticks) {
		this.frozenLib$suffocationSourceTimers.merge(type, ticks, Math::max);
	}

	@Unique
	@Override
	public Map<Holder<SuffocationType>, Integer> frozenLib$suffocationSourceTimers() {
		return this.frozenLib$suffocationSourceTimers;
	}
}

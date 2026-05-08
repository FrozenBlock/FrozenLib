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

package net.frozenblock.lib.block.mixin.fire;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.frozenblock.lib.block.api.fire.FireEvents;
import net.frozenblock.lib.block.api.fire.FireTypes;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 987)
public class LivingEntityMixin {

	@Inject(method = "hurtServer", at = @At("HEAD"))
	public void frozenLib$modifyFireDamageAndTriggerEvent(
		ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> info,
		@Local(argsOnly = true) LocalFloatRef damageRef
	) {
		if (!source.is(DamageTypes.ON_FIRE)) return;

		final FireData fireData = LivingEntity.class.cast(this).getAttached(FireData.ATTACHMENT);
		final float newDamage = damage == 1F && fireData != null
			? fireData.type().value().damage()
			: damage;
		damageRef.set(newDamage);
		FireEvents.ON_ENTITY_BURN_TICK.invoker().onEntityBurnTick(LivingEntity.class.cast(this), FireTypes.getFromDataOrDefault(level.registryAccess(), fireData));
	}
}

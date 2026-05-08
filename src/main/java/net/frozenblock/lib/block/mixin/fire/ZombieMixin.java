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

import java.util.Optional;
import net.frozenblock.lib.block.api.fire.FireEvents;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public class ZombieMixin {

	@Inject(
		method = "doHurtTarget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$setFireType(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> info) {
		final FireData fireData = Zombie.class.cast(this).getAttached(FireData.ATTACHMENT);
		if (fireData == null || !fireData.type().value().spreadsFromZombie()) return;

		final ResourceKey<FireType> fireType = FireEvents.SELECT_FIRE_TYPE.invoker().selectFireType(
			target,
			Optional.empty(),
			Optional.of(Zombie.class.cast(this)),
			Optional.empty()
		);
		FireData.trySet(target, fireType);
	}
}

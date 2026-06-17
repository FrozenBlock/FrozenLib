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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Optional;
import net.frozenblock.lib.block.api.fire.FireEvents;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(
		method = "lavaIgnite",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$lavaIgnite(CallbackInfo info) {
		final Entity entity = Entity.class.cast(this);
		final ResourceKey<FireType> fireType = FireEvents.SELECT_FIRE_TYPE.invoker().selectFireType(
			entity,
			Optional.of(Blocks.LAVA),
			Optional.empty(),
			Optional.empty()
		);
		FireData.trySet(entity, fireType);
	}

	@Inject(
		method = "thunderHit",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$thunderHit(CallbackInfo info) {
		final Entity entity = Entity.class.cast(this);
		final ResourceKey<FireType> fireType = FireEvents.SELECT_FIRE_TYPE.invoker().selectFireType(
			entity,
			Optional.empty(),
			Optional.empty(),
			Optional.empty()
		);
		FireData.trySet(entity, fireType);
	}

	@WrapOperation(
		method = "setSharedFlagOnFire",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;setSharedFlag(IZ)V"
		)
	)
	public void frozenLib$setSharedFlagOnFire(Entity instance, int flag, boolean value, Operation<Void> original) {
		if (!instance.level().isClientSide() && !value) instance.removeAttached(FireData.ATTACHMENT);
		original.call(instance, flag, value);
	}
}

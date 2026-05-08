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
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.api.fire.FireEvents;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {

	@Inject(method = "lambda$entityInside$0", at = @At("HEAD"))
	public void frozenLib$setFireType(Entity entity, CallbackInfo info) {
		final ResourceKey<FireType> fireType = FireEvents.SELECT_FIRE_TYPE.invoker().selectFireType(
			entity,
			Optional.of(BaseFireBlock.class.cast(this)),
			Optional.empty(),
			Optional.empty()
		);
		FireData.trySet(entity, fireType);
	}

	@ModifyReturnValue(method = "getState", at = @At("RETURN"))
	private static BlockState frozenLib$selectFireBlockState(
		BlockState original,
		@Local(argsOnly = true) BlockGetter level,
		@Local(name = "below") BlockPos below, @Local(name = "belowState") BlockState belowState
	) {
		final BlockState newState = FireEvents.SELECT_FIRE_BLOCK_STATE.invoker().selectFireBlockState(level, below, belowState);
		return newState != null ? newState : original;
	}
}

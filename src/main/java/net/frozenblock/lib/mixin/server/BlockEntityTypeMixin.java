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

package net.frozenblock.lib.mixin.server;

import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

	/**
	 * This allows custom sign and hanging sign blocks to be added to their block entities
	 */
	@Inject(method = "isValid", at = @At("RETURN"), cancellable = true)
	private void isValid(BlockState state, CallbackInfoReturnable<Boolean> info) {
		var type = BlockEntityType.class.cast(this);

		if ((type == BlockEntityType.SIGN &&
				(state.getBlock() instanceof StandingSignBlock || state.getBlock() instanceof WallSignBlock))
				|| (type == BlockEntityType.HANGING_SIGN
				&& (state.getBlock() instanceof CeilingHangingSignBlock || state.getBlock() instanceof WallHangingSignBlock))) {
			info.setReturnValue(true);
		}
	}
}

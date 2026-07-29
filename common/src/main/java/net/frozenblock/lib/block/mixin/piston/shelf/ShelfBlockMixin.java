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

package net.frozenblock.lib.block.mixin.piston.shelf;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShelfBlock.class)
public class ShelfBlockMixin {

	@Inject(method = "onPlace", at = @At("HEAD"))
	private void frozenLib$updatePowerOnMovement(
		BlockState state, Level level, BlockPos pos, BlockState replacingState, boolean movedByPiston, CallbackInfo info,
		@Local(argsOnly = true, ordinal = 0) LocalRef<BlockState> stateRef
	) {
		// This is technically unnecessary, but is here just to be safe.
		if (!movedByPiston) return;
		final boolean powered = level.hasNeighborSignal(pos);
		stateRef.set(state.setValue(ShelfBlock.POWERED, powered));
	}
}

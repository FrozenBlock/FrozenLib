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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SideChainPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public class ShelfBlockUnpowerOnMoveMixin {

	@Inject(
		method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)V",
		at = @At(
			value = "CTOR_HEAD",
			args = "enforce=PRE_BODY"
		)
	)
	public void frozenLib$powerDownShelfOnPush(
		CallbackInfo info,
		@Local(ordinal = 1, argsOnly = true) LocalRef<BlockState> movedStateRef
	) {
		final BlockState movedState = movedStateRef.get();
		if (!movedState.is(BlockTags.WOODEN_SHELVES)) return;
		movedStateRef.set(movedState.trySetValue(ShelfBlock.POWERED, false).trySetValue(ShelfBlock.SIDE_CHAIN_PART, SideChainPart.UNCONNECTED));
	}
}

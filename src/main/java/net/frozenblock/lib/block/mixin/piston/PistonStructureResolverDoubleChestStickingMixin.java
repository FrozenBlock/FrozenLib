/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.mixin.piston;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverDoubleChestStickingMixin {

	@WrapOperation(
		method = {"resolve", "addBlockLine"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;isSticky(Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean frozenLib$doubleChestSticking(BlockState state, Operation<Boolean> original) {
		if (state.is(ConventionalBlockTags.CHESTS) && state.is(FrozenBlockTags.HAS_PUSHABLE_BLOCK_ENTITY)) return true;
		return original.call(state);
	}

	@Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
	private static void frozenLib$doubleChestSticking(BlockState state1, BlockState state2, CallbackInfoReturnable<Boolean> info) {
		if (!state1.is(ConventionalBlockTags.CHESTS) || !state2.is(ConventionalBlockTags.CHESTS)) return;
		if (!state1.is(FrozenBlockTags.HAS_PUSHABLE_BLOCK_ENTITY) || !state2.is(FrozenBlockTags.HAS_PUSHABLE_BLOCK_ENTITY)) return;

		final ChestType chest1Type = state1.getValueOrElse(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
		if (chest1Type == ChestType.SINGLE) return;

		final ChestType chest2Type = state2.getValueOrElse(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
		if (chest2Type == ChestType.SINGLE) return;

		if (!state1.hasProperty(ChestBlock.FACING) || !state2.hasProperty(ChestBlock.FACING)) return;

		final Direction connectedDirection1 = ChestBlock.getConnectedDirection(state1);
		final Direction connectedDirection2 = ChestBlock.getConnectedDirection(state2);
		if (connectedDirection1 == connectedDirection2.getOpposite()) info.setReturnValue(true);
	}

}

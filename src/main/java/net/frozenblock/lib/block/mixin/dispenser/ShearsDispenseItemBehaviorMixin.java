/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.dispenser;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.block.api.dispenser.ShearsDispenseItemBehaviorApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShearsDispenseItemBehavior.class)
public class ShearsDispenseItemBehaviorMixin {

	@WrapOperation(
		method = "execute",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/dispenser/ShearsDispenseItemBehavior;tryShearBeehive(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean frozenLib$attemptAdditionalBehaviors(ServerLevel level, ItemStack tool, BlockPos pos, Operation<Boolean> operation) {
		if (operation.call(level, tool, pos)) return true;

		final BlockState state = level.getBlockState(pos);
		return ShearsDispenseItemBehaviorApi.tryShear(state, level, tool, pos);
	}
}

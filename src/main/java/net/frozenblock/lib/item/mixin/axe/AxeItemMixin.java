/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.item.mixin.axe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Optional;
import net.frozenblock.lib.item.api.axe.AxeBehaviors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AxeItem.class)
public class AxeItemMixin {

	@WrapOperation(
		method = "useOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/AxeItem;evaluateNewBlockState(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;",
			ordinal = 0
		)
	)
	public Optional<BlockState> frozenlib$runAxeBehavior(
		AxeItem instance,
		Level world,
		BlockPos pos,
		Player player,
		BlockState state,
		Operation<Optional<BlockState>> original,
		UseOnContext context
	) {
		BlockState blockState = world.getBlockState(pos);
		Direction direction = context.getClickedFace();
		AxeBehaviors.AxeBehavior axeBehavior = AxeBehaviors.get(blockState.getBlock());
		if (axeBehavior != null && axeBehavior.meetsRequirements(world, pos, direction, state)) {
			BlockState outputState = axeBehavior.getOutputBlockState(state);
			if (outputState != null) {
				axeBehavior.onSuccess(world, pos, direction, outputState, state);
				return Optional.of(outputState);
			}
		}

		return original.call(instance, world, pos, player, state);
	}

}

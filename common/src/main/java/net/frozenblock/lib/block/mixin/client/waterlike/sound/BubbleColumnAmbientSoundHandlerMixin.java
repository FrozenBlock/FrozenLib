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

package net.frozenblock.lib.block.mixin.client.waterlike.sound;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.frozenblock.lib.platform.api.ClientOnly;import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(BubbleColumnAmbientSoundHandler.class)
public class BubbleColumnAmbientSoundHandlerMixin {

	@WrapOperation(
		method = "lambda$tick$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z",
			ordinal = 0
		),
		require = 0
	)
	private static boolean frozenLib$filterWithWaterLike(BlockState state, Object block, Operation<Boolean> operation) {
		return operation.call(state, block) || WaterLikeBlock.hasBubbleColumn(state);
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z",
			ordinal = 0
		),
		require = 0
	)
	private boolean frozenLib$checkIfBubbleColumnOrWaterLikeAsBubbleColumn(BlockState state, Object block, Operation<Boolean> operation) {
		return operation.call(state, block) || WaterLikeBlock.hasBubbleColumn(state);
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;",
			ordinal = 0
		),
		require = 0
	)
	private Comparable<Boolean> frozenLib$checkIfBubbleColumnOrWaterLikeAsBubbleColumnDraggingDown(BlockState state, Property<?> property, Operation<Comparable<Boolean>> operation) {
		return state.hasProperty(BubbleColumnBlock.DRAG_DOWN) ? operation.call(state, property) : WaterLikeBlock.isDraggingDownAsBubbleColumn(state);
	}

}

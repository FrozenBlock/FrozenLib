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
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.impl.PistonMovingBlockEntityInterface;
import net.frozenblock.lib.block.impl.PushableBlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin implements PistonMovingBlockEntityInterface {
	@Unique
	private CompoundTag frozenLib$pushedBlockEntityTag = null;
	@Unique
	private BlockEntity frozenLib$fakeBlockEntity = null;

	@Unique
	@Override
	public void frozenLib$setPushedBlockEntityTag(CompoundTag tag) {
		this.frozenLib$pushedBlockEntityTag = tag;
	}

	@Unique
	@Override
	public CompoundTag frozenLib$getPushedBlockEntityTag() {
		return this.frozenLib$pushedBlockEntityTag;
	}

	@Unique
	@Override
	public BlockEntity frozenLib$getPushedFakeBlockEntity() {
		if (this.frozenLib$pushedBlockEntityTag == null) {
			this.frozenLib$fakeBlockEntity = null;
			return null;
		}

		if (this.frozenLib$fakeBlockEntity == null) {
			final PistonMovingBlockEntity movingBlockEntity = PistonMovingBlockEntity.class.cast(this);
			final Level level = movingBlockEntity.getLevel();
			if (level == null) return null;

			final BlockEntity blockEntity = BlockEntity.loadStatic(
				movingBlockEntity.getBlockPos(),
				movingBlockEntity.getMovedState(),
				this.frozenLib$pushedBlockEntityTag,
				level.registryAccess()
			);
			if (blockEntity != null) blockEntity.setLevel(level);
			this.frozenLib$fakeBlockEntity = blockEntity;
		}

		return this.frozenLib$fakeBlockEntity;
	}

	@WrapOperation(
		method = "finalTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	public boolean frozenLib$setBlockFinalTick(Level level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
		final boolean setBlock = original.call(level, pos, state, flags);
		return PushableBlockEntityUtil.setBlockAndEntity(setBlock, level, pos, state, PistonMovingBlockEntity.class.cast(this));
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private static boolean frozenLib$setBlockTick(
		Level level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original,
		@Local(argsOnly = true) PistonMovingBlockEntity pistonEntity
	) {
		final boolean setBlock = original.call(level, pos, state, flags);
		return PushableBlockEntityUtil.setBlockAndEntity(setBlock, level, pos, state, pistonEntity);
	}

	@Inject(method = "loadAdditional", at = @At("TAIL"))
	public void frozenLib$loadAdditional(ValueInput input, CallbackInfo info) {
		this.frozenLib$pushedBlockEntityTag = input.read("frozenLib_PushedBlockEntity", CompoundTag.CODEC).orElse(null);
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	public void frozenLib$saveAdditional(ValueOutput output, CallbackInfo info) {
		output.storeNullable("frozenLib_PushedBlockEntity", CompoundTag.CODEC, this.frozenLib$pushedBlockEntityTag);
	}

}

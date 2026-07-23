/*
 * Copyright (C) 2025-2026 FrozenBlock
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
import com.mojang.serialization.Dynamic;
import net.frozenblock.lib.block.api.piston.PistonEvents;
import net.frozenblock.lib.block.impl.piston.PistonMovingBlockEntityInterface;
import net.frozenblock.lib.block.impl.piston.PistonPushUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.SharedConstants;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin implements PistonMovingBlockEntityInterface {
	@Unique
	private CompoundTag frozenLib$pushedBlockEntityTag = null;
	@Unique
	private BlockEntity frozenLib$fakeBlockEntity = null;

	@Unique
	@Override
	public void frozenLib$setPushedBlockEntityTag(@Nullable CompoundTag tag) {
		this.frozenLib$pushedBlockEntityTag = tag;
	}

	@Unique
	@Nullable
	@Override
	public CompoundTag frozenLib$getPushedBlockEntityTag() {
		return this.frozenLib$pushedBlockEntityTag;
	}

	@Unique
	@Nullable
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
	public boolean frozenLib$setBlockFinalTick(Level level, BlockPos pos, BlockState blockState, int updateFlags, Operation<Boolean> original) {
		final boolean setBlock = original.call(level, pos, blockState, updateFlags);
		PistonPushUtil.trySetBlockAndEntity(level, pos, blockState, PistonMovingBlockEntity.class.cast(this));
		PistonEvents.ON_MOVING_BLOCK_SET.invoker().onMovingBlockSet(level, pos, blockState, PistonMovingBlockEntity.class.cast(this));
		return setBlock;
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private static boolean frozenLib$setBlockTick(
		Level level, BlockPos pos, BlockState blockState, int updateFlags, Operation<Boolean> original,
		@Local(argsOnly = true) PistonMovingBlockEntity entity
	) {
		final boolean setBlock = original.call(level, pos, blockState, updateFlags);
		PistonPushUtil.trySetBlockAndEntity(level, pos, blockState, entity);
		PistonEvents.ON_MOVING_BLOCK_SET.invoker().onMovingBlockSet(level, pos, entity.getMovedState(), entity);
		return setBlock;
	}

	@Inject(method = "loadAdditional", at = @At("TAIL"))
	public void frozenLib$loadAdditional(ValueInput input, CallbackInfo info) {
		CompoundTag blockEntityTag = input.read("frozenLib_PushedBlockEntity", CompoundTag.CODEC).orElse(null);
		if (blockEntityTag == null) return;

		// Casting the value to a CompoundTag can be seen in DataFixTypes.
		// If this ever causes a crash, check if that class has changed and offers a new solution.
		blockEntityTag = (CompoundTag) DataFixers.getDataFixer().update(
			References.BLOCK_ENTITY,
			new Dynamic<>(NbtOps.INSTANCE, blockEntityTag),
			NbtUtils.getDataVersion(blockEntityTag),
			SharedConstants.getCurrentVersion().dataVersion().version()
		).getValue();
		this.frozenLib$pushedBlockEntityTag = blockEntityTag;
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	public void frozenLib$saveAdditional(ValueOutput output, CallbackInfo info) {
		NbtUtils.addCurrentDataVersion(this.frozenLib$pushedBlockEntityTag);
		output.storeNullable("frozenLib_PushedBlockEntity", CompoundTag.CODEC, this.frozenLib$pushedBlockEntityTag);
	}
}

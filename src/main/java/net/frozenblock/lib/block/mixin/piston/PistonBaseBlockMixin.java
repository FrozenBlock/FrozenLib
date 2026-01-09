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

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.block.impl.PushableBlockEntityUtil;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

	@WrapOperation(
		method = "isPushable",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;hasBlockEntity()Z"
		)
	)
	private static boolean frozenLib$allowBlockEntityPushing(BlockState state, Operation<Boolean> original) {
		final boolean hasBlockEntity = original.call(state);
		if (hasBlockEntity && state.is(FrozenBlockTags.HAS_PUSHABLE_BLOCK_ENTITY)) return false;
		return hasBlockEntity;
	}

	// triggerEvent

	@WrapOperation(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
			)
		)
	)
	public boolean frozenLib$captureBlockEntity(
		Level instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original,
		@Share("frozenLib$blockEntityTag") LocalRef<CompoundTag> blockEntityTagRef
	) {
		blockEntityTagRef.set(null);
		final BlockEntity blockEntity = instance.getBlockEntity(pos);
		if (blockEntity != null) blockEntityTagRef.set(blockEntity.saveWithFullMetadata(instance.registryAccess()));
		return original.call(instance, pos, state, flags);
	}

	@ModifyExpressionValue(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	public BlockEntity frozenLib$saveBlockEntityToMovingBlock(
		BlockEntity original,
		@Share("frozenLib$blockEntityTag") LocalRef<CompoundTag> blockEntityTagRef
	) {
		PushableBlockEntityUtil.saveTag(blockEntityTagRef.get(), original);
		return original;
	}

	//moveBlocks

	@Inject(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;getToPush()Ljava/util/List;"
		)
	)
	public void frozenLib$createBlockEntityList(
		Level level, BlockPos pos, Direction direction, boolean bl, CallbackInfoReturnable<Boolean> info,
		@Share("frozenLib$blockEntityList") LocalRef<List<Optional<BlockEntity>>> blockEntityListRef
	) {
		final List<Optional<BlockEntity>> blockEntityList = Lists.newArrayList();
		blockEntityListRef.set(blockEntityList);
	}

	@WrapOperation(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;getToPush()Ljava/util/List;"
			)
		)
	)
	public BlockState frozenLib$captureBlockEntities(
		Level instance, BlockPos pos, Operation<BlockState> original,
		@Share("frozenLib$blockEntityList") LocalRef<List<Optional<BlockEntity>>> blockEntityListRef
	) {
		final List<Optional<BlockEntity>> blockEntityList = blockEntityListRef.get();
		if (blockEntityList != null) blockEntityList.add(Optional.ofNullable(instance.getBlockEntity(pos)));
		return original.call(instance, pos);
	}

	@WrapOperation(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;get(I)Ljava/lang/Object;",
			ordinal = 1
		)
	)
	public Object frozenLib$captureBlockIndex(
		List instance, int i, Operation<Object> original,
		@Share("frozenLib$listIndex") LocalIntRef listIndexRef
	) {
		listIndexRef.set(i);
		return original.call(instance, i);
	}

	@ModifyExpressionValue(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;",
			ordinal = 0
		)
	)
	public BlockEntity frozenLib$saveBlockEntityToMovingBlocks(
		BlockEntity original,
		@Local(argsOnly = true) Level level,
		@Share("frozenLib$blockEntityList") LocalRef<List<Optional<BlockEntity>>> blockEntityListRef,
		@Share("frozenLib$listIndex") LocalIntRef listIndexRef
	) {
		final List<Optional<BlockEntity>> blockEntityList = blockEntityListRef.get();
		if (blockEntityList != null) {
			final Optional<BlockEntity> optionalBlockEntity = blockEntityList.get(listIndexRef.get());
			optionalBlockEntity.ifPresent(blockEntity -> PushableBlockEntityUtil.saveBlockEntity(level, blockEntity, original));
		}
		return original;
	}

	@WrapOperation(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Ljava/util/Map;keySet()Ljava/util/Set;"
			)
		)
	)
	public boolean frozenLib$saveBlockEntityToMovingBlocks(Level instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
		// The AND check for 256 dictates whether `BlockEntity$preRemoveSideEffects` is called.
		// With 82, & 256 returns 0.
		// Adding 256 makes this not return 0, while keeping all other calls intact.
		final BlockState movingState = instance.getBlockState(pos);
		if (movingState.hasBlockEntity() && movingState.is(FrozenBlockTags.HAS_PUSHABLE_BLOCK_ENTITY) && (flags & PistonBaseBlock.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS) == 0) {
			flags += PistonBaseBlock.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS;
		}
		return original.call(instance, pos, state, flags);
	}

}

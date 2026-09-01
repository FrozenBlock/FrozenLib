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

package net.frozenblock.lib.block.impl.piston;

import net.frozenblock.lib.block.api.piston.PistonEvents;
import net.frozenblock.lib.tag.api.ConventionalBlockTags;
import net.frozenblock.lib.tag.api.FrozenLibBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.SideChainPart;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class PistonPushUtil {

	public static boolean cannotPushBlockEntity(boolean hasBlockEntity, BlockState state, Direction direction) {
		if (!hasBlockEntity) return hasBlockEntity;

		final PistonEvents.PushResult pushResult = PistonEvents.DETERMINE_BLOCK_ENTITY_PUSH_RESULT.invoker().determineBlockEntityPushResult(state, direction);
		if (pushResult == PistonEvents.PushResult.FAIL) return true;
		if (pushResult == PistonEvents.PushResult.SUCCESS) return false;
		if (pushResult == PistonEvents.PushResult.PASS && state.is(FrozenLibBlockTags.HAS_PUSHABLE_BLOCK_ENTITY)) return false;
		return hasBlockEntity;
	}

	public static boolean isBlockEntityPushableSuccess(BlockState state, Direction direction) {
		return PistonEvents.DETERMINE_BLOCK_ENTITY_PUSH_RESULT.invoker().determineBlockEntityPushResult(state, direction) == PistonEvents.PushResult.SUCCESS;
	}

	public static boolean isSticky(boolean original, BlockState state, Direction direction) {
		final PistonEvents.StickyResult result = PistonEvents.DETERMINE_BLOCK_STICKINESS.invoker().determineBlockStickiness(state, direction);
		if (result == PistonEvents.StickyResult.PASS) return original;
		if (result == PistonEvents.StickyResult.FAIL) return false;
		if (result == PistonEvents.StickyResult.SUCCESS) return true;
		return original;
	}

	public static boolean canBlocksStickTogether(boolean original, BlockState previousState, BlockState nextState, Direction direction) {
		final PistonEvents.StickTogetherResult result = PistonEvents.TRY_STICK_BLOCKS_TOGETHER.invoker().tryStickBlocksTogether(previousState, nextState, direction);
		if (result == PistonEvents.StickTogetherResult.PASS) return original;
		if (result == PistonEvents.StickTogetherResult.FAIL) return false;
		if (result == PistonEvents.StickTogetherResult.SUCCESS) return true;
		return original;
	}

	public static boolean trySaveBlockEntity(Level level, BlockEntity blockEntity, BlockEntity pistonEntity) {
		if (!(pistonEntity instanceof PistonMovingBlockEntity pistonMovingBlock)) return false;

		final CompoundTag blockEntityTag = blockEntity.saveWithFullMetadata(level.registryAccess());
		pistonMovingBlock.frozenLib$setPushedBlockEntityTag(blockEntityTag);
		return true;
	}

	public static boolean trySaveTag(@Nullable CompoundTag tag, BlockEntity pistonEntity) {
		if (tag == null) return true;
		if (!(pistonEntity instanceof PistonMovingBlockEntity pistonMovingBlock)) return false;

		pistonMovingBlock.frozenLib$setPushedBlockEntityTag(tag);
		return true;
	}

	public static void trySetBlockAndEntity(
		Level level,
		BlockPos pos,
		BlockState state,
		PistonMovingBlockEntity pistonEntity
	) {
		if (!state.hasBlockEntity() || !(pistonEntity instanceof PistonMovingBlockEntity pistonMovingBlock)) return;

		final CompoundTag blockEntityTag = pistonMovingBlock.frozenLib$getPushedBlockEntityTag();
		if (blockEntityTag == null) return;

		final BlockEntity blockEntity = BlockEntity.loadStatic(pos, state, blockEntityTag, level.registryAccess());
		if (blockEntity != null) level.setBlockEntity(blockEntity);
	}

	@Nullable
	public static BlockEntity getFakeBlockEntity(BlockEntity pistonEntity) {
		if (!(pistonEntity instanceof PistonMovingBlockEntity pistonMovingBlock)) return null;
		return pistonMovingBlock.frozenLib$getPushedFakeBlockEntity();
	}

	public static void init() {
		// CHEST
		PistonEvents.DETERMINE_BLOCK_STICKINESS.register((state, direction) -> {
			if (!state.is(ConventionalBlockTags.CHESTS)) return PistonEvents.StickyResult.PASS;
			return isBlockEntityPushableSuccess(state, direction) ? PistonEvents.StickyResult.SUCCESS : PistonEvents.StickyResult.PASS;
		});
		PistonEvents.TRY_STICK_BLOCKS_TOGETHER.register(((previousState, nextState, direction) -> {
			return canChestsStick(previousState, nextState, direction) ? PistonEvents.StickTogetherResult.SUCCESS : PistonEvents.StickTogetherResult.PASS;
		}));

		// SHELF
		PistonEvents.DETERMINE_BLOCK_STICKINESS.register((state, direction) -> {
			if (!state.is(BlockTags.WOODEN_SHELVES)) return PistonEvents.StickyResult.PASS;
			return isBlockEntityPushableSuccess(state, direction) ? PistonEvents.StickyResult.SUCCESS : PistonEvents.StickyResult.PASS;
		});
		PistonEvents.TRY_STICK_BLOCKS_TOGETHER.register(((previousState, nextState, direction) -> {
			return canShelvesStick(previousState, nextState, direction) ? PistonEvents.StickTogetherResult.SUCCESS : PistonEvents.StickTogetherResult.PASS;
		}));
	}

	@ApiStatus.Internal
	public static boolean canChestsStick(BlockState state1, BlockState state2, Direction direction) {
		if (!state1.is(ConventionalBlockTags.CHESTS) || !state2.is(ConventionalBlockTags.CHESTS)) return false;
		if (!isBlockEntityPushableSuccess(state1, direction) || !isBlockEntityPushableSuccess(state2, direction)) return false;

		final ChestType chest1Type = state1.getValueOrElse(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
		if (chest1Type == ChestType.SINGLE) return false;

		final ChestType chest2Type = state2.getValueOrElse(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
		if (chest2Type == ChestType.SINGLE) return false;

		if (!state1.hasProperty(ChestBlock.FACING) || !state2.hasProperty(ChestBlock.FACING)) return false;

		final Direction connectedDirection1 = ChestBlock.getConnectedDirection(state1);
		if (connectedDirection1 != direction) return false;

		final Direction connectedDirection2 = ChestBlock.getConnectedDirection(state2);
		return connectedDirection1 == connectedDirection2.getOpposite();
	}

	@ApiStatus.Internal
	public static boolean canShelvesStick(BlockState state1, BlockState state2, Direction direction) {
		if (!state1.is(BlockTags.WOODEN_SHELVES) || !state2.is(BlockTags.WOODEN_SHELVES)) return false;
		if (!isBlockEntityPushableSuccess(state1, direction) || !isBlockEntityPushableSuccess(state2, direction)) return false;

		final SideChainPart chainPart1 = state1.getValueOrElse(ShelfBlock.SIDE_CHAIN_PART, SideChainPart.UNCONNECTED);
		if (chainPart1 == SideChainPart.UNCONNECTED) return false;

		final SideChainPart chainPart2 = state2.getValueOrElse(ShelfBlock.SIDE_CHAIN_PART, SideChainPart.UNCONNECTED);
		if (chainPart2 == SideChainPart.UNCONNECTED) return false;

		if (!state1.hasProperty(ShelfBlock.FACING) || !state2.hasProperty(ShelfBlock.FACING)) return false;
		final Direction facing1 = state1.getValue(ShelfBlock.FACING);
		final Direction facing2 = state2.getValue(ShelfBlock.FACING);
		if (facing1 != facing2) return false;

		final Direction left = facing1.getCounterClockWise();
		final Direction right = facing1.getClockWise();
		final Direction oppositeDirection = direction.getOpposite();

		if (chainPart1 == SideChainPart.CENTER) {
			if (oppositeDirection == left) return chainPart2 != SideChainPart.RIGHT;
			if (oppositeDirection == right) return chainPart2 != SideChainPart.LEFT;
			return false;
		}

		if (chainPart1 == SideChainPart.LEFT) {
			if (oppositeDirection == right) return chainPart2 != SideChainPart.LEFT;
			return false;
		}

		if (chainPart1 == SideChainPart.RIGHT) {
			if (oppositeDirection == left) return chainPart2 != SideChainPart.RIGHT;
			return false;
		}

		return false;
	}
}

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

package net.frozenblock.lib.block.impl;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.Nullable;

public class PushableBlockEntityUtil {

	public static boolean saveBlockEntity(Level level, BlockEntity blockEntity, BlockEntity pistonEntity) {
		if (!(pistonEntity instanceof PistonMovingBlockEntityInterface pistonInterface)) return false;

		final CompoundTag blockEntityTag = blockEntity.saveWithFullMetadata(level.registryAccess());
		pistonInterface.frozenLib$setPushedBlockEntityTag(blockEntityTag);
		return true;
	}

	public static boolean saveTag(@Nullable CompoundTag tag, BlockEntity pistonEntity) {
		if (tag == null) return true;
		if (!(pistonEntity instanceof PistonMovingBlockEntityInterface pistonInterface)) return false;

		pistonInterface.frozenLib$setPushedBlockEntityTag(tag);
		return true;
	}

	public static boolean setBlockAndEntity(
		boolean setBlock,
		Level level,
		BlockPos pos,
		BlockState state,
		PistonMovingBlockEntity pistonEntity
	) {
		if (!state.hasBlockEntity() || !(pistonEntity instanceof PistonMovingBlockEntityInterface pistonInterface)) return setBlock;

		final CompoundTag blockEntityTag = pistonInterface.frozenLib$getPushedBlockEntityTag();
		if (blockEntityTag == null) return setBlock;

		final BlockEntity blockEntity = BlockEntity.loadStatic(pos, state, blockEntityTag, level.registryAccess());
		if (blockEntity != null) level.setBlockEntity(blockEntity);

		return setBlock;
	}

	public static BlockEntity getFakeBlockEntity(BlockEntity pistonEntity) {
		if (!(pistonEntity instanceof PistonMovingBlockEntityInterface pistonInterface)) return null;
		return pistonInterface.frozenLib$getPushedFakeBlockEntity();
	}

	public static boolean canChestsStick(BlockState state1, BlockState state2, Direction direction) {
		if (!state1.is(ConventionalBlockTags.CHESTS) || !state2.is(ConventionalBlockTags.CHESTS)) return false;
		if (!state1.is(FrozenBlockTags.HAS_PUSHABLE_BLOCK_ENTITY) || !state2.is(FrozenBlockTags.HAS_PUSHABLE_BLOCK_ENTITY)) return false;

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

}

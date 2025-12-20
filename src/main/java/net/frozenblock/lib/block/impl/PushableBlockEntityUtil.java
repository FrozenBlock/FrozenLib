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

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

}

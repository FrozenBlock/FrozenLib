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

package net.frozenblock.lib.block.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link MultifaceBlock}, but only one face is permitted.
 */
public abstract class FaceBlock extends MultifaceBlock {
	public FaceBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isValidStateForPlacement(BlockGetter level, BlockState state, BlockPos pos, Direction direction) {
		if (this.isFaceSupported(direction) && !state.is(this)) {
			BlockPos blockPos = pos.relative(direction);
			return canAttachTo(level, direction, blockPos, level.getBlockState(blockPos));
		} else {
			return false;
		}
	}
}

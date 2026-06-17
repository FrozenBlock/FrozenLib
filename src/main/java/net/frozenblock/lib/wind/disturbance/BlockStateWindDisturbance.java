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

package net.frozenblock.lib.wind.disturbance;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

public abstract class BlockStateWindDisturbance<T extends ChunkAccess> implements WindDisturbance<T> {
	private final BlockState blockState;
	private final BlockPos position;

	protected BlockStateWindDisturbance(BlockState blockState, BlockPos position) {
		this.blockState = blockState;
		this.position = position;
	}

	public BlockState getBlockStateAtOrigin(T source) {
		return source.getBlockState(this.position);
	}

	@Override
	public Vec3 origin(T source, Level level) {
		return Vec3.atCenterOf(this.position);
	}

	@Override
	public boolean expired(T source, Level level) {
		return !this.getBlockStateAtOrigin(source).equals(this.blockState);
	}
}

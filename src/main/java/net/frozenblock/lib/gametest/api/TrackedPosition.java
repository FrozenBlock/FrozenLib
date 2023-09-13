/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.gametest.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public record TrackedPosition<T>(PositionType type, T pos, T opposite) {

	/**
	 * Creates a new {@link TrackedPosition} with the given position as its relative value.
	 */
	public static TrackedPosition<BlockPos> createRelative(GameTestHelper helper, BlockPos pos) {
		return new TrackedPosition<>(PositionType.RELATIVE, pos, helper.absolutePos(pos));
	}

	/**
	 * Creates a new {@link TrackedPosition} with the given position as its relative value.
	 * <p>
	 * Asserts the given block is at the position.
	 */
	public static TrackedPosition<BlockPos> createRelative(GameTestHelper helper, Block block, BlockPos pos) {
		return createRelative(helper, pos).assertBlockPresent(helper, block);
	}

	/**
	 * Creates a new {@link TrackedPosition} with the given position as its absolute value.
	 */
	public static TrackedPosition<BlockPos> createAbsolute(GameTestHelper helper, BlockPos pos) {
		return new TrackedPosition<>(PositionType.ABSOLUTE, pos, helper.relativePos(pos));
	}

	/**
	 * Creates a new {@link TrackedPosition} with the given position as its absolute value.
	 * <p>
	 * Asserts the given block is at the relative position.
	 */
	public static TrackedPosition<BlockPos> createAbsolute(GameTestHelper helper, Block block, BlockPos pos) {
		return createAbsolute(helper, pos).assertBlockPresent(helper, block);
	}

	/**
	 * Creates a new {@link TrackedPosition} with the given position as its relative value.
	 */
	public static TrackedPosition<Vec3> createRelative(GameTestHelper helper, Vec3 pos) {
		return new TrackedPosition<>(PositionType.RELATIVE, pos, helper.absoluteVec(pos));
	}

	/**
	 * Creates a new {@link TrackedPosition} with the given position as its relative value.
	 * <p>
	 * Asserts the given block is at the position.
	 */
	public static TrackedPosition<Vec3> createRelative(GameTestHelper helper, Block block, Vec3 pos) {
		return createRelative(helper, pos).assertBlockPresent(helper, block);
	}

	/**
	 * Creates a new {@link TrackedPosition} with the given position as its absolute value.
	 */
	public static TrackedPosition<Vec3> createAbsolute(GameTestHelper helper, Vec3 pos) {
		return new TrackedPosition<>(PositionType.ABSOLUTE, pos, helper.relativeVec(pos));
	}

	/**
	 * Creates a new {@link TrackedPosition} with the given position as its absolute value.
	 * <p>
	 * Asserts the given block is at the relative position.
	 */
	public static TrackedPosition<Vec3> createAbsolute(GameTestHelper helper, Block block, Vec3 pos) {
		return createAbsolute(helper, pos).assertBlockPresent(helper, block);
	}

	public T absolute() {
		return switch (this.type()) {
			case RELATIVE -> this.opposite();
			case ABSOLUTE -> this.pos();
		};
	}

	public T relative() {
		return switch (this.type()) {
			case RELATIVE -> this.pos();
			case ABSOLUTE -> this.opposite();
		};
	}

	public TrackedPosition<T> assertBlockPresent(GameTestHelper helper, Block block) throws IllegalStateException {
		T relative = this.relative();
		if (relative instanceof Position position) { // covers Vec3
			helper.assertBlockPresent(block, BlockPos.containing(position));
		} else if (relative instanceof BlockPos blockPos) {
			helper.assertBlockPresent(block, blockPos);
		} else if (relative instanceof Vec3i vec3i) {
			helper.assertBlockPresent(block, new BlockPos(vec3i));
		} else
			throw new IllegalStateException("Unknown position type: " + relative.getClass().getName());
		return this;
	}
}

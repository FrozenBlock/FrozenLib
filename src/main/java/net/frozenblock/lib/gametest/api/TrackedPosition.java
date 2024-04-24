/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.gametest.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

/**
 *
 * @param type The {@link PositionType} linked to {@link #pos}.
 * @param pos The position
 * @param opposite The position in the opposite position type
 * @param <T> The type of position being tracked
 * @since 1.3.8
 */
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
		} else if (relative instanceof Vector3ic vec3i) {
			helper.assertBlockPresent(block, new BlockPos(vec3i.x(), vec3i.y(), vec3i.z()));
		} else if (relative instanceof Vector3fc vec3f) {
			helper.assertBlockPresent(block, BlockPos.containing(vec3f.x(), vec3f.y(), vec3f.z()));
		} else if (relative instanceof Vector3dc vec3d) {
			helper.assertBlockPresent(block, BlockPos.containing(vec3d.x(), vec3d.y(), vec3d.z()));
		} else
			throw new IllegalStateException("Invalid position type: " + relative.getClass().getName());
		return this;
	}
}

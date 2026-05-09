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

package net.frozenblock.lib.block.impl.waterlike;

import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public interface InWaterLikeInterface {
	default void frozenLib$setInWaterLike(WaterLikeType type, boolean inside) {
		throw new AssertionError();
	}

	default void frozenLib$clearInWaterLikes() {
		throw new AssertionError();
	}

	default boolean frozenLib$wasInWaterLike(WaterLikeType type) {
		throw new AssertionError();
	}

	default Map<WaterLikeType, Boolean> frozenLib$inWaterLikeStatuses() {
		throw new AssertionError();
	}

	default void frozenLib$setTouchingWaterLike(WaterLikeType type, boolean touching) {
		throw new AssertionError();
	}

	default void frozenLib$clearTouchingWaterLikes() {
		throw new AssertionError();
	}

	default boolean frozenLib$wasTouchingWaterLike(WaterLikeType type) {
		throw new AssertionError();
	}

	default Map<WaterLikeType, Boolean> frozenLib$touchingWaterLikeStatuses() {
		throw new AssertionError();
	}

	default boolean frozenLib$isTouchingWaterLikeOrUnderWaterAndWaterLike(WaterLikeType type) {
		throw new AssertionError();
	}

	default void frozenLib$setWaterReplacementParticlesFromBlock(@Nullable WaterLikeBlock waterLike) {
		throw new AssertionError();
	}
}

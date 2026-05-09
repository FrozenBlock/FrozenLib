/*
 * Copyright 2025-2026 FrozenBlock
 * This file is part of Wilder Wild.
 *
 * This program is free software; you can modify it under
 * the terms of version 1 of the FrozenBlock Modding Oasis License
 * as published by FrozenBlock Modding Oasis.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * FrozenBlock Modding Oasis License for more details.
 *
 * You should have received a copy of the FrozenBlock Modding Oasis License
 * along with this program; if not, see <https://github.com/FrozenBlock/Licenses>.
 */

package net.frozenblock.lib.block.impl.waterlike;

import java.util.Map;

public interface PlayerInWaterLikeInterface {
	default void frozenLib$setPlayerInWaterLike(WaterLikeType type, boolean inside) {
		throw new AssertionError();
	}

	default boolean frozenLib$wasPlayerInWaterLike(WaterLikeType type) {
		throw new AssertionError();
	}

	default Map<WaterLikeType, Boolean> frozenLib$playerInWaterLikeStatuses() {
		throw new AssertionError();
	}
}

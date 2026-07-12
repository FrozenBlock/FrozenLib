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

package net.frozenblock.lib.storage.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.storage.ValueInput;

public interface ValueInputExtension {

	@SuppressWarnings("deprecation")
	default Collection<String> frozenLib$keySet() {
		return ((ValueInput) this).read(ValueIOCodecs.KEYS_EXTRACT).orElse(List.of());
	}

	@SuppressWarnings("deprecation")
	default boolean frozenLib$contains(String key) {
		return ((ValueInput) this).read(ValueIOCodecs.contains(key)).orElseThrow();
	}

	default Optional<long[]> frozenLib$getOptionalLongArray(String key) {
		return ((ValueInput) this).read(key, ValueIOCodecs.LONG_ARRAY);
	}

	default Optional<byte[]> frozenLib$getOptionalByteArray(String key) {
		return ((ValueInput) this).read(key, ValueIOCodecs.BYTE_ARRAY);
	}
}

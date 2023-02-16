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

package net.frozenblock.lib.config.api.entry;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import java.util.List;

public record TypedEntryType<T>(String modId, Codec<T> codec) {

	public static final TypedEntryType<Boolean> BOOLEAN = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.BOOL)
	);

	public static final TypedEntryType<Byte> BYTE = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.BYTE)
	);
	public static final TypedEntryType<Short> SHORT = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.SHORT)
	);
	public static final TypedEntryType<Integer> INTEGER = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.INT)
	);
	public static final TypedEntryType<Long> LONG = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.LONG)
	);
	public static final TypedEntryType<Float> FLOAT = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.FLOAT)
	);
	public static final TypedEntryType<Double> DOUBLE = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.DOUBLE)
	);

	public static final TypedEntryType<String> STRING = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.STRING)
	);

	// LISTS

	public static final TypedEntryType<List<Byte>> BYTE_LIST = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.list(Codec.BYTE))
	);
	public static final TypedEntryType<List<Short>> SHORT_LIST = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.list(Codec.SHORT))
	);
	public static final TypedEntryType<List<Integer>> INTEGER_LIST = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.list(Codec.INT))
	);
	public static final TypedEntryType<List<Long>> LONG_LIST = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.list(Codec.LONG))
	);
	public static final TypedEntryType<List<Float>> FLOAT_LIST = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.list(Codec.FLOAT))
	);
	public static final TypedEntryType<List<Double>> DOUBLE_LIST = register(
			new TypedEntryType<>(FrozenMain.MOD_ID, Codec.list(Codec.DOUBLE))
	);

	public static <T> TypedEntryType<T> register(TypedEntryType<T> type) {
		return ConfigRegistry.register(type);
	}
}

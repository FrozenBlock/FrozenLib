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

import com.google.gson.reflect.TypeToken;
import net.frozenblock.lib.FrozenMain;
import org.jetbrains.annotations.ApiStatus;

public class TypedEntry<T> {

	@ApiStatus.Internal
	public static final String DEFAULT_MOD_ID = FrozenMain.MOD_ID + "_default";

	public static final TypeToken<TypedEntry<?>> TYPE_TOKEN = new TypeToken<>() {
	};

	private final TypedEntryType<T> type;
	private T value;

	public TypedEntry(TypedEntryType<T> type, T value) {
		this.type = type;
		this.value = value;
	}

	public TypedEntryType<T> type() {
		return type;
	}

	public T value() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}
}

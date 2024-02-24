/*
 * Copyright 2023-2024 FrozenBlock
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

// java records are meant to be immutable but i dont care
public class TypedEntry<T>{
	public final TypedEntryType<T> type;
	private T value;

    public TypedEntry(TypedEntryType<T> type, T value) {
        this.type = type;
        this.value = value;
    }

	public T value() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public TypedEntryType<T> type() {
		return this.type;
	}
}

/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.config.api.entry;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import org.jetbrains.annotations.Contract;

public record TypedEntryType<T>(String modId, Codec<T> codec) {

	public TypedEntryType<T> register() {
		return register(this);
	}

	@Contract("_ -> param1")
	public static <T> TypedEntryType<T> register(TypedEntryType<T> type) {
		return ConfigRegistry.register(type);
	}
}

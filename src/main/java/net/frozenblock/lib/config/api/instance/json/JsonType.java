/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.config.api.instance.json;

import blue.endless.jankson.JsonGrammar;
import net.frozenblock.lib.config.api.instance.ConfigSerialization;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum JsonType implements StringRepresentable {
	JSON("json", JsonGrammar.STRICT),
	JSON5("json5", JsonGrammar.JSON5),
	/**
	 * Like JSON5 but it supports not having quotes on keys
	 */
	JSON5_UNQUOTED_KEYS("json5", ConfigSerialization.JSON5_UNQUOTED_KEYS);

	@NotNull
	private final String name;

	@NotNull
	private final JsonGrammar grammar;

	JsonType(@NotNull String name, @NotNull JsonGrammar grammar) {
		this.name = name;
		this.grammar = grammar;
	}

	@Override
	@NotNull
	public String getSerializedName() {
		return this.name;
	}

	@NotNull
	public JsonGrammar getGrammar() {
		return this.grammar;
	}
}

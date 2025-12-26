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

package net.frozenblock.lib.config.api.instance;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.json.JanksonTypedEntrySerializer;

public class ConfigSerialization {
	public static final JsonGrammar JSON5_UNQUOTED_KEYS = JsonGrammar.builder()
		.withComments(true)
		.printTrailingCommas(true)
		.bareSpecialNumerics(true)
		.printUnquotedKeys(true)
		.build();

	public static final JsonGrammar JSON5_UNQUOTED_KEYS_NO_ROOT = JsonGrammar.builder()
		.withComments(true)
		.printTrailingCommas(true)
		.bareSpecialNumerics(true)
		.printUnquotedKeys(true)
		.bareRootObject(true)
		.build();

	private ConfigSerialization() {}

	public static Jankson createJankson(Jankson.Builder builder, String modId) {
		final JanksonTypedEntrySerializer typedEntrySerializer = new JanksonTypedEntrySerializer(modId);
		return builder
			.registerSerializer(TypedEntry.class, typedEntrySerializer)
			.registerDeserializer(JsonElement.class, TypedEntry.class, typedEntrySerializer)
			.build();
	}

	public static Jankson createJankson(String modId) {
		return createJankson(Jankson.builder(), modId);
	}
}

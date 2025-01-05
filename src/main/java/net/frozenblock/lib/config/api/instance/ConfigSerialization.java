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

package net.frozenblock.lib.config.api.instance;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import com.mojang.datafixers.DataFixer;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.json.JanksonTypedEntrySerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigSerialization {
	private ConfigSerialization() {}

	// JANKSON

	public static final JsonGrammar JSON5_UNQUOTED_KEYS = JsonGrammar.builder()
		.withComments(true)
		.printTrailingCommas(true)
		.bareSpecialNumerics(true)
		.printUnquotedKeys(true)
		.build();

	public static Jankson createJankson(@NotNull Jankson.Builder builder, String modId) {
		JanksonTypedEntrySerializer typedEntrySerializer = new JanksonTypedEntrySerializer(modId);
		return builder
			.registerSerializer(TypedEntry.class, typedEntrySerializer)
			.registerDeserializer(JsonElement.class, TypedEntry.class, typedEntrySerializer)
			.build();
	}

	public static Jankson createJankson(String modId, @Nullable DataFixer dataFixer) {
		return createJankson(Jankson.builder().withFixer(dataFixer), modId);
	}

	public static Jankson createJankson(String modId) {
		return createJankson(modId, null);
	}
}

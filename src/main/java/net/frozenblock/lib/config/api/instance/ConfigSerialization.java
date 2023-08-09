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

package net.frozenblock.lib.config.api.instance;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.json.JanksonTypedEntrySerializer;

public class ConfigSerialization {
	private ConfigSerialization() {
	}

	// JANKSON

	public static Jankson createJankson(Jankson.Builder builder, String modId) {
		JanksonTypedEntrySerializer typedEntrySerializer = new JanksonTypedEntrySerializer(modId);
		return builder
			.registerSerializer(TypedEntry.class, typedEntrySerializer)
			.registerDeserializer(JsonElement.class, TypedEntry.class, typedEntrySerializer)
			.build();
	}

	public static Jankson createJankson(String modId) {
		return createJankson(Jankson.builder(), modId);
	}
}

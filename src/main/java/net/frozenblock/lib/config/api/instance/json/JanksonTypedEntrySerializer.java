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

package net.frozenblock.lib.config.api.instance.json;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.Marshaller;
import com.google.gson.JsonParseException;
import java.util.Objects;
import java.util.function.BiFunction;
import net.frozenblock.lib.config.api.entry.TypedEntry;

public class JanksonTypedEntrySerializer implements BiFunction<TypedEntry, Marshaller, JsonElement> {

	private final String modId;

	public JanksonTypedEntrySerializer(String modId) {
		this.modId = modId;
	}

	/**
	 * Serializes a {@link TypedEntry} to a {@link JsonElement}.
	 */
	@Override
	public JsonElement apply(TypedEntry src, Marshaller marshaller) {
		if (src != null) {
			var type = src.type();
			if (type != null) {
				if (Objects.equals(type.modId(), this.modId)) {
					var codec = type.codec();
					if (codec != null) {
						var encoded = codec.encodeStart(JanksonOps.INSTANCE, src.value());
						if (encoded != null && encoded.error().isEmpty()) {
							var optional = encoded.result();
							if (optional.isPresent()) {
								return (JsonElement) optional.get();
							}
						}
					}
				}
			}
		}
		throw new JsonParseException("Failed to serialize typed entry " + src);
	}
}

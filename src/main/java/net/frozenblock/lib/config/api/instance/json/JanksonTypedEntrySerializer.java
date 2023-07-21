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
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.DeserializerFunction;
import blue.endless.jankson.api.Marshaller;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;

public class JanksonTypedEntrySerializer implements BiFunction<TypedEntry, Marshaller, JsonElement>, DeserializerFunction<JsonElement, TypedEntry> {

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

	/**
	 * Deserializes a {@link JsonElement} to a {@link TypedEntry}.
	 */
	@Override
	public TypedEntry apply(JsonElement json, Marshaller m) throws DeserializationException {
		var modEntry = getFromRegistry(json, ConfigRegistry.getForMod(this.modId));
		if (modEntry != null) {
			return modEntry;
		}
		throw new DeserializationException("Failed to deserialize typed entry " + json);
	}

	private TypedEntry<?> getFromRegistry(JsonElement json, Collection<TypedEntryType<?>> registry) {
		for (var entryType : registry) {
			var entry = getFromType(json, entryType);
			if (entry != null) {
				return entry;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> TypedEntry<T> getFromType(JsonElement json, TypedEntryType<?> entryType) throws ClassCastException {
		if (entryType.modId().equals(modId)) {
			var codec = entryType.codec();
			DataResult<? extends Pair<?, JsonElement>> result = codec.decode(JanksonOps.INSTANCE, json);

			if (result.error().isEmpty()) {
				var optional = result.result();

				if (optional.isPresent()) {
					Pair<?, JsonElement> pair = optional.get();
					Object first = pair.getFirst();
					TypedEntryType<T> newType = (TypedEntryType<T>) entryType;
					T newFirst = (T) first;
					TypedEntry<T> entry = new TypedEntry<>(newType, newFirst);
					FrozenMain.log("Built typed entry " + entry, FrozenMain.UNSTABLE_LOGGING);
					return entry;
				}
			}
		}
		return null;
	}
}

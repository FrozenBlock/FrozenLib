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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;

public class TypedEntrySerializer<T> implements JsonSerializer<TypedEntry<T>>, JsonDeserializer<TypedEntry<T>> {

	private final String modId;

	public TypedEntrySerializer(String modId) {
		this.modId = modId;
	}

	@Override
	public TypedEntry<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		var modEntry = getFromRegistry(json, ConfigRegistry.getForMod(this.modId));
		if (modEntry != null) {
			return modEntry;
		} else {
			FrozenMain.error("Failed to deserialize typed entry " + json, true);
			return new TypedEntry<>(null, null);
		}
	}

	private TypedEntry<T> getFromRegistry(JsonElement json, Collection<TypedEntryType<?>> registry) {
		for (var entryType : registry) {
			var entry = getFromType(json, entryType);
			if (entry != null)
				return entry;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private TypedEntry<T> getFromType(JsonElement json, TypedEntryType<?> entryType) throws ClassCastException {
		if (entryType.modId().equals(this.modId)) {
			var codec = entryType.codec();
			DataResult<? extends Pair<?, JsonElement>> result = codec.decode(JsonOps.INSTANCE, json);

			if (result.error().isEmpty()) {
				var optional = result.result();

				if (optional.isPresent()) {
					Pair<?, JsonElement> pair = optional.get();
					Object first = pair.getFirst();
					TypedEntryType<T> newType = (TypedEntryType<T>) entryType;
					T newFirst = (T) first;
					TypedEntry<T> entry = new TypedEntry<>(newType, newFirst);
					FrozenMain.log("Built entry " + entry, FrozenMain.UNSTABLE_LOGGING);
					return entry;
				}
			}
		}
		return null;
	}

	@Override
	public JsonElement serialize(TypedEntry<T> src, Type typeOfSrc, JsonSerializationContext context) {
		if (src != null) {
			var type = src.type();
			if (type != null) {
				if (Objects.equals(type.modId(), this.modId)) {
					var codec = type.codec();
					if (codec != null) {
						var encoded = codec.encodeStart(JsonOps.INSTANCE, src.value());
						if (encoded != null && encoded.error().isEmpty()) {
							var optional = encoded.result();
							if (optional.isPresent()) {
								return optional.get();
							}
						}
					}
				}
			}
		}
		return JsonOps.INSTANCE.empty();
	}
}

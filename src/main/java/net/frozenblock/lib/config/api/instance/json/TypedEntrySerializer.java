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

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

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
			var defaultEntry = getFromRegistry(json, ConfigRegistry.getDefault());
			if (defaultEntry != null) {
				return defaultEntry;
			}
		}
		FrozenMain.error("Failed to deserialize typed entry " + json, true);
		return new TypedEntry<>(null, null);
	}

	@Nullable
	private TypedEntry<T> getFromRegistry(JsonElement json, Collection<TypedEntryType<?>> registry) {
		for (var entryType : registry) {
			if (Objects.equals(entryType.modId(), this.modId) || Objects.equals(entryType.modId(), TypedEntry.DEFAULT_MOD_ID)) {
				var codec = entryType.codec();
				var result = codec.decode(JsonOps.INSTANCE, json);

				if (result.error().isPresent()) {
					continue;
				}

				var optional = result.result();
				if (optional.isPresent()) {
					try {
						Pair<T, JsonElement> type = (Pair<T, JsonElement>) optional.get();
						var entry = new TypedEntry<>((TypedEntryType<T>) entryType, type.getFirst());
						FrozenMain.log("Built new typed entry: " + entry + " type: " + entry.type() + " value: " + entry.value(), FrozenMain.UNSTABLE_LOGGING);
						return entry;
					} catch (Throwable ignored) {
					}
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
				if (Objects.equals(type.modId(), this.modId) || Objects.equals(type.modId(), TypedEntry.DEFAULT_MOD_ID)) {
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

/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.config.api.instance.json;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.DeserializerFunction;
import blue.endless.jankson.api.Marshaller;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JanksonTypedEntrySerializer implements BiFunction<TypedEntry, Marshaller, JsonElement>, DeserializerFunction<JsonElement, TypedEntry> {

	private final String modId;

	public JanksonTypedEntrySerializer(String modId) {
		this.modId = modId;
	}

	/**
	 * Serializes a {@link TypedEntry} to a {@link JsonElement}.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public JsonElement apply(TypedEntry src, Marshaller marshaller) {
		if (src != null) {
			var type = src.type();
			if (type != null && Objects.equals(type.modId(), this.modId)) {
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
		throw new JsonParseException("Failed to serialize typed entry " + src);
	}

	/**
	 * Deserializes a {@link JsonElement} to a {@link TypedEntry}.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public TypedEntry apply(JsonElement json, Marshaller m) throws DeserializationException {
		var modEntry = getFromRegistry(json, ConfigRegistry.getTypedEntryTypesForMod(this.modId));
		if (modEntry != null) {
			return modEntry;
		}
		throw new DeserializationException("Failed to deserialize typed entry " + json);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private <T> TypedEntry<T> getFromRegistry(JsonElement json, @NotNull Collection<TypedEntryType<?>> registry) throws ClassCastException {
		for (TypedEntryType<?> entryType : registry) {
			TypedEntryType<T> newType = (TypedEntryType<T>) entryType;
			TypedEntry<T> entry = getFromType(json, newType);
			if (entry != null) {
				return entry;
			}
		}
		return null;
	}

	@Nullable
	private <T> TypedEntry<T> getFromType(JsonElement json, @NotNull TypedEntryType<T> entryType) throws ClassCastException {
		if (entryType.modId().equals(modId)) {
			var codec = entryType.codec();
			DataResult<Pair<T, JsonElement>> result = codec.decode(JanksonOps.INSTANCE, json);

			if (result.error().isEmpty()) {
				var optional = result.result();

				if (optional.isPresent()) {
					Pair<T, JsonElement> pair = optional.get();
					T first = pair.getFirst();
					TypedEntry<T> entry = new TypedEntry<>(entryType, first);
					FrozenLogUtils.log("Built typed entry " + entry, FrozenSharedConstants.UNSTABLE_LOGGING);
					return entry;
				}
			}
		}
		return null;
	}
}

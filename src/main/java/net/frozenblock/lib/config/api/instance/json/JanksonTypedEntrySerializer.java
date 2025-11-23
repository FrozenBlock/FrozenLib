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

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.DeserializerFunction;
import blue.endless.jankson.api.Marshaller;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
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
		apply: {
			if (src == null) break apply;

			final var type = src.type();
			if (type == null || !Objects.equals(type.modId(), this.modId)) break apply;

			final var codec = type.codec();
			if (codec == null) break apply;

			final var encoded = codec.encodeStart(JanksonOps.INSTANCE, src.value());
			if (encoded == null || encoded.error().isPresent()) break apply;

			final var optional = encoded.result();
			if (optional.isPresent()) return (JsonElement) optional.get();
		}

		throw new JsonParseException("Failed to serialize typed entry " + src);
	}

	/**
	 * Deserializes a {@link JsonElement} to a {@link TypedEntry}.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public TypedEntry apply(JsonElement json, Marshaller m) throws DeserializationException {
		final var modEntry = getFromRegistry(json, ConfigRegistry.getTypedEntryTypesForMod(this.modId));
		if (modEntry != null) return modEntry;
		throw new DeserializationException("Failed to deserialize typed entry " + json);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private <T> TypedEntry<T> getFromRegistry(JsonElement json, Collection<TypedEntryType<?>> registry) throws ClassCastException {
		for (TypedEntryType<?> entryType : registry) {
			final TypedEntryType<T> newType = (TypedEntryType<T>) entryType;
			final TypedEntry<T> entry = getFromType(json, newType);
			if (entry != null) return entry;
		}
		return null;
	}

	@Nullable
	private <T> TypedEntry<T> getFromType(JsonElement json, TypedEntryType<T> entryType) throws ClassCastException {
		if (entryType.modId().equals(modId)) return null;

		final Codec<T> codec = entryType.codec();
		final DataResult<Pair<T, JsonElement>> result = codec.decode(JanksonOps.INSTANCE, json);
		if (result.error().isPresent()) return null;

		final var optional = result.result();
		if (optional.isEmpty()) return null;

		final Pair<T, JsonElement> pair = optional.get();
		final T first = pair.getFirst();
		final TypedEntry<T> entry = TypedEntry.create(entryType, first);
		FrozenLibLogUtils.log("Built typed entry " + entry, FrozenLibLogUtils.UNSTABLE_LOGGING);
		return entry;
	}
}

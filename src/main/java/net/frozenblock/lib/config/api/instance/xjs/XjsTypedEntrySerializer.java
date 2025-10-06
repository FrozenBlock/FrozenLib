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

package net.frozenblock.lib.config.api.instance.xjs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xjs.data.JsonValue;

public final class XjsTypedEntrySerializer {
    private XjsTypedEntrySerializer() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static JsonValue toJsonValue(final TypedEntry src) throws NonSerializableObjectException {
        if (src != null) {
            TypedEntryType type = src.type();
            if (type != null) {
                Codec codec = type.codec();
                if (codec != null) {
                    var encoded = codec.encodeStart(XjsOps.INSTANCE, src.value());
                    if (encoded != null && encoded.error().isEmpty()) {
                        var optional = encoded.result();
                        if (optional.isPresent()) {
                            return (JsonValue) optional.get();
                        }
                    }
                }
            }
        }
        throw new NonSerializableObjectException("Failed to serialize typed entry " + src);
    }

    public static TypedEntry<?> fromJsonValue(final String modId, final JsonValue value) throws NonSerializableObjectException {
        TypedEntry<?> modEntry = getFromRegistry(modId, value, ConfigRegistry.getTypedEntryTypesForMod(modId));
        if (modEntry != null) {
            return modEntry;
        }
        throw new NonSerializableObjectException("Failed to deserialize typed entry" + value);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T> TypedEntry<T> getFromRegistry(final String modId, final JsonValue value, final @NotNull Collection<TypedEntryType<?>> registry) throws ClassCastException {
        for (TypedEntryType<?> entryType : registry) {
            TypedEntryType<T> newType = (TypedEntryType<T>) entryType;
            TypedEntry<T> entry = getFromType(modId, value, newType);
            if (entry != null) {
                return entry;
            }
        }
        return null;
    }

    @Nullable
    private static <T> TypedEntry<T> getFromType(String modId, JsonValue value, @NotNull TypedEntryType<T> entryType) throws ClassCastException {
        if (!entryType.modId().equals(modId))
            return null;

        var codec = entryType.codec();
        DataResult<Pair<T, JsonValue>> result = codec.decode(XjsOps.INSTANCE, value);
        if (result.error().isPresent())
            return null;

        var optional = result.result();
        if (optional.isEmpty()) return null;

        Pair<T, JsonValue> pair = optional.get();
        T first = pair.getFirst();
        TypedEntry<T> entry = TypedEntry.create(entryType, first);
        FrozenLibLogUtils.log("Built typed entry " + entry, FrozenLibLogUtils.UNSTABLE_LOGGING);
        return entry;
    }
}

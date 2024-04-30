/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
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

package net.frozenblock.lib.config.api.instance.xjs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
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
        TypedEntry<T> entry = new TypedEntry<>(entryType, first);
        FrozenLogUtils.log("Built typed entry " + entry, FrozenSharedConstants.UNSTABLE_LOGGING);
        return entry;
    }
}
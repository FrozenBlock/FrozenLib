package net.frozenblock.lib.config.api.entry;

import com.mojang.serialization.Codec;

public record TypedEntryType<T>(String modId, Class<T> classType, Codec<T> codec) {
}

package net.frozenblock.lib.config.api.entry;

import com.mojang.serialization.Codec;

public record TypedEntry<T>(String modId, Class<T> classType, Codec<T> codec) {
}

package net.frozenblock.lib.config.api.entry;

import com.mojang.serialization.Codec;

public record TypedEntry<T>(Class<T> classType, Codec<T> codec) {
}

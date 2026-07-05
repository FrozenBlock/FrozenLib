package net.frozenblock.lib.storage.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.storage.ValueInput;

public interface ValueInputExtension {

	@SuppressWarnings("deprecation")
	default Collection<String> frozenLib$keySet() {
		return ((ValueInput) this).read(ValueIOCodecs.KEYS_EXTRACT).orElse(List.of());
	}

	@SuppressWarnings("deprecation")
	default boolean frozenLib$contains(String key) {
		return ((ValueInput) this).read(ValueIOCodecs.contains(key)).orElseThrow();
	}

	default Optional<long[]> frozenLib$getOptionalLongArray(String key) {
		return ((ValueInput) this).read(key, ValueIOCodecs.LONG_ARRAY);
	}

	default Optional<byte[]> frozenLib$getOptionalByteArray(String key) {
		return ((ValueInput) this).read(key, ValueIOCodecs.BYTE_ARRAY);
	}
}

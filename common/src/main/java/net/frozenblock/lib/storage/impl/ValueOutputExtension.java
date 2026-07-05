package net.frozenblock.lib.storage.impl;

import net.minecraft.world.level.storage.ValueOutput;

public interface ValueOutputExtension {

	default void frozenLib$putLongArray(String key, long[] value) {
		((ValueOutput) this).store(key, ValueIOCodecs.LONG_ARRAY, value);
	}

	default void frozenLib$putByteArray(String key, byte[] value) {
		((ValueOutput) this).store(key, ValueIOCodecs.BYTE_ARRAY, value);
	}
}

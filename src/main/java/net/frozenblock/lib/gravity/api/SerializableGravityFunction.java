package net.frozenblock.lib.gravity.api;

import com.mojang.serialization.Codec;

public interface SerializableGravityFunction<T extends GravityFunction> extends GravityFunction {
	Codec<T> codec();
}

package net.frozenblock.lib.worldgen.feature.api;

import net.minecraft.data.worldgen.BootstapContext;

public final class FrozenFeatureUtils {
	private FrozenFeatureUtils() {
		throw new UnsupportedOperationException("FrozenFeatureUtils contains only static declarations.");
	}

	public static BootstapContext<Object> BOOTSTAP_CONTEXT = null;
}

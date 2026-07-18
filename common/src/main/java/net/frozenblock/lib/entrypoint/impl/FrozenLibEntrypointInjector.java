package net.frozenblock.lib.entrypoint.impl;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibEntrypointInjector {

	@PlatformImpl
	public static void inject() {
		throw new AssertionError();
	}

	private FrozenLibEntrypointInjector() {}
}
